package com.chaintriangle.engine

import com.chaintriangle.model.LatticeAxis
import com.chaintriangle.model.Line4Peg
import com.chaintriangle.model.Peg
import kotlin.math.abs
import kotlin.math.sign

/**
 * Validates selected endpoints and interpolates intermediate pegs for 4-peg straight line moves.
 */
object LineValidator {

    /**
     * Validates whether [start] and [end] form a valid 4-peg line on the specified [board].
     */
    fun validate(start: Peg, end: Peg, board: Board): LineValidationResult {
        if (!board.containsPeg(start)) {
            return LineValidationResult.Invalid(LineValidationResult.Invalid.Reason.START_PEG_OUT_OF_BOUNDS)
        }
        if (!board.containsPeg(end)) {
            return LineValidationResult.Invalid(LineValidationResult.Invalid.Reason.END_PEG_OUT_OF_BOUNDS)
        }

        val dq = end.q - start.q
        val dr = end.r - start.r

        val axis = LatticeAxis.fromVector(dq, dr)
            ?: return LineValidationResult.Invalid(LineValidationResult.Invalid.Reason.NOT_COLLINEAR)

        val distance = (abs(dq) + abs(dr) + abs(dq + dr)) / 2
        if (distance != 3) {
            return LineValidationResult.Invalid(LineValidationResult.Invalid.Reason.INVALID_LENGTH)
        }

        val line = Line4Peg.ofOrNull(start, end)
            ?: return LineValidationResult.Invalid(LineValidationResult.Invalid.Reason.NOT_COLLINEAR)

        // Check if all intermediate pegs reside within board bounds
        for (peg in line.pegs) {
            if (!board.containsPeg(peg)) {
                return LineValidationResult.Invalid(LineValidationResult.Invalid.Reason.INTERMEDIATE_PEG_OUT_OF_BOUNDS)
            }
        }

        return LineValidationResult.Valid(line)
    }

    /**
     * Resolves and interpolates the intermediate pegs between [start] and [end] for a 4-peg line.
     *
     * @return List of 4 pegs `[start, intermediate1, intermediate2, end]`, or `null` if not a valid 4-peg line.
     */
    fun resolvePegs(start: Peg, end: Peg): List<Peg>? {
        return Line4Peg.ofOrNull(start, end)?.pegs
    }
}
