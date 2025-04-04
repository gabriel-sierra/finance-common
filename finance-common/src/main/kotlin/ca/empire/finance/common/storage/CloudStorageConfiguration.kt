package ca.empire.finance.common.storage

import com.google.auth.oauth2.GoogleCredentials

class CloudStorageConfiguration(
    val projectId: String,
    val credentials: GoogleCredentials
) {
    companion object Builder {
        @Throws(IllegalArgumentException::class)
        fun build(projectId: String?, credentials: GoogleCredentials?): CloudStorageConfiguration {
            val blankConfiguration = mapOf<String, Any?>(
                "projectId" to (!projectId.isNullOrBlank()),
                "credentials" to (credentials != null)
            )
                .filterValues { it == false }
                .keys
                .toList()
            if (blankConfiguration.isNotEmpty()) {
                throw IllegalArgumentException("missing configuration attributes: $blankConfiguration")
            }

            return CloudStorageConfiguration(projectId!!, credentials!!)
        }
    }

    fun storageReader() = CloudStorageReader(this)
}