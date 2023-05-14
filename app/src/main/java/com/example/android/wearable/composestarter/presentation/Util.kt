package com.example.android.wearable.composestarter.presentation

class Util {
    companion object {
        fun displayTimeString(milliSeconds: Long): String {
            var tempSeconds = milliSeconds
            val minute = (tempSeconds - (tempSeconds % (60 * 1000))) / (60 * 1000)
            tempSeconds -= minute * 60 * 1000
            val second = (tempSeconds - (tempSeconds % 1000)) / 1000

            var outputString = ""

            if (minute < 10){
                outputString += "0"
            }
            outputString += "$minute : "
            if (second < 10){
                outputString += "0"
            }
            outputString += "$second"

            return outputString
        }
    }
}
