# Google Cloud deployment

Provisions the Google Cloud infrastructure ledgerx-backend runs on: a
VPC-native GKE cluster (with Cloud NAT for outbound access, a dedicated
minimally-privileged node service account, and Workload Identity enabled)
and an Artifact Registry repository to hold the application image.

This configuration provisions the *cluster and registry only*. The
application, Postgres, RabbitMQ and the optional Grafana LGTM stack are then
deployed into that cluster with the Kustomize manifests in `k8s/` at the
repository root — see [`k8s/README.md`](../../k8s/README.md).

## Prerequisites

- Terraform >= 1.6
- A Google Cloud project with billing enabled, and an authenticated
  `gcloud` CLI (`gcloud auth application-default login`)
- `kubectl` installed locally
- Docker, to build and push the application image

## Usage

```
cp terraform.tfvars.example terraform.tfvars
# edit terraform.tfvars: at minimum, set project_id

terraform init
terraform plan
terraform apply
```

Configure `kubectl` against the new cluster:

```
$(terraform output -raw configure_kubectl)
```

Build, tag and push the application image to the Artifact Registry
repository created above:

```
$(terraform output -raw artifact_registry_login_command)
docker build -t "$(terraform output -raw artifact_registry_repository)/ledgerx-backend:latest" ../..
docker push "$(terraform output -raw artifact_registry_repository)/ledgerx-backend:latest"
```

Deploy the application and its dependencies, then point the Deployment at
the pushed image:

```
kubectl apply -k ../../k8s
kubectl -n ledgerx set image deployment/ledgerx-backend \
  ledgerx-backend="$(terraform output -raw artifact_registry_repository)/ledgerx-backend:latest"
```

## Teardown

```
kubectl delete -k ../../k8s
terraform destroy
```

## Notes

- `02-secrets.yaml` in `k8s/` ships plaintext placeholder credentials —
  replace them (e.g. with Secret Manager + the Secret Manager CSI driver)
  before running this anywhere beyond evaluation.
- `deletion_protection` on the cluster is left `false` so `terraform destroy`
  works out of the box during evaluation; set it back to `true` once the
  cluster holds anything that matters.
- State is local by default; see the commented-out `backend "gcs"` block in
  `versions.tf` for team use.
