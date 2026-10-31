package com.chaintriangle.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PegTest {

    @Test
    fun `test peg comparison ordering`() {
        val p1 = Peg(0, 0)
        val p2 = Peg(0, 1)
        val p3 = Peg(1, 0)
        val p4 = Peg(1, 1)

        assertTrue(p1 < p2)
        assertTrue(p2 < p3)
        assertTrue(p3 < p4)
        assertEquals(0, p1.compareTo(Peg(0, 0)))
    }

    @Test
    fun `test peg vector operations`() {
        val p1 = Peg(1, 2)
        val p2 = Peg(2, 3)

        assertEquals(Peg(3, 5), p1 + p2)
        assertEquals(Peg(1, 1), p2 - p1)
    }

    @Test
    fun `test grid distance calculation`() {
        val origin = Peg(0, 0)

        // Horizontal neighbors
        assertEquals(1, origin.gridDistanceTo(Peg(1, 0)))
        assertEquals(1, origin.gridDistanceTo(Peg(-1, 0)))

        // Diagonal Right neighbors
        assertEquals(1, origin.gridDistanceTo(Peg(0, 1)))
        assertEquals(1, origin.gridDistanceTo(Peg(0, -1)))

        // Diagonal Left neighbors
        assertEquals(1, origin.gridDistanceTo(Peg(1, -1)))
        assertEquals(1, origin.gridDistanceTo(Peg(-1, 1)))

        // Distance 3 along axes
        assertEquals(3, origin.gridDistanceTo(Peg(3, 0)))
        assertEquals(3, origin.gridDistanceTo(Peg(0, 3)))
        assertEquals(3, origin.gridDistanceTo(Peg(3, -3)))

        // Non-collinear distance
        assertEquals(2, origin.gridDistanceTo(Peg(1, 1)))
    }

    @Test
    fun `test adjacency check`() {
        val p = Peg(1, 1)

        assertTrue(p.isAdjacentTo(Peg(2, 1))) // Horizontal
        assertTrue(p.isAdjacentTo(Peg(1, 2))) // Diagonal Right
        assertTrue(p.isAdjacentTo(Peg(2, 0))) // Diagonal Left

        assertFalse(p.isAdjacentTo(Peg(3, 1))) // Distance 2
        assertFalse(p.isAdjacentTo(Peg(1, 1))) // Self
    }
}
