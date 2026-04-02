package br.gohan.videofeed.domain

import br.gohan.videofeed.domain.error.DataError
import br.gohan.videofeed.domain.error.Result
import br.gohan.videofeed.domain.error.map
import br.gohan.videofeed.domain.error.onFailure
import br.gohan.videofeed.domain.error.onSuccess
import br.gohan.videofeed.domain.error.asEmptyResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResultExtensionsTest {

    @Test
    fun `map transforms success value`() {
        val result: Result<Int, DataError.Network> = Result.Success(2)
        val mapped = result.map { it * 3 }
        assertEquals(Result.Success(6), mapped)
    }

    @Test
    fun `map preserves error`() {
        val result: Result<Int, DataError.Network> = Result.Error(DataError.Network.NO_INTERNET)
        val mapped = result.map { it * 3 }
        assertEquals(Result.Error(DataError.Network.NO_INTERNET), mapped)
    }

    @Test
    fun `onSuccess is called for success`() {
        var called = false
        Result.Success(42).onSuccess<Int, DataError.Network> { called = true }
        assertTrue(called)
    }

    @Test
    fun `onFailure is called for error`() {
        var called = false
        Result.Error(DataError.Network.UNKNOWN).onFailure<Int, DataError.Network> { called = true }
        assertTrue(called)
    }

    @Test
    fun `asEmptyResult maps success to Unit`() {
        val result: Result<String, DataError.Network> = Result.Success("hello")
        assertEquals(Result.Success(Unit), result.asEmptyResult())
    }
}
