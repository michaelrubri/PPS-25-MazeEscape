/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions._

class InventoryTest:

  import InventoryError._

  @Test
  def testAddNonStackableWithinCapacity(): Unit =
    val inv = Inventory(capacity = 5)
    inv.add(SealOfTheChallenge, quantity = 2) match {
      case Right(updatedInv) =>
        assertTrue(updatedInv.has(SealOfTheChallenge, 2))
        assertEquals(2, updatedInv.bag(SealOfTheChallenge))
        assertEquals(2, updatedInv.slotsUsed)
      case Left(err) =>
        fail(s"Expected to add non-stackable items, but got error: $err")
    }

  @Test
  def testAddNonStackableExceedCapacity(): Unit =
    val inv = Inventory(capacity = 1)
    inv.add(SealOfTheChallenge, quantity = 2) match {
      case Right(_) =>
        fail("Expected NotEnoughSpace error when adding 2 non-stackable items when capacity is 1")
      case Left(NotEnoughSpace(required, available)) =>
        assertEquals(2, required)
        assertEquals(1, available)
      case Left(err) =>
        fail(s"Unexpected error: $err")
    }

  @Test
  def testAddStackableWithinCapacity(): Unit =
    val inv = Inventory(capacity = 1)
    inv.add(InvisibilityPotion(), quantity = 3) match {
      case Right(updatedInv) =>
        assertTrue(updatedInv.has(InvisibilityPotion(), 3))
        assertEquals(3, updatedInv.bag(InvisibilityPotion()))
        assertEquals(1, updatedInv.slotsUsed)
      case Left(err) =>
        fail(s"Expected to add stackable items, but got error: $err")
    }

  @Test
  def testAddStackableExceedCapacity(): Unit =
    val inv = Inventory(capacity = 1)
    inv.add(InvisibilityPotion(), quantity = 6) match {
      case Right(_) =>
        fail("Expected NotEnoughSpace error when adding 6 potions when capacity is 1")
      case Left(NotEnoughSpace(required, available)) =>
        assertEquals(2, required)
        assertEquals(1, available)
      case Left(err) =>
        fail(s"Unexpected error: $err")
    }

  @Test
  def testRemoveSufficientQuantityAndInsufficientQuantity(): Unit =
    val inv = Inventory(capacity = 5)
      .add(SealOfTheChallenge, 2)
      .flatMap(_.add(InvisibilityPotion(), 6))
      .getOrElse(fail("Setup failure"))

    assertEquals(4, inv.slotsUsed)

    inv.remove(SealOfTheChallenge) match {
      case Right(updatedInv) =>
        assertTrue(updatedInv.has(SealOfTheChallenge))
        assertEquals(3, updatedInv.slotsUsed)
      case Left(err) =>
        fail(s"Expected successful remove, but got: $err")
    }

    inv.remove(InvisibilityPotion(), 7) match {
      case Right(_) =>
        fail("Expected InsufficientQuantity when removing more potions than present")
      case Left(InsufficientQuantity(item, req, avail)) =>
        assert(item.isInstanceOf[InvisibilityPotion])
        assertEquals(7, req)
        assertEquals(6, avail)
      case Left(err) =>
        fail(s"Unexpected error type: $err")
    }

  @Test
  def testRemoveNotFound(): Unit =
    val inv = Inventory(capacity = 3)
    inv.remove(SealOfTheChallenge) match {
      case Right(_) =>
        fail("Expected ItemNotFound when removing from empty inventory")
      case Left(ItemNotFound(item)) =>
        assertEquals(SealOfTheChallenge, item)
      case Left(err) =>
        fail(s"Unexpected error type: $err")
    }

  @Test
  def testQuantityNotPositive(): Unit =
    val inv = Inventory(capacity = 3)
    inv.add(SealOfTheChallenge, quantity = 0) match {
      case Right(_) =>
        fail("Expected QuantityNotPositive when adding zero items")
      case Left(QuantityNotPositive(quantity)) =>
        assertEquals(0, quantity)
      case Left(err) =>
        fail(s"Unexpected error type: $err")
    }

    inv.remove(SealOfTheChallenge, quantity = -1) match {
      case Right(_) =>
        fail("Expected QuantityNotPositive when removing negative items")
      case Left(QuantityNotPositive(quantity)) =>
        assertEquals(-1, quantity)
      case Left(err) =>
        fail(s"Unexpected error type: $err")
    }

  @Test
  def testHasMethod(): Unit =
    val inv = Inventory(capacity = 4)
      .add(SealOfTheChallenge)
      .flatMap(_.add(InvisibilityPotion(), 2))
      .getOrElse(fail("Setup failure"))
    assertTrue(inv.has(SealOfTheChallenge))
    assertFalse(inv.has(SealOfTheChallenge, 2))
    assertTrue(inv.has(InvisibilityPotion(), 2))
    assertFalse(inv.has(InvisibilityPotion(), 3))
    assertEquals(2, inv.slotsUsed)

  @Test
  def testAddStackableAndNonStackableWithinCapacity(): Unit =
    val inv = Inventory(capacity = 5)
    val result = for
      invWithPotions <- inv.add(InvisibilityPotion(), 9)
      invWithKeysAndPotions <- invWithPotions.add(SealOfTheChallenge, 2)
    yield invWithKeysAndPotions
    result match
      case Right(updatedInv) =>
        assertTrue(updatedInv.has(InvisibilityPotion(), 9))
        assertTrue(updatedInv.has(SealOfTheChallenge, 2))
        assertEquals(9, updatedInv.bag(InvisibilityPotion()))
        assertEquals(2, updatedInv.bag(SealOfTheChallenge))
        assertEquals(4, updatedInv.slotsUsed)
      case Left(err) =>
        fail(s"Expected to add items within capacity, but got error: $err")