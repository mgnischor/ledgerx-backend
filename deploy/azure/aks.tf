// AKS cluster that runs ledgerx-backend and its in-cluster dependencies
// (Postgres, RabbitMQ, Grafana LGTM, per k8s/). AKS ships a default
// "managed-csi" StorageClass out of the box, so the Postgres/RabbitMQ
// PersistentVolumeClaims in k8s/ (which don't set storageClassName) bind
// without any extra configuration here.

resource "azurerm_kubernetes_cluster" "main" {
  name                = "${var.project_name}-aks"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  dns_prefix          = "${var.project_name}-${var.environment}"
  kubernetes_version  = var.kubernetes_version
  tags                = local.common_tags

  default_node_pool {
    name                 = "system"
    vm_size              = var.node_vm_size
    vnet_subnet_id       = azurerm_subnet.aks.id
    auto_scaling_enabled = true
    node_count           = var.node_desired_size
    min_count            = var.node_min_size
    max_count            = var.node_max_size
  }

  // Cluster-managed identity used both to manage Azure resources on the
  // cluster's behalf and, below, to pull images from the ACR registry.
  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin = "azure"
    network_policy = "azure"
  }
}

// Grants the cluster's kubelet identity permission to pull images from the
// ACR registry created in acr.tf, without needing a Kubernetes imagePullSecret.
resource "azurerm_role_assignment" "aks_acr_pull" {
  principal_id                     = azurerm_kubernetes_cluster.main.kubelet_identity[0].object_id
  role_definition_name             = "AcrPull"
  scope                            = azurerm_container_registry.main.id
  skip_service_principal_aad_check = true
}
