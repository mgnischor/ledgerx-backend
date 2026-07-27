// Pins the Terraform CLI and provider versions used to provision the Google
// Cloud infrastructure for ledgerx-backend (VPC, GKE cluster and Artifact
// Registry repository).
terraform {
  required_version = ">= 1.6.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.0"
    }
  }

  // Local state is fine for a single operator or evaluation use. For team
  // use, switch to a remote backend (a Google Cloud Storage bucket) before
  // running this against a shared environment:
  //
  // backend "gcs" {
  //   bucket = "<your-terraform-state-bucket>"
  //   prefix = "ledgerx-backend/gcp"
  // }
}
