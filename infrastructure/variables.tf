variable "product" {
  type    = string
  default = "ethos"
}

variable "location" {
  type    = string
  default = "UK South"
}

variable "env" {
  type = string
}

variable "tornado_url" {
  default = "http://tornado:8090/rs/render"
}

variable "subscription" {
  type = string
}

variable "ilbIp"{}

variable "tenant_id" {}

variable "jenkins_AAD_objectId" {
  type                        = string
  description                 = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "capacity" {
  default = "1"
}

variable "deployment_namespace" {
  default = ""
}

variable "common_tags" {
  type = "map"
}

variable "autoheal" {
  description = "Enabling Proactive Auto Heal for Webapps"
  type        = string
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
  type = string
}

variable "location_api" {
  type    = string
  default = "UK South"
}

variable "managed_identity_object_id" {
  default = ""
}

variable "appinsights_location" {
  type        = string
  default     = "West Europe"
  description = "Location for Application Insights"
}

variable "enable_ase" {
  default = false
}