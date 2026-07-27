output "cluster_name" {
  description = "Name of the GKE cluster."
  value       = google_container_cluster.main.name
}

output "artifact_registry_repository" {
  description = "Full path of the Artifact Registry repository the application image should be pushed to."
  value       = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.app.repository_id}"
}

output "configure_kubectl" {
  description = "Command to update the local kubeconfig to point at this cluster."
  value       = "gcloud container clusters get-credentials ${google_container_cluster.main.name} --region ${var.region} --project ${var.project_id}"
}

output "artifact_registry_login_command" {
  description = "Command to authenticate the local Docker client against the Artifact Registry repository."
  value       = "gcloud auth configure-docker ${var.region}-docker.pkg.dev"
}
