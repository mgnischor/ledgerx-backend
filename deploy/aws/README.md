# AWS deployment

Provisions the AWS infrastructure ledgerx-backend runs on: a VPC (public +
private subnets across two availability zones, one NAT gateway), an EKS
cluster with a managed node group and the EBS CSI driver (so the Postgres
and RabbitMQ `PersistentVolumeClaim`s in `k8s/` can bind), and an ECR
repository to hold the application image.

This configuration provisions the *cluster and registry only*. The
application, Postgres, RabbitMQ and the optional Grafana LGTM stack are then
deployed into that cluster with the Kustomize manifests in `k8s/` at the
repository root — see [`k8s/README.md`](../../k8s/README.md).

## Prerequisites

- Terraform >= 1.6
- An AWS account and credentials available to the AWS provider (e.g. via
  `aws configure`, environment variables, or an assumed role)
- AWS CLI and `kubectl` installed locally
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

Build, tag and push the application image to the ECR repository created
above:

```
$(terraform output -raw ecr_login_command)
docker build -t "$(terraform output -raw ecr_repository_url):latest" ../..
docker push "$(terraform output -raw ecr_repository_url):latest"
```

Deploy the application and its dependencies, then point the Deployment at
the pushed image:

```
kubectl apply -k ../../k8s
kubectl -n ledgerx set image deployment/ledgerx-backend \
  ledgerx-backend="$(terraform output -raw ecr_repository_url):latest"
```

## Teardown

```
kubectl delete -k ../../k8s
terraform destroy
```

## Notes

- `02-secrets.yaml` in `k8s/` ships plaintext placeholder credentials —
  replace them (e.g. with AWS Secrets Manager + the External Secrets
  Operator) before running this anywhere beyond evaluation.
- A single NAT gateway is used for all private subnets to keep costs down;
  add one per availability zone for NAT-level high availability.
- State is local by default; see the commented-out `backend "s3"` block in
  `versions.tf` for team use.
