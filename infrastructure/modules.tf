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
