package edu.csh.chase.sprint

interface SprintListener {

    fun springResponseReceived(response: Response.Success) {

    }

    fun springRequestError(response: Response.Error) {

    }

    fun sprintRequestQueued(request: Request) {

    }

    fun sprintRequestCanceled(request: Request) {

    }

}