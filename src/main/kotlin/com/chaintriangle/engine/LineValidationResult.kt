package com.chaintriangle.engine

import com.chaintriangle.model.Line4Peg

/**
 * Result of validating a 4-peg line attempt against board constraints and coordinate geometry.
 */
sealed class LineValidationResult {
    /**
     * Line is completely valid on the board.
     */
    data class Valid(val line: Line4Peg) : LineValidationResult()

    /**
     * Line validation failed.
     */
    data class Invalid(val reason: Reason) : LineValidationResult() {
        enum class Reason {
            /** Endpoints are identical or do not lie along any of the 3 lattice axes. */
            NOT_COLLINEAR,

            /** Line length is not exactly 3 primitive edges (4 pegs). */
            INVALID_LENGTH,

            /** Start peg is outside board boundaries. */
            START_PEG_OUT_OF_BOUNDS,

            /** End peg is outside board boundaries. */
            END_PEG_OUT_OF_BOUNDS,

            /** One or more intermediate pegs fall outside board boundaries. */
            INTERMEDIATE_PEG_OUT_OF_BOUNDS
        }
    }

    val isValid: Boolean get() = this is Valid
}
