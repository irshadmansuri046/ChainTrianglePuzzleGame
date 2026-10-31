package com.chaintriangle.engine

import com.chaintriangle.model.Peg
import com.chaintriangle.model.Triangle
import com.chaintriangle.model.TriangleOrientation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TriangleDetectionTest {

    private lateinit var state: TriangleBoardState

    @BeforeEach
    fun setUp() {
        state = TriangleBoardState(8) // Size N = 8 board (36 pegs, 49 triangles)
    }

    @Test
    fun `test initial board state is empty`() {
        assertEquals(0, state.claimedEdges.size)
        assertEquals(0, state.completedTriangles.size)
        assertEquals(49, state.getUncompletedTriangles().size)
        assertFalse(state.isBoardFullyCompleted())
    }

    @Test
    fun `test single move with zero completed triangles`() {
        val result = state.commitMove(Peg(1, 0), Peg(4, 0))

        assertEquals(3, result.newlyClaimedEdges.size)
        assertEquals(0, result.newlyCompletedTriangles.size)
        assertFalse(result.closedTriangles)
        assertEquals(0, result.closedTrianglesCount)
        assertEquals(0, state.completedTriangles.size)
    }

    @Test
    fun `test single triangle completion`() {
        // Target: UP Triangle at (3,0)
        // Edges: (3,0)<->(4,0) [Horizontal], (3,0)<->(3,1) [Diagonal Right], (4,0)<->(3,1) [Diagonal Left]

        // Move 1: Horizontal line (1,0) -> (4,0)
        state.commitMove(Peg(1, 0), Peg(4, 0))

        // Move 2: Diagonal Right line (3,0) -> (3,3)
        state.commitMove(Peg(3, 0), Peg(3, 3))

        // Move 3: Diagonal Left line (4,0) -> (1,3)
        val result = state.commitMove(Peg(4, 0), Peg(1, 3))

        assertTrue(result.closedTriangles)
        assertEquals(1, result.closedTrianglesCount)

        val expectedTriangle = Triangle(Peg(3, 0), TriangleOrientation.UP)
        assertEquals(setOf(expectedTriangle), result.newlyCompletedTriangles)
        assertTrue(state.isTriangleCompleted(expectedTriangle))
        assertEquals(1, state.completedTriangles.size)
    }

    @Test
    fun `test simultaneous multi-triangle closure of 2 triangles in a single move`() {
        // Targets:
        // UP Triangle at (3,0): edges (3,0)<->(4,0), (3,0)<->(3,1), (4,0)<->(3,1)
        // DOWN Triangle at (3,0): edges (4,0)<->(4,1), (3,1)<->(4,1), (4,0)<->(3,1)
        // Shared edge: (4,0)<->(3,1) on Diagonal Left line (4,0) -> (1,3)

        // Pre-claim non-shared edges of UP(3,0):
        state.commitMove(Peg(1, 0), Peg(4, 0)) // Horizontal r=0
        state.commitMove(Peg(3, 0), Peg(3, 3)) // Diagonal Right q=3

        // Pre-claim non-shared edges of DOWN(3,0):
        state.commitMove(Peg(4, 0), Peg(4, 3)) // Diagonal Right q=4
        state.commitMove(Peg(1, 1), Peg(4, 1)) // Horizontal r=1

        // Verify 0 triangles closed so far
        assertEquals(0, state.completedTriangles.size)

        // Commit Diagonal Left line (4,0) -> (1,3) which claims shared edge (4,0)<->(3,1)
        val result = state.commitMove(Peg(4, 0), Peg(1, 3))

        // Both UP(3,0) and DOWN(3,0) should complete SIMULTANEOUSLY!
        assertTrue(result.closedTriangles)
        assertEquals(2, result.closedTrianglesCount)

        val upTri = Triangle(Peg(3, 0), TriangleOrientation.UP)
        val downTri = Triangle(Peg(3, 0), TriangleOrientation.DOWN)

        assertEquals(setOf(upTri, downTri), result.newlyCompletedTriangles)
        assertTrue(state.isTriangleCompleted(upTri))
        assertTrue(state.isTriangleCompleted(downTri))
        assertEquals(2, state.completedTriangles.size)
    }

    @Test
    fun `test simultaneous multi-triangle closure of 3 triangles in a single move`() {
        // Targets:
        // 1. UP(3,0): edges (3,0)<->(4,0), (3,0)<->(3,1), (4,0)<->(3,1)
        // 2. DOWN(3,0): edges (4,0)<->(4,1), (3,1)<->(4,1), (4,0)<->(3,1)
        // 3. UP(2,1): edges (2,1)<->(3,1), (2,1)<->(2,2), (3,1)<->(2,2)

        // Line L3 (4,0)->(1,3) contains BOTH (4,0)<->(3,1) AND (3,1)<->(2,2)!

        // Pre-claim edges:
        state.commitMove(Peg(1, 0), Peg(4, 0)) // L1: Horizontal r=0 -> claims (3,0)<->(4,0)
        state.commitMove(Peg(3, 0), Peg(3, 3)) // L2: Diagonal Right q=3 -> claims (3,0)<->(3,1)
        state.commitMove(Peg(4, 0), Peg(4, 3)) // L4: Diagonal Right q=4 -> claims (4,0)<->(4,1)
        state.commitMove(Peg(1, 1), Peg(4, 1)) // L5: Horizontal r=1 -> claims (3,1)<->(4,1) AND (2,1)<->(3,1)
        state.commitMove(Peg(2, 0), Peg(2, 3)) // L6: Diagonal Right q=2 -> claims (2,1)<->(2,2)

        // Verify 0 triangles closed so far
        assertEquals(0, state.completedTriangles.size)

        // Commit Diagonal Left line L3: (4,0) -> (1,3)
        val result = state.commitMove(Peg(4, 0), Peg(1, 3))

        // All 3 triangles should complete SIMULTANEOUSLY!
        assertTrue(result.closedTriangles)
        assertEquals(3, result.closedTrianglesCount)

        val tri1 = Triangle(Peg(3, 0), TriangleOrientation.UP)
        val tri2 = Triangle(Peg(3, 0), TriangleOrientation.DOWN)
        val tri3 = Triangle(Peg(2, 1), TriangleOrientation.UP)

        assertEquals(setOf(tri1, tri2, tri3), result.newlyCompletedTriangles)
        assertTrue(state.isTriangleCompleted(tri1))
        assertTrue(state.isTriangleCompleted(tri2))
        assertTrue(state.isTriangleCompleted(tri3))
        assertEquals(3, state.completedTriangles.size)
    }

    @Test
    fun `test reset clears all state`() {
        state.commitMove(Peg(1, 0), Peg(4, 0))
        assertTrue(state.claimedEdges.isNotEmpty())

        state.reset()
        assertEquals(0, state.claimedEdges.size)
        assertEquals(0, state.completedTriangles.size)
    }
}
