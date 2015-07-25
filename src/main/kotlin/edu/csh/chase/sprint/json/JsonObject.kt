package edu.csh.chase.sprint.json

import java.util.*

class JsonObject {

    val map = HashMap<String, Any?>()

    constructor(tokener: JsonTokener) : this {
        val c: Char
        var key: String

        if (tokener.nextClean() != '{') {
            throw tokener.syntaxError("A JSONObject text must begin with '{'")
        }
        while (true) {
            c = tokener.nextClean();
            when (c) {
                0.toChar() -> throw tokener.syntaxError("A JSONObject text must end with '}'")
                '}' -> return
                else -> {
                    tokener.back()
                    key = tokener.nextValue().toString()
                }

            }

            // The key is followed by ':'.

            c = tokener.nextClean();
            if (c != ':') {
                throw tokener.syntaxError("Expected a ':' after a key");
            }
            this.putOnce(key, tokener.nextValue());

            // Pairs are separated by ','.

            switch (tokener.nextClean()) {
                case ';':
                case ',':
                if (tokener.nextClean() == '}') {
                    return;
                }
                tokener.back();
                break;
                case '}':
                return;
                default:
                        throw tokener.syntaxError("Expected a ',' or '}'");
            }
        }
    }

}