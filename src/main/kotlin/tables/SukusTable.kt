package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SukusTable : UUIDTable("sukus") {
    val nama = varchar("nama", 100)
    val pathGambar = varchar("path_gambar", 255)
    val deskripsi = text("deskripsi")
    val makanan = text("makanan")
    val rumahadat = text("rumahadat")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}