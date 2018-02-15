package edu.csh.chase.sprint

interface SprintListener {

    fun sprintSuccess(response: Response.Success) {

    }

    fun sprintFailure(response: Response.Failure) {

    }

    fun sprintRequestQueued(request: Request) {

    }

    fun sprintRequestCanceled(request: Request) {

    }

}