package com.riteshbkadam.beatsfitapp.util

fun formatPhoneNumber(phoneNumber: String): String {

    val cleanedNumber = phoneNumber.replace(Regex("[^\\d]"), "")
    val formattedNumber:String
    formattedNumber = if(cleanedNumber.startsWith("91")){
        "+$cleanedNumber"
    }else{
        "+91$cleanedNumber"
    }
    return formattedNumber
}
