package com.chaintriangle.model

import kotlin.math.abs

/**
 * Represents a node/peg on an equilateral triangular lattice using axial coordinates (q, r).
 *
 * @property q The column index along the horizontal axis.
 * @property r The row index along the diagonal right axis.
 */
data class Peg(val q: Int, val r: Int) : Comparable<Peg> {

    /**
     * Canonical ordering: compare primary [q], then secondary [r].
     */
    override fun compareTo(other: Peg): Int {
        val qComp = this.q.compareTo(other.q)
        if (qComp != 0) return qComp
        return this.r.compareTo(other.r)
    }

    /**
     * Adds vector displacement to this peg.
     */
    operator fun plus(other: Peg): Peg = Peg(q + other.q, r + other.r)

    /**
     * Subtracts vector displacement from this peg.
     */
    operator fun minus(other: Peg): Peg = Peg(q - other.q, r - other.r)

    /**
     * Calculates the grid step distance between this peg and [other] in axial coordinates.
     * Formula: (|dq| + |dr| + |dq + dr|) / 2
     */
    fun gridDistanceTo(other: Peg): Int {
        val dq = other.q - this.q
        val dr = other.r - this.r
        return (abs(dq) + abs(dr) + abs(dq + dr)) / 2
    }

    /**
     * Checks if [other] is a direct neighbor (grid distance == 1).
     */
    fun isAdjacentTo(other: Peg): Boolean = gridDistanceTo(other) == 1

    override fun toString(): String = "($q, $r)"
}
