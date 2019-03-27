
output "app_namespace" {
  value = "${var.deployment_namespace}"
}

//output "vaultName" {
//  value = "${module.key-vault.key_vault_name}"
//}

output "vaultName" {
  value = "${local.vaultName}"
}

//output "vaultUri" {
//  value = "${local.vaultUri}"
//}

output "tornado_url" {
  value = "${var.tornado_url}"
}

output "tornado_access_key" {
  value = "${var.tornado_access_key}"
}

output "idam_api_url" {
  value = "${var.idam_api_url}"
}

output "ccd_data_store_api_url" {
  value = "${var.ccd_data_store_api_url}"
}