package com.golfcart.utils.village_constant

import com.golfcart.BuildConfig

object VillageConstants {

    val TOOLBAR_TITLE = "x-toolbar-title"
    val CATEGORY_ID = "x-category-id"
    val SUB_CATEGORY_ID = "x-sub-category-id"
    val EMPTY = "__________"
    val FONT_TYPE = "DS-DIGI.TTF"
    val WEB_URL = "x-web-url"
    val SCREE_NAME = "x-screen-name"
    val SNIPPET = "x-snippet"
    val CURRENT_LATITUDE = "x-current-latitude"
    val CURRENT_LONGITUDE = "x-current-longitude"
    val DESITNATION_LATITUDE = "x-destination-latitude"
    val DESITNATION_LONGITUDE = "x-destination-longitude"
    val IS_POINT_SEARCH = "x-is-point-search"
    val IS_NOTIFICATION = "x-is-notification"
    val APP_RATE_COUNT = "x-app-rate-count"
    val TYPE = "x-type"
    val EVENT_ID = "x-event-id"
    val DETAIL_MODEL = "x-detail-model"
    val FAVOURITE_STATUS = "x-favourite-status"
    val IS_FROM_GOLF_SUBCATEGORY = "x-is-from-golf-subcategory"
    val POSITION = "x-position"
    val IS_FROM_FAVOURITE = "x-is-favourite"
    val Direction = "DirectionBy"
    val IS_FROM_POINT_TO_POINT = "x-is-from-point"
    val POINT_TYPE = "x-point-source"
    val POINT_SOURCE = "1"
    val POINT_DESTINATION = "2"
    val GOLF_CART = "1"
    val AUTO = "0"
    var IS_FROM_POINT = false
    var HOME_PROFILE_MODEL = "x-profile-model"
    var NAVIGATION_INSTRUCTION = "x-navigation-instruction"

    val TYPE_YELP = "Y"
    val TYPE_GOLF = "N"
    val TYPE_SEARCH = "G"

    val TERMS_AND_CONDITION = BuildConfig.BASE + "terms_of_use"

    val WEATHER_APP = "00b16d4dde1f13bbeceffb34b1ccdc99" //WeatherApi Key
    val WEATHER_UNITS = "imperial" //WeatherApi units
    val WEATHER_TEMP_URL = "https://api.openweathermap.org/data/2.5/weather?" //WeatherApi url
    val WEATHER_URL = "https://api.openweathermap.org/data/2.5/forecast?" //WeatherApi url
    val WEATHER_IMAGE_URL = "https://openweathermap.org/img/w/" //WeatherApi image url
    val PNG = ".png"

    val VILLAGE_LAT_START = 28.95103366514291
    val VILLAGE_LNG_START = -81.98695931807181

    val VILLAGE_LAT_END = 28.846275232301167
    val VILLAGE_LNG_END = -81.98158971976704

    val YELP_SEARCH_BASE_URL = "https://api.yelp.com/v3/businesses/search?"
    var PROPERTY_URL = "https://classifieds.talkofthevillages.com/json/"

    val GOLF_CART_HELP_WEBSITE = "http://www.24hrcartclub.com/"
    val GOLF_CART_NUMBER = "352-330-1911"

    val IS_FROM_HOME = "x-is-from-home"

    val STADIA_MAP_URL = "https://api.stadiamaps.com/geocoding/v1/autocomplete?"
    val MAPBOX_URL = "https://api.mapbox.com/geocoding/v5/mapbox.places/"
    var APP_URL = "https://play.google.com/store/apps/details?id=com.golfcart"

    val FAQ_URL = "https://villagesgps.com/faq/"
    var ABOUT_US_URL = BuildConfig.BASE + "about"
    var DISCLAIMER_URL = BuildConfig.BASE + "term_condition"

    var STADIA_MAP_BASE_URL = "https://api.stadiamaps.com/navigate/v1?"
    var STADIA_MAP_AUTOCOMPETE_BASE_URL = "https://api.stadiamaps.com/geocoding/v1/search"

    /*secret keys*/
    var STADIAMAP_KEY = "5ac39977-35f0-4cd2-9a9a-ae689ce2e52f"

    /*static parameters*/
    var API_PARAM = "api_key="

    var VILLAGE_PINCODE_1 = "34785"
    var VILLAGE_PINCODE_2 = "33585"
    var VILLAGE_PINCODE_3 = "34484"
    var VILLAGE_PINCODE_4 = "32159"
    var VILLAGE_PINCODE_5 = "34731"
    var VILLAGE_PINCODE_6 = "34491"
    var VILLAGE_PINCODE_7 = "32162"
    var VILLAGE_PINCODE_9 = "32163"
    var VILLAGE_PINCODE_10 = "34785"

    val PRODUCT_ID = "com.villagegps.12month"
}