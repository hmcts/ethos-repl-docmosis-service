
output "app_namespace" {
  value = "${var.deployment_namespace}"
}

# this variable will be accessible to tests as API_GATEWAY_URL environment variable
output "api_gateway_url" {
  value = "https://core-api-mgmt-${var.env}.azure-api.net/${local.api_base_path}"
}

output "vaultName" {
  value = "${module.key-vault.key_vault_name}"
}

output "tornado_url" {
  value = "${var.tornado_url}"
}

output "tornado_access_key" {
  value = "${var.tornado_access_key}"
}

output "idam_api_url" {
  value = "${var.idam_api_url}"
}

//output "ccd_data_store_api_url" {
//  value = "${var.ccd_data_store_api_url}"
//}