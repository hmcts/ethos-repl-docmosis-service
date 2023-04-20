
output "app_namespace" {
  value = var.deployment_namespace
}

output "vaultName" {
  value = local.vaultName
}

output "sharedVaultName" {
  value = local.sharedVaultName
}

output "sharedVaultUri" {
  value = local.sharedVaultUri
}

output "tornado_url" {
  value = var.tornado_url
}

output "idam_api_url" {
  value = var.idam_api_url
}

output "ccd_data_store_api_url" {
  value = var.ccd_data_store_api_url
}