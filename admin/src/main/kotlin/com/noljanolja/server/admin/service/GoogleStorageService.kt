package com.noljanolja.server.admin.service

import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.noljanolja.server.admin.model.FileExceedMaxSize
import com.noljanolja.server.admin.model.UploadInfo
import kotlinx.coroutines.Dispatchers
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
        private const val FILE_SIZE_LIMIT : Long = 1024 * 1024
    }
    @Value("\${gcloud.storage.bucket}")
    private val bucketName: String = "noljanolja2023.appspot.com"
    suspend fun uploadFile(
        path: String,
        contentType: String?,
        content: ByteBuffer,
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
                currentUploadSize += content.remaining()
                withContext(Dispatchers.IO) {
                    writer.write(content)
                }
                if (currentUploadSize > limitSize)
                    throw FileExceedMaxSize
            }
            val uploadedFile = storage.get(blobId)
            storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))
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