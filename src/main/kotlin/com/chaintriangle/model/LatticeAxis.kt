package com.chaintriangle.model

/**
 * Represents the 3 symmetrical lattice axes of an equilateral triangular grid in axial coordinates (q, r).
 *
 * 1. [HORIZONTAL]: dr = 0 (movement along constant r)
 * 2. [DIAGONAL_RIGHT]: dq = 0 (movement along constant q)
 * 3. [DIAGONAL_LEFT]: dq + dr = 0 (movement along constant q + r)
 */
enum class LatticeAxis {
    HORIZONTAL,
    DIAGONAL_RIGHT,
    DIAGONAL_LEFT;

    companion object {
        /**
         * Resolves the [LatticeAxis] for a non-zero vector (dq, dr).
         * Returns null if the vector is (0, 0) or does not lie along one of the 3 lattice axes.
         */
        fun fromVector(dq: Int, dr: Int): LatticeAxis? {
            if (dq == 0 && dr == 0) return null
            return when {
                dr == 0 -> HORIZONTAL
                dq == 0 -> DIAGONAL_RIGHT
                dq + dr == 0 -> DIAGONAL_LEFT
                else -> null
            }
        }
    }
}
