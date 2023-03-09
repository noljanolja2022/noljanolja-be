package com.noljanolja.server.consumer.service

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.noljanolja.server.consumer.exception.FileExceedMaxSize
import com.noljanolja.server.consumer.model.UploadInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.ByteBuffer

@Component
class GoogleStorageService(
    @Qualifier("cloudStorage") private val storage: Storage,
) {

    @Value("\${spring.storage.google.bucket.name}")
    private val bucketName: String = "noljanolja2023.appspot.com"

    suspend fun uploadFile(
        path: String,
        contentType: String?,
        data: Flow<ByteBuffer>,
        limitSize: Long = 1024 * 1024,
    ): UploadInfo {
        var currentUploadSize = 0L
        val blobId = BlobId.of(bucketName, path)
        val blobInfo = BlobInfo.newBuilder(blobId).apply {
            contentType?.let {
                setContentType(it)
            }
        }.build()
         try {
            storage.writer(blobInfo).use { writer ->
                    data.collect {
                        currentUploadSize += it.remaining()
                        withContext(Dispatchers.IO) {
                            writer.write(it)
                        }
                        if (currentUploadSize > limitSize)
                            throw FileExceedMaxSize
                    }
                }
             val uploadedFile = storage.get(blobId)
             return UploadInfo(
                path = "${uploadedFile.storage.options.host}/${uploadedFile.blobId.bucket}/${uploadedFile.blobId.name}",
                size = uploadedFile.size,
                md5 = uploadedFile.md5ToHexString
            )
        } catch (ex: Throwable) {
            storage.delete(blobId)
            throw ex
        }
    }
}