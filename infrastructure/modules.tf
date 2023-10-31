data "azurerm_user_assigned_identity" "ethos-identity" {
  name                = var.managed_identity_name
  resource_group_name = var.managed_identity_rg_name
}

module "db" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product            = "${var.product}-postgres-db"
  location           = var.location_api
  env                = var.env
  database_name      = "ethos"
  postgresql_user    = "ethos"
  postgresql_version = "10"
  sku_name           = "GP_Gen5_2"
  sku_tier           = "GeneralPurpose"
  common_tags        = var.common_tags
  subscription       = var.subscription
}

resource "azurerm_key_vault_secret" "ecm_postgres_user" {
  name         = "ethos-postgres-user"
  value        = module.db.user_name
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

resource "azurerm_key_vault_secret" "ecm_postgres_password" {
  name         = "ethos-postgres-password"
  value        = module.db.postgresql_password
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

resource "azurerm_key_vault_secret" "ecm_postgres_host" {
  name         = "ethos-postgres-host"
  value        = module.db.host_name
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

resource "azurerm_key_vault_secret" "ecm_postgres_port" {
  name         = "ethos-postgres-port"
  value        = module.db.postgresql_listen_port
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

resource "azurerm_key_vault_secret" "ecm_postgres_database" {
  name         = "ethos-postgres-database"
  value        = module.db.postgresql_database
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}
