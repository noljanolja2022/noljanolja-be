package com.nolgobuljia.server.giftbiz.model

data class GiftBizBrandListResponse(
    val listNum: Long,
    val goodsList: List<GiftBizBrand> = emptyList()
)

data class GiftBizBrand(
    val brandCode: String,
    val brandName: String,
    val brandSeq: Int,
    val sort: Int = 1,
    val content: String = "",
    val brandBannerImg: String = "",
    val brandIConImg: String = "",
    val mmsThumImg: String,
    val category1Seq: Int,
    val category1Name: String = "",
    val category2Seq: Int,
    val category2Name: String = ""
)