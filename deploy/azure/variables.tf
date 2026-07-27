// Input variables for the Azure deployment. See terraform.tfvars.example for
// sample values; override them with a terraform.tfvars file or -var flags.

variable "project_name" {
  description = "Short name used as a prefix for every resource created by this configuration."
  type        = string
  default     = "ledgerx"
}

variable "environment" {
  description = "Deployment environment name, used for tagging and resource naming (e.g. production, staging)."
  type        = string
  default     = "production"
}

variable "location" {
  description = "Azure region to deploy into."
  type        = string
  default     = "eastus"
}

variable "vnet_cidr" {
  description = "Address space for the virtual network that hosts the AKS cluster."
  type        = string
  default     = "10.30.0.0/16"
}

variable "subnet_cidr" {
  description = "Address prefix for the AKS node subnet."
  type        = string
  default     = "10.30.1.0/24"
}

variable "kubernetes_version" {
  description = "Kubernetes version to run on the AKS cluster. Leave null to use AKS's current default."
  type        = string
  default     = null
}

variable "node_vm_size" {
  description = "VM size used by the AKS default node pool."
  type        = string
  default     = "Standard_D2s_v5"
}

variable "node_desired_size" {
  description = "Initial number of worker nodes in the AKS default node pool."
  type        = number
  default     = 2
}

variable "node_min_size" {
  description = "Minimum number of worker nodes the AKS default node pool can autoscale down to."
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum number of worker nodes the AKS default node pool can autoscale up to."
  type        = number
  default     = 4
}

variable "tags" {
  description = "Extra tags applied to every taggable resource created by this configuration."
  type        = map(string)
  default     = {}
}
