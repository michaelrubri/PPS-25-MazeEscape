/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.entities

import model.utils._
import model.utils.Usable.given
import model.utils.Slots.given
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class PlayerTest:

  @Test
  def testInitialValues(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    assertEquals(Position(0, 0), player.position)
    assertEquals(1, player.lives)
    assertEquals(0, player.score)

  @Test
  def testMove(): Unit =
    val player = Player(Position(0, 0), 1, 0)
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
    val player = Player(Position(0, 0), 1, 0)
    val result = player.loseLife()
    assertTrue(result.isRight)
    val updatedPlayer = result.getOrElse(fail("Expected Right"))
    assertEquals(0, updatedPlayer.lives, "Player should lose a life")

  @Test
  def testInvalidLoseLife(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    val updatedPlayer = player.loseLife().getOrElse(fail("Expected Right"))
    assertEquals(0, updatedPlayer.lives)
    val result = updatedPlayer.loseLife()
    assertTrue(result.isLeft)
    result match
      case Left(PlayerError.NoLivesLeft) => ()
      case other                         => fail(s"Unexpected error: $other")

  @Test
  def testValidAddScore(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    val result = player.addScore(50)
    assertTrue(result.isRight)
    val updatedPlayer = result.getOrElse(fail("Expected Right"))
    assertEquals(50, updatedPlayer.score, "The player's score should be increased")

  @Test
  def testInvalidAddScore(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    val result = player.addScore(-10)
    assertTrue(result.isRight)
    val updatedPlayer = result.getOrElse(fail("Expected Right"))
    assertEquals(0, updatedPlayer.score, "Player score should be reset to 0 when going negative")

  @Test
  def testStatusEffects(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    assertFalse(player.hasStatus("invisible"), "No status on new player")
    val withStatus = player.addStatus("invisible", 2)
    assertTrue(withStatus.hasStatus("invisible"), "Status should be active")
    val afterOne = withStatus.decreaseStatusEffects()
    val afterTwo = afterOne.decreaseStatusEffects()
    assertFalse(afterTwo.hasStatus("invisible"), "Status should be removed after 2 turns")

  @Test
  def testPickUpMakesInventoryFull(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    assertFalse(player.isInventoryFull, "Empty inventory should not be full")
    val item = SealOfTheChallenge
    player.pickUp(item, 3) match
      case Right(newPlayer) =>
        assertTrue(newPlayer.isInventoryFull, "Inventory with 3 slots should be full after picking 3 non stackable items")
      case Left(err) => fail(s"Expected to pick up items successfully but got error: $err")

  @Test
  def testPickUpExceedsCapacity(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    val item = InvisibilityPotion()
    player.pickUp(item, 16) match
      case Left(err) =>
        assertTrue(err.contains("NotEnoughSpace"), s"Error should be NotEnoughSpace, got: $err")
      case Right(_) => fail("Expected pickUp to fail due to exceeding capacity")

  @Test
  def testUseItemNotAvailable(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    val item = InvisibilityPotion()
    player.useItem(item) match
      case Left(err: String) =>
        assertTrue(err.toLowerCase.contains("not available"))
      case Right(_) =>
        fail("Expected useItem to fail when item is missing")

  @Test
  def testSuccessUseItemRemovesFromInventory(): Unit =
    val player = Player(Position(0, 0), 1, 0)
    val item = InvisibilityPotion()
    val withItem = player.pickUp(item).getOrElse(fail("pickUp failed"))
    withItem.useItem(item) match
      case Right(playerAfterUse: Player) =>
        assertFalse(playerAfterUse.inventory.has(item), "Item should be removed from inventory after use")
      case Left(err) =>
        fail(s"Expected useItem to succeed but got error: $err")