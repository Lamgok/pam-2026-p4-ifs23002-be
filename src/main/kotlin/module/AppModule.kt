package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.services.PlantService
import org.delcom.repositories.ISukusRepository
import org.delcom.repositories.SukusRepository
import org.delcom.services.SukusService
import org.delcom.services.ProfileService
import org.koin.dsl.module


val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }

    // Plant Service
    single {
        PlantService(get())
    }

    // Sukus Repository
    single<ISukusRepository> {
        SukusRepository()
    }

    // Sukus Service
    single {
        SukusService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }
}
