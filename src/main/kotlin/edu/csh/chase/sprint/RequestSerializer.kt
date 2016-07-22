package edu.csh.chase.sprint

import okhttp3.RequestBody

interface RequestSerializer {

    fun isValidType(requestData: Any?): Boolean

    fun serialize(requestData: Any?): RequestBody?

}