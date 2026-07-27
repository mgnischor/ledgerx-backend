# Azure deployment

Provisions the Azure infrastructure ledgerx-backend runs on: a resource
group, a virtual network/subnet, an AKS cluster with an autoscaling node
pool and Azure CNI networking, and an ACR registry to hold the application
image (with the cluster's managed identity granted `AcrPull` on it, so no
image pull secret is needed).

This configuration provisions the *cluster and registry only*. The
application, Postgres, RabbitMQ and the optional Grafana LGTM stack are then
deployed into that cluster with the Kustomize manifests in `k8s/` at the
repository root — see [`k8s/README.md`](../../k8s/README.md).

## Prerequisites

- Terraform >= 1.6
- An Azure subscription and an authenticated Azure CLI (`az login`)
- `kubectl` installed locally
- Docker, to build and push the application image

## Usage

```
cp terraform.tfvars.example terraform.tfvars
# edit terraform.tfvars as needed

terraform init
terraform plan
terraform apply
```

Configure `kubectl` against the new cluster:

```
$(terraform output -raw configure_kubectl)
```

Build, tag and push the application image to the ACR repository created
above:

```
$(terraform output -raw acr_login_command)
docker build -t "$(terraform output -raw acr_login_server)/ledgerx-backend:latest" ../..
docker push "$(terraform output -raw acr_login_server)/ledgerx-backend:latest"
```

Deploy the application and its dependencies, then point the Deployment at
the pushed image:

```
kubectl apply -k ../../k8s
kubectl -n ledgerx set image deployment/ledgerx-backend \
  ledgerx-backend="$(terraform output -raw acr_login_server)/ledgerx-backend:latest"
```

## Teardown

```
kubectl delete -k ../../k8s
terraform destroy
```

## Notes

- `02-secrets.yaml` in `k8s/` ships plaintext placeholder credentials —
  replace them (e.g. with Azure Key Vault + the Secrets Store CSI driver)
  before running this anywhere beyond evaluation.
- AKS ships a default `managed-csi` StorageClass out of the box, so the
  Postgres/RabbitMQ `PersistentVolumeClaim`s in `k8s/` bind without any extra
  configuration.
- State is local by default; see the commented-out `backend "azurerm"` block
  in `versions.tf` for team use.
