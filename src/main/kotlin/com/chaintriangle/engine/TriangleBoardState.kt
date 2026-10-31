package com.chaintriangle.engine

import com.chaintriangle.model.Edge
import com.chaintriangle.model.Line4Peg
import com.chaintriangle.model.Peg
import com.chaintriangle.model.Triangle

/**
 * Manages game board state, claimed primitive edges, completed triangles, and real-time triangle detection.
 *
 * @property board The underlying precomputed board model.
 */
class TriangleBoardState(val board: Board) {

    /** Secondary constructor initializing board by size N. */
    constructor(size: Int) : this(Board(size))

    private val _claimedEdges = mutableSetOf<Edge>()
    private val _completedTriangles = mutableSetOf<Triangle>()

    /**
     * Read-only view of all currently claimed primitive edges on the board.
     */
    val claimedEdges: Set<Edge> get() = _claimedEdges

    /**
     * Read-only view of all currently completed unit triangles on the board.
     */
    val completedTriangles: Set<Triangle> get() = _completedTriangles

    /**
     * Commits a 4-peg straight line move to the board and detects any newly completed unit triangles.
     *
     * @param line The 4-peg line to commit.
     * @return [CommitResult] containing newly claimed edges and newly completed triangles.
     * @throws IllegalArgumentException if [line] is invalid or extends out of board bounds.
     */
    fun commitLine(line: Line4Peg): CommitResult {
        require(board.containsLine(line)) {
            "Line $line extends beyond board boundaries for size ${board.size}."
        }

        val newlyClaimedEdges = mutableSetOf<Edge>()
        for (edge in line.primitiveEdges) {
            if (_claimedEdges.add(edge)) {
                newlyClaimedEdges.add(edge)
            }
        }

        // Candidate triangles are those containing at least one of the primitive edges in the move
        val candidates = line.primitiveEdges
            .flatMap { board.edgeToTrianglesMap[it] ?: emptySet() }
            .toSet()

        val newlyCompletedTriangles = mutableSetOf<Triangle>()
        for (triangle in candidates) {
            if (triangle !in _completedTriangles && _claimedEdges.containsAll(triangle.edges)) {
                _completedTriangles.add(triangle)
                newlyCompletedTriangles.add(triangle)
            }
        }

        return CommitResult(
            line = line,
            newlyClaimedEdges = newlyClaimedEdges,
            newlyCompletedTriangles = newlyCompletedTriangles,
            totalCompletedTriangles = _completedTriangles.size
        )
    }

    /**
     * Convenience method to validate and commit a 4-peg line move between [start] and [end].
     *
     * @throws IllegalArgumentException if endpoints do not form a valid 4-peg line on this board.
     */
    fun commitMove(start: Peg, end: Peg): CommitResult {
        val validation = LineValidator.validate(start, end, board)
        require(validation is LineValidationResult.Valid) {
            val reason = (validation as LineValidationResult.Invalid).reason
            "Invalid move from $start to $end: $reason"
        }
        return commitLine(validation.line)
    }

    /**
     * Checks if a primitive edge is currently claimed.
     */
    fun isEdgeClaimed(edge: Edge): Boolean = edge in _claimedEdges

    /**
     * Checks if a unit triangle is currently completed.
     */
    fun isTriangleCompleted(triangle: Triangle): Boolean = triangle in _completedTriangles

    /**
     * Returns the set of all unit triangles on the board that remain uncompleted.
     */
    fun getUncompletedTriangles(): Set<Triangle> = board.validTriangles - _completedTriangles

    /**
     * Checks if all unit triangles on the board are completed (game over condition).
     */
    fun isBoardFullyCompleted(): Boolean = _completedTriangles.size == board.totalTrianglesCount

    /**
     * Resets the board state, clearing all claimed edges and completed triangles.
     */
    fun reset() {
        _claimedEdges.clear()
        _completedTriangles.clear()
    }
}
