package org.delcom.dao

import org.delcom.tables.SukusTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID


class SukusDao(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, SukusDao>(SukusTable)

    var nama by SukusTable.nama
    var pathGambar by SukusTable.pathGambar
    var deskripsi by SukusTable.deskripsi
    var makanan by SukusTable.makanan
    var rumahadat by SukusTable.rumahadat
    var createdAt by SukusTable.createdAt
    var updatedAt by SukusTable.updatedAt
}
