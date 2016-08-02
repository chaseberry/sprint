package edu.csh.chase.sprint.internal

import edu.csh.chase.sprint.Request
import edu.csh.chase.sprint.RequestType
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request as OkRequest
import okhttp3.RequestBody

abstract class Processor(val request: Request, protected val client: OkHttpClient) {

}