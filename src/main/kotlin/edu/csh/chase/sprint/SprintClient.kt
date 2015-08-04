package edu.csh.chase.sprint

import com.squareup.okhttp.OkHttpClient

abstract class SprintClient(val urlBase: String) {

    abstract val client: OkHttpClient

}