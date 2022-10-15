package com.h0tk3y.spbsu.parallel

import org.junit.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals

class Tests {
    class EmulatorTests {
        // Example from the task
        @Test
        fun testEmulator1() {
            assert(emulateNFA(File("src/test/kotlin/resources/emulator1.txt").inputStream()))
        }

        // Have no acc
        @Test
        fun testEmulator2() {
            assert(!emulateNFA(File("src/test/kotlin/resources/emulator2.txt").inputStream()))
        }

        // Next 3 tests should accept only strings (1|0)*(00)(1|0)* (00, 100, 11101100110, ...)
        @Test
        fun testEmulator3() {
            assert(emulateNFA(File("src/test/kotlin/resources/emulator3.txt").inputStream()))
        }

        @Test
        fun testEmulator4() {
            assert(!emulateNFA(File("src/test/kotlin/resources/emulator4.txt").inputStream()))
        }

        @Test
        fun testEmulator5() {
            assert(emulateNFA(File("src/test/kotlin/resources/emulator5.txt").inputStream()))
        }
    }

    class NFA2DFATests {
        // Example from the task. I checked that it is done correctly (compared with notes from the class)
        @Test
        fun testConvertor1() {
            writeOutPut(convertNFA2DFA(File("src/test/kotlin/resources/converter1.txt").inputStream()))
        }

        // DFA (equal to the input)

        @Test
        fun testConvertor2() {
            writeOutPut(convertNFA2DFA(File("src/test/kotlin/resources/converter2.txt").inputStream()), File("testConvertor2").outputStream())
            assertEquals(Files.readAllLines(File("src/test/kotlin/resources/converter2.txt").toPath()),Files.readAllLines(File("testConvertor2").toPath()))
        }
    }


}