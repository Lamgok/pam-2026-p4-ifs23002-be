package org.delcom.repositories

import org.delcom.dao.SukusDao
import org.delcom.entities.Sukus
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.SukusTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class SukusRepository : ISukusRepository {
    override suspend fun getSukus(search: String): List<Sukus> = suspendTransaction {
        if (search.isBlank()) {
            SukusDao.all()
                .orderBy(SukusTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            SukusDao
                .find {
                    SukusTable.nama.lowerCase() like keyword
                }
                .orderBy(SukusTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getSukusById(id: String): Sukus? = suspendTransaction {
        SukusDao
            .find { (SukusTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getSukusByName(name: String): Sukus? = suspendTransaction {
        SukusDao
            .find { (SukusTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addSukus(sukus: Sukus): String = suspendTransaction {
        val sukusDao = SukusDao.new {
            nama = sukus.nama
            pathGambar = sukus.pathGambar
            deskripsi = sukus.deskripsi
            makanan = sukus.makanan
            rumahadat = sukus.rumahadat
            createdAt = sukus.createdAt
            updatedAt = sukus.updatedAt
        }

        sukusDao.id.value.toString()
    }

    override suspend fun updateSukus(id: String, newSukus: Sukus): Boolean = suspendTransaction {
        val sukusDao = SukusDao
            .find { SukusTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (sukusDao != null) {
            sukusDao.nama = newSukus.nama
            sukusDao.pathGambar = newSukus.pathGambar
            sukusDao.deskripsi = newSukus.deskripsi
            sukusDao.makanan = newSukus.makanan
            sukusDao.rumahadat = newSukus.rumahadat
            sukusDao.updatedAt = newSukus.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeSukus(id: String): Boolean = suspendTransaction {
        val rowsDeleted = SukusTable.deleteWhere {
            SukusTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}
