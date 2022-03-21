/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*
import kotlin.math.max


public class NumberedRationalFunction<C> internal constructor(
    public override val numerator: NumberedPolynomial<C>,
    public override val denominator: NumberedPolynomial<C>
) : AbstractRationalFunction<C, NumberedPolynomial<C>> {
    override fun toString(): String = "NumberedRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

// Waiting for context receivers :( TODO: Replace with context receivers when they will be available

//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> RationalFunction(numerator: Polynomial<C>, denominator: Polynomial<C>): RationalFunction<C> =
//    if (denominator.isZero()) throw ArithmeticException("/ by zero")
//    else RationalFunction<C>(numerator, denominator)
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numeratorCoefficients: List<C>, denominatorCoefficients: List<C>, reverse: Boolean = false): RationalFunction<C> =
//    RationalFunction<C>(
//        Polynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
//        Polynomial( with(denominatorCoefficients) { if (reverse) reversed() else this } ).also { if (it.isZero()) }
//    )
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numerator: Polynomial<C>): RationalFunction<C> =
//    RationalFunction(numerator, onePolynomial)
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numeratorCoefficients: List<C>, reverse: Boolean = false): RationalFunction<C> =
//    RationalFunction(
//        Polynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } )
//    )

// TODO: Rewrite former constructors as fabrics
//constructor(numeratorCoefficients: Map<List<Int>, C>, denominatorCoefficients: Map<List<Int>, C>) : this(
//Polynomial(numeratorCoefficients),
//Polynomial(denominatorCoefficients)
//)
//constructor(numeratorCoefficients: Collection<Pair<List<Int>, C>>, denominatorCoefficients: Collection<Pair<List<Int>, C>>) : this(
//Polynomial(numeratorCoefficients),
//Polynomial(denominatorCoefficients)
//)
//constructor(numerator: Polynomial<C>) : this(numerator, numerator.getOne())
//constructor(numeratorCoefficients: Map<List<Int>, C>) : this(
//Polynomial(numeratorCoefficients)
//)
//constructor(numeratorCoefficients: Collection<Pair<List<Int>, C>>) : this(
//Polynomial(numeratorCoefficients)
//)

public class NumberedRationalFunctionSpace<C, A: Ring<C>> (
    public val ring: A,
) :
    AbstractRationalFunctionalSpaceOverPolynomialSpace<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            NumberedPolynomialSpace<C, A>,
            >,
    AbstractPolynomialFractionsSpace<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            >() {

    override val polynomialRing : NumberedPolynomialSpace<C, A> = NumberedPolynomialSpace(ring)
    override fun constructRationalFunction(
        numerator: NumberedPolynomial<C>,
        denominator: NumberedPolynomial<C>
    ): NumberedRationalFunction<C> =
        NumberedRationalFunction(numerator, denominator)

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: NumberedRationalFunction<C> = NumberedRationalFunction(polynomialZero, polynomialOne)
    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: NumberedRationalFunction<C> = NumberedRationalFunction(polynomialOne, polynomialOne)

    /**
     * Checks equality of the rational functions.
     */
    public override infix fun NumberedRationalFunction<C>.equalsTo(other: NumberedRationalFunction<C>): Boolean {
        if (this === other) return true

        if (numerator.isZero() != other.numerator.isZero()) return false

        val countOfVariables = max(this.lastVariable, other.lastVariable)
        val thisNumeratorDegrees = this.numerator.degrees
        val thisDenominatorDegrees = this.denominator.degrees
        val otherNumeratorDegrees = other.numerator.degrees
        val otherDenominatorDegrees = other.denominator.degrees
        for (variable in 0 .. countOfVariables)
            if (
                thisNumeratorDegrees.getOrElse(variable) { 0u } + otherDenominatorDegrees.getOrElse(variable) { 0u }
                != thisDenominatorDegrees.getOrElse(variable) { 0u } + otherNumeratorDegrees.getOrElse(variable) { 0u }
            ) return false

        return numerator * other.denominator equalsTo other.numerator * denominator
    }

    /**
     * Maximal index (ID) of variable occurring in the polynomial with positive power. If there is no such variable,
     * the result is `-1`.
     */
    public val NumberedPolynomial<C>.lastVariable: Int get() = polynomialRing { lastVariable }
    /**
     * List that associates indices of variables (that appear in the polynomial in positive exponents) with their most
     * exponents in which the variables are appeared in the polynomial.
     *
     * As consequence all values in the list are non-negative integers. Also, if the polynomial is constant, the list is empty.
     * And last index of the list is [lastVariable].
     */
    public val NumberedPolynomial<C>.degrees: List<UInt> get() = polynomialRing { degrees }
    /**
     * Count of variables occurring in the polynomial with positive power. If there is no such variable,
     * the result is `0`.
     */
    public val NumberedPolynomial<C>.countOfVariables: Int get() = polynomialRing { countOfVariables }

    /**
     * Count of all variables that appear in the polynomial in positive exponents.
     */
    public val NumberedRationalFunction<C>.lastVariable: Int
        get() = polynomialRing { max(numerator.lastVariable, denominator.lastVariable) }
    /**
     * Count of variables occurring in the rational function with positive power. If there is no such variable,
     * the result is `0`.
     */
    public val NumberedRationalFunction<C>.countOfVariables: Int
        get() =
            MutableList(lastVariable + 1) { false }.apply {
                numerator.coefficients.entries.forEach { (degs, c) ->
                    if (c.isNotZero()) degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
                denominator.coefficients.entries.forEach { (degs, c) ->
                    if (c.isNotZero()) degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
            }.count { it }

    // TODO: Разобрать

    public operator fun NumberedRationalFunction<C>.div(other: NumberedRationalFunction<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator * other.denominator,
            denominator * other.numerator
        )

    public operator fun NumberedRationalFunction<C>.div(other: NumberedPolynomial<C>): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator,
            denominator * other
        )

    public operator fun NumberedRationalFunction<C>.div(other: C): NumberedRationalFunction<C> =
        NumberedRationalFunction(
            numerator,
            denominator * other
        )

//    operator fun invoke(arg: Map<Int, C>): NumberedRationalFunction<C> =
//        NumberedRationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    @JvmName("invokePolynomial")
//    operator fun invoke(arg: Map<Int, Polynomial<C>>): NumberedRationalFunction<C> =
//        NumberedRationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    @JvmName("invokeRationalFunction")
//    operator fun invoke(arg: Map<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> {
//        var num = numerator invokeRFTakeNumerator arg
//        var den = denominator invokeRFTakeNumerator arg
//        for (variable in 0 until max(numerator.countOfVariables, denominator.countOfVariables)) if (variable in arg) {
//            val degreeDif = numerator.degrees.getOrElse(variable) { 0 } - denominator.degrees.getOrElse(variable) { 0 }
//            if (degreeDif > 0)
//                den = multiplyByPower(den, arg[variable]!!.denominator, degreeDif)
//            else
//                num = multiplyByPower(num, arg[variable]!!.denominator, -degreeDif)
//        }
//        return NumberedRationalFunction(num, den)
//    }
//
//    override fun toString(): String = toString(Polynomial.variableName)
//
//    fun toString(withVariableName: String = Polynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(withVariableName)
//            else -> "${numerator.toStringWithBrackets(withVariableName)}/${denominator.toStringWithBrackets(withVariableName)}"
//        }
//
//    fun toString(namer: (Int) -> String): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(namer)
//            else -> "${numerator.toStringWithBrackets(namer)}/${denominator.toStringWithBrackets(namer)}"
//        }
//
//    fun toStringWithBrackets(withVariableName: String = Polynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(withVariableName)
//            else -> "(${numerator.toStringWithBrackets(withVariableName)}/${denominator.toStringWithBrackets(withVariableName)})"
//        }
//
//    fun toStringWithBrackets(namer: (Int) -> String): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(namer)
//            else -> "(${numerator.toStringWithBrackets(namer)}/${denominator.toStringWithBrackets(namer)})"
//        }
//
//    fun toReversedString(withVariableName: String = Polynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(withVariableName)
//            else -> "${numerator.toReversedStringWithBrackets(withVariableName)}/${denominator.toReversedStringWithBrackets(withVariableName)}"
//        }
//
//    fun toReversedString(namer: (Int) -> String): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(namer)
//            else -> "${numerator.toReversedStringWithBrackets(namer)}/${denominator.toReversedStringWithBrackets(namer)}"
//        }
//
//    fun toReversedStringWithBrackets(withVariableName: String = Polynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(withVariableName)
//            else -> "(${numerator.toReversedStringWithBrackets(withVariableName)}/${denominator.toReversedStringWithBrackets(withVariableName)})"
//        }
//
//    fun toReversedStringWithBrackets(namer: (Int) -> String): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(namer)
//            else -> "(${numerator.toReversedStringWithBrackets(namer)}/${denominator.toReversedStringWithBrackets(namer)})"
//        }
}