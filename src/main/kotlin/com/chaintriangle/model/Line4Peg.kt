package com.chaintriangle.model

import kotlin.math.abs
import kotlin.math.sign

/**
 * Represents a valid player move spanning 4 collinear pegs in a straight line,
 * consisting of 3 contiguous primitive edges along one of the 3 lattice axes.
 *
 * @property start The starting peg endpoint.
 * @property end The ending peg endpoint.
 * @property axis The lattice axis along which the line lies.
 * @property pegs The ordered list of 4 pegs from [start] to [end].
 * @property primitiveEdges The 3 contiguous primitive edges forming this line.
 */
data class Line4Peg(
    val start: Peg,
    val end: Peg,
    val axis: LatticeAxis,
    val pegs: List<Peg>,
    val primitiveEdges: List<Edge>
) {
    /**
     * The set of primitive edges belonging to this line, canonicalized as undirected edges.
     */
    val canonicalEdges: Set<Edge> = primitiveEdges.toSet()

    companion object {
        /**
         * Validates and constructs a [Line4Peg] from endpoints [start] and [end].
         *
         * @return A valid [Line4Peg] or `null` if the endpoints do not form a valid 4-peg line.
         */
        fun ofOrNull(start: Peg, end: Peg): Line4Peg? {
            val dq = end.q - start.q
            val dr = end.r - start.r

            val axis = LatticeAxis.fromVector(dq, dr) ?: return null

            // Calculate grid distance along lattice step units
            val distance = (abs(dq) + abs(dr) + abs(dq + dr)) / 2
            if (distance != 3) return null

            val stepQ = dq.sign
            val stepR = dr.sign

            val linePegs = (0..3).map { i ->
                Peg(start.q + i * stepQ, start.r + i * stepR)
            }

            val edges = (0..2).map { i ->
                Edge.of(linePegs[i], linePegs[i + 1])
            }

            return Line4Peg(start, end, axis, linePegs, edges)
        }

        /**
         * Validates and constructs a [Line4Peg] from endpoints [start] and [end].
         *
         * @throws IllegalArgumentException if the endpoints do not form a valid 4-peg line.
         */
        fun of(start: Peg, end: Peg): Line4Peg {
            return ofOrNull(start, end)
                ?: throw IllegalArgumentException("Endpoints $start and $end do not form a valid 4-peg line.")
        }
    }

    override fun toString(): String = "Line4Peg(start=$start, end=$end, axis=$axis, pegs=$pegs)"
}
