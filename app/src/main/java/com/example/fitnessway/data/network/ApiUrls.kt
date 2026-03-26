package com.example.fitnessway.data.network

import com.example.fitnessway.data.mappers.toRequestParams
import com.example.fitnessway.data.model.m_26.PaginationParams

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

        fun getIntakesByDatePath(date: String) = "$NUTRIENT_INTAKES_PATH?date=$date"
        fun getIntakesByDateUrl(date: String) = BASE_URL_GO + getIntakesByDatePath(date)

        const val NUTRIENT_GOAL_SET_PATH = "nutrient/set-goal"
        const val NUTRIENT_GOAL_SET_URL = "$BASE_URL_GO$NUTRIENT_GOAL_SET_PATH"

        const val NUTRIENT_COLOR_SET_PATH = "nutrient/set-color"
        const val NUTRIENT_COLOR_SET_URL = "$BASE_URL_GO$NUTRIENT_COLOR_SET_PATH"
    }

    object AppFood {
        const val LIST_PATH = "food/app/search"
        const val LIST_URL = "$BASE_URL_KT$LIST_PATH"

        fun getPaginationPath(params: PaginationParams) = "$LIST_PATH?${params.toRequestParams()}"
        fun getPaginationUrl(params: PaginationParams) = BASE_URL_KT + getPaginationPath(params)
    }

    object PendingFood {
        const val LIST_PATH = "food/pending/my-own"
        const val LIST_URL = "$BASE_URL_KT$LIST_PATH"

        fun getPaginationPath(params: PaginationParams) = "$LIST_PATH?${params.toRequestParams()}"
        fun getPaginationUrl(params: PaginationParams) = BASE_URL_KT + getPaginationPath(params)

        const val ADD_PATH = "food/pending/add"
        const val ADD_URL = "$BASE_URL_KT$ADD_PATH"
    }

    object FoodLog {
        const val LIST_PATH = "food/log/get-logs"
        const val LIST_URL = "$BASE_URL_GO$LIST_PATH"

        fun getListByDatePath(date: String) = "$LIST_PATH?date=$date"
        fun getListByDateUrl(date: String) = BASE_URL_GO + getListByDatePath(date)

        const val ADD_PATH = "food/log/add"
        const val ADD_URL = "$BASE_URL_GO$ADD_PATH"

        const val UPDATE_PATH = "food/log/update"
        const val UPDATE_URL = "$BASE_URL_GO$UPDATE_PATH"

        const val DELETE_PATH = "food/log/delete"
        const val DELETE_URL = "$BASE_URL_GO$DELETE_PATH"
    }

    object Food {
        const val LIST_PATH = "food/user/get-foods"
        const val LIST_URL = "$BASE_URL_GO$LIST_PATH"

        const val ADD_PATH = "food/user/add"
        const val ADD_URL = "$BASE_URL_GO$ADD_PATH"

        const val UPDATE_PATH = "food/user/update"
        const val UPDATE_URL = "$BASE_URL_GO$UPDATE_PATH"

        const val DELETE_PATH = "food/user/delete"
        const val DELETE_URL = "$BASE_URL_GO$DELETE_PATH"

        const val FAVORITE_STATUS_UPDATE_PATH = "food/user/update-favorite-status"
        const val FAVORITE_STATUS_UPDATE_URL = "$BASE_URL_GO$FAVORITE_STATUS_UPDATE_PATH"

        const val SORT_GET_PATH = "food/user/preferences/get-food-sort"
        const val SORT_GET_URL = "$BASE_URL_GO$SORT_GET_PATH"

        const val SORT_UPDATE_PATH = "food/user/preferences/update-food-sort"
        const val SORT_UPDATE_URL = "$BASE_URL_GO$SORT_UPDATE_PATH"
    }
}