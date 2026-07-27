// Container registry that holds the ledgerx-backend image built from the
// repository's Dockerfile. Push to it before deploying the k8s/ manifests
// to this cluster; see README.md for the exact commands.

resource "google_artifact_registry_repository" "app" {
  location      = var.region
  repository_id = "${var.project_name}-backend"
  format        = "DOCKER"
  description   = "Container images for ledgerx-backend."

  depends_on = [google_project_service.required]
}
