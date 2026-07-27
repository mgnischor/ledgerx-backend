// Input variables for the AWS deployment. See terraform.tfvars.example for
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

variable "aws_region" {
  description = "AWS region to deploy into."
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC that hosts the EKS cluster."
  type        = string
  default     = "10.20.0.0/16"
}

variable "availability_zone_count" {
  description = "Number of availability zones to spread public/private subnets across."
  type        = number
  default     = 2
}

variable "kubernetes_version" {
  description = "Kubernetes minor version to run on the EKS control plane."
  type        = string
  default     = "1.31"
}

variable "node_instance_type" {
  description = "EC2 instance type used by the EKS managed node group."
  type        = string
  default     = "t3.medium"
}

variable "node_desired_size" {
  description = "Desired number of worker nodes in the EKS managed node group."
  type        = number
  default     = 2
}

variable "node_min_size" {
  description = "Minimum number of worker nodes in the EKS managed node group."
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum number of worker nodes in the EKS managed node group."
  type        = number
  default     = 4
}

variable "tags" {
  description = "Extra tags applied to every taggable resource created by this configuration."
  type        = map(string)
  default     = {}
}
