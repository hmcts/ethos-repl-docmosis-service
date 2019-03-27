provider "azurerm" {
  version = "1.23.0"
}

locals {
  app = "repl-docmosis-backend"
  create_api = "${var.env != "preview" && var.env != "spreview"}"

  previewVaultName = "${var.product}-aat"
  nonPreviewVaultName = "${var.product}-${var.env}"
  vaultName = "${var.env == "preview" ? local.previewVaultName : local.nonPreviewVaultName}"
  //vaultUri = "${data.azurerm_key_vault.ethos_key_vault.vault_uri}"
  previewVaultGroupName = "${var.product}-${local.app}-aat"
  nonPreviewVaultGroupName = "${var.product}-${local.app}-${var.env}"
  vaultGroupName = "${var.env == "preview" ? local.previewVaultGroupName : local.nonPreviewVaultGroupName}"

}

module "repl-docmosis-backend" {
  source                          = "git@github.com:hmcts/cnp-module-webapp?ref=master"
  product                         = "${var.product}-${local.app}"
  location                        = "${var.location}"
  env                             = "${var.env}"
  ilbIp                           = "${var.ilbIp}"
  subscription                    = "${var.subscription}"
  capacity                        = "${var.capacity}"
  common_tags                     = "${var.common_tags}"
  appinsights_instrumentation_key = "${var.appinsights_instrumentation_key}"
  is_frontend                     = false

  app_settings                         = {
    WEBSITE_PROACTIVE_AUTOHEAL_ENABLED = "${var.autoheal}"
    TORNADO_URL                        = "${var.tornado_url}"
    TORNADO_ACCESS_KEY                 = "${var.tornado_access_key}"
    //IDAM_S2S_AUTH_TOTP_SECRET          = "${data.azurerm_key_vault_secret.s2s_secret.value}"
    IDAM_USER_BASE_URI                 = "${var.idam_api_url}"
    CCD_DATA_STORE_API_URL             = "${var.ccd_data_store_api_url}"
    DOCUMENT_MANAGEMENT_URL            = "${var.dm_url}"
    DOCUMENT_MANAGEMENT_CASEWORKERROLE = "caseworker-ethos"
    SERVICE_AUTH_PROVIDER_URL          = "${var.s2s_url}"
  }
}

//data "azurerm_key_vault" "ethos_key_vault" {
//  name                = "${local.vaultName}"
//  resource_group_name = "${local.vaultGroupName}"
//}

//data "azurerm_key_vault_secret" "s2s_secret" {
//  name = "ethos-repl-docmosis-s2s-secret"
//  vault_uri = "${data.azurerm_key_vault.ethos_key_vault.vault_uri}"
//}

module "key-vault" {
  source                  = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product                 = "${var.product}"
  env                     = "${var.env}"
  tenant_id               = "${var.tenant_id}"
  object_id               = "${var.jenkins_AAD_objectId}"
  resource_group_name     = "${local.vaultGroupName}"
  # dcd_cc-dev group object ID
  product_group_object_id = "38f9dea6-e861-4a50-9e73-21e64f563537"
  common_tags             = "${var.common_tags}"
}
