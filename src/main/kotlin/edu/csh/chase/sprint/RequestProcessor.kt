package edu.csh.chase.sprint

import com.squareup.okhttp.OkHttpClient

class RequestProcessor(val request: Request, val client: OkHttpClient) {

    init {
        val builder = com.squareup.okhttp.Request.Builder()
        builder.addHeader()
    }

}