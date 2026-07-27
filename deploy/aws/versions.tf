// Pins the Terraform CLI and provider versions used to provision the AWS
// infrastructure for ledgerx-backend (VPC, EKS cluster and ECR repository).
terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.33"
    }
    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0"
    }
  }

  // Local state is fine for a single operator or evaluation use. For team use,
  // switch to a remote backend (e.g. S3 + DynamoDB for state locking) before
  // running this against a shared environment:
  //
  // backend "s3" {
  //   bucket         = "<your-terraform-state-bucket>"
  //   key            = "ledgerx-backend/aws/terraform.tfstate"
  //   region         = "<your-region>"
  //   dynamodb_table = "<your-lock-table>"
  //   encrypt        = true
  // }
}
