package com.noljanolja.server.admin.model

data class CreateBrandRequest(
    val name: String,
    var image: String = "",
)

data class UpdateBrandRequest(
    val name: String?,
    var image: String?,
)