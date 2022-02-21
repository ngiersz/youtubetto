package com.example.youtubetto

fun main() {
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
