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
   * @param addQty quantity to add.
   * @return the updated number of slots for the item.
   */
  def neededSlots(item: T, existingQty: Int, addQty: Int): Int =
    val before = slotsOccupied(item, existingQty)
    val after = slotsOccupied(item, existingQty + addQty)
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
 * Represents the inventory as a collection of items.
 *
 * @param capacity maximum number of slots.
 * @param bag represents for each item the quantity.
 * @param usedSlots slots currently in use.
 */
case class Inventory private (capacity: Int,
                              bag: Map[String, (Item, Int)],
                              usedSlots: Int
                             ):

  /**
   * Adds a certain quantity of an item to the inventory.
   *
   * @param item the item to add.
   * @param quantity the quantity of the item.
   * @param slots given parameter of type Slots[T].
   * @param stackable given parameter of type Usable[T].
   * @tparam T a subtype of Item.
   * @return a new inventory instance if the item is successfully added,
   *         an error message otherwise.
   */
  def add[T <: Item](item: T, quantity: Int = 1)
                    (using slots: Slots[T], stackable: Stackable[T]): Either[String, Inventory] =
    if quantity <= 0 then
      Left("Quantity must be positive")
    else
      val id = item.id
      val existingQty = bag.get(id).map(_._2).getOrElse(0)
      val newSlots = slots.neededSlots(item, existingQty, quantity)
      if usedSlots + newSlots > capacity then
        Left(s"Not enough space: need $newSlots more slots, free ${capacity - usedSlots}")
      else
        val newQty = existingQty + quantity
        val newBag = bag.updated(id, (item, newQty))
        Right(copy(bag = newBag, usedSlots = usedSlots + newSlots))

  /**
   * Removes a certain quantity of an item to the inventory.
   *
   * @param item the item to remove.
   * @param quantity the quantity of the item.
   * @param slots given parameter of type Slots[T].
   * @tparam T a subtype of Item.
   * @return a new inventory instance if the item is successfully removed,
   *         an error message otherwise.
   */
  def remove[T <: Item](item: T, quantity: Int = 1)
                       (using slots: Slots[T]): Either[String, Inventory] =
    val id = item.id
    bag.get(id) match
      case None =>
        Left("Item not present in inventory")
      case Some((_, existingQty)) if existingQty < quantity =>
        Left(s"Cannot remove $quantity, only $existingQty available")
      case Some((_, existingQty)) =>
        val slotsToDelete = slots.neededSlots(item, existingQty, -quantity)
        val newQty = existingQty - quantity
        val newBag =
          if newQty > 0 then bag.updated(id, (item, newQty))
          else bag - id
        Right(copy(bag = newBag, usedSlots = usedSlots + slotsToDelete))

  /**
   * Checks that there is at least a certain quantity of an item in
   * the inventory.
   *
   * @param item the specific item.
   * @param quantity the quantity of the item.
   * @return true if there is at least that specific quantity
   *         of the item in inventory, false otherwise.
   */
  def has(item: Item, quantity: Int = 1): Boolean =
    bag.get(item.id).exists(_._2 >= quantity)

  override def toString: String =
    if bag.isEmpty then "Empty inventory"
    else
      bag.
        values.
        map { case (item, qty) =>
          s"${item.name} x$qty — ${item.description}"
        }.
        mkString("\n")

object Inventory:
  def apply(capacity: Int): Inventory =
    new Inventory(capacity, Map.empty, 0)