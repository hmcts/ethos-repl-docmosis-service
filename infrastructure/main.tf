provider "azurerm" {
  version = "1.23.0"
}

locals {
  db_connection_options = "?sslmode=require"
  app = "repl-docmosis-backend"
  create_api = "${var.env != "preview" && var.env != "spreview"}"

  previewVaultName = "${var.product}-aat"
  nonPreviewVaultName = "${var.product}-${var.env}"
  vaultName = "${var.env == "preview" ? local.previewVaultName : local.nonPreviewVaultName}"
  vaultUri = "${data.azurerm_key_vault.ethos_key_vault.vault_uri}"
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
    TORNADO_ACCESS_KEY                 = "${data.azurerm_key_vault_secret.tornado_access_key.value}"
    ETHOS_S2S_SECRET_KEY               = "${data.azurerm_key_vault_secret.ethos-repl-service-s2s-secret.value}"
    IDAM_API_URL                       = "${var.idam_api_url}"
    CCD_DATA_STORE_API_URL             = "${var.ccd_data_store_api_url}"
    DOCUMENT_MANAGEMENT_URL            = "${var.dm_url}"
    DOCUMENT_MANAGEMENT_CASEWORKERROLE = "caseworker-ethos"
    SERVICE_AUTH_PROVIDER_URL          = "${var.s2s_url}"
    MICRO_SERVICE                      = "${var.micro_service}"
    CCD_GATEWAY_BASE_URL               = "${var.ccd_gateway_url}"
    ETHOS_REPL_DB_HOST                 = "${module.db.host_name}"
    ETHOS_REPL_DB_PORT                 = "5432"
    ETHOS_REPL_DB_PASSWORD             = "${module.db.postgresql_password}"
    ETHOS_REPL_DB_USER_NAME            = "${module.db.user_name}"
    ETHOS_REPL_DB_NAME                 = "${module.db.postgresql_database}"
    ETHOS_REPL_DB_CONN_OPTIONS         = "${local.db_connection_options}"
  }
}

module "db" {
  source             = "git@github.com:hmcts/moj-module-postgres?ref=master"
  product            = "${var.product}-postgres-db"
  location           = "${var.location_api}"
  env                = "${var.env}"
  database_name      = "ethos"
  postgresql_user    = "ethos"
  postgresql_version = "10"
  sku_name           = "GP_Gen5_2"
  sku_tier           = "GeneralPurpose"
  common_tags        = "${var.common_tags}"
  subscription       = "${var.subscription}"
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name         = "${var.component}-POSTGRES-USER"
  value        = "${module.db.user_name}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name         = "${var.component}-POSTGRES-PASS"
  value        = "${module.db.postgresql_password}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name         = "${var.component}-POSTGRES-HOST"
  value        = "${module.db.host_name}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name         = "${var.component}-POSTGRES-PORT"
  value        = "5432"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name         = "${var.component}-POSTGRES-DATABASE"
  value        = "${module.db.postgresql_database}"
  key_vault_id = "${module.key-vault.key_vault_id}"
}

data "azurerm_key_vault" "ethos_key_vault" {
  name                = "${local.vaultName}"
  resource_group_name = "${local.vaultGroupName}"
}

data "azurerm_key_vault_secret" "ethos-repl-service-s2s-secret" {
  name = "ethos-repl-service-s2s-secret"
  vault_uri = "${data.azurerm_key_vault.ethos_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "tornado_access_key" {
  name = "tornado-access-key"
  vault_uri = "${data.azurerm_key_vault.ethos_key_vault.vault_uri}"
}

module "key-vault" {
  source                  = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product                 = "${var.product}"
  env                     = "${var.env}"
  tenant_id               = "${var.tenant_id}"
  object_id               = "${var.jenkins_AAD_objectId}"
  resource_group_name     = "${local.vaultGroupName}"
  # dcd_group_ethos_v2 group object ID
  product_group_object_id = "414c132d-5160-42b3-bbff-43a2e1daafcf"
  common_tags             = "${var.common_tags}"
}
