/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import space.kscience.kmath.domains.Domain1D
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.asSequence
import space.kscience.kmath.structures.Buffer


/**
 * A univariate bin based on a range
 *
 * @property value The value of histogram including weighting
 * @property standardDeviation Standard deviation of the bin value. Zero or negative if not applicable
 */
@UnstableKMathAPI
public class Bin1D<T : Comparable<T>, out V>(
    public val domain: Domain1D<T>,
    override val value: V,
) : Bin<T, V>, ClosedRange<T> by domain.range {

    override val dimension: Int get() = 1

    override fun contains(point: Buffer<T>): Boolean = point.size == 1 && contains(point[0])
}

@OptIn(UnstableKMathAPI::class)
public interface Histogram1D<T : Comparable<T>, V> : Histogram<T, V, Bin1D<T, V>> {
    override val dimension: Int get() = 1
    public operator fun get(value: T): Bin1D<T, V>?
    override operator fun get(point: Buffer<T>): Bin1D<T, V>? = get(point[0])
}

@UnstableKMathAPI
public interface Histogram1DBuilder<in T : Any, V : Any> : HistogramBuilder<T, V> {
    /**
     * Thread safe put operation
     */
    public fun putValue(at: T, value: V = defaultValue)
}

@UnstableKMathAPI
public fun Histogram1DBuilder<Double, *>.fill(items: Iterable<Double>): Unit =
    items.forEach(this::putValue)

@UnstableKMathAPI
public fun Histogram1DBuilder<Double, *>.fill(array: DoubleArray): Unit =
    array.forEach(this::putValue)

@UnstableKMathAPI
public fun Histogram1DBuilder<Double, *>.fill(buffer: Buffer<Double>): Unit =
    buffer.asSequence().forEach(this::putValue)