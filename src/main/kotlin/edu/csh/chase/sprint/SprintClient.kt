package edu.csh.chase.sprint

import com.squareup.okhttp.OkHttpClient

abstract class SprintClient {

    abstract val client: OkHttpClient

}