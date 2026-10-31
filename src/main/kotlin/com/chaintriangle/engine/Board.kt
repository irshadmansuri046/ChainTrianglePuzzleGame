package com.chaintriangle.engine

import com.chaintriangle.model.Edge
import com.chaintriangle.model.Line4Peg
import com.chaintriangle.model.Peg
import com.chaintriangle.model.Triangle
import com.chaintriangle.model.TriangleOrientation

/**
 * Represents an equilateral triangular board of size [size] (N pegs per outer side).
 * Precomputes all valid pegs, primitive edges, unit triangles, and edge-to-triangle lookup mappings.
 *
 * @param size Side length N (number of pegs along each outer edge of the triangle board).
 *             Must be at least 3 to support 4-peg straight lines.
 */
class Board(val size: Int) {

    init {
        require(size >= 3) { "Board size must be at least 3, but was $size." }
    }

    /**
     * Precomputed set of all valid peg coordinates (q, r) belonging to this board.
     * Axial constraint: q >= 0, r >= 0, q + r < size.
     */
    val validPegs: Set<Peg> = computeValidPegs(size)

    /**
     * Precomputed set of all valid unit triangles (faces) on the board.
     * UP (Δ) triangles: basePeg (q, r) with q + r <= size - 2
     * DOWN (∇) triangles: basePeg (q, r) with q + r <= size - 3
     */
    val validTriangles: Set<Triangle> = computeValidTriangles(size)

    /**
     * Precomputed set of all unique primitive edges on the board.
     */
    val validEdges: Set<Edge> = computeValidEdges(validTriangles)

    /**
     * Mapping from each primitive edge to the set of unit triangles bounded by that edge.
     * Each edge bounds at most 2 unit triangles (one UP, one DOWN).
     */
    val edgeToTrianglesMap: Map<Edge, Set<Triangle>> = computeEdgeToTrianglesMap(validTriangles)

    companion object {
        private fun computeValidPegs(size: Int): Set<Peg> {
            val pegs = mutableSetOf<Peg>()
            for (r in 0 until size) {
                for (q in 0 until (size - r)) {
                    pegs.add(Peg(q, r))
                }
            }
            return pegs
        }

        private fun computeValidTriangles(size: Int): Set<Triangle> {
            val triangles = mutableSetOf<Triangle>()
            // Precompute UP (Δ) triangles
            for (r in 0 until (size - 1)) {
                for (q in 0 until (size - 1 - r)) {
                    triangles.add(Triangle(Peg(q, r), TriangleOrientation.UP))
                }
            }
            // Precompute DOWN (∇) triangles
            for (r in 0 until (size - 2)) {
                for (q in 0 until (size - 2 - r)) {
                    triangles.add(Triangle(Peg(q, r), TriangleOrientation.DOWN))
                }
            }
            return triangles
        }

        private fun computeValidEdges(triangles: Set<Triangle>): Set<Edge> {
            val edges = mutableSetOf<Edge>()
            for (triangle in triangles) {
                edges.addAll(triangle.edges)
            }
            return edges
        }

        private fun computeEdgeToTrianglesMap(triangles: Set<Triangle>): Map<Edge, Set<Triangle>> {
            val map = mutableMapOf<Edge, MutableSet<Triangle>>()
            for (triangle in triangles) {
                for (edge in triangle.edges) {
                    val set = map.getOrPut(edge) { mutableSetOf() }
                    set.add(triangle)
                }
            }
            return map
        }
    }

    /**
     * Checks if [peg] lies within board bounds.
     */
    fun containsPeg(peg: Peg): Boolean = peg.q >= 0 && peg.r >= 0 && (peg.q + peg.r) < size

    /**
     * Checks if [edge] is a valid primitive edge on this board.
     */
    fun containsEdge(edge: Edge): Boolean = containsPeg(edge.u) && containsPeg(edge.v)

    /**
     * Checks if [triangle] is a valid unit face on this board.
     */
    fun containsTriangle(triangle: Triangle): Boolean = triangle in validTriangles

    /**
     * Checks if all 4 pegs of [line] reside within board bounds.
     */
    fun containsLine(line: Line4Peg): Boolean = line.pegs.all { containsPeg(it) }

    /**
     * Total number of pegs on the board: N * (N + 1) / 2
     */
    val totalPegsCount: Int get() = validPegs.size

    /**
     * Total number of unit triangles on the board: (N - 1)^2
     */
    val totalTrianglesCount: Int get() = validTriangles.size

    override fun toString(): String = "Board(size=$size, pegs=$totalPegsCount, triangles=$totalTrianglesCount)"
}
