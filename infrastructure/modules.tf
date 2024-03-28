data "azurerm_user_assigned_identity" "ethos-identity" {
  name                = var.managed_identity_name
  resource_group_name = var.managed_identity_rg_name
}
