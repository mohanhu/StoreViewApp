package com.codeloop.storeviewapp.core

class Demo {

    init {
        (1..3).forEachIndexed { index,item ->
            val result = safeApiCall { item }
            println("Demo result $result")
        }
    }

    fun safeApiCall(action:()-> Int) : Int {
        return try {
            if (action() == 8){
                action()
            }
            else{
                listOf(1).get(4)
            }
        }
        catch (e:Exception){
            if (safeApiCall { 8 } % 2 == 0){
                action()
            }
            else{
                -1
            }
        }
    }
}