// GKE cluster that runs ledgerx-backend and its in-cluster dependencies
// (Postgres, RabbitMQ, Grafana LGTM, per k8s/). GKE ships a default
// "standard-rwo" StorageClass out of the box, so the Postgres/RabbitMQ
// PersistentVolumeClaims in k8s/ (which don't set storageClassName) bind
// without any extra configuration here.

// Dedicated, minimally-privileged service account for the node pool below,
// instead of relying on the broad default Compute Engine service account.
resource "google_service_account" "gke_nodes" {
  account_id   = "${var.project_name}-gke-nodes"
  display_name = "ledgerx-backend GKE node service account"
}

resource "google_project_iam_member" "gke_nodes" {
  for_each = toset([
    "roles/logging.logWriter",
    "roles/monitoring.metricWriter",
    "roles/monitoring.viewer",
    "roles/artifactregistry.reader",
  ])

  project = var.project_id
  role    = each.value
  member  = "serviceAccount:${google_service_account.gke_nodes.email}"
}

resource "google_container_cluster" "main" {
  name     = "${var.project_name}-gke"
  location = var.region
  project  = var.project_id

  network    = google_compute_network.main.id
  subnetwork = google_compute_subnetwork.main.id

  // The default node pool can't be resized/configured the way this
  // configuration's dedicated node pool (below) needs, so it's removed
  // immediately after cluster creation.
  remove_default_node_pool = true
  initial_node_count       = 1

  ip_allocation_policy {
    cluster_secondary_range_name  = "${var.project_name}-pods"
    services_secondary_range_name = "${var.project_name}-services"
  }

  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  // Convenient for evaluation/teardown with `terraform destroy`; disable
  // (set to true) once this cluster holds anything that matters.
  deletion_protection = false

  resource_labels = local.common_labels

  depends_on = [google_project_service.required]
}

resource "google_container_node_pool" "primary" {
  name     = "${var.project_name}-nodes"
  cluster  = google_container_cluster.main.name
  location = var.region
  project  = var.project_id

  autoscaling {
    min_node_count = var.node_min_size
    max_node_count = var.node_max_size
  }

  initial_node_count = var.node_desired_size

  node_config {
    machine_type    = var.node_machine_type
    service_account = google_service_account.gke_nodes.email
    oauth_scopes    = ["https://www.googleapis.com/auth/cloud-platform"]
    labels          = local.common_labels

    workload_metadata_config {
      mode = "GKE_METADATA"
    }
  }
}
