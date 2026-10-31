package com.chaintriangle.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EdgeTest {

    @Test
    fun `test edge canonicalization and equality`() {
        val p1 = Peg(0, 0)
        val p2 = Peg(1, 0)

        val edgeForward = Edge.of(p1, p2)
        val edgeReverse = Edge.of(p2, p1)

        assertEquals(edgeForward, edgeReverse)
        assertEquals(edgeForward.hashCode(), edgeReverse.hashCode())
        assertEquals(p1, edgeForward.u)
        assertEquals(p2, edgeForward.v)
    }

    @Test
    fun `test edge canonicalization across diagonal left axis`() {
        val p1 = Peg(1, 0)
        val p2 = Peg(0, 1)

        val edge = Edge.of(p1, p2)

        // Peg(0, 1) < Peg(1, 0) because 0 < 1
        assertEquals(Peg(0, 1), edge.u)
        assertEquals(Peg(1, 0), edge.v)
        assertEquals(LatticeAxis.DIAGONAL_LEFT, edge.axis)
    }

    @Test
    fun `test edge creation fails for non-adjacent pegs`() {
        val p1 = Peg(0, 0)
        val p2 = Peg(2, 0) // Distance is 2

        assertThrows(IllegalArgumentException::class.java) {
            Edge.of(p1, p2)
        }
    }

    @Test
    fun `test edge creation fails for non-collinear pegs`() {
        val p1 = Peg(0, 0)
        val p2 = Peg(1, 1) // Non-lattice vector

        assertThrows(IllegalArgumentException::class.java) {
            Edge.of(p1, p2)
        }
    }

    @Test
    fun `test edge contains peg`() {
        val p1 = Peg(1, 1)
        val p2 = Peg(1, 2)
        val edge = Edge.of(p1, p2)

        assertTrue(edge.contains(p1))
        assertTrue(edge.contains(p2))
        assertTrue(!edge.contains(Peg(0, 0)))
    }
}
