package com.example.fitnessway.data.network

object ApiUrls {
    const val BASE_URL = "http://10.0.0.4:8080/api/"

    object Nutrient {
        fun getIntakes(date: String) = "${BASE_URL}nutrient/get-intakes?date=$date"
    }

    object Food {
        const val GET_FOODS = "${BASE_URL}food/get-foods"
        const val ALL_LOGS = "${BASE_URL}food/log/get-logs"
        fun getLogs(date: String) = "${BASE_URL}food/log/get-logs?date=$date"
    }
}