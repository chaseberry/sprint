package edu.csh.chase.sprint.json

class JsonException(message: String?) : RuntimeException (message) {

    var throwable: Throwable? = null

    constructor(cause: Throwable) : this(cause.getMessage()) {
        throwable = cause
    }

}


