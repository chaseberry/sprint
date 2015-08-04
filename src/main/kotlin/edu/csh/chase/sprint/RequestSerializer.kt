package edu.csh.chase.sprint

import com.squareup.okhttp.RequestBody

interface RequestSerializer {

    fun isValidType(requestData: Any?): Boolean

    fun serialize(requestData: Any?): RequestBody?

}