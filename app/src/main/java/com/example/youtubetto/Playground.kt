package com.example.youtubetto

import com.android.volley.Response
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.coroutines.*

fun main() {
    run_parallel_requests()

}


fun run_parallel_requests() {
    GlobalScope.launch {
        parallelRequests()
//        getApiData("1")
//        getApiData("2")
//        getApiData("3")
    }
}

suspend fun getApiData(param : String) {
//    val httpClient = HttpClient()
//
//    val htmlContent = httpClient.request<String> {
//        "https://en.wikipedia.org/wiki/Main_Page"
//        method = HttpMethod.Get
//    }
//    println(htmlContent)
}

suspend fun parallelRequests() = coroutineScope<Unit> {
    val httpClient = HttpClient()

    val response: HttpResponse = httpClient.request("https://ktor.io/") {

//    val firstRequest = async { httpClient.get<ByteArray>("https://en.wikipedia.org/wiki/Main_Page") }
//    val secondRequest = async { httpClient.get<ByteArray>("https://en.wikipedia.org/wiki/Main_Page") }
//
//    val bytes1 = firstRequest.await()    // Suspension point.
//    val bytes2 = secondRequest.await()   // Suspension point.
//
//    println(bytes1)
//    println(bytes2)
        httpClient.close()
    }
    println(response)

}

fun get_timestamp() {
    val timeZone = TimeZone.getTimeZone("UTC")
    val calendar = Calendar.getInstance(timeZone)
    calendar.add(Calendar.DAY_OF_YEAR, -7)

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN)
    println(simpleDateFormat.format(calendar.getTime()))

    simpleDateFormat.timeZone = timeZone
    var result = simpleDateFormat.format(calendar.getTime()) + "Z"
    println(result)
}

fun chunk_list() {
    //    generate array
    val length = 20
    val numberOfChunks = 7
    var arr = ArrayList<Int>()

    for (i in 0 until length) {
        arr.add(i)
    }
    println(arr.joinToString(", "))

//    divide to 7 parts
    val standard_chunk_length = length / numberOfChunks

    var arr2 = arr.chunked(standard_chunk_length).toMutableList()
//    println(arr2.joinToString(", "))

    println(arr2.last())
    if(arr2.size > numberOfChunks) {
        println("arr2.size > numberOfChunks")
        println(arr2.size)
//        arr2.add(numberOfChunks, arr2[numberOfChunks] + arr2[numberOfChunks+1])
        arr2[numberOfChunks-1] = arr2[numberOfChunks-1] + arr2[numberOfChunks]
        arr2.remove(arr2[numberOfChunks])
    }
    println(arr2.joinToString(", "))
}