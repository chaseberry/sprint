package edu.csh.chase.sprint

interface SprintSuccess {

    fun sprintSuccess(request: Request, response: Response)

}

interface SprintFailure {

    fun sprintFailure(request: Request, response: Response)

}