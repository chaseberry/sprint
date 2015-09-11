package edu.csh.chase.sprint.json

import java.io.*

/**
 * A JsonTokener takes a source string and extracts characters and tokens from
 * it. It is used by the JSONObject and JSONArray constructors to parse
 * JSON source strings.
 * @author JSON.org
 * @version 2014-05-03
 */
//TODO see if there is a better way of parsing
class JsonTokener(private var reader: Reader) {

    private var character = 1L
    private var eof = false
    private var usePrevious = false
    private var index = 0L
    private var line = 1L
    private var previous = '0'

    /**
     * Construct a JsonTokener from a string.
     *
     * @param stream     A source string.
     */
    constructor(stream: InputStream) : this(InputStreamReader(stream))

    /**
     * Construct a JsonTokener from an InputStream.
     * @param inputStream The source.
     */
    constructor(str: String) : this(StringReader(str))

    /**
     * Construct a JsonTokener from a Reader.
     *
     * @param reader     A reader.
     */
    init {
        reader = if (reader.markSupported()) reader else BufferedReader(reader)
    }

    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the next number or identifier.
     */
    fun back() {
        if (this.usePrevious || this.index <= 0) {
            throw JsonException("Stepping back two steps is not supported")
        }
        this.index--
        this.character--
        this.usePrevious = true
        this.eof = false
    }


    /**
     * Get the hex value of a character (base16).
     * @param char A character between '0' and '9' or between 'A' and 'F' or
     * between 'a' and 'f'.
     * @return  An int between 0 and 15, or -1 if c was not a hex digit.
     */
    //TODO Remove this from this scope? Or just remove? I don't see where it's used
    fun dehexchar(char: Char): Char {
        if (char >= '0' && char <= '9') {
            return (char - '0').toChar()
        }
        if (char >= 'A' && char <= 'F') {
            return (char - ('A' - 10.toChar())).toChar()
        }
        if (char >= 'a' && char <= 'f') {
            return (char - ('a' - 10.toChar())).toChar()
        }
        return (-1).toChar()
    }

    fun stringToValue(string: String): Any? {
        when (string.toLowerCase()) {
            "true" -> return true
            "false" -> return false
            "null" -> return null
        }
        if ((string[0] >= '0' && string[0] <= '9') || string[0] == '-') {
            try {
                //Is it a Double?
                if (string.contains('.') || string.contains('e') || string.contains('E')) {
                    val double = string.toDouble()
                    if (!double.isNaN() && !double.isInfinite()) {
                        return double
                    }
                    //Long or Int
                } else {
                    val long = string.toLong()
                    if (long.compareTo(long.toInt()) == 0) {
                        return long.toInt()
                    }
                    return long
                }
            } catch(exception: ClassCastException) {

            }
        }
        return string
    }

    /**
     *
     */
    fun end(): Boolean {
        return this.eof && !this.usePrevious
    }


    /**
     * Determine if the source string still contains characters that next()
     * can consume.
     * @return true if not yet at the end of the source.
     */
    fun hasMore(): Boolean {
        this.next()
        if (this.end()) {
            return false
        }
        this.back()
        return true
    }


    /**
     * Get the next character in the source string.
     *
     * @return The next character, or 0 if past the end of the source string.
     */
    fun next(): Char {
        var c: Char
        if (this.usePrevious) {
            this.usePrevious = false
            c = this.previous
        } else {
            try {
                val charIntVal = this.reader.read()
                if (charIntVal <= 0) {
                    // End of stream
                    this.eof = true
                    return 0.toChar()
                } else {
                    c = charIntVal.toChar()
                }
            } catch (exception: IOException) {
                throw JsonException(exception)
            }
        }
        this.index += 1
        if (this.previous == '\r') {
            this.line++
            this.character = if (c == '\n') 0 else 1
        } else if (c == '\n') {
            this.line++
            this.character = 0
        } else {
            this.character++
        }
        this.previous = c
        return this.previous
    }


    /**
     * Consume the next character, and check that it matches a specified
     * character.
     * @param char The character to match.
     * @return The character.
     * @throws JsonException if the character does not match.
     */
    fun next(char: Char): Char {
        val n = this.next()
        if (n != char) {
            throw this.syntaxError("Expected '$char' and instead saw '$n'")
        }
        return n
    }


    /**
     * Get the next n characters.
     *
     * @param n     The number of characters to take.
     * @return      A string of n characters.
     * @throws JSONException
     *   Substring bounds error if there are not
     *   n characters remaining in the source string.
     */
    fun next(n: Int): String {
        if (n == 0) {
            return ""
        }

        val chars = Array(n) { 0.toChar() }
        var pos = 0

        while (pos < n) {
            chars[pos] = this.next()
            if (this.end()) {
                throw this.syntaxError("Substring bounds error")
            }
            pos += 1
        }
        return String(chars.toCharArray())
    }


    /**
     * Get the next char in the string, skipping whitespace.
     * @throws JsonException
     * @return  A character, or 0 if there are no more characters.
     */
    fun nextClean(): Char {
        while (true) {
            val c = this.next()
            if (c == 0.toChar() || c > ' ') {
                return c
            }
        }
    }


    /**
     * Return the characters up to the next close quote character.
     * Backslash processing is done. The formal JSON format does not
     * allow strings in single quotes, but an implementation is allowed to
     * accept them.
     * @param quote The quoting character, either
     *      <code>"</code>&nbsp<small>(double quote)</small> or
     *      <code>'</code>&nbsp<small>(single quote)</small>.
     * @return      A String.
     * @throws JsonException Unterminated string.
     */
    fun nextString(quote: Char): String {
        var c: Char;
        val sb = StringBuilder()
        while (true) {
            c = this.next()
            when (c) {
                0.toChar() -> throw this.syntaxError("Unterminated string")
                '\n' -> throw this.syntaxError("Unterminated string")
                '\r' -> throw this.syntaxError("Unterminated string")
                '\\' -> {
                    c = this.next()
                    when (c) {
                        'b' -> sb.append('\b')
                        't' -> sb.append('\t')
                        'n' -> sb.append('\n')
                        'f' -> sb.append('\u000C')
                        'r' -> sb.append('\r')
                        'u' -> sb.append(Integer.parseInt(next(4), 16).toChar())
                        '"', '\'', '\\', '/' -> sb.append(c)
                        else -> throw this.syntaxError("Illegal escape.")
                    }
                }
                else -> {
                    if (c == quote) {
                        return sb.toString()
                    }
                    sb.append(c)
                }
            }
        }
    }


    /**
     * Get the text up but not including the specified character or the
     * end of line, whichever comes first.
     * @param  delimiter A delimiter character.
     * @return   A string.
     */
    fun nextTo(delimiter: Char): String {
        val sb = StringBuilder()
        while (true) {
            val c = this.next()
            if (c == delimiter || c == 0.toChar() || c == '\n' || c == '\r') {
                if (c != 0.toChar()) {
                    this.back()
                }
                return sb.toString().trim()
            }
            sb.append(c)
        }
    }


    /**
     * Get the text up but not including one of the specified delimiter
     * characters or the end of line, whichever comes first.
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    fun nextTo(delimiters: String): String {
        var c: Char
        val sb = StringBuilder()
        while (true) {
            c = this.next()
            if (delimiters.indexOf(c) >= 0 || c == 0.toChar() || c == '\n' || c == '\r') {
                if (c != 0.toChar()) {
                    this.back()
                }
                return sb.toString().trim()
            }
            sb.append(c)
        }
    }


    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     * @throws JSONException If syntax error.
     *
     * @return An object.
     */
    fun nextValue(): Any? {
        var c = this.nextClean()
        var string: String

        when (c) {
            '"' -> return this.nextString(c)
            '\'' -> return this.nextString(c)

            '{' -> {
                this.back()
                return JsonObject(this)
            }
            '[' -> {
                this.back()
                return JsonArray(this)
            }
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        val sb = StringBuilder()
        while (c >= ' ' && ",:]}/\\\"[{=#".indexOf(c) < 0) {
            sb.append(c)
            c = this.next()
        }
        this.back()

        string = sb.toString().trim()
        if ("".equals(string)) {
            throw this.syntaxError("Missing value")
        }
        return stringToValue(string)
    }


    /**
     * Skip characters until the next character is the requested character.
     * If the requested character is not found, no characters are skipped.
     * @param to A character to skip to.
     * @return The requested character, or zero if the requested character
     * is not found.
     */
    fun skipTo(to: Char): Char {
        var c: Char
        try {
            val startIndex = this.index
            val startCharacter = this.character
            val startLine = this.line
            this.reader.mark(1000000)
            do {
                c = this.next()
                if (c == 0.toChar()) {
                    this.reader.reset()
                    this.index = startIndex
                    this.character = startCharacter
                    this.line = startLine
                    return c
                }
            } while (c != to)
        } catch (exception: IOException) {
            throw JsonException(exception)
        }
        this.back()
        return c
    }


    /**
     * Make a JSONException to signal a syntax error.
     *
     * @param message The error message.
     * @return  A JSONException object, suitable for throwing
     */
    fun syntaxError(message: String): JsonException {
        return JsonException(message + this.toString())
    }


    /**
     * Make a printable string of this JSONTokener.
     *
     * @return " at {index} [character {character} line {line}]"
     */
    override fun toString(): String {
        return " at ${this.index} [character ${this.character} line ${this.line}]"
    }
}