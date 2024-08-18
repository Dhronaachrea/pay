package com.skilrock.paypr.network

import java.io.IOException

class NoConnectivityException : IOException() {

    override fun getLocalizedMessage(): String {
        return "~~~ NO INTERNET CONNECTIVITY ~~~"
    }

}