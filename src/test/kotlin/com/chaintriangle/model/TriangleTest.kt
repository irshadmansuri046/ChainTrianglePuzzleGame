package com.chaintriangle.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TriangleTest {

    @Test
    fun `test up pointing delta triangle properties`() {
        val base = Peg(1, 1)
        val triangle = Triangle(base, TriangleOrientation.UP)

        val expectedPegs = setOf(Peg(1, 1), Peg(2, 1), Peg(1, 2))
        assertEquals(expectedPegs, triangle.pegs)

        val expectedEdges = setOf(
            Edge.of(Peg(1, 1), Peg(2, 1)),
            Edge.of(Peg(1, 1), Peg(1, 2)),
            Edge.of(Peg(2, 1), Peg(1, 2))
        )
        assertEquals(expectedEdges, triangle.edges)
    }

    @Test
    fun `test down pointing nabla triangle properties`() {
        val base = Peg(1, 1)
        val triangle = Triangle(base, TriangleOrientation.DOWN)

        val expectedPegs = setOf(Peg(2, 1), Peg(1, 2), Peg(2, 2))
        assertEquals(expectedPegs, triangle.pegs)

        val expectedEdges = setOf(
            Edge.of(Peg(2, 1), Peg(2, 2)),
            Edge.of(Peg(1, 2), Peg(2, 2)),
            Edge.of(Peg(2, 1), Peg(1, 2))
        )
        assertEquals(expectedEdges, triangle.edges)
    }

    @Test
    fun `test shared diagonal left edge between up and down triangles at same base peg`() {
        val base = Peg(0, 0)
        val up = Triangle(base, TriangleOrientation.UP)
        val down = Triangle(base, TriangleOrientation.DOWN)

        val sharedEdge = Edge.of(Peg(1, 0), Peg(0, 1))

        assertTrue(up.edges.contains(sharedEdge))
        assertTrue(down.edges.contains(sharedEdge))
    }
}
