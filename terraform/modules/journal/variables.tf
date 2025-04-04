variable "service_account_id" {
  type        = string
  description = "Service Account having pubsub permission"
}

variable "project_id" {
  type        = string
  description = "The targeted environment to deploy infrastructure"
}

variable "region" {
  type        = string
  description = "The cloud region to deploy infrastructure"
}

variable "subscription_message_retention_seconds" {
  type         = number
  description  = "Subscription retention seconds"
}

variable "pubsub_topic_message_retention_seconds" {
  type         = string
  description  = "Pubsub retention seconds"
}