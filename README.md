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
[![Latest version of 'sprint' @ Cloudsmith](https://api-prd.cloudsmith.io/v1/badges/version/chase-s-projects/sprint/maven/sprint/latest/a=noarch;xg=edu.csh.chase.sprint/?render=true&show_latest=true)](https://cloudsmith.io/~chase-s-projects/repos/sprint/packages/detail/maven/sprint/latest/a=noarch;xg=edu.csh.chase.sprint/)

To download through Gradle include this in your repositories
```Groovy
maven { url  "https://dl.cloudsmith.io/public/chase-s-projects/sprint/maven/" }
maven { url  "https://dl.cloudsmith.io/public/chase-s-projects/kjson/maven/" }
```

`compile 'edu.csh.chase.sprint:sprint:0.1.9'`

This library comes with KJson bundled in
https://github.com/chaseberry/KJson
