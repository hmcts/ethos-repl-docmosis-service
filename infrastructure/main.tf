provider "azurerm" {
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false 
    }
  }
}

provider "azurerm" {
  features {}
  skip_provider_registration = true
  alias                      = "private_endpoint"
  subscription_id            = var.aks_subscription_id
}

locals {
  db_connection_options = "?sslmode=require"
  app                   = "repl-docmosis-backend"

  previewVaultName         = "${var.product}-aat"
  nonPreviewVaultName      = "${var.product}-${var.env}"
  vaultName                = var.env == "preview" ? local.previewVaultName : local.nonPreviewVaultName
  previewVaultGroupName    = "${var.product}-${local.app}-aat"
  nonPreviewVaultGroupName = "${var.product}-${local.app}-${var.env}"
  vaultGroupName           = var.env == "preview" ? local.previewVaultGroupName : local.nonPreviewVaultGroupName

  previewSharedVaultName    = "${var.product}-shared-aat"
  nonPreviewSharedVaultName = "${var.product}-shared-${var.env}"
  sharedVaultName           = var.env == "preview" ? local.previewSharedVaultName : local.nonPreviewSharedVaultName
  sharedVaultUri            = data.azurerm_key_vault.ethos_shared_key_vault.vault_uri
  previewSharedRG           = "${var.product}-aat"
  nonPreviewSharedRG        = "${var.product}-${var.env}"
  sharedResourceGroup       = var.env == "preview" ? local.previewSharedRG : local.nonPreviewSharedRG

  localEnv = var.env == "preview" ? "aat" : var.env
  s2sRG    = "rpe-service-auth-provider-${local.localEnv}"
  tagEnv = var.env == "aat" ? "staging" : var.env == "perftest" ? "testing" : var.env == "prod" ? "production" : var.env
  tags = merge(var.common_tags,
    tomap({
      "environment"  = local.tagEnv,
      "managedBy"    = var.team_name,
      "Team Contact" = var.team_contact
      "application"  = "employment-tribunals",
      "businessArea" = var.businessArea,
      "builtFrom"    = "ethos-repl-service",

    })
  )
}

data "azurerm_subnet" "postgres" {
  name                 = "core-infra-subnet-0-${var.env}"
  resource_group_name  = "core-infra-${var.env}"
  virtual_network_name = "core-infra-vnet-${var.env}"
}

# SHARED KEY VAULT DATA
data "azurerm_key_vault" "ethos_shared_key_vault" {
  name                = local.sharedVaultName
  resource_group_name = local.sharedResourceGroup
}

data "azurerm_key_vault_secret" "create_updates_queue_send_conn_str" {
  name         = "create-updates-queue-send-connection-string"
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

# S2S KEY VAULT DATA
data "azurerm_key_vault" "s2s_key_vault" {
  name                = "s2s-${local.localEnv}"
  resource_group_name = local.s2sRG
}

data "azurerm_key_vault_secret" "microservicekey_ethos_repl_service" {
  name         = "microservicekey-ethos-repl-service"
  key_vault_id = data.azurerm_key_vault.s2s_key_vault.id
}
