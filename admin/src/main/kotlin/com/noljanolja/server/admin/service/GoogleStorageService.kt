package com.noljanolja.server.admin.service

import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.noljanolja.server.admin.exception.Error
import com.noljanolja.server.admin.model.FileExceedMaxSize
import com.noljanolja.server.admin.model.ResourceInfo
import com.noljanolja.server.admin.model.UploadInfo
import com.noljanolja.server.common.utils.toByteBuffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.util.*

@Component
class GoogleStorageService(
    @Qualifier("cloudStorage") private val storage: Storage,
) {
    companion object {
        internal const val FILE_SIZE_LIMIT: Long = 1024 * 1024
        internal const val STICKER_BUCKET = "stickers"
        internal const val BRAND_BUCKET = "brands"
        internal const val GIFT_BUCKET = "gifts"
    }

    @Value("\${gcloud.storage.bucket}")
    private val bucketName: String = "noljanolja2023.appspot.com"
    suspend fun uploadFile(
        path: String,
        contentType: String?,
        content: ByteBuffer,
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
                currentUploadSize += content.remaining()
                withContext(Dispatchers.IO) {
                    writer.write(content)
                }
                if (currentUploadSize > limitSize)
                    throw FileExceedMaxSize
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

    suspend fun uploadFilePart(filePart: FilePart, bucket: String): UploadInfo {
        val fileExtension = filePart.filename().split(".").last()
        val fileName = UUID.randomUUID()
        return uploadFile(
            path = "$bucket/$fileName.$fileExtension",
            contentType = "image/$fileExtension",
            content = filePart.toByteBuffer(),
            isPublicAccessible = true
        )
    }
}