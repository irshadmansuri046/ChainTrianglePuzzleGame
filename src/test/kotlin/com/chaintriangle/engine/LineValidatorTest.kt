package com.chaintriangle.engine

import com.chaintriangle.model.LatticeAxis
import com.chaintriangle.model.Peg
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LineValidatorTest {

    private val board = Board(4)

    @Test
    fun `test valid horizontal line validation`() {
        val result = LineValidator.validate(Peg(0, 0), Peg(3, 0), board)
        assertTrue(result is LineValidationResult.Valid)

        val valid = result as LineValidationResult.Valid
        assertEquals(LatticeAxis.HORIZONTAL, valid.line.axis)
        assertEquals(4, valid.line.pegs.size)
        assertEquals(Peg(0, 0), valid.line.start)
        assertEquals(Peg(3, 0), valid.line.end)
    }

    @Test
    fun `test valid diagonal right line validation`() {
        val result = LineValidator.validate(Peg(0, 0), Peg(0, 3), board)
        assertTrue(result is LineValidationResult.Valid)
    }

    @Test
    fun `test valid diagonal left line validation`() {
        val result = LineValidator.validate(Peg(3, 0), Peg(0, 3), board)
        assertTrue(result is LineValidationResult.Valid)
    }

    @Test
    fun `test start peg out of bounds`() {
        val result = LineValidator.validate(Peg(-1, 0), Peg(2, 0), board)
        assertTrue(result is LineValidationResult.Invalid)
        assertEquals(
            LineValidationResult.Invalid.Reason.START_PEG_OUT_OF_BOUNDS,
            (result as LineValidationResult.Invalid).reason
        )
    }

    @Test
    fun `test end peg out of bounds`() {
        val result = LineValidator.validate(Peg(1, 0), Peg(4, 0), board)
        assertTrue(result is LineValidationResult.Invalid)
        assertEquals(
            LineValidationResult.Invalid.Reason.END_PEG_OUT_OF_BOUNDS,
            (result as LineValidationResult.Invalid).reason
        )
    }

    @Test
    fun `test non-collinear line validation`() {
        val result = LineValidator.validate(Peg(0, 0), Peg(2, 1), board)
        assertTrue(result is LineValidationResult.Invalid)
        assertEquals(
            LineValidationResult.Invalid.Reason.NOT_COLLINEAR,
            (result as LineValidationResult.Invalid).reason
        )
    }

    @Test
    fun `test invalid line length`() {
        val result = LineValidator.validate(Peg(0, 0), Peg(2, 0), board)
        assertTrue(result is LineValidationResult.Invalid)
        assertEquals(
            LineValidationResult.Invalid.Reason.INVALID_LENGTH,
            (result as LineValidationResult.Invalid).reason
        )
    }

    @Test
    fun `test resolve pegs helper`() {
        val pegs = LineValidator.resolvePegs(Peg(0, 0), Peg(3, 0))
        assertEquals(listOf(Peg(0, 0), Peg(1, 0), Peg(2, 0), Peg(3, 0)), pegs)
    }
}
