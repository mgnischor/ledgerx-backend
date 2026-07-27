// Container registry that holds the ledgerx-backend image built from the
// repository's Dockerfile. Push to it before deploying the k8s/ manifests
// to this cluster; see README.md for the exact commands.

resource "azurerm_container_registry" "main" {
  name                = "${var.project_name}${var.environment}acr"
  resource_group_name = azurerm_resource_group.main.name
  location            = azurerm_resource_group.main.location
  sku                 = "Standard"
  // Authentication is via the AKS cluster's managed identity (see the
  // AcrPull role assignment in aks.tf), so the registry's built-in admin
  // account stays disabled.
  admin_enabled = false
  tags          = local.common_tags
}
