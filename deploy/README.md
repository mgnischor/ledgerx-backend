# Cloud deployment

Terraform configurations that provision a managed Kubernetes cluster and
container registry for ledgerx-backend on each of the three major cloud
providers:

| Folder             | Cloud         | Cluster              | Registry           |
| ------------------- | ------------- | --------------------- | ------------------- |
| [`aws/`](aws)     | Amazon Web Services | EKS             | ECR                 |
| [`azure/`](azure) | Microsoft Azure     | AKS             | ACR                 |
| [`gcp/`](gcp)     | Google Cloud        | GKE             | Artifact Registry   |

## Scope

Each configuration provisions only the cloud-specific infrastructure a
Kubernetes cluster needs to exist and pull images: a virtual network, the
managed Kubernetes control plane and a worker node pool, IAM wiring so nodes
can pull from the registry (and, on AWS, so the EBS CSI driver can manage
volumes), and the registry itself.

Everything that runs *inside* the cluster — the ledgerx-backend application,
Postgres, RabbitMQ and the optional Grafana LGTM observability stack — is
the same regardless of cloud, and is defined once as Kubernetes manifests in
[`k8s/`](../k8s) at the repository root. Each cloud's `README.md` shows the
exact commands to point `kubectl`/Docker at that cloud and apply them there.

This split keeps the three clouds consistent with each other and with local
development (`kind`/`minikube`/Docker Desktop, see `k8s/README.md`) instead
of duplicating three different flavors of managed-database/managed-broker
configuration.

## Usage

Pick a cloud, then follow its `README.md`:

```
cd aws    # or azure, or gcp
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
```

## Conventions shared across all three

- **No embedded credentials.** Every provider is configured to use the
  cloud's standard local credential chain (AWS CLI/profile, `az login`,
  `gcloud auth application-default login`, or CI-provided equivalents).
  Nothing secret is read from or written to these `.tf` files.
- **State is local by default.** Fine for a single operator; each
  `versions.tf` has a commented-out remote backend block (S3, Azure Storage,
  GCS) to switch to for team use.
- **`terraform.tfvars.example`** in each folder lists every variable with a
  sensible default; copy it to `terraform.tfvars` (already `.gitignore`d)
  and adjust before applying.
- **Registries are private and unauthenticated by default** — nodes pull
  through cloud-native IAM (an IRSA-scoped role on AWS, the kubelet managed
  identity on Azure, the node service account on GCP), not a shared
  imagePullSecret.
