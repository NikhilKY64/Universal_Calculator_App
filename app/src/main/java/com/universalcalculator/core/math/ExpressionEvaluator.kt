package com.universalcalculator.core.math

import kotlin.math.*

sealed class ExprNode {
    abstract fun eval(x: Double, y: Double): Double
}

class NumberNode(val value: Double) : ExprNode() {
    override fun eval(x: Double, y: Double): Double = value
}

class VarNode(val name: String) : ExprNode() {
    override fun eval(x: Double, y: Double): Double = when (name) {
        "x" -> x
        "y" -> y
        else -> 0.0
    }
}

class BinaryOpNode(val left: ExprNode, val right: ExprNode, val op: Char) : ExprNode() {
    override fun eval(x: Double, y: Double): Double {
        val l = left.eval(x, y)
        val r = right.eval(x, y)
        return when (op) {
            '+' -> l + r
            '-' -> l - r
            '*' -> l * r
            '/' -> if (r != 0.0) l / r else Double.NaN
            '^' -> l.pow(r)
            else -> 0.0
        }
    }
}

class UnaryOpNode(val expr: ExprNode, val op: Char) : ExprNode() {
    override fun eval(x: Double, y: Double): Double {
        val v = expr.eval(x, y)
        return when (op) {
            '-' -> -v
            '+' -> v
            else -> v
        }
    }
}

class FuncNode(val name: String, val arg: ExprNode) : ExprNode() {
    override fun eval(x: Double, y: Double): Double {
        val v = arg.eval(x, y)
        return when (name) {
            "sqrt" -> sqrt(v)
            "sin" -> sin(Math.toRadians(v))
            "cos" -> cos(Math.toRadians(v))
            "tan" -> tan(Math.toRadians(v))
            "asin" -> Math.toDegrees(asin(v))
            "acos" -> Math.toDegrees(acos(v))
            "atan" -> Math.toDegrees(atan(v))
            "log" -> log10(v)
            "ln" -> ln(v)
            "abs" -> abs(v)
            else -> 0.0
        }
    }
}

class ExpressionEvaluator {
    fun evaluate(expression: String, variables: Map<String, Double> = emptyMap()): Double {
        val ast = compile(expression)
        return ast.eval(variables["x"] ?: 0.0, variables["y"] ?: 0.0)
    }

    fun compile(expression: String): ExprNode {
        if (expression.isBlank()) return NumberNode(0.0)
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(" ", "")
            .replace("²", "^2")
            .replace("³", "^3")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())

        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < sanitized.length) sanitized[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): ExprNode {
                nextChar()
                val x = parseExpression()
                if (pos < sanitized.length) throw IllegalArgumentException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): ExprNode {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x = BinaryOpNode(x, parseTerm(), '+')
                    else if (eat('-'.code)) x = BinaryOpNode(x, parseTerm(), '-')
                    else return x
                }
            }

            fun parseTerm(): ExprNode {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x = BinaryOpNode(x, parseFactor(), '*')
                    else if (eat('/'.code)) x = BinaryOpNode(x, parseFactor(), '/')
                    else return x
                }
            }

            fun parseFactor(): ExprNode {
                if (eat('+'.code)) return UnaryOpNode(parseFactor(), '+')
                if (eat('-'.code)) return UnaryOpNode(parseFactor(), '-')

                var x: ExprNode
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) {
                    while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                    x = NumberNode(sanitized.substring(startPos, pos).toDouble())
                } else if (ch >= 'a'.code && ch <= 'z'.code) {
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = sanitized.substring(startPos, pos)
                    if (func == "x" || func == "y") {
                        x = VarNode(func)
                    } else {
                        x = FuncNode(func, parseFactor())
                    }
                } else {
                    throw IllegalArgumentException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) x = BinaryOpNode(x, parseFactor(), '^')

                return x
            }
        }.parse()
    }
}
