package model.utils

import model.entities.Player
import scala.util.NotGiven

/**
 * Un oggetto che può occupare slot in un inventario
 * e calcolare quanti ne occupa per una certa quantità.
 */
trait Slots[T <: Item]:
  
  /** Numero di slot occupati da `quantity` elementi di `item`. */
  def slotsOccupied(item: T, quantity: Int): Int

  /** Incremento di slot richiesti passando da existingQty a existingQty + addQty. */
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

/** Inventario immutabile con capacità a numero di slot. */
case class Inventory private (capacity: Int,
                              bag: Map[String, (Item, Int)],
                              usedSlots: Int
                             ):

  /** Costruisce un inventario vuoto con capacità predefinita. */
  // def this(capacity: Int = 10) = this(capacity, Map.empty, 0)

  /**
   * Aggiunge `quantity` unità di `item`.
   *
   * @return Right(nuova Inventory) se c'è spazio e i vincoli di stack vengono rispettati,
   *         Left(messaggio di errore) altrimenti.
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
   * Rimuove `quantity` unità di `item`.
   *
   * @return Right(nuova Inventory) se la rimozione è avvenuta,
   *         Left(messaggio di errore) altrimenti.
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
   * Controlla se ci sono almeno `quantity` unità di `item`.
   *
   * @return true se disponibili, false altrimenti.
   */
  def has(item: Item, quantity: Int = 1): Boolean =
    bag.get(item.id).exists(_._2 >= quantity)

  /** Stringa testuale dell'inventario. */
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
  /** Factory per un inventario vuoto di capacità specificata. */
  def apply(capacity: Int): Inventory =
    new Inventory(capacity, Map.empty, 0)