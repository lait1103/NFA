package com.h0tk3y.spbsu.parallel

import java.io.InputStream
import java.io.OutputStream
import java.util.*

data class Input(
    val n: Int,
    val m: Int,
    val start: Set<Int>,
    val finish: Set<Int>,
    val edges: Map<Pair<Int, Int>, List<Int>>,
    val inputString: String
)

fun getInput(`in`: InputStream = System.`in`): Input {
    val scanner = Scanner(`in`)

    val n = scanner.nextInt()
    val m = scanner.nextInt()
    scanner.nextLine()
    val startLine = scanner.nextLine()
    val start = if (startLine.isNullOrEmpty()) setOf() else startLine.split(" ").map { it.toInt() }.toSet()
    val finishLine = scanner.nextLine()
    val finish = if (finishLine.isNullOrEmpty()) setOf() else finishLine.split(" ").map { it.toInt() }.toSet()
    val edges = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
    while (true) {
        val nextLine = scanner.nextLine()
        if (nextLine.isNullOrEmpty()) break
        if (!nextLine.contains(" ")) return Input(n, m, start, finish, edges, nextLine)
        val numbers = nextLine.split(" ").map { it.toInt() }
        if (edges.contains(Pair(numbers[0], numbers[1]))) edges[Pair(numbers[0], numbers[1])]!!.add(numbers[2])
        else edges[Pair(numbers[0], numbers[1])] = mutableListOf(numbers[2])
    }

    return Input(n, m, start, finish, edges, "")
}

fun getNextReachable(edges: Map<Pair<Int, Int>, List<Int>>, curStep: Set<Int>, a: Int): Set<Int> {
    val nextStep = mutableSetOf<Int>()
    curStep.forEach { q ->
        edges[Pair(q, a)]?.forEach { newQ ->
            nextStep.add(newQ)
        } ?: listOf<Int>()
    }
    return nextStep
}

fun emulateNFA(`in`: InputStream = System.`in`): Boolean {
    val input = getInput(`in`)
    var nexStep = input.start
    input.inputString.forEach {
        nexStep = getNextReachable(input.edges, nexStep, Character.getNumericValue(it))
    }
    return nexStep.intersect(input.finish).isNotEmpty()
}

fun writeOutPut(data: Input, out: OutputStream = System.out) {
    out.write("${data.n}\n".toByteArray())
    out.write("${data.m}\n".toByteArray())
    out.write(("${data.start.first()}\n".toByteArray()))
    data.finish.forEach {out.write("$it ".toByteArray()) }
    out.write("\n".toByteArray())
    data.edges.forEach {out.write(("${it.key.first} ${it.key.second} ${it.value.first()}\n".toByteArray()))}
}
fun convertNFA2DFA(`in`: InputStream = System.`in`): Input {
    val input = getInput(`in`)

    val subsetToIndex = mutableMapOf<Set<Int>, Int>()
    var lastIndex = 0
    val queue: Queue<Set<Int>> = LinkedList()
    queue.add(input.start)
    subsetToIndex[input.start] = lastIndex++
    val answer = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
    while (queue.isNotEmpty()) {
        val current = queue.first()
        queue.remove()
        (0 until input.m).forEach { a ->
            val nextReachable = getNextReachable(input.edges, current, a)
            if (subsetToIndex.contains(nextReachable)) {
                if (answer.contains(Pair(subsetToIndex[current], a))) answer[Pair(subsetToIndex[current], a)]!!.add(
                    subsetToIndex[nextReachable]!!
                )
                else answer[Pair(subsetToIndex[current]!!, a)] = mutableListOf(subsetToIndex[nextReachable]!!)
            } else {
                subsetToIndex[nextReachable] = lastIndex++
                queue.add(nextReachable)
                if (answer.contains(Pair(subsetToIndex[current], a))) answer[Pair(subsetToIndex[current], a)]!!.add(
                    subsetToIndex[nextReachable]!!
                )
                else answer[Pair(subsetToIndex[current]!!, a)] = mutableListOf(subsetToIndex[nextReachable]!!)
            }
        }
    }

    return Input(
        input.n,
        lastIndex,
        setOf(0),
        subsetToIndex.keys.filter { it.intersect(input.finish).isNotEmpty() }.map { subsetToIndex[it]!! }.toSet(),
        answer,
        ""
    )
}

fun main() {
}