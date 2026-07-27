// Input variables for the Google Cloud deployment. See
// terraform.tfvars.example for sample values; override them with a
// terraform.tfvars file or -var flags.

variable "project_id" {
  description = "Google Cloud project id to deploy into. Must already exist and have billing enabled."
  type        = string
}

variable "project_name" {
  description = "Short name used as a prefix for every resource created by this configuration."
  type        = string
  default     = "ledgerx"
}

variable "environment" {
  description = "Deployment environment name, used for labeling and resource naming (e.g. production, staging)."
  type        = string
  default     = "production"
}

variable "region" {
  description = "Google Cloud region to deploy into."
  type        = string
  default     = "us-central1"
}

variable "subnet_cidr" {
  description = "Primary IP range for the GKE node subnet."
  type        = string
  default     = "10.40.0.0/20"
}

variable "pods_cidr" {
  description = "Secondary IP range for GKE pod IPs (VPC-native cluster)."
  type        = string
  default     = "10.41.0.0/16"
}

variable "services_cidr" {
  description = "Secondary IP range for GKE service IPs (VPC-native cluster)."
  type        = string
  default     = "10.42.0.0/20"
}

variable "node_machine_type" {
  description = "Compute Engine machine type used by the GKE node pool."
  type        = string
  default     = "e2-standard-2"
}

variable "node_desired_size" {
  description = "Initial number of worker nodes in the GKE node pool."
  type        = number
  default     = 2
}

variable "node_min_size" {
  description = "Minimum number of worker nodes the GKE node pool can autoscale down to."
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum number of worker nodes the GKE node pool can autoscale up to."
  type        = number
  default     = 4
}

variable "labels" {
  description = "Extra labels applied to every labelable resource created by this configuration."
  type        = map(string)
  default     = {}
}
