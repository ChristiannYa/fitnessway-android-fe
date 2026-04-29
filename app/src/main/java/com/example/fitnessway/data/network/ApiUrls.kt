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

        const val NUTRIENT_INTAKES_PATH = "nutrient/intake/date"

        fun getIntakesByDatePath(date: String) = "$NUTRIENT_INTAKES_PATH/$date"
        fun getIntakesByDateUrl(date: String) = BASE_URL_KT + getIntakesByDatePath(date)

        const val NUTRIENT_GOAL_SET_PATH = "nutrient/set-goal"
        const val NUTRIENT_GOAL_SET_URL = "$BASE_URL_GO$NUTRIENT_GOAL_SET_PATH"

        const val NUTRIENT_COLOR_SET_PATH = "nutrient/set-color"
        const val NUTRIENT_COLOR_SET_URL = "$BASE_URL_GO$NUTRIENT_COLOR_SET_PATH"
    }

    object AppFood {
        const val PATH = "edible/app"

        const val LIST_PATH = "$PATH/search"
        const val LIST_URL = "$BASE_URL_KT$LIST_PATH"
    }

    object PendingEdible {
        const val PATH = "edible/pending"

        const val LIST_PATH = "$PATH/me"
        const val LIST_URL = "$BASE_URL_KT$LIST_PATH"

        const val ADD_PATH = "$PATH/add"
        const val ADD_URL = "$BASE_URL_KT$ADD_PATH"

        const val DISMISS_REVIEW_PATH = "$PATH/dismiss-review"
        const val DISMISS_REVIEW_URL = "$BASE_URL_KT/$DISMISS_REVIEW_PATH"
    }

    object FoodLog {
        const val PATH = "edible/log"

        const val LIST_PATH = "$PATH/date"
        const val LIST_URL = "$BASE_URL_KT$LIST_PATH"

        fun getListByDatePath(date: String) = "$LIST_PATH/$date"
        fun getListByDateUrl(date: String) = BASE_URL_KT + getListByDatePath(date)

        const val LIST_LATEST_FOODS_PATH = "$PATH/latest"
        const val LIST_LATEST_FOODS_URL = "$BASE_URL_KT$LIST_LATEST_FOODS_PATH"

        const val LIST_RECENT_PATH = "$PATH/latest"
        const val LIST_RECENT_URL = "$BASE_URL_KT$LIST_RECENT_PATH"

        const val ADD_URL = "$BASE_URL_KT$PATH"

        const val UPDATE_URL = "$BASE_URL_KT/$PATH"

        const val DELETE_PATH = "food/log/delete"
        const val DELETE_URL = "$BASE_URL_GO$DELETE_PATH"
    }

    object UserEdible {
        const val PATH_KT = "edible/user"

        const val LIST_PATH_KT = "$PATH_KT/me"
        const val LIST_URL_KT = "$BASE_URL_KT$LIST_PATH_KT"

        const val UPDATE_PATH = "food/user/update"
        const val UPDATE_URL = "$BASE_URL_GO$UPDATE_PATH"

        const val DELETE_PATH = "food/user/delete"
        const val DELETE_URL = "$BASE_URL_GO$DELETE_PATH"
    }
}