/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.utils

import model.entities.Direction
import model.map.Maze
import model.utils.Position.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class PositionTest:

  private given Maze = Maze.generate(3)

  @Test
  def testApplyAndAccessors(): Unit =
    val p = Position(1, 2)
    assertEquals(1, p.row, "Row should return the first coordinate")
    assertEquals(2, p.col, "Col should return the second coordinate")

  @Test
  def testEqualityAndHashCode(): Unit =
    val p1 = Position(0, 0)
    val p2 = Position(0, 0)
    assertEquals(p1, p2, "Positions with same coords should be equal")
    assertEquals(p1.hashCode(), p2.hashCode(), "Equal positions should have same hashCode")

  @Test
  def testMove(): Unit =
    val origin = Position(1, 1)
    val moved = origin.move(1, -1)
    assertEquals(Position(2, 0), moved)

  @Test
  def testIsAdjacent(): Unit =
    val p = Position(1, 1)
    assertTrue(p.isAdjacent(Position(2, 1)))
    assertTrue(p.isAdjacent(Position(0, 1)))
    assertTrue(p.isAdjacent(Position(1, 2)))
    assertTrue(p.isAdjacent(Position(1, 0)))
    assertFalse(p.isAdjacent(Position(2, 2)))

  @Test
  def testAdjacentPositionsWithinBounds(): Unit =
    val center = Position(1, 1)
    val adj = adjacentPositions(center)
    val expected = Set(
      Position(0, 1),
      Position(2, 1),
      Position(1, 0),
      Position(1, 2)
    )
    assertEquals(expected, adj.toSet)

  @Test
  def testAdjacentPositionsAtEdge(): Unit =
    val corner = Position(0, 0)
    val adj = adjacentPositions(corner)
    val expected = Set(
      Position(1, 0),
      Position(0, 1)
    )
    assertEquals(expected, adj.toSet)

  @Test
  def testIsAdjacentTwoArgs(): Unit =
    val p1 = Position(0, 1)
    val p2 = Position(1, 1)
    assertTrue(Position.isAdjacentTwoArgs(p1, p2))
    assertFalse(Position.isAdjacentTwoArgs(p1, Position(2, 2)))

  @Test
  def testDirectionBetween(): Unit =
    val p = Position(1, 1)
    assertEquals(Some(Direction.Right), p.directionBetween(Position(2, 1)))
    assertEquals(Some(Direction.Left), p.directionBetween(Position(0, 1)))
    assertEquals(Some(Direction.Up), p.directionBetween(Position(1, 2)))
    assertEquals(Some(Direction.Down), p.directionBetween(Position(1, 0)))
    assertEquals(None, p.directionBetween(Position(2, 2)))

  @Test
  def testShowAndUnapply(): Unit =
    val p = Position(2, 1)
    assertEquals("Position(2, 1)", p.show)
    p match
      case Position(row, col) =>
        assertEquals(2, row)
        assertEquals(1, col)
      case _ =>
        fail("unapply should extract (row, col)")



