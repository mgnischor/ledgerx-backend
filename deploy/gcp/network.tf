// VPC and subnet for the GKE cluster. GKE VPC-native clusters need
// secondary IP ranges reserved on the subnet for pod and service IPs.

resource "google_compute_network" "main" {
  name                    = "${var.project_name}-vpc"
  auto_create_subnetworks = false

  depends_on = [google_project_service.required]
}

resource "google_compute_subnetwork" "main" {
  name          = "${var.project_name}-subnet"
  network       = google_compute_network.main.id
  region        = var.region
  ip_cidr_range = var.subnet_cidr

  secondary_ip_range {
    range_name    = "${var.project_name}-pods"
    ip_cidr_range = var.pods_cidr
  }

  secondary_ip_range {
    range_name    = "${var.project_name}-services"
    ip_cidr_range = var.services_cidr
  }
}

// Allows the GKE nodes/pods to reach the internet (image pulls, RabbitMQ/
// PostgreSQL client libraries resolving DNS, etc.) without exposing any
// externally-reachable ports, since the cluster's own firewall rules
// (managed by GKE) still govern inbound traffic.
resource "google_compute_router" "main" {
  name    = "${var.project_name}-router"
  network = google_compute_network.main.id
  region  = var.region
}

resource "google_compute_router_nat" "main" {
  name                               = "${var.project_name}-nat"
  router                             = google_compute_router.main.name
  region                             = var.region
  nat_ip_allocate_option             = "AUTO_ONLY"
  source_subnetwork_ip_ranges_to_nat = "ALL_SUBNETWORKS_ALL_IP_RANGES"
}
