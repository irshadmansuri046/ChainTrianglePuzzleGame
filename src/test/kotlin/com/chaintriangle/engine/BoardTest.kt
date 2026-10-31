package com.chaintriangle.model

import com.chaintriangle.engine.Board
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BoardTest {

    @Test
    fun `test board size 3 generation`() {
        val board = Board(3)

        // N=3: Total pegs = 3 * 4 / 2 = 6
        assertEquals(6, board.totalPegsCount)
        assertEquals(6, board.validPegs.size)

        // N=3: Triangles = (3-1)^2 = 4 (3 UP, 1 DOWN)
        assertEquals(4, board.totalTrianglesCount)
        assertEquals(4, board.validTriangles.size)

        val upTriangles = board.validTriangles.filter { it.orientation == TriangleOrientation.UP }
        val downTriangles = board.validTriangles.filter { it.orientation == TriangleOrientation.DOWN }
        assertEquals(3, upTriangles.size)
        assertEquals(1, downTriangles.size)
    }

    @Test
    fun `test board size 4 precomputation`() {
        val board = Board(4)

        // N=4: Pegs = 4 * 5 / 2 = 10
        assertEquals(10, board.totalPegsCount)

        // N=4: Triangles = (4-1)^2 = 9 (6 UP, 3 DOWN)
        assertEquals(9, board.totalTrianglesCount)

        val upTriangles = board.validTriangles.filter { it.orientation == TriangleOrientation.UP }
        val downTriangles = board.validTriangles.filter { it.orientation == TriangleOrientation.DOWN }
        assertEquals(6, upTriangles.size)
        assertEquals(3, downTriangles.size)
    }

    @Test
    fun `test board size 5 precomputation`() {
        val board = Board(5)

        // N=5: Pegs = 5 * 6 / 2 = 15
        assertEquals(15, board.totalPegsCount)

        // N=5: Triangles = (5-1)^2 = 16 (10 UP, 6 DOWN)
        assertEquals(16, board.totalTrianglesCount)

        val upTriangles = board.validTriangles.filter { it.orientation == TriangleOrientation.UP }
        val downTriangles = board.validTriangles.filter { it.orientation == TriangleOrientation.DOWN }
        assertEquals(10, upTriangles.size)
        assertEquals(6, downTriangles.size)
    }

    @Test
    fun `test board size constraint validation`() {
        assertThrows(IllegalArgumentException::class.java) {
            Board(2)
        }
    }

    @Test
    fun `test contains peg and bounds check`() {
        val board = Board(4)

        assertTrue(board.containsPeg(Peg(0, 0)))
        assertTrue(board.containsPeg(Peg(3, 0)))
        assertTrue(board.containsPeg(Peg(0, 3)))
        assertTrue(board.containsPeg(Peg(1, 2)))

        assertFalse(board.containsPeg(Peg(4, 0)))
        assertFalse(board.containsPeg(Peg(2, 2))) // 2 + 2 = 4 >= 4
        assertFalse(board.containsPeg(Peg(-1, 0)))
    }

    @Test
    fun `test edge to triangle lookup mapping integrity`() {
        val board = Board(4)

        // Verify that every triangle's 3 edges are mapped back to that triangle in the lookup table
        for (triangle in board.validTriangles) {
            for (edge in triangle.edges) {
                val mappedTriangles = board.edgeToTrianglesMap[edge]
                assertNotNull(mappedTriangles)
                assertTrue(mappedTriangles!!.contains(triangle))
                assertTrue(mappedTriangles.size in 1..2)
            }
        }
    }

    private fun assertNotNull(obj: Any?) {
        assertTrue(obj != null)
    }
}
