/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 AuroraLS3
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.playeranalytics.plugin.scheduling;

import java.util.concurrent.TimeUnit;

/**
 * Utility for converting time amounts to ticks and milliseconds.
 */
public enum TimeAmount {

    WEEK(TimeUnit.DAYS.toMillis(7L)),
    MONTH(TimeUnit.DAYS.toMillis(30L)),
    YEAR(TimeUnit.DAYS.toMillis(365L));

    private final long ms;

    TimeAmount(long ms) {
        this.ms = ms;
    }

    /**
     * Convert a time into ticks.
     *
     * @param amount How many of the time unit?
     * @param unit   Unit of time
     * @return How many ticks that time span is.
     */
    public static long toTicks(long amount, TimeUnit unit) {
        return unit.toMillis(amount) / 50L;
    }

    /**
     * Convert a tick count into milliseconds.
     *
     * @param ticks How many ticks?
     * @return How many milliseconds pass during those ticks.
     */
    public static long ticksToMillis(long ticks) {
        return ticks * 50L;
    }

    /**
     * Find how many milliseconds a TimeAmount is.
     * <p>
     * This method is provided as extension for TimeUnit that does not provide WEEK, MONTH or YEAR.
     *
     * @param amount Amount of the time unit.
     * @return Milliseconds.
     */
    public long toMillis(long amount) {
        return ms * amount;
    }
}
