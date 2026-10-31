package com.chaintriangle.model

/**
 * Represents a unit face (triangle) on the board bounded by 3 primitive edges.
 *
 * @property basePeg The reference peg (q, r) defining the bottom-left corner of the unit triangle cell.
 * @property orientation Whether the unit face points UP (Δ) or DOWN (∇).
 */
data class Triangle(
    val basePeg: Peg,
    val orientation: TriangleOrientation
) {
    /**
     * The set of 3 pegs forming the vertices of this unit triangle.
     */
    val pegs: Set<Peg> = when (orientation) {
        TriangleOrientation.UP -> setOf(
            basePeg,
            Peg(basePeg.q + 1, basePeg.r),
            Peg(basePeg.q, basePeg.r + 1)
        )
        TriangleOrientation.DOWN -> setOf(
            Peg(basePeg.q + 1, basePeg.r),
            Peg(basePeg.q, basePeg.r + 1),
            Peg(basePeg.q + 1, basePeg.r + 1)
        )
    }

    /**
     * The set of 3 bounding primitive edges forming this unit triangle.
     */
    val edges: Set<Edge> = when (orientation) {
        TriangleOrientation.UP -> setOf(
            Edge.of(basePeg, Peg(basePeg.q + 1, basePeg.r)),
            Edge.of(basePeg, Peg(basePeg.q, basePeg.r + 1)),
            Edge.of(Peg(basePeg.q + 1, basePeg.r), Peg(basePeg.q, basePeg.r + 1))
        )
        TriangleOrientation.DOWN -> setOf(
            Edge.of(Peg(basePeg.q + 1, basePeg.r), Peg(basePeg.q + 1, basePeg.r + 1)),
            Edge.of(Peg(basePeg.q, basePeg.r + 1), Peg(basePeg.q + 1, basePeg.r + 1)),
            Edge.of(Peg(basePeg.q + 1, basePeg.r), Peg(basePeg.q, basePeg.r + 1))
        )
    }

    override fun toString(): String {
        val symbol = if (orientation == TriangleOrientation.UP) "Δ" else "∇"
        return "Triangle$symbol(base=$basePeg, vertices=$pegs)"
    }
}
