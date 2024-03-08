# ET COS FlexiDB
module "postgres" {
  source = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"
  env    = var.env
  providers = {
    azurerm.postgres_network = azurerm.private_endpoint
  }
  name          = "ethos-postgres-v15"
  product       = var.product
  component     = var.component
  business_area = var.businessArea
  common_tags   = local.tags
  pgsql_databases = [
    {
      name : "ethos"
    }
  ]
  pgsql_version        = "15"
  admin_user_object_id = var.jenkins_AAD_objectId
  force_user_permissions_trigger = "1"
}

resource "azurerm_key_vault_secret" "ethos_postgres_user_v15" {
  name         = "ethos-postgres-user-v15"
  value        = module.postgres.username
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

resource "azurerm_key_vault_secret" "ethos_postgres_password_v15" {
  name         = "ethos-postgres-password-v15"
  value        = module.postgres.password
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

resource "azurerm_key_vault_secret" "ethos_postgres_host_v15" {
  name         = "ethos-postgres-host-v15"
  value        = module.postgres.fqdn
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}

resource "azurerm_key_vault_secret" "ethos_postgres_port_v15" {
  name         = "ethos-postgres-port-v15"
  value        = "5432"
  key_vault_id = data.azurerm_key_vault.ethos_shared_key_vault.id
}
