package com.chaintriangle.engine

import com.chaintriangle.model.Edge
import com.chaintriangle.model.Line4Peg
import com.chaintriangle.model.Triangle

/**
 * Result data payload emitted when a player move (4-peg line) is committed to the board state.
 *
 * @property line The 4-peg line committed.
 * @property newlyClaimedEdges Edges in the move that were not previously claimed.
 * @property newlyCompletedTriangles Unit triangles that completed as a direct result of this move.
 * @property totalCompletedTriangles Total number of completed triangles on the board after this move.
 */
data class CommitResult(
    val line: Line4Peg,
    val newlyClaimedEdges: Set<Edge>,
    val newlyCompletedTriangles: Set<Triangle>,
    val totalCompletedTriangles: Int
) {
    /**
     * Whether this move resulted in closing at least one triangle (allowing the player to take another turn or gain points).
     */
    val closedTriangles: Boolean get() = newlyCompletedTriangles.isNotEmpty()

    /**
     * Number of triangles closed by this single move.
     */
    val closedTrianglesCount: Int get() = newlyCompletedTriangles.size
}
