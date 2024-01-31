package com.golfcart.model.remote

import com.google.gson.Gson
import java.util.HashMap

class ReflectionUtil (private val gson: Gson) {

    fun convertPojoToMap(model: Any): java.util.HashMap<String, String> {
        val json = gson.toJson(model)
        return gson.fromJson<java.util.HashMap<String, String>>(json, HashMap::class.java)
    }

    fun convertPojoToJson(model: Any): String {
        return gson.toJson(model)
    }

    fun <T> getStringToPojo(mString:String,pojoClassType: Class<T>):T{
        return  gson.fromJson(mString,pojoClassType)
    }

}