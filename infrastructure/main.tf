provider "azurerm" {
  features {}
}

locals {
  db_connection_options = "?sslmode=require"
  app = "repl-docmosis-backend"

  previewVaultName = "${var.product}-aat"
  nonPreviewVaultName = "${var.product}-${var.env}"
  vaultName = var.env == "preview" ? local.previewVaultName : local.nonPreviewVaultName
  vaultUri = data.azurerm_key_vault.ethos_key_vault.vault_uri
  previewVaultGroupName = "${var.product}-${local.app}-aat"
  nonPreviewVaultGroupName = "${var.product}-${local.app}-${var.env}"
  vaultGroupName = var.env == "preview" ? local.previewVaultGroupName : local.nonPreviewVaultGroupName

  previewSharedVaultName = "${var.product}-shared-aat"
  nonPreviewSharedVaultName = "${var.product}-shared-${var.env}"
  sharedVaultName = var.env == "preview" ? local.previewSharedVaultName : local.nonPreviewSharedVaultName
  sharedVaultUri = data.azurerm_key_vault.ethos_shared_key_vault.vault_uri
  previewSharedRG = "${var.product}-aat"
  nonPreviewSharedRG = "${var.product}-${var.env}"
  sharedResourceGroup = var.env == "preview" ? local.previewSharedRG : local.nonPreviewSharedRG

  localEnv = var.env == "preview" ? "aat" : var.env
  s2sRG  = "rpe-service-auth-provider-${local.localEnv}"

}

data "azurerm_subnet" "postgres" {
  name                 = "core-infra-subnet-0-${var.env}"
  resource_group_name  = "core-infra-${var.env}"
  virtual_network_name = "core-infra-vnet-${var.env}"
}

module "repl-docmosis-backend" {
  source                          = "git@github.com:hmcts/cnp-module-webapp?ref=master"
  product                         = "${var.product}-${local.app}"
  location                        = var.location
  env                             = var.env
  ilbIp                           = var.ilbIp
  subscription                    = var.subscription
  capacity                        = var.capacity
  common_tags                     = var.common_tags
  is_frontend                     = false
  enable_ase                      = var.enable_ase

  app_settings                         = {
    WEBSITE_PROACTIVE_AUTOHEAL_ENABLED = var.autoheal
    TORNADO_URL                        = var.tornado_url
    TORNADO_ACCESS_KEY                 = data.azurerm_key_vault_secret.tornado_access_key.value
    ETHOS_REPL_SERVICE_S2S_SECRET      = data.azurerm_key_vault_secret.microservicekey_ethos_repl_service.value
    IDAM_API_URL                       = var.idam_api_url
    IDAM_API_JWK_URL                   = "${var.idam_api_url}/jwks"
    CCD_DATA_STORE_API_URL             = var.ccd_data_store_api_url
    DOCUMENT_MANAGEMENT_URL            = var.dm_url
    DOCUMENT_MANAGEMENT_CASEWORKERROLE = "caseworker-ethos"
    SERVICE_AUTH_PROVIDER_URL          = var.s2s_url
    MICRO_SERVICE                      = var.micro_service
    CCD_GATEWAY_BASE_URL               = var.ccd_gateway_url
    ETHOS_REPL_DB_HOST                 = module.db.host_name
    ETHOS_REPL_DB_PORT                 = "5432"
    ETHOS_REPL_DB_PASSWORD             = module.db.postgresql_password
    ETHOS_REPL_DB_USER_NAME            = module.db.user_name
    ETHOS_REPL_DB_NAME                 = module.db.postgresql_database
    ETHOS_REPL_DB_CONN_OPTIONS         = local.db_connection_options
    CREATE_UPDATES_QUEUE_SEND_CONNECTION_STRING = data.azurerm_key_vault_secret.create_updates_queue_send_conn_str.value
  }
}

resource "azurerm_key_vault_secret" "AZURE_APPINSGHTS_KEY" {
  name         = "AppInsightsInstrumentationKey"
  value        = azurerm_application_insights.appinsights.instrumentation_key
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_application_insights" "appinsights" {
  name                = "${var.product}-${var.component}-appinsights-${var.env}"
  location            = var.appinsights_location
  resource_group_name = local.vaultGroupName
  application_type    = "web"

  tags = var.common_tags

  lifecycle {
    ignore_changes = [
      # Ignore changes to appinsights as otherwise upgrading to the Azure provider 2.x
      # destroys and re-creates this appinsights instance
      application_type,
    ]
  }
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name         = "${var.component}-POSTGRES-USER"
  value        = module.db.user_name
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name         = "${var.component}-POSTGRES-PASS"
  value        = module.db.postgresql_password
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name         = "${var.component}-POSTGRES-HOST"
  value        = module.db.host_name
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name         = "${var.component}-POSTGRES-PORT"
  value        = "5432"
  key_vault_id = module.key-vault.key_vault_id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name         = "${var.component}-POSTGRES-DATABASE"
  value        = module.db.postgresql_database
  key_vault_id = module.key-vault.key_vault_id
}

data "azurerm_key_vault" "ethos_key_vault" {
  name                = local.vaultName
  resource_group_name = local.vaultGroupName
}

resource "azurerm_key_vault_secret" "ethos_repl_service_s2s_secret" {
  name         = "ethos-repl-service-s2s-secret"
  value        = data.azurerm_key_vault_secret.microservicekey_ethos_repl_service.value
  key_vault_id = data.azurerm_key_vault.ethos_key_vault.id
}

data "azurerm_key_vault_secret" "tornado_access_key" {
  name = "tornado-access-key"
  key_vault_id = data.azurerm_key_vault.ethos_key_vault.id
}

# SHARED KEY VAULT DATA
data "azurerm_key_vault" "ethos_shared_key_vault" {
  name                = local.sharedVaultName
  resource_group_name = local.sharedResourceGroup
}

data "azurerm_key_vault_secret" "create_updates_queue_send_conn_str" {
  name = "create-updates-queue-send-connection-string"
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

# S2S KEY VAULT DATA
data "azurerm_key_vault" "s2s_key_vault" {
  name                = "s2s-${local.localEnv}"
  resource_group_name = local.s2sRG
}

data "azurerm_key_vault_secret" "microservicekey_ethos_repl_service" {
  name = "microservicekey-ethos-repl-service"
  key_vault_id = data.azurerm_key_vault.s2s_key_vault.id
}
