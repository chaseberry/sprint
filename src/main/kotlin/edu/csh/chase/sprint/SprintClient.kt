package edu.csh.chase.sprint

import com.squareup.okhttp.OkHttpClient

abstract class SprintClient(val urlBase: String? = null) {

    private val client = OkHttpClient()

    init {
        configureClient(client)
    }

    abstract fun configureClient(client: OkHttpClient)

    abstract fun defaultRequestSerializer()

}