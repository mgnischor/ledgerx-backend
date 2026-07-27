// Provider configuration for AWS. Credentials are not defined here: the AWS
// provider picks them up from the standard credential chain (environment
// variables, ~/.aws/credentials, an assumed role, or the EC2/CI instance
// profile), so no secrets need to live in this repository.

locals {
  common_tags = merge(
    {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "terraform"
    },
    var.tags,
  )
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = local.common_tags
  }
}

// Configures the Kubernetes provider against the EKS cluster created below,
// so Terraform can also manage the default gp3 StorageClass (see eks.tf).
// The auth token is short-lived and only used at apply time.
data "aws_eks_cluster_auth" "main" {
  name = aws_eks_cluster.main.name
}

provider "kubernetes" {
  host                   = aws_eks_cluster.main.endpoint
  cluster_ca_certificate = base64decode(aws_eks_cluster.main.certificate_authority[0].data)
  token                  = data.aws_eks_cluster_auth.main.token
}
