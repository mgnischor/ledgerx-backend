output "cluster_name" {
  description = "Name of the AKS cluster."
  value       = azurerm_kubernetes_cluster.main.name
}

output "resource_group_name" {
  description = "Name of the resource group holding every resource created by this configuration."
  value       = azurerm_resource_group.main.name
}

output "acr_login_server" {
  description = "Login server (registry URL) of the ACR repository the application image should be pushed to."
  value       = azurerm_container_registry.main.login_server
}

output "configure_kubectl" {
  description = "Command to update the local kubeconfig to point at this cluster."
  value       = "az aks get-credentials --resource-group ${azurerm_resource_group.main.name} --name ${azurerm_kubernetes_cluster.main.name}"
}

output "acr_login_command" {
  description = "Command to authenticate the local Docker client against the ACR repository."
  value       = "az acr login --name ${azurerm_container_registry.main.name}"
}
