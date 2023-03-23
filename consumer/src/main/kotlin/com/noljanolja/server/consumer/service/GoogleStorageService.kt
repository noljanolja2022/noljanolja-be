package com.noljanolja.server.consumer.service

import com.google.cloud.storage.*
import com.noljanolja.server.consumer.exception.Error
import com.noljanolja.server.consumer.model.ResourceInfo
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
    companion object {
        private const val FILE_SIZE_LIMIT: Long = 1024 * 1024
    }

    @Value("\${gcloud.storage.bucket}")
    private val bucketName: String = "noljanolja2023.appspot.com"
    suspend fun uploadFile(
        path: String,
        contentType: String?,
        content: Flow<ByteBuffer>,
        isPublicAccessible: Boolean = false,
        limitSize: Long = FILE_SIZE_LIMIT,
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
                content.collect {
                    currentUploadSize += it.remaining()
                    withContext(Dispatchers.IO) {
                        writer.write(it)
                    }
                    if (currentUploadSize > limitSize)
                        throw com.noljanolja.server.consumer.exception.Error.FileExceedMaxSize
                }
            }
            val uploadedFile = storage.get(blobId)
            if (isPublicAccessible) {
                storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))
            }
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

    suspend fun getResource(path: String, fileName: String? = null): ResourceInfo {
        val blobId = BlobId.of(bucketName, path)
        val blob = storage.get(blobId) ?: throw Error.FileNotFound
        return ResourceInfo(
            data = blob.getContent().inputStream(),
            contentType = blob.contentType,
            fileName = fileName
        )
    }
}