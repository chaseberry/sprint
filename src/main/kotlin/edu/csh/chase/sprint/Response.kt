package edu.csh.chase.sprint

import com.squareup.okhttp.Headers
import com.squareup.okhttp.ResponseBody

data class Response(val statusCode: Int, val responseData: ResponseBody?, val headers: Headers?) {

}