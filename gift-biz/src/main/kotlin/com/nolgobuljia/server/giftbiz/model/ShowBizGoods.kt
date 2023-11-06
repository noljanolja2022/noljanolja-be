package com.nolgobuljia.server.giftbiz.model

data class GiftBizGoodsListResponse(
    val listNum: Long,
    val goodsList: List<GiftBizGood> = emptyList()
)

data class GiftBizGood(
    val rmIdBuyCntFlagCd: String,
    val discountRate: Double,
    val mdCode: String,
    val endDate: String,
    val rmYn: String?, //bool
    val discountPrice: Int,
    val mmsGoodsImg: String?,
    val srchKeyword: String?,
    val content: String,
    val goodsImgB: String,
    val rmIdBuyCntFlag: BooleanFlag?, //bool
    val goodsTypeNm: String?,
    val category2Seq: String?,
    val exhGenderCd: String?,
    val exhAgeCd: String?,
    val validPrdDay: String?,
    val rmRecvNumAmount: Long,
    val goodsComName: String,
    val goodsName: String,
    val mmsReserveFlag: BooleanFlag?, //bool
    val firstBuyFlag: BooleanFlag?, //bool
    val goodsStateCd: String?,
    val brandCode: String,
    val goodsNo: Int,
    val brandName: String,
    val mmsBarcdCreateYn: String?, //bool
    val goodsEventDesc: String?,
    val salePrice: Long,
    val rmRecvNumDay: Int?,
    val brandIconImg: String,
    val goodsComId: String,
    val affiliateId: String,
    val goodsDescImgWeb: String?,
    val rmCntFlag: String?, //bool
    val saleDateFlagCd: String?,
    val rmRecvNumFlag: String?, //bool
    val goodsCode: String,
    val goodsTypeDtlNm: String,
    val category1Seq: Int?,
    val goodsImgS: String,
    val affiliate: String,
    val validPrdTypeCd: String,
    val saleDateFlag: String?, //bool
    val realPrice: Long
)