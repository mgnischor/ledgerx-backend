// Provider configuration for Azure. Credentials are not defined here: the
// azurerm provider picks them up from the standard Azure credential chain
// (Azure CLI login, environment variables, or a managed identity), so no
// secrets need to live in this repository.

locals {
  common_tags = merge(
    {
      project     = var.project_name
      environment = var.environment
      managed-by  = "terraform"
    },
    var.tags,
  )
}

provider "azurerm" {
  features {}
}
