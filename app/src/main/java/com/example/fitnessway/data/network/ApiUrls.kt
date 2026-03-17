package com.example.fitnessway.data.network

object ApiUrls {
    const val BASE_URL_GO = "http://10.0.0.4:5050/api/go/"
    const val BASE_URL_KT = "http://10.0.0.4:1144/api/kt/"

    object Auth {
        const val REGISTER_PATH = "auth/register"
        const val REGISTER_URL = "$BASE_URL_KT$REGISTER_PATH"

        const val LOGIN_PATH = "auth/login"
        const val LOGIN_URL = "$BASE_URL_KT$LOGIN_PATH"

        const val LOGOUT_PATH = "auth/logout"
        const val LOGOUT_URL = "$BASE_URL_KT$LOGOUT_PATH"

        const val REFRESH_PATH = "auth/refresh"
        const val REFRESH_URL = "$BASE_URL_KT$REFRESH_PATH"
    }

    object User {
        const val USER_PATH = "user"
        const val USER_URL = "$BASE_URL_KT$USER_PATH"
    }

    object Nutrient {
        const val NUTRIENT_LIST_PATH = "nutrient/get-nutrients"
        const val NUTRIENT_LIST_URL = "$BASE_URL_GO$NUTRIENT_LIST_PATH"

        const val NUTRIENT_INTAKES_PATH = "nutrient/get-intakes"
        const val NUTRIENT_INTAKES_URL = "$BASE_URL_GO/$NUTRIENT_INTAKES_PATH"

        const val NUTRIENT_GOAL_SET_PATH = "nutrient/set-goal"
        const val NUTRIENT_GOAL_SET_URL = "$BASE_URL_GO$NUTRIENT_GOAL_SET_PATH"

        const val NUTRIENT_COLOR_SET_PATH = "nutrient/set-color"
        const val NUTRIENT_COLOR_SET_URL = "$BASE_URL_GO$NUTRIENT_COLOR_SET_PATH"

        fun getIntakesByDatePath(date: String) = "$NUTRIENT_INTAKES_PATH?date=$date"
        fun getIntakesByDateUrl(date: String) = BASE_URL_GO + getIntakesByDatePath(date)
    }

    object Food {
        const val FOOD_USER_LIST_PATH = "food/user/get-foods"
        const val FOOD_USER_LIST_URL = "$BASE_URL_GO$FOOD_USER_LIST_PATH"

        const val FOOD_USER_ADD_PATH = "food/user/add"
        const val FOOD_USER_ADD_URL = "$BASE_URL_GO$FOOD_USER_ADD_PATH"

        const val FOOD_USER_UPDATE_PATH = "food/user/update"
        const val FOOD_USER_UPDATE_URL = "$BASE_URL_GO$FOOD_USER_UPDATE_PATH"

        const val FOOD_USER_DELETE_PATH = "food/user/delete"
        const val FOOD_USER_DELETE_URL = "$BASE_URL_GO$FOOD_USER_DELETE_PATH"

        const val FOOD_USER_FAVORITE_STATUS_UPDATE_PATH = "food/user/update-favorite-status"
        const val FOOD_USER_FAVORITE_STATUS_UPDATE_URL = "$BASE_URL_GO$FOOD_USER_FAVORITE_STATUS_UPDATE_PATH"

        const val FOOD_USER_SORT_GET_PATH = "food/user/preferences/get-food-sort"
        const val FOOD_USER_SORT_GET_URL = "$BASE_URL_GO$FOOD_USER_SORT_GET_PATH"

        const val FOOD_USER_SORT_UPDATE_PATH = "food/user/preferences/update-food-sort"
        const val FOOD_USER_SORT_UPDATE_URL = "$BASE_URL_GO$FOOD_USER_SORT_UPDATE_PATH"

        const val FOOD_LOG_LIST_PATH = "food/log/get-logs"
        const val FOOD_LOG_LIST_URL = "$BASE_URL_GO$FOOD_LOG_LIST_PATH"

        const val FOOD_LOG_ADD_PATH = "food/log/add"
        const val FOOD_LOG_ADD_URL = "$BASE_URL_GO$FOOD_LOG_ADD_PATH"

        const val FOOD_LOG_UPDATE_PATH = "food/log/update"
        const val FOOD_LOG_UPDATE_URL = "$BASE_URL_GO$FOOD_LOG_UPDATE_PATH"

        const val FOOD_LOG_DELETE_PATH = "food/log/delete"
        const val FOOD_LOG_DELETE_URL = "$BASE_URL_GO$FOOD_LOG_DELETE_PATH"

        fun getLogsByDatePath(date: String) = "$FOOD_LOG_LIST_PATH?date=$date"
        fun getLogsByDateUrl(date: String) = BASE_URL_GO + getLogsByDatePath(date)
    }
}