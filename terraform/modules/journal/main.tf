
locals {
  journal_avro_schema = file("${path.module}/schemas/journal_avro_schema.json")
  journal_sql_schema = file("${path.module}/schemas/journal_sql_schema.json")
  journal_partition_expiration_ms = "34214400000"
}

data "google_service_account" "pubsub_service_account" {
  project    = var.project_id
  account_id = var.service_account_id
}

data "google_project" "current_google_project" {
  project_id = var.project_id
}

resource "google_pubsub_topic_iam_binding" "journal_writer_events_pubsub_binding" {
  project     = var.project_id
  topic       = google_pubsub_topic.mainfundcalculator_journal_topic.id
  role        = "roles/pubsub.publisher"
  members     = ["serviceAccount:${data.google_service_account.pubsub_service_account.email}"]
  depends_on  = [google_pubsub_topic.mainfundcalculator_journal_topic]
}


resource "google_project_iam_member" "pubsub_can_edit_bigquery" {
  project = var.project_id
  role    = "roles/bigquery.dataEditor"
  member  = "serviceAccount:service-${data.google_project.current_google_project.number}@gcp-sa-pubsub.iam.gserviceaccount.com"
}

resource "google_project_iam_member" "pubsub_can_view_bigquery_metadata" {
  project = var.project_id
  role    = "roles/bigquery.metadataViewer"
  member  = "serviceAccount:service-${data.google_project.current_google_project.number}@gcp-sa-pubsub.iam.gserviceaccount.com"
}

resource "google_bigquery_dataset" "journal_dataset" {
  project = var.project_id
  delete_contents_on_destroy = false
  dataset_id  = "journal_dataset"
  description = "Global journal dataset"
  location    = var.region
}

resource "google_bigquery_table" "mainfundcalculator_journal_table" {
  project = var.project_id
  deletion_protection = false
  table_id   = "mainfundcalculator_journal_table"
  dataset_id = google_bigquery_dataset.journal_dataset.dataset_id
  labels     = {}
  schema     = local.journal_sql_schema

  time_partitioning {
    type          = "DAY"
    expiration_ms = local.journal_partition_expiration_ms
  }
}

resource "google_pubsub_schema" "journal_avro_schema" {
  project    = var.project_id
  name       = "journal-avro-schema"
  type       = "AVRO"
  definition = local.journal_avro_schema
}

resource "google_pubsub_topic" "mainfundcalculator_journal_topic" {
  project = var.project_id
  name    = "mainfundcalculator-journal-topic"

  schema_settings {
    schema   = "projects/${var.project_id}/schemas/${google_pubsub_schema.journal_avro_schema.name}"
    encoding = "JSON"
  }
  depends_on = [google_pubsub_schema.journal_avro_schema]
}

resource "google_pubsub_subscription" "mainfundcalculator_journal_topic_subscription" {
  project = var.project_id
  name    = "mainfundcalculator-journal-topic-subscription"
  topic   = google_pubsub_topic.mainfundcalculator_journal_topic.name

  bigquery_config {
    table            = "${var.project_id}.${google_bigquery_table.mainfundcalculator_journal_table.dataset_id}.${google_bigquery_table.mainfundcalculator_journal_table.table_id}"
    use_topic_schema = true
  }

  depends_on = [
    google_project_iam_member.pubsub_can_edit_bigquery,
    google_project_iam_member.pubsub_can_view_bigquery_metadata
  ]
}