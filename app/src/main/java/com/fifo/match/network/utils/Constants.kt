package com.fifo.match.network.utils

object Constants {

    const val APP_NAME = "FIFO Match"

    /**
     * For octal live url
     * */
   // const val BASE_URL = "https://74.octaldevs.com/fifomatch/public/api/"

    /**
     * For local url
     * */
    //  const val BASE_URL="http://192.168.1.116/fifomatch/public/api/"

    /**
     * For Client live url
     * */
//    const val BASE_URL="https://fifomatch.com/admin/public/api/"
    const val BASE_URL="https://fifomatch.com/admin/public/api/"


    const val LOGIN_OTP_API = "send_login_otp"
    const val LOGIN_API = "apiLogin"
    const val SEND_OTP = "send_otp"
    const val VERIFY_OTP = "verify_otp"
    const val PROFILE_LIST_DATA = "profile_list_data"
    const val COMPLETE_PROFILE_FIRST = "complete_profile"
    const val COMPLETE_PROFILE_SECOND = "upload_photos"
    const val COMPLETE_SURVERY_FIRST = "complete_questionnaire"
    const val PLAN_DETAILS = "plan_details"
    const val LIVE_UPDATE = "update_live_time"
    const val SAVE_TRANSACTION = "save_in_app_transaction"
    const val SURVEY_SETUP = "surrey_data"
    const val MATCH_LIST = "match_listing"
    const val USER_DETAILS = "user_details?"
    const val USER_SAVE_Like = "user_saved"
    const val SAVE_LIST = "saved_users?"
    const val REMOVE_USER = "remove_user?"
    const val CHECK_ACTIVE_PLAN = "active_plan?"
    const val MY_PROFILE = "profile"
    const val ONLINE_OFFLINE = "update_login_status"
    const val NOTIFICATION_STATUS = "notification_status"
    const val NOTIFICATION_LIST = "my_notifications"
    const val USER_LIKE = "user_like"
    const val USER_DISLIKE = "user_like_delete"
    const val LIKE_LIST = "user_like_list"
    const val LIKE_DETAILS = "user_like_details"
    const val APPEARENCE = "user_appearance"
    const val CREATE_CHAT = "create_chat"
    const val CLEAR_NOTIFICATION = "clear_notification"
    const val SEND_CHAT_NOTIFICATION = "send_notification"
    const val USER_VERIFICATION = "user_verification"
    const val SIGN_OUT = "sing_out"
    const val DEACTIVATE_ACCOUNT = "play_pause_account"
    const val DELETE_PHOTO = "delete_photo"
    const val DELETE_CHAT = "deleteChat"

    // Sending Intent
    val Mobile_LOGIN = "mobile"
    val countryCode = "countryCode"
    const val SIGN_FLAG = "signup_flag"
    val SIGN_NAME = "name"
    val SIGN_GENDER = "gender"
    val SIGN_INTEREST = "interest"
    val COUNTRY_NAME = "countryName"
    val USER_NAME = "userName"
    val OTP_ = "otp"
    val profileInt = "1"
    const val X_ACCESS_TOKEN = "Authorization"
    const val CHECK_FLAG = "flg"
    const val PROFILE_FLAG = "profile_flag"
    const val LIKE_FLAG = "like"
    const val PROFILE = "profile"
    const val IMAGE_FLAG = "image_flag"
    const val WEB_FLAG = "webview"

    // const  val weburl = "https://74.octaldevs.com/fifomatch/public/content?type="
    const  val weburl =  "http://fifomatch.com/admin/public/api/pages?slug="

    const val PRIVACY_POLICY = weburl + "privacy_policy"
    const val ABOUT_US = weburl + "about"
    const val TERMS_SERVICE = weburl + "terms_service"
    const val FAQ = weburl + "faq"
    const val CONTACT_US = weburl + "contact_us"

    const val checkLogin = "check_login"
    const val PROFILE_VERIFY = "verify"
    const val PHOTOS = "photos"

}