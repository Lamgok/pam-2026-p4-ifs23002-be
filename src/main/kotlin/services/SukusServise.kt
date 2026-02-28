package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.SukusRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ISukusRepository
import java.io.File
import java.util.*

class SukusService(private val sukusRepository: ISukusRepository) {
    // Mengambil semua data suku
    suspend fun getAllSukus(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val sukus = sukusRepository.getSukus(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar suku",
            mapOf(Pair("sukus", sukus))
        )
        call.respond(response)
    }

    // Mengambil data suku berdasarkan id
    suspend fun getSukusById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID suku tidak boleh kosong!")

        val sukus = sukusRepository.getSukusById(id) ?: throw AppException(404, "Data suku tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data suku",
            mapOf(Pair("sukus", sukus))
        )
        call.respond(response)
    }

    // Ambil data request
    private suspend fun getSukusRequest(call: ApplicationCall): SukusRequest {
        // Buat object penampung
        val sukusReq = SukusRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                // Ambil request berupa teks
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> sukusReq.nama = part.value.trim()
                        "deskripsi" -> sukusReq.deskripsi = part.value
                        "makanan" -> sukusReq.makanan = part.value
                        "rumahadat" -> sukusReq.rumahadat = part.value
                    }
                }

                // Upload file
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/sukus/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs() // pastikan folder ada

                    part.provider().copyAndClose(file.writeChannel())
                    sukusReq.pathGambar = filePath
                }

                else -> {}
            }

            part.dispose()
        }

        return sukusReq
    }

    // Validasi request data dari pengguna
    private fun validateSukusRequest(sukusReq: SukusRequest) {
        val validatorHelper = ValidatorHelper(sukusReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("makanan", "Makanan tidak boleh kosong")
        validatorHelper.required("rumahadat", "Rumah adat tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(sukusReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar suku gagal diupload!")
        }
    }

    // Menambahkan data suku
    suspend fun createSukus(call: ApplicationCall) {
        // Ambil data request
        val sukusReq = getSukusRequest(call)

        // Validasi request
        validateSukusRequest(sukusReq)

        // periksa suku dengan nama yang sama
        val existSukus = sukusRepository.getSukusByName(sukusReq.nama)
        if (existSukus != null) {
            val tmpFile = File(sukusReq.pathGambar)
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            throw AppException(409, "Suku dengan nama ini sudah terdaftar!")
        }

        val sukusId = sukusRepository.addSukus(
            sukusReq.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data suku",
            mapOf(Pair("sukusId", sukusId))
        )
        call.respond(response)
    }

    // Mengubah data suku
    suspend fun updateSukus(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID suku tidak boleh kosong!")

        val oldSukus = sukusRepository.getSukusById(id) ?: throw AppException(404, "Data suku tidak tersedia!")

        // Ambil data request
        val sukusReq = getSukusRequest(call)

        if (sukusReq.pathGambar.isEmpty()) {
            sukusReq.pathGambar = oldSukus.pathGambar
        }

        // Validasi request
        validateSukusRequest(sukusReq)

        // periksa suku dengan nama yang sama jika nama diubah
        if (sukusReq.nama != oldSukus.nama) {
            val existSukus = sukusRepository.getSukusByName(sukusReq.nama)
            if (existSukus != null) {
                val tmpFile = File(sukusReq.pathGambar)
                if (tmpFile.exists()) {
                    tmpFile.delete()
                }
                throw AppException(409, "Suku dengan nama ini sudah terdaftar!")
            }
        }

        // Hapus gambar lama jika mengupload file baru
        if (sukusReq.pathGambar != oldSukus.pathGambar) {
            val oldFile = File(oldSukus.pathGambar)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val isUpdated = sukusRepository.updateSukus(
            id, sukusReq.toEntity()
        )
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data suku!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data suku",
            null
        )
        call.respond(response)
    }

    // Menghapus data suku
    suspend fun deleteSukus(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID suku tidak boleh kosong!")

        val oldSukus = sukusRepository.getSukusById(id) ?: throw AppException(404, "Data suku tidak tersedia!")

        val oldFile = File(oldSukus.pathGambar)

        val isDeleted = sukusRepository.removeSukus(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data suku!")
        }

        // Hapus data gambar jika data suku sudah dihapus
        if (oldFile.exists()) {
            oldFile.delete()
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data suku",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar suku
    suspend fun getSukusImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val sukus = sukusRepository.getSukusById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(sukus.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}
