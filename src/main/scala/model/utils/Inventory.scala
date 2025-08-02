/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.utils

import scala.util.NotGiven

/**
 * Represents the slots of an inventory.
 */
trait Slots[T <: Item]:

  /**
   * Calculates the number of slots occupied by an item.
   *
   * @param item the specific item.
   * @param quantity the quantity of the item.
   * @return the number of slots occupied by the item.
   */
  def slotsOccupied(item: T, quantity: Int): Int

  /**
   * Calculate the number of slots needed for the item
   * considering the quantity to be added.
   *
   * @param item the specific item.
   * @param existingQty existing quantity of item in the inventory.
   * @param newQty quantity to add.
   * @return the updated number of slots for the item.
   */
  def neededSlots(item: T, existingQty: Int, newQty: Int): Int =
    val before = slotsOccupied(item, existingQty)
    val after = slotsOccupied(item, existingQty + newQty)
    after - before

object Slots:
  def apply[T <: Item](using slots: Slots[T]): Slots[T] = slots
  
  export StackableSlots.given
  export NonStackableSlots.given

  object StackableSlots:
    given [T <: Item](using stackable: Stackable[T]): Slots[T] with
      override def slotsOccupied(item: T, quantity: Int): Int =
        if quantity <= 0 then 0
        else
          val maxStack = stackable.maxStack(item)
          (quantity + maxStack - 1) / maxStack

  object NonStackableSlots:
    given [T <: Item](using NotGiven[Stackable[T]]): Slots[T] with
      override def slotsOccupied(item: T, quantity: Int): Int = quantity

/**
 * Represents errors associated with the inventory.
 */
sealed trait InventoryError[T]

object InventoryError:
  case class NotEnoughSpace[T](requiredSlots: Int, availableSlots: Int) extends InventoryError[T]
  case class ItemNotFound[T](item: T) extends InventoryError[T]
  case class InsufficientQuantity[T](item: T, required: Int, available: Int) extends InventoryError[T]
  case class QuantityNotPositive[T](quantity: Int) extends InventoryError[T]

/**
 * Represents the inventory as a collection of items.
 *
 * @param capacity maximum number of slots.
 * @param bag represents the quantity for each item.
 */
case class Inventory(capacity: Int,
                     bag: Map[Item, Int]
                    ):

  /**
   * Calculates the number of slots used based on the items
   * in the inventory.
   *
   * @return the number of slots used.
   */
  def slotsUsed: Int =
    bag.
      view.
      map { case (item, quantity) =>
        item match
          case invPot: InvisibilityPotion =>
            summon[Slots[InvisibilityPotion]].slotsOccupied(invPot, quantity)
          case sealChallenge: SealOfTheChallenge.type =>
            summon[Slots[SealOfTheChallenge.type]].slotsOccupied(sealChallenge, quantity)
      }.
      sum

  /**
   * Adds a certain quantity of an item to the inventory.
   *
   * @param item the item to add.
   * @param quantity the quantity of the item.
   * @param slots given parameter of type Slots[I].
   * @tparam I subtype of Item.
   * @return a new inventory instance if the item is successfully added,
   *         an error message otherwise.
   */
  def add[I <: Item](item: I, quantity: Int = 1)
         (using slots: Slots[I]): Either[InventoryError[I], Inventory] =
    if quantity <= 0 then Left(InventoryError.QuantityNotPositive(quantity))
    else
      val itemQty = bag.getOrElse(item, 0)
      val deltaSlots = slots.neededSlots(item, itemQty, quantity)
      if slotsUsed + deltaSlots > capacity then
        Left(InventoryError.NotEnoughSpace(deltaSlots, capacity - slotsUsed))
      else Right(copy(bag = bag.updated(item, itemQty + quantity)))

  /**
   * Removes a certain quantity of an item to the inventory.
   *
   * @param item the item to remove.
   * @param quantity the quantity of the item.
   * @param slots given parameter of type Slots[I].
   * @tparam I subtype of Item.
   * @return a new inventory instance if the item is successfully removed,
   *         an error message otherwise.
   */
  def remove[I <: Item](item: I, quantity: Int = 1)
            (using slots: Slots[I]): Either[InventoryError[I], Inventory] =
    if quantity <= 0 then Left(InventoryError.QuantityNotPositive(quantity))
    else
      bag.get(item) match
        case Some(existingQty) if existingQty >= quantity =>
          val newBag =
            if existingQty - quantity > 0 then bag.updated(item, existingQty - quantity)
            else bag - item
          Right(copy(bag = newBag))
        case Some(existingQty) => Left(InventoryError.InsufficientQuantity(item, quantity, existingQty))
        case None => Left(InventoryError.ItemNotFound(item))

  /**
   * Checks that there is at least a certain quantity of an item in
   * the inventory.
   *
   * @param item the specific item.
   * @param quantity the quantity of the item.
   * @tparam I subtype of Item.
   * @return true if there is at least that specific quantity
   *         of the item in inventory, false otherwise.
   */
  def has[I <: Item](item: I, quantity: Int = 1): Boolean =
    bag.getOrElse(item, 0) >= quantity
    
  def isFull: Boolean = slotsUsed == capacity

  override def toString: String =
    if bag.isEmpty then "Empty inventory"
    else
      bag.
        map { case (item, qty) => s"${item.name}: $qty - ${item.description}" }.
        mkString("\n")

object Inventory:
  def apply(capacity: Int): Inventory = Inventory(capacity, Map.empty)