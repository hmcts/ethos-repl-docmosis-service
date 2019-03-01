variable "product" {
  type    = "string"
  default = "ethos"
}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "env" {
  type = "string"
}

variable "tornado_url" {
  default = "http://tornado:8082/rs/render"
}

variable "tornado_access_key" {
  default = ""
}

variable "subscription" {
  type = "string"
}

variable "ilbIp"{}

variable "tenant_id" {}

variable "jenkins_AAD_objectId" {
  type                        = "string"
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

# thumbprint of the SSL certificate for API gateway tests
variable api_gateway_test_certificate_thumbprint {
  type = "string"
  # keeping this empty by default, so that no thumbprint will match
  default = ""
}

variable "autoheal" {
  description = "Enabling Proactive Auto Heal for Webapps"
  type        = "string"
  default     = "True"
}

variable "idam_api_url" {
  default = "http://betaDevBccidamAppLB.reform.hmcts.net:80"
}

variable "dm_url" {
  default = "http://dm-store:8080"
}

variable "s2s_url" {
  default = "http://service-auth-provider-api:8080"
}

variable "appinsights_instrumentation_key" {
  description = "Instrumentation key of the App Insights instance this webapp should use. Module will create own App Insights resource if this is not provided"
  default = ""
}