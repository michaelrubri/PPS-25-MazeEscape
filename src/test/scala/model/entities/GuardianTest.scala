/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.entities

import model.utils.Position
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import scala.compiletime.uninitialized

class GuardianTest:

  var guardian: Guardian = uninitialized

  @BeforeEach
  def init(): Unit = guardian = Guardian(Position(0, 0))

  @Test
  def testInitialValues(): Unit = assertEquals(Position(0, 0), guardian.position)

  @Test
  def testGuardianAdjacentPlayerNotMove(): Unit =
    val adjacentHorizontally = guardian.intercept(Position(1, 0))
    assertEquals(Position(0, 0), adjacentHorizontally,"Guardian should not move")
    val adjacentVertically = guardian.intercept(Position(0, 1))
    assertEquals((0, 0), adjacentVertically, "Guardian should not move")

  @Test
  def testInterceptHorizontally(): Unit =
    val moveRight = guardian.intercept(Position(3, 0))
    assertEquals(Position(1, 0), moveRight, "Guardian should move to the right")
    val moveLeft = guardian.intercept(Position(-3, 0))
    assertEquals(Position(-1, 0), moveLeft, "Guardian should move to the left")

  @Test
  def testInterceptVertically(): Unit =
    val moveUp = guardian.intercept(Position(0, 3))
    assertEquals(Position(0, 1), moveUp, "Guardian should move to the top")
    val moveDown = guardian.intercept(Position(0, -3))
    assertEquals(Position(0, -1), moveDown, "Guardian should move to the bottom")

  @Test
  def testUpdatePosition(): Unit =
    val position = Position(2, 2)
    guardian.updatePosition(position)
    assertEquals(position, guardian.position, "The guardian should be in the new position")