package com.example.fitnessway.data.network

object ApiUrls {
    const val BASE_URL = "http://10.0.0.4:8080/api/"

    object Nutrient {
        fun getIntakes(date: String) = "${BASE_URL}nutrient/get-intakes?date=$date"
    }

    object Food {
        fun getLogs(date: String) = "${BASE_URL}food/log/get-logs?date=$date"
        fun deleteLog(userId: String, logId: String) = "${BASE_URL}food/log/delete"
    }
}