package edu.csh.chase.sprint

import com.squareup.okhttp.OkHttpClient
import edu.csh.chase.sprint.parameters.Header
import edu.csh.chase.sprint.parameters.UrlBody
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

public object Sprint {

    private val client: OkHttpClient by Delegates.lazy {
        val client = OkHttpClient()
        client.setConnectTimeout(30, TimeUnit.SECONDS)
        client.setWriteTimeout(30, TimeUnit.SECONDS)
        client.setReadTimeout(30, TimeUnit.SECONDS)
        client
    }


    public fun get(url: String, urlParameters: UrlBody, headers: ArrayList<Header>?,
                   requestFinished: ((Request, Response) -> Unit)?) {

    }

    public fun get(request: Request): RequestProcessor {

    }

}