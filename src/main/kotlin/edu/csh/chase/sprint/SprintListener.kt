package edu.csh.chase.sprint

interface SprintListener {

    fun sprintSuccess(response: Response.Success)

    fun sprintFailure(response: Response.Failure)

    fun sprintConnectionError(response: Response.ConnectionError)

    fun sprintRequestQueued(request: Request) {

    }

    fun sprintRequestCanceled(request: Request) {

    }

}