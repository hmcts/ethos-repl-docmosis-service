data "azurerm_user_assigned_identity" "ethos-identity" {
  name                = "${var.managed_identity_name}"
  resource_group_name = "${var.managed_identity_rg_name}"
}

module "key-vault" {
  source                  = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product                 = var.product
  env                     = var.env
  tenant_id               = var.tenant_id
  object_id               = var.jenkins_AAD_objectId
  resource_group_name     = local.vaultGroupName
  # dcd_group_ethos_v2 group object ID
  product_group_object_id = "414c132d-5160-42b3-bbff-43a2e1daafcf"
  common_tags             = var.common_tags
  managed_identity_object_ids = ["${data.azurerm_user_assigned_identity.ethos-identity.principal_id}"]
  create_managed_identity = false
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
