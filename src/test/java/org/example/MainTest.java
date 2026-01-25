package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
    @Test
    void testMain() {
        Main.writeOut();

        String output = outputStream.toString();
        String expectedOutput = "i = 1" + System.lineSeparator() +
                "i = 2" + System.lineSeparator() +
                "i = 3" + System.lineSeparator() +
                "i = 4" + System.lineSeparator() +
                "i = 5" + System.lineSeparator();

        assertEquals(expectedOutput, output);
    }
}
