package com.h0tk3y.spbsu.parallel

import java.io.InputStream
import java.io.OutputStream
import java.util.*

data class InputForNfa(
    val n: Int,
    val m: Int,
    val start: Set<Int>,
    val finish: Set<Int>,
    val edges: Map<Pair<Int, Int>, List<Int>>,
    val inputString: String
)

data class InputForDfa(
    val n: Int,
    val m: Int,
    val start: Int,
    val finish: Set<Int>,
    val edges: Map<Pair<Int, Int>, Int>,
    val inputString: String
)

fun convertInputNfaToInputDfa(input: InputForNfa): InputForDfa = InputForDfa(
    input.n,
    input.m,
    input.start.first(),
    input.finish,
    input.edges.map { (key, value) -> key to value.first() }.toMap(),
    input.inputString
)

fun getInputNfa(`in`: InputStream = System.`in`): InputForNfa {
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
        if (!nextLine.contains(" ")) return InputForNfa(n, m, start, finish, edges, nextLine)
        val numbers = nextLine.split(" ").map { it.toInt() }
        if (edges.contains(Pair(numbers[0], numbers[1]))) edges[Pair(numbers[0], numbers[1])]!!.add(numbers[2])
        else edges[Pair(numbers[0], numbers[1])] = mutableListOf(numbers[2])
    }

    return InputForNfa(n, m, start, finish, edges, "")
}

fun getInputDfa(`in`: InputStream = System.`in`): InputForDfa {
    val scanner = Scanner(`in`)

    val n = scanner.nextInt()
    val m = scanner.nextInt()
    val start = scanner.nextInt()
    scanner.nextLine()
    val finishLine = scanner.nextLine()
    val finish = if (finishLine.isNullOrEmpty()) setOf() else finishLine.split(" ").map { it.toInt() }.toSet()
    val edges = mutableMapOf<Pair<Int, Int>, Int>()
    while (true) {
        val nextLine = scanner.nextLine()
        if (nextLine.isNullOrEmpty()) break
        if (!nextLine.contains(" ")) return InputForDfa(n, m, start, finish, edges, nextLine)
        val numbers = nextLine.split(" ").map { it.toInt() }
        edges[Pair(numbers[0], numbers[1])] = numbers[2]
    }

    return InputForDfa(n, m, start, finish, edges, "")
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
    val input = getInputNfa(`in`)
    var nexStep = input.start
    input.inputString.forEach {
        nexStep = getNextReachable(input.edges, nexStep, Character.getNumericValue(it))
    }
    return nexStep.intersect(input.finish).isNotEmpty()
}

fun writeOutPut(data: InputForDfa, out: OutputStream = System.out, writeInputString: Boolean = false) {
    out.write("${data.n}\n".toByteArray())
    out.write("${data.m}\n".toByteArray())
    out.write(("${data.start}\n".toByteArray()))
    data.finish.forEachIndexed { index, value ->
        if (index != data.finish.size - 1) out.write("$value ".toByteArray()) else out.write(
            "$value".toByteArray()
        )
    }
    out.write("\n".toByteArray())
    data.edges.forEach { out.write(("${it.key.first} ${it.key.second} ${it.value}\n".toByteArray())) }
    if (writeInputString) out.write((data.inputString + "\n").toByteArray())
    else out.write("\n".toByteArray())
}

fun convertNFA2DFA(`in`: InputStream = System.`in`): InputForNfa {
    val input = getInputNfa(`in`)

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

    return InputForNfa(
        lastIndex,
        input.m,
        setOf(0),
        subsetToIndex.keys.filter { it.intersect(input.finish).isNotEmpty() }.map { subsetToIndex[it]!! }.toSet(),
        answer,
        ""
    )
}

fun deleteNotReachable(input: InputForDfa): InputForDfa {
    val possibleToReach = MutableList(input.n) { false }
    val queue: Queue<Int> = LinkedList()
    /// trying to reach all reachable q from q_0
    possibleToReach[input.start] = true; queue.add(input.start)
    while (queue.isNotEmpty()) {
        val elem = queue.first()
        queue.remove()
        val edgesFromElem = input.edges.filter { (key, _) -> key.first == elem }.values
        edgesFromElem.forEach { q ->
            if (!possibleToReach[q]) {
                possibleToReach[q] = true; queue.add(q)
            }
        }
    }

    /// recalculate q indexes
    val oldIndexToNew = mutableMapOf<Int, Int>()
    var currentIndex = 0
    possibleToReach.forEachIndexed { index, value ->
        if (value) oldIndexToNew[index] = currentIndex++
        else oldIndexToNew[index] = -1
    }

    /// recalculate indexes in edges
    val newEdges =
        input.edges.filter { (key, value) -> oldIndexToNew[key.first]!! != -1 && oldIndexToNew[value]!! != -1 }
            .map { (key, value) -> (oldIndexToNew[key.first]!! to key.second) to oldIndexToNew[value]!! }.toMap()

    return InputForDfa(
        currentIndex,
        input.m,
        oldIndexToNew[input.start]!!,
        input.finish.filter { q -> oldIndexToNew[q]!! != -1 }.map { q -> oldIndexToNew[q]!! }.toSet(),
        newEdges,
        input.inputString
    )
}

fun minDFA(`in`: InputStream = System.`in`): InputForDfa {
    val input = deleteNotReachable(getInputDfa(`in`))

    var currenAmountOfClasses = 1
    val classes = mutableMapOf<Int, Int>()
    (0 until input.n).forEach { classes[it] = if (input.finish.contains(it)) 0 else 1 }

    var wasChanges = true
    while (wasChanges) {
        wasChanges = false
        (0 until currenAmountOfClasses).forEach { classIndex -> // all classes
            if (!wasChanges) {
                (0 until input.m).forEach { a -> // all symbols
                    if (!wasChanges) {
                        val newClasses = mutableMapOf<Int, MutableList<Int>>()
                        classes.filter { (_, value) -> value == classIndex }.keys.forEach { q -> //all q
                            if(input.edges.contains(q to a)) {
                                val to = input.edges[q to a]!!
                                if (newClasses.contains(to)) newClasses[to]!!.add(q)
                                else newClasses[to] = mutableListOf(q)
                            }
                        }
                        if (newClasses.size > 1) {
                            wasChanges = true
                            val newClassesList = newClasses.toList()
                            newClassesList.first().second.forEach { q ->
                                classes[q] = classIndex
                            }

                            newClassesList.drop(1).forEach { (_, qs) ->
                                currenAmountOfClasses++
                                qs.forEach { q ->
                                    classes[q] = currenAmountOfClasses
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val newEdges = input.edges.map { (key, value) -> (classes[key.first]!! to key.second) to classes[value]!! }.toMap()

    return InputForDfa(
        currenAmountOfClasses + 1,
        input.m,
        classes[input.start]!!,
        input.finish.map { classes[it]!! }.toSet(),
        newEdges,
        input.inputString
    )
}

fun main() {
}