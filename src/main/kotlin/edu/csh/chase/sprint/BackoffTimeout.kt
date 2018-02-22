package edu.csh.chase.sprint

import java.util.concurrent.TimeUnit

/**
 * A class to generate delays for backoff retry logic
 *
 * @param maxAttempts The max number of attempts
 */
abstract class BackoffTimeout(val maxAttempts: Int?) {

    var currentAttempt = 0
        private set

    init {
        if (maxAttempts ?: 0 < 0) {
            throw IllegalArgumentException("Cannot have negative maxAttempts")
        }
    }

    class Exponential(val start: Long, val power: Int, val maxTimeout: Long, maxAttempts: Int?) : BackoffTimeout(maxAttempts) {

        constructor(start: Long, startUnit: TimeUnit, power: Int, maxTimeout: Long, timeoutUnit: TimeUnit, maxAttempts: Int?) : this(
            startUnit.toMillis(start), power, timeoutUnit.toMillis(maxTimeout), maxAttempts
        )

        constructor(start: Long, power: Int, maxTimeout: Long, unit: TimeUnit, maxAttempts: Int?) : this(
            unit.toMillis(start), power, unit.toMillis(maxTimeout), maxAttempts
        )

        override fun getDelay(attempt: Int): Long = Math.min(start * Math.pow(power.toDouble(), attempt.toDouble()).toLong(), maxTimeout)

    }

    class Linear(val start: Long, val step: Long, val maxTimeout: Long, maxAttempts: Int?) : BackoffTimeout(maxAttempts) {

        constructor(start: Long, startUnit: TimeUnit, step: Long, stepUnit: TimeUnit, maxTimeout: Long, timeoutUnit: TimeUnit, maxAttempts: Int?) : this(
            startUnit.toMillis(start), stepUnit.toMillis(step), timeoutUnit.toMillis(maxTimeout), maxAttempts
        )

        constructor(start: Long, step: Long, maxTimeout: Long, unit: TimeUnit, maxAttempts: Int?) : this(
            unit.toMillis(start), unit.toMillis(step), unit.toMillis(maxTimeout), maxAttempts
        )

        override fun getDelay(attempt: Int): Long = Math.min(start + (step * attempt), maxTimeout)

    }

    class Constant(val step: Long, maxAttempts: Int?) : BackoffTimeout(maxAttempts) {

        constructor(step: Long, unit: TimeUnit, maxAttempts: Int?) : this(unit.toMillis(step), maxAttempts)

        override fun getDelay(attempt: Int): Long = step

    }

    class NoRetry() : BackoffTimeout(0) {
        override fun getDelay(attempt: Int): Long = 0L
    }

    protected abstract fun getDelay(attempt: Int): Long

    fun getNextDelay(): Long {
        maxAttempts?.let {
            if (currentAttempt >= it) {
                throw IllegalArgumentException("Getting delay for attempt:$currentAttempt with maxAttempts:$maxAttempts")
            }
        }

        currentAttempt += 1

        return getDelay(currentAttempt - 1)
    }

    fun reset() {
        currentAttempt = 0
    }

    fun shouldRetry(): Boolean = maxAttempts?.let { currentAttempt < maxAttempts } ?: true

}