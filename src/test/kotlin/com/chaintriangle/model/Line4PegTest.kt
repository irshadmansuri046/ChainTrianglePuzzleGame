package com.chaintriangle.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class Line4PegTest {

    @Test
    fun `test horizontal 4-peg line creation and peg interpolation`() {
        val start = Peg(0, 0)
        val end = Peg(3, 0)

        val line = Line4Peg.of(start, end)

        assertEquals(LatticeAxis.HORIZONTAL, line.axis)
        assertEquals(listOf(Peg(0, 0), Peg(1, 0), Peg(2, 0), Peg(3, 0)), line.pegs)
        assertEquals(3, line.primitiveEdges.size)
        assertEquals(Edge.of(Peg(0, 0), Peg(1, 0)), line.primitiveEdges[0])
        assertEquals(Edge.of(Peg(1, 0), Peg(2, 0)), line.primitiveEdges[1])
        assertEquals(Edge.of(Peg(2, 0), Peg(3, 0)), line.primitiveEdges[2])
    }

    @Test
    fun `test reverse horizontal 4-peg line creation`() {
        val start = Peg(3, 0)
        val end = Peg(0, 0)

        val line = Line4Peg.of(start, end)

        assertEquals(LatticeAxis.HORIZONTAL, line.axis)
        assertEquals(listOf(Peg(3, 0), Peg(2, 0), Peg(1, 0), Peg(0, 0)), line.pegs)
        assertEquals(3, line.primitiveEdges.size)
    }

    @Test
    fun `test diagonal right 4-peg line creation`() {
        val start = Peg(1, 0)
        val end = Peg(1, 3)

        val line = Line4Peg.of(start, end)

        assertEquals(LatticeAxis.DIAGONAL_RIGHT, line.axis)
        assertEquals(listOf(Peg(1, 0), Peg(1, 1), Peg(1, 2), Peg(1, 3)), line.pegs)
    }

    @Test
    fun `test diagonal left 4-peg line creation`() {
        val start = Peg(3, 0)
        val end = Peg(0, 3)

        val line = Line4Peg.of(start, end)

        assertEquals(LatticeAxis.DIAGONAL_LEFT, line.axis)
        assertEquals(listOf(Peg(3, 0), Peg(2, 1), Peg(1, 2), Peg(0, 3)), line.pegs)
        assertEquals(3, line.primitiveEdges.size)
        assertEquals(Edge.of(Peg(2, 1), Peg(3, 0)), line.primitiveEdges[0])
        assertEquals(Edge.of(Peg(1, 2), Peg(2, 1)), line.primitiveEdges[1])
        assertEquals(Edge.of(Peg(0, 3), Peg(1, 2)), line.primitiveEdges[2])
    }

    @Test
    fun `test canonical primitive edge sets match regardless of line direction`() {
        val lineForward = Line4Peg.of(Peg(0, 0), Peg(3, 0))
        val lineReverse = Line4Peg.of(Peg(3, 0), Peg(0, 0))

        assertEquals(lineForward.canonicalEdges, lineReverse.canonicalEdges)
    }

    @Test
    fun `test invalid line lengths return null or throw`() {
        // Length 2 steps (3 pegs)
        assertNull(Line4Peg.ofOrNull(Peg(0, 0), Peg(2, 0)))

        // Length 4 steps (5 pegs)
        assertNull(Line4Peg.ofOrNull(Peg(0, 0), Peg(4, 0)))

        // Non-collinear endpoints
        assertNull(Line4Peg.ofOrNull(Peg(0, 0), Peg(2, 1)))

        assertThrows(IllegalArgumentException::class.java) {
            Line4Peg.of(Peg(0, 0), Peg(2, 0))
        }
    }
}
