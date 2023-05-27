/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.StreamingLm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import space.kscience.kmath.nd.*
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.zeros
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.internal.LMSettings
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.reflect.KFunction3

fun streamLm(lm_func: KFunction3<MutableStructure2D<Double>, MutableStructure2D<Double>, LMSettings, MutableStructure2D<Double>>,
             startData: StartDataLm, launchFrequencyInMs: Long): Flow<MutableStructure2D<Double>> = flow{

    var example_number = startData.example_number
    var p_init = startData.p_init
    var t = startData.t
    var y_dat = startData.y_dat
    val weight = startData.weight
    val dp = startData.dp
    val p_min = startData.p_min
    val p_max = startData.p_max
    val consts = startData.consts
    val opts = startData.opts

    while (true) {
        val result = DoubleTensorAlgebra.lm(
            lm_func,
            p_init,
            t,
            y_dat,
            weight,
            dp,
            p_min,
            p_max,
            consts,
            opts,
            10,
            example_number
        )
        emit(result.result_parameters)
        delay(launchFrequencyInMs)
        p_init = result.result_parameters
        y_dat = generateNewYDat(y_dat, 0.1)
    }
}

fun generateNewYDat(y_dat: MutableStructure2D<Double>, delta: Double): MutableStructure2D<Double>{
    val n = y_dat.shape.component1()
    val y_dat_new = zeros(ShapeND(intArrayOf(n, 1))).as2D()
    for (i in 0 until n) {
        val randomEps = Random.nextDouble(delta + delta) - delta
        y_dat_new[i, 0] = y_dat[i, 0] + randomEps
    }
    return y_dat_new
}

suspend fun main(){
    val startData = getStartDataForFunc1()
    // Создание потока:
    val lmFlow = streamLm(::func1ForLm, startData, 1000)
    // Запуск потока
    lmFlow.collect { parameters ->
        for (i in 0 until parameters.shape.component1()) {
            val x = (parameters[i, 0] * 10000).roundToInt() / 10000.0
            print("$x ")
            if (i == parameters.shape.component1() - 1) println()
        }
    }
}