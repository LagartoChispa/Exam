package com.exam.me.repository

import com.exam.me.network.ExternalApiInstance

class CountryRepository {
    suspend fun getAllCountries() = ExternalApiInstance.api.getAllCountries()
}