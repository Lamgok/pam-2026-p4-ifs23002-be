package org.delcom.repositories

import org.delcom.entities.Sukus

interface ISukusRepository {
    suspend fun getSukus(search: String): List<Sukus>
    suspend fun getSukusById(id: String): Sukus?
    suspend fun getSukusByName(name: String): Sukus?
    suspend fun addSukus(sukus: Sukus): String
    suspend fun updateSukus(id: String, newSukus: Sukus): Boolean
    suspend fun removeSukus(id: String): Boolean
}
