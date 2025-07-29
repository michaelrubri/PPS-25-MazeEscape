/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.entities

import model.utils.Position
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import scala.compiletime.uninitialized

class PlayerTest:
  
  var player: Player = uninitialized
  
  @BeforeEach
  def init(): Unit = player = Player(Position(0, 0), 1, 0)

  @Test
  def testInitialValues(): Unit =
    assertEquals(Position(0, 0), player.position)
    assertEquals(1, player.lives)
    assertEquals(0, player.score)

  @Test
  def testMove(): Unit =
    val up = player.move(Direction.Up)
    assertEquals(Position(0, 1), up.position, "Player should move to the top")
    val down = player.move(Direction.Down)
    assertEquals(Position(0, -1), down.position, "Player should move to the bottom")
    val right = player.move(Direction.Right)
    assertEquals(Position(1, 0), right.position, "Player should move to the right")
    val left = player.move(Direction.Left)
    assertEquals(Position(-1, 0), left.position, "Player should move to the left")

  @Test
  def testValidLoseLife(): Unit =
    val result = player.loseLife()
    assertTrue(result.isRight)
    val updatedPlayer = result.getOrElse(fail("Expected Right"))
    assertEquals(0, updatedPlayer.lives, "Player should lose a life")

  @Test
  def testInvalidLoseLife(): Unit =
    val updatedPlayer = player.loseLife().getOrElse(fail("Expected Right"))
    assertEquals(0, updatedPlayer.lives)
    val result = updatedPlayer.loseLife()
    assertTrue(result.isLeft)
    result match
      case Left(PlayerError.NoLivesLeft) => ()
      case other                         => fail(s"Unexpected error: $other")

  @Test
  def testValidAddScore(): Unit =
    val result = player.addScore(50)
    assertTrue(result.isRight)
    val updatedPlayer = result.getOrElse(fail("Expected Right"))
    assertEquals(50, updatedPlayer.score, "The player's score should be increased")

  @Test
  def testInvalidAddScore(): Unit =
    val result = player.addScore(-10)
    assertTrue(result.isLeft)
    result match
      case Left(PlayerError.NegativeScore(invalid)) =>
        assertEquals(-10, invalid)
      case other => fail(s"Errore inatteso: $other")