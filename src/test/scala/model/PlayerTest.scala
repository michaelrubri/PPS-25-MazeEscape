/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import scala.compiletime.uninitialized

class PlayerTest:
  
  var player: Player = uninitialized
  
  @BeforeEach
  def init(): Unit = player = Player((0, 0), 3, 0)

  @Test
  def testInitialValues(): Unit =
    assertEquals((0, 0), player.position)
    assertEquals(3, player.lives)
    assertEquals(0, player.score)

  @Test
  def testMove(): Unit =
    player.move(Direction.Up)
    assertEquals((0, 1), player.position, "Player should move to the top")
    player.move(Direction.Right)
    assertEquals((1, 1), player.position, "Player should move to the right")
    player.move(Direction.Left)
    assertEquals((0, 1), player.position, "Player should move to the left")
    player.move(Direction.Down)
    assertEquals((0, 0), player.position, "Player should move to the bottom")

  @Test
  def testLoseLife(): Unit =
    player.loseLife()
    assertEquals(2, player.lives, "Player should lose a life")
    player.loseLife()
    assertEquals(1, player.lives, "Player should lose a life")

  @Test
  def testAddScore(): Unit =
    player.addScore(10)
    assertEquals(10, player.score, "Score should be added")
    player.addScore(5)
    assertEquals(15, player.score, "Score should be added")