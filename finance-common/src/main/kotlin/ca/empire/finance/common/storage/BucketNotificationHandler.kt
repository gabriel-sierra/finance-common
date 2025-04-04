package ca.empire.finance.common.storage

import arrow.core.Either
import ca.empire.finance.common.proto.BucketNotification
import ca.empire.messaging.EventMessage
import ca.empire.pu.PUEvent
import org.slf4j.LoggerFactory
import com.google.protobuf.util.JsonFormat
import java.io.BufferedReader
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets
import java.util.stream.Stream

abstract class BucketNotificationHandler(private val storageReader: CloudStorageReader) {
    companion object {
        private val logger = LoggerFactory.getLogger(BucketNotificationHandler::class.java)
    }
    abstract fun successHandler(bucketNotification: BucketNotification, lines: Sequence<String>): Stream<PUEvent>
    fun handleEvent(eventMessage: EventMessage): Stream<PUEvent> {
        val eventType = eventMessage.attributes["eventType"]
        return when (eventType) {
            "OBJECT_FINALIZE" -> {
                val builder = BucketNotification.newBuilder()
                JsonFormat.parser().ignoringUnknownFields().merge(eventMessage.data, builder)
                val bucketNotification = builder.build()

                logger.info("built bucketNotification with {} {}", bucketNotification.bucket, bucketNotification.name)

                when (val blob = storageReader.loadCloudStorageBlob(bucketNotification.bucket, bucketNotification.name)) {
                    is Either.Left -> throw Exception("Error streaming file named '${bucketNotification.name}' from storage bucket '${bucketNotification.bucket}' with md5hash '${bucketNotification.md5Hash}'")
                    is Either.Right -> {
                        val contentsSequence = BufferedReader(Channels.newReader(blob.value.reader(), StandardCharsets.ISO_8859_1)).lineSequence()
                        successHandler(bucketNotification, contentsSequence)
                    }
                }
            }
            else -> {
                Stream.empty()
            }
        }
    }
}