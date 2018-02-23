package edu.csh.chase.sprint

import java.util.concurrent.TimeUnit

/**
 * A class to generate delays for backoff retry logic
 *
 * @param maxAttempts The max number of attempts
 *
 * @throws IllegalArgumentException if maxAttempts is less than 0
 */
abstract class BackoffTimeout(val maxAttempts: Int?) {

    var currentAttempt = 0
        private set

    init {
        if (maxAttempts ?: 0 < 0) {
            throw IllegalArgumentException("Cannot have negative maxAttempts")
        }
    }

    /**
     * A class representing an exponential backoff
     *
     * Example:
     * With a start of 500ms, power of 2, and maxTimeout of 60s
     * The first 5 values will be 500, 1000, 2000, 4000, 8000
     *
     * @param start The length of the first backoff
     * @param power The exponential factor to backoff with
     * @param maxTimeout The maximum timeout this backoff can output. If the value of the next delay would exceed maxTimeout,
     * maxTimeout will return instead
     *
     * @throws IllegalArgumentException if power is < 2 or if start, maxTimeout are < 1
     */
    class Exponential(val start: Long, val power: Int, val maxTimeout: Long, maxAttempts: Int?) : BackoffTimeout(maxAttempts) {

        init {
            if (power < 2) {
                throw IllegalArgumentException("power must be > 1")
            }

            if (start < 1) {
                throw IllegalArgumentException("start must be > 0")
            }

            if (maxTimeout < 1) {
                throw IllegalArgumentException("maxTimeout must be > 0")
            }
        }

        constructor(start: Long, startUnit: TimeUnit, power: Int, maxTimeout: Long, timeoutUnit: TimeUnit, maxAttempts: Int?) : this(
            startUnit.toMillis(start), power, timeoutUnit.toMillis(maxTimeout), maxAttempts
        )

        constructor(start: Long, power: Int, maxTimeout: Long, unit: TimeUnit, maxAttempts: Int?) : this(
            unit.toMillis(start), power, unit.toMillis(maxTimeout), maxAttempts
        )

        override fun getDelay(attempt: Int): Long = Math.min(start * Math.pow(power.toDouble(), attempt.toDouble()).toLong(), maxTimeout)

    }

    /**
     * A class representing a linear backoff
     *
     * Example:
     * With a start of 100ms, step of 100ms, and maxTimeout of 60s
     * The first 5 values will be 100, 200, 300, 400, 500
     *
     * @param start The length of the first backoff
     * @param step The amount to increase for each attempt
     * @param maxTimeout The maximum timeout this backoff can output. If the value of the next delay would exceed maxTimeout,
     * maxTimeout will return instead
     *
     * @throws IllegalArgumentException if any of step, or maxTimeout are less than 1. Or if start is < 0
     */
    class Linear(val start: Long, val step: Long, val maxTimeout: Long, maxAttempts: Int?) : BackoffTimeout(maxAttempts) {

        init {
            if (step < 1) {
                throw IllegalArgumentException("step must be > 0")
            }

            if (start < 0) {
                throw IllegalArgumentException("start must be >= 0")
            }

            if (maxTimeout < 1) {
                throw IllegalArgumentException("maxTimeout must be > 0")
            }
        }

        constructor(start: Long, startUnit: TimeUnit, step: Long, stepUnit: TimeUnit, maxTimeout: Long, timeoutUnit: TimeUnit, maxAttempts: Int?) : this(
            startUnit.toMillis(start), stepUnit.toMillis(step), timeoutUnit.toMillis(maxTimeout), maxAttempts
        )

        constructor(start: Long, step: Long, maxTimeout: Long, unit: TimeUnit, maxAttempts: Int?) : this(
            unit.toMillis(start), unit.toMillis(step), unit.toMillis(maxTimeout), maxAttempts
        )

        override fun getDelay(attempt: Int): Long = Math.min(start + (step * attempt), maxTimeout)

    }

    /**
     * A class representing a constant backoff
     * Example:
     * With a step of 100ms
     * The first 5 values will be 100, 100, 100, 100, 100
     *
     * @param step The amount of time each backoff will be
     *
     * @throws IllegalArgumentException if step < 1
     */
    class Constant(val step: Long, maxAttempts: Int?) : BackoffTimeout(maxAttempts) {

        init {
            if (step < 1) {
                throw IllegalArgumentException("step must be > 0")
            }
        }

        constructor(step: Long, unit: TimeUnit, maxAttempts: Int?) : this(unit.toMillis(step), maxAttempts)

        override fun getDelay(attempt: Int): Long = step

    }

    class NoRetry() : BackoffTimeout(0) {
        override fun getDelay(attempt: Int): Long = 0L
    }

    protected abstract fun getDelay(attempt: Int): Long

    /**
     * Gets the next delay for this backoff
     *
     * @return The delay in Milliseconds
     */
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