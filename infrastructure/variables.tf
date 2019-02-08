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
  default = "http://tornado:8080/rs/render"
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