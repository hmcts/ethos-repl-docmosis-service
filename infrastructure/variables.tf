variable "product" {
  default = "ethos"
}

variable "location" {
  default = "UK South"
}

variable "env" {
}

variable "tornado_url" {
  default = "http://tornado:8090/rs/render"
}

variable "subscription" {
}

variable "tenant_id" {}

variable "jenkins_AAD_objectId" {
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "capacity" {
  default = "1"
}

variable "deployment_namespace" {
  default = ""
}

variable "common_tags" {
  type = map(string)
}

variable "autoheal" {
  description = "Enabling Proactive Auto Heal for Webapps"
  default     = "True"
}

variable "idam_api_url" {
  default = "http://sidam-api:5000"
}

variable "ccd_data_store_api_url" {
  default = "http://ccd-data-store-api:4452"
}

variable "dm_url" {
  default = "http://dm-store:8080"
}

variable "s2s_url" {
  default = "http://service-auth-provider-api:8080"
}

variable "micro_service" {
  default = "ethos_repl_service"
}

variable "ccd_gateway_url" {
  default = "http://127.0.0.1:3453"
}

variable "component" {
  default = "repl-docmosis-backend"
}

variable "location_api" {
  default = "UK South"
}

variable "appinsights_location" {
  default     = "West Europe"
  description = "Location for Application Insights"
}

variable "enable_ase" {
  default = false
}

variable "managed_identity_rg_name" {
}

variable "managed_identity_name" {
}
