// Provider configuration for Google Cloud. Credentials are not defined here:
// the google provider picks them up from the standard Application Default
// Credentials chain (gcloud CLI login, a service account key file pointed to
// by GOOGLE_APPLICATION_CREDENTIALS, or workload identity), so no secrets
// need to live in this repository.

locals {
  common_labels = merge(
    {
      project     = var.project_name
      environment = var.environment
      managed-by  = "terraform"
    },
    var.labels,
  )
}

provider "google" {
  project = var.project_id
  region  = var.region
}

// APIs required by the resources below. Google Cloud projects don't have
// these enabled by default, so Terraform enables them before anything else
// is created.
resource "google_project_service" "required" {
  for_each = toset([
    "compute.googleapis.com",
    "container.googleapis.com",
    "artifactregistry.googleapis.com",
  ])

  project            = var.project_id
  service            = each.value
  disable_on_destroy = false
}
