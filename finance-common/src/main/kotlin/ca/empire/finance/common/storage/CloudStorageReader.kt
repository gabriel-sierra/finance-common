package ca.empire.finance.common.storage

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Blob
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions

class CloudStorageReader(private val storage: Storage) {
    constructor(configuration: CloudStorageConfiguration) : this(
        configuration.projectId,
        configuration.credentials
    )

    constructor(projectId: String, credentials: GoogleCredentials) : this(
        StorageOptions.newBuilder()
            .setCredentials(credentials)
            .setProjectId(projectId)
            .build()
            .service
    )

    fun loadCloudStorageBlob(bucket: String, blobName: String): Either<Exception, Blob> {
        val blob = storage.get(bucket, blobName)
        if (blob == null || !blob.exists()) return Exception("Blob is null or does not exists").left()
        return blob.right()
    }
}