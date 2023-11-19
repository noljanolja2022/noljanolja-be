package com.nolgobuljia.server.giftbiz.model

import java.time.Instant

data class GiftBizGoodsListResponse(
    val listNum: Long,
    val goodsList: List<GiftBizGood> = emptyList()
)

data class GiftBizGood(
    val rmIdBuyCntFlagCd: String,
    val discountRate: Double,
    val mdCode: String,
    /**
     * Sale end date
     */
    val endDate: Instant,
    val rmYn: String?, //bool
    val discountPrice: Int,
    val mmsGoodsImg: String?,
    val srchKeyword: String?,
    val content: String,
    /**
     * Additional content
     */
    val contentAddDesc : String? = null,
    val goodsImgB: String,
    val rmIdBuyCntFlag: BooleanFlag?, //bool
    /**
     * Product type
     */
    val goodsTypeNm: String?,
    val category2Seq: String?,
    /**
     * Exhibition gender code
     */
    val exhGenderCd: String?,
    /**
     * Exhibition age code
     */
    val exhAgeCd: String?,
//    val rmRecvNumAmount: Int,
    val goodsName: String,
    val mmsReserveFlag: BooleanFlag?, //bool
    val firstBuyFlag: BooleanFlag?, //bool
    /**
     * Product status code. Can be SALE or SUS aka discontinued
     */
    val goodsStateCd: String?,
    val brandCode: String,
    /**
     * Product number
     */
    val goodsNo: Long,
    val brandName: String,
    val mmsBarcdCreateYn: String?, //bool
    val goodsEventDesc: String?,
    /**
     * Recommend price (after discount)
     */
    val salePrice: Long,
    val rmRecvNumDay: Int?,
    val brandIconImg: String,
    /**
     * Supplier Id
     */
    val goodsComId: String,
    /**
     * Supplier Name
     */
    val goodsComName: String,
    /**
     * Exchange Id
     */
    val affiliateId: String,
    /**
     * Exchange name
     */
    val affiliate: String,

    /**
     * Description image
     */
    val goodsDescImgWeb: String?,
    val rmCntFlag: String?, //bool
    val saleDateFlagCd: String?,
    val rmRecvNumFlag: String?, //bool
    /**
     * product Id
     */
    val goodsCode: String,
    val goodsTypeDtlNm: String,
    val category1Seq: Long?,
    val categoryName1: String?,
    val goodsImgS: String,
    /**
     * Valid period type can be days, or months, etc
     */
    val validPrdTypeCd: String,
    /**
     * Validity period (date)
     */
    val validPrdDay: String?,
    /**
     * Validity period starting from purchased date
     */
    val limitDay :Int,
    val saleDateFlag: String?, //bool
    /**
     * Price after discount from supplier
     */
    val realPrice: Long
)