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
            writeOutPut(convertInputNfaToInputDfa(convertNFA2DFA(File("src/test/kotlin/resources/converter1.txt").inputStream())))
        }

        // DFA (equal to the input)

        @Test
        fun testConvertor2() {
            writeOutPut(convertInputNfaToInputDfa(convertNFA2DFA(File("src/test/kotlin/resources/converter2.txt").inputStream())), File("testConvertor2").outputStream())
            assertEquals(Files.readAllLines(File("src/test/kotlin/resources/converter2.txt").toPath()),Files.readAllLines(File("testConvertor2").toPath()))
        }
    }

    class MinDfaTest {
        @Test
        fun testMin1() {
            writeOutPut(minDFA(File("src/test/kotlin/resources/converter2.txt").inputStream()), File("testMin1").outputStream())
            assertEquals(emulateNFA(File("src/test/kotlin/resources/converter2.txt").inputStream()) ,emulateNFA(File("testMin1").inputStream()))
        }

        @Test
        fun testMin2() {
            writeOutPut(minDFA(File("src/test/kotlin/resources/emulator2.txt").inputStream()), File("testMin2").outputStream(), true)
            assertEquals(emulateNFA(File("src/test/kotlin/resources/emulator2.txt").inputStream()) ,emulateNFA(File("testMin2").inputStream()))
        }

        @Test
        fun testMin3() {
            writeOutPut(minDFA(File("src/test/kotlin/resources/emulator3.txt").inputStream()), File("testMin3").outputStream(), true)
            assertEquals(emulateNFA(File("src/test/kotlin/resources/emulator3.txt").inputStream()) ,emulateNFA(File("testMin3").inputStream()))
        }

        @Test
        fun testMin4() {
            writeOutPut(minDFA(File("src/test/kotlin/resources/emulator4.txt").inputStream()), File("testMin4").outputStream(), true)
            assertEquals(emulateNFA(File("src/test/kotlin/resources/emulator4.txt").inputStream()) ,emulateNFA(File("testMin4").inputStream()))
        }

        @Test
        fun testMin5() {
            writeOutPut(minDFA(File("src/test/kotlin/resources/emulator5.txt").inputStream()), File("testMin5").outputStream(), true)
            assertEquals(emulateNFA(File("src/test/kotlin/resources/emulator5.txt").inputStream()) ,emulateNFA(File("testMin5").inputStream()))
        }

        @Test
        fun testMin6() {
            writeOutPut(minDFA(File("src/test/kotlin/resources/badDfa1").inputStream()), File("testMin6").outputStream(), true)
            assertEquals(emulateNFA(File("src/test/kotlin/resources/badDfa1").inputStream()) ,emulateNFA(File("testMin6").inputStream()))
        }

        @Test
        fun testMin7() {
            writeOutPut(minDFA(File("src/test/kotlin/resources/badDfa2").inputStream()), File("testMin7").outputStream(), true)
            assertEquals(emulateNFA(File("src/test/kotlin/resources/badDfa2").inputStream()) ,emulateNFA(File("testMin7").inputStream()))
        }


    }


}