package edu.csh.chase.sprint

import edu.csh.chase.sprint.parameters.Header
import java.util.*

data class Response(val statusCode: Int, val responseData: Array<Byte>, val headers: ArrayList<Header>) {

}