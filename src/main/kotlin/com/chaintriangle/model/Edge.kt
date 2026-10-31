package com.chaintriangle.model

/**
 * Represents a normalized, undirected connection (edge) between two adjacent pegs.
 * Endpoints [u] and [v] are strictly canonicalized ([u] <= [v]) to eliminate direction duplicates.
 */
data class Edge(val u: Peg, val v: Peg) {

    val axis: LatticeAxis = requireNotNull(LatticeAxis.fromVector(v.q - u.q, v.r - u.r)) {
        "Edge endpoints $u and $v must lie on a valid lattice axis."
    }

    init {
        require(u <= v) { "Edge endpoints must be canonicalized such that u <= v." }
        require(u.isAdjacentTo(v)) { "Edge endpoints $u and $v must be adjacent primitive neighbors." }
    }

    companion object {
        /**
         * Factory method to create a canonicalized primitive [Edge] between two adjacent pegs.
         * Automatically orders endpoints so that `Edge.of(p1, p2) == Edge.of(p2, p1)`.
         *
         * @throws IllegalArgumentException if [p1] and [p2] are not adjacent or are identical.
         */
        fun of(p1: Peg, p2: Peg): Edge {
            val (u, v) = if (p1 <= p2) p1 to p2 else p2 to p1
            return Edge(u, v)
        }
    }

    /**
     * Checks if this edge contains the given [peg].
     */
    fun contains(peg: Peg): Boolean = u == peg || v == peg

    override fun toString(): String = "Edge($u <-> $v)"
}
