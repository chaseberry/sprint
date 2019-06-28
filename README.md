# Sprint

A simple to use Rest Client written in, and for, Kotlin

A get request is as easy as

```Kotlin

Sprint.get(url = "http://someurl.com/api/users"){
    if(response.successful){
        //Do something
    }
}

val response = Sprint.get("https://someurl.com/api/users").get()

```

If you need more power behind making requests you can subclass SprintClient
It allows you to provide it a base url and uses endpoints, as well as serializing a body for you.

```Kotlin


class myClient() : SprintClient("https://mybase.com/api/") {

    override fun configureClient(client: OkHttpClient.Builder) {
        //Configure your http client, timeouts and such
    }

    //A default serializer for the body of the request
    override val defaultRequestSerializer: RequestSerializer = JsonRequestSerializer()

    fun myRequest(param: String) {
        get(
                endpoint = "users",
                urlParameters = urlParams(
                    "key" to param
                )
            ) {
            //Whatever you what to do once the request finishes
        }
    }

}

```

## Gradle

`compile 'edu.csh.chase.sprint:sprint:0.1.8'`

This library comes with KJson bundled in
https://github.com/chaseberry/KJson
