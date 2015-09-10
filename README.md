# Sprint

A simple to use Rest Client written in, and for, Kotlin

A get request is as easy as

```Kotlin

Sprint.get(url = "http://someUrl.com/api/users"){ request, response ->
    if(response.successful){
        //Do something
    }
}

```

If you need more power behind making requests you can subclass SprintClient
It allows you to provide it a base url and uses endpoints, as well as serializing a body for you.

```Kotlin

class myClient(): SprintClient("https://mybase.com/api/"){
  
    override fun configureClient(client:OkHttpClient){
        //Configure your http client, timeouts and such
    }
    
    //A default serializer for the body of the request
    override val defaultRequestSerializer: RequestSerializer = JsonRequestSerializer()
    
    fun myRequest(param:String){
      get(endpoint="users/",
        urlParams = UrlParams{ arrayOf(
            "key" to param
            )}){ request, response ->
        //Whatever you what to do once the request finishes
      }
    }
  
}

```

In the current form, this library contains a separate Json package.
