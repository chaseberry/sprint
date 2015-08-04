package edu.csh.chase.sprint

interface SprintListener {

    fun sprintSuccess(request: Request, response: Response) {

    }

    fun sprintFailure(request: Request, response: Response) {

    }

}