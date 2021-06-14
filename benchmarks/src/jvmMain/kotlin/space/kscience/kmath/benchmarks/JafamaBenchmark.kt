/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Blackhole
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.jafama.JafamaDoubleField
import space.kscience.kmath.jafama.StrictJafamaDoubleField
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import kotlin.random.Random

@State(Scope.Benchmark)
internal class JafamaBenchmark {
    @Benchmark
    fun jafamaBench(blackhole: Blackhole) = invokeBenchmarks(blackhole) { x ->
        JafamaDoubleField { x * power(x, 4) * exp(x) / cos(x) + sin(x) }
    }

    @Benchmark
    fun coreBench(blackhole: Blackhole) = invokeBenchmarks(blackhole) { x ->
        DoubleField { x * power(x, 4) * exp(x) / cos(x) + sin(x) }
    }

    @Benchmark
    fun strictJafamaBench(blackhole: Blackhole) = invokeBenchmarks(blackhole) { x ->
        StrictJafamaDoubleField { x * power(x, 4) * exp(x) / cos(x) + sin(x) }
    }

    private inline fun invokeBenchmarks(blackhole: Blackhole, expr: (Double) -> Double) {
        val rng = Random(0)
        repeat(1000000) { blackhole.consume(expr(rng.nextDouble())) }
    }
}
