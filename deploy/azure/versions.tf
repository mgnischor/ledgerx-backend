// Pins the Terraform CLI and provider versions used to provision the Azure
// infrastructure for ledgerx-backend (resource group, VNet, AKS cluster and
// ACR registry).
terraform {
  required_version = ">= 1.6.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
  }

  // Local state is fine for a single operator or evaluation use. For team
  // use, switch to a remote backend (e.g. an Azure Storage Account) before
  // running this against a shared environment:
  //
  // backend "azurerm" {
  //   resource_group_name  = "<your-state-resource-group>"
  //   storage_account_name = "<your-state-storage-account>"
  //   container_name       = "tfstate"
  //   key                  = "ledgerx-backend/azure/terraform.tfstate"
  // }
}
