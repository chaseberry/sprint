package edu.csh.chase.sprint.websockets

sealed class RetryReason {

    class Disconnect(val code: Int, val reason: String?) : RetryReason()
    class Error(val error: Exception) : RetryReason()

}