package com.golfcart.model.weather

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class WeatherResponse {

    @SerializedName("cod")
    @Expose
    var cod: String? = null

    @SerializedName("message")
    @Expose
    var message: Float? = null

    @SerializedName("cnt")
    @Expose
    var cnt: Int? = null

    @SerializedName("list")
    @Expose
    var list: kotlin.collections.List<WeatherList>? = null

    @SerializedName("weather")
    @Expose
    var weather: kotlin.collections.List<WeatherArray>?= null

    @SerializedName("city")
    @Expose
    var city: City? = null

    @SerializedName("main")
    @Expose
    var main: MainTemp? = null

    class MainTemp:Serializable{
        @SerializedName("temp")
        @Expose
        var temp: Float? = null
    }

    class WeatherArray:Serializable{
        @SerializedName("main")
        @Expose
        var main: String? = null
    }

    class Wind : Serializable {
        @SerializedName("speed")
        @Expose
        var speed: Float? = null

        @SerializedName("deg")
        @Expose
        var deg: Float? = null
    }

    class Weather : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("main")
        @Expose
        var main: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("icon")
        @Expose
        var icon: String? = null
    }

    class Sys : Serializable {
        @SerializedName("pod")
        @Expose
        var pod: String? = null
    }

    class Rain : Serializable {
        @SerializedName("3h")
        @Expose
        var _3h: Float? = null
    }

    class Main : Serializable {
        @SerializedName("temp")
        @Expose
        var temp: Float? = null

        @SerializedName("temp_min")
        @Expose
        var temp_min: Float? = null

        @SerializedName("temp_max")
        @Expose
        var temp_max: Float? = null

        @SerializedName("pressure")
        @Expose
        var pressure: Float? = null

        @SerializedName("sea_level")
        @Expose
        var sea_level: Float? = null

        @SerializedName("grnd_level")
        @Expose
        var grnd_level: Float? = null

        @SerializedName("humidity")
        @Expose
        var humidity: Int? = null

        @SerializedName("temp_kf")
        @Expose
        var temp_kf: Float? = null
    }

    class WeatherList : Serializable {
        @SerializedName("dt")
        @Expose
        var dt: Int? = null

        @SerializedName("main")
        @Expose
        var main: Main? = null

        @SerializedName("weather")
        @Expose
        var weather: kotlin.collections.List<Weather>? = null

        @SerializedName("clouds")
        @Expose
        var clouds: Clouds? = null

        @SerializedName("wind")
        @Expose
        var wind: Wind? = null

        @SerializedName("rain")
        @Expose
        var rain: Rain? = null

        @SerializedName("sys")
        @Expose
        var sys: Sys? = null

        @SerializedName("dt_txt")
        @Expose
        var dt_txt: String? = null

        var bitMap:Bitmap?=null
    }

    class Coord : Serializable {
        @SerializedName("lat")
        @Expose
        var lat: Float? = null

        @SerializedName("lon")
        @Expose
        var lon: Float? = null
    }

    class Clouds : Serializable {
        @SerializedName("all")
        @Expose
        var all: Int? = null
    }

    class City : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("coord")
        @Expose
        var coord: Coord? = null

        @SerializedName("country")
        @Expose
        var country: String? = null

        @SerializedName("population")
        @Expose
        var population: Int? = null
    }

}