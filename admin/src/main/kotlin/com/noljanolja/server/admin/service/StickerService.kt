package com.noljanolja.server.admin.service

import com.google.gson.Gson
import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.StickerPack
import com.noljanolja.server.common.FileUtils
import com.noljanolja.server.common.exception.DefaultBadRequestException
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class StickerService(
    private val coreApi: CoreApi,
) {

    suspend fun createStickerPack(stickPackDir: File) : StickerPack {
        val metadataFile = File(stickPackDir.absoluteFile.path + File.separator + "contents.json")
        if (!metadataFile.exists()) {
            throw DefaultBadRequestException(Error("No metadata file found"))
        }
        val metadataString = FileUtils.readFileToString(metadataFile)
        val stickerPack = try {
            Gson().fromJson(metadataString, StickerPack::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            throw DefaultBadRequestException(Error("Invalid `contents.json` file format"))
        }
        val createdStickerId = coreApi.createStickerPack(stickerPack)
        return stickerPack.apply {
            id = createdStickerId
        }
    }
}