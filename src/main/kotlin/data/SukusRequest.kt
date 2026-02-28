package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Sukus

@Serializable
data class SukusRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var makanan: String = "",
    var rumahadat: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "makanan" to makanan,
            "rumahadat" to rumahadat,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Sukus {
        return Sukus(
            nama = nama,
            pathGambar = pathGambar,
            deskripsi = deskripsi,
            makanan = makanan,
            rumahadat = rumahadat,
        )
    }
}
