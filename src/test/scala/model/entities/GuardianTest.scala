/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.entities.Guardian
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

import scala.compiletime.uninitialized

class GuardianTest:

  var guardian: Guardian = uninitialized

  @BeforeEach
  def init(): Unit = guardian = Guardian((0, 0))

  @Test
  def testInitialValues(): Unit = assertEquals((0, 0), guardian.position)

  @Test
  def testGuardianAdjacentPlayerNotMove(): Unit =
    val adjacentHorizontally = guardian.intercept(1, 0)
    assertEquals((0, 0), adjacentHorizontally,"Guardian should not move")
    val adjacentVertically = guardian.intercept(0, 1)
    assertEquals((0, 0), adjacentVertically, "Guardian should not move")

  @Test
  def testInterceptHorizontally(): Unit =
    val moveRight = guardian.intercept(3, 0)
    assertEquals((1, 0), moveRight, "Guardian should move to the right")
    val moveLeft = guardian.intercept(-3, 0)
    assertEquals((-1, 0), moveLeft, "Guardian should move to the left")

  @Test
  def testInterceptVertically(): Unit =
    val moveUp = guardian.intercept(0, 3)
    assertEquals((0, 1), moveUp, "Guardian should move to the top")
    val moveDown = guardian.intercept(0, -3)
    assertEquals((0, -1), moveDown, "Guardian should move to the bottom")

  @Test
  def testUpdatePosition(): Unit =
    val position = (1,1)
    guardian.updatePosition(position)
    assertEquals(position, guardian.position, "The guardian should be in the new position")