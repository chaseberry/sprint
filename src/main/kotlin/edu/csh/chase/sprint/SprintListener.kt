package edu.csh.chase.sprint

interface SprintListener {

    fun springResponseReceived(response: Response.ServerResponse) {

    }

    fun springRequestError(response: Response.ConnectionError) {

    }

    fun sprintRequestQueued(request: Request) {

    }

    fun sprintRequestCanceled(request: Request) {

    }

}