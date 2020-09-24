package com.example.sunnyweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    private val placeService=ServiceCreator.create(PlaceService::class.java)
    suspend fun searchPlaces(query:String)= placeService.searchPlaces(query).await()//searchPlaces包含请求的地址，await()发出请求实现回调
    private suspend fun <T> Call<T>.await():T{//Call<T>的扩展函数，searchPlaces的返回值就是Call<PlaceResponse>
        return suspendCoroutine { continuation ->//suspendCoroutine会立即挂起当前协程
            enqueue(object :Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body=response.body()
                    if (body!=null)continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}