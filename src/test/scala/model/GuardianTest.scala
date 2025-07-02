/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import org.junit.*
import org.junit.Assert.*
import scala.compiletime.uninitialized

class GuardianTest:

  var guardian: Guardian = uninitialized

  @Before
  def init(): Unit =
    guardian = Guardian((0, 0))

  @Test
  def testInitialValues(): Unit =
    assertEquals((0, 0), guardian.position)

  @Test
  def testIntercept(): Unit =
    guardian.intercept((4, 4))
    assertEquals((0, 1), guardian.position)
    guardian.intercept((5, 4))
    assertEquals((1, 1), guardian.position)
    guardian.intercept((5, 5))
    assertEquals((1, 2), guardian.position)
    guardian.intercept((5, 4))
    assertEquals((2, 2), guardian.position)
    guardian.intercept((6, 4))
    assertEquals((3, 2), guardian.position)