package com.noljanolja.server.core.repo.shop

import com.noljanolja.server.core.model.Product
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("shop_products")
data class ProductModel(
    @Id
    @Column("id")
    val code: String = "",

    @Column("name")
    val name: String = "",

    @Column("img")
    val img: String = "",

    @Column("brand")
    val brand: String = "",

    @Column("brand_img")
    val brandImg: String = "",

    @Column("brand_code")
    val brandCode: String = "",

    @Column("description")
    val description: String = "",

    @Column("real_money_price")
    val realMoneyPrice: Long = 0,

    @Column("price")
    val price: Long = 0,

    @Column("is_active")
    val isActive: Boolean = false,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) : Persistable<String> {
    @Transient
    var isNewRecord: Boolean = false

    override fun getId() = code

    override fun isNew() = isNewRecord

    companion object {
        fun fromProduct(p: Product, isNew: Boolean = false): ProductModel {
            return ProductModel(
                code = p.code,
                name = p.name,
                img = p.img,
                brand = p.brand,
                brandCode = p.brandCode,
                brandImg = p.brandImg,
                description = p.description,
                realMoneyPrice = p.realMoneyPrice,
                isActive = p.isActive,
                price = p.price,

            ).apply {
                isNewRecord = isNew
            }
        }
    }

    fun toProduct(): Product {
        return Product(
            code = code,
            name = this.name,
            img = this.img,
            brand = this.brand,
            brandCode = this.brandCode,
            brandImg = this.brandImg,
            description = this.description,
            realMoneyPrice = this.realMoneyPrice,
            isActive = this.isActive,
            price = this.price
        )
    }
}
