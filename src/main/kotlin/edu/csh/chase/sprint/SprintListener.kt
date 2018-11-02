package edu.csh.chase.sprint

interface SprintListener {

    /**
     * Called when the response is returned with a status code in 200..299
     *
     * @param response The response from the HTTP request
     */
    fun sprintSuccess(response: Response.Success)

    /**
     * Called when the response is returned with a status code not in 200..299
     *
     * @param response The response from the HTTP request
     */
    fun sprintFailure(response: Response.Failure)

    /**
     * Called when the request failed due to an IOException
     *
     * @param response The response from the HTTP request
     */
    fun sprintConnectionError(response: Response.ConnectionError)

    /**
     * Called when the request is started
     *
     * @param request The request being processed
     */
    fun sprintRequestQueued(request: Request) {

    }

    /**
     * Called when the request is canceled
     *
     * @param request The request being canceled
     */
    fun sprintRequestCanceled(request: Request) {

    }

}