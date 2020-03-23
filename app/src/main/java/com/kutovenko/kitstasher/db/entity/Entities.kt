package com.kutovenko.kitstasher.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currencies(
        @PrimaryKey
        val _id: Int,
        val currency: String
)

@Entity
data class Myshops(
        @PrimaryKey
        val _id: Int,
        val shop_name: String,
        val shop_desc: String,
        val shop_url: String,
        val shop_contact: String,
        val shop_rating: Int
)

@Entity
data class Kits(
        @PrimaryKey
        var _id: Int,
        var barcode: String,
        var brand: String,
        var brand_catno: String,
        var scale: Int,
        var kit_name: String,
        var description: String,
        var original_name: String,
        var category: String,
        var collection: String,
        var send_status: String,
        var id_online: String,
        var boxart_uri: String,
        var boxart_url: String,
        var is_deleted: Int,
        var date: String,
        var year: String,
        var scalemates_url: String,
        var purchase_date: String,
        var price: Int,
        var quantity: Int,
        var notes: String,
        var currency: String,
        var purchase_place: String,
        var status: Int,
        var media: String,
        var item_type: String
)

@Entity
data class Brands(
        @PrimaryKey
        val  _id: Int,
        var brand: String
)