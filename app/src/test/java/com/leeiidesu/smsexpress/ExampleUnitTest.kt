package com.leeiidesu.smsexpress

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(Regex("[\\d]{6,}").find("sss666666sagfasga")!!.value)


    }
}
