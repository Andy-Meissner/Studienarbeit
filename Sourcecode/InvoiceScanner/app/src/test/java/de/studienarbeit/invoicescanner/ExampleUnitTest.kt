package de.studienarbeit.invoicescanner

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun ibantest1()
    {
        var testString = "DE75 6425 0040 0000 7393 33"
        assertEquals(true, ImageAnalyzer.containsIBAN(testString))
    }
    @Test
    fun ibantest2()
    {
        var testString = "DE75 6425 ?0040 ??0000 7393 33"
        assertEquals(false, ImageAnalyzer.containsIBAN(testString))
    }
    @Test
    fun ibantest3()
    {
        var testString = "absdfasdDE75642500400000739333xcvasdfga"
        assertEquals(true, ImageAnalyzer.containsIBAN(testString))
    }

    @Test
    fun bictest1()
    {
        var testString = "SOLADES1RWL"
        assertEquals(true, ImageAnalyzer.containsBIC(testString))
    }
    @Test
    fun bictest2()
    {
        var testString = "SOLA?)=?FA EWFA SD FASD"
        assertEquals(false, ImageAnalyzer.containsBIC(testString))
    }
}
