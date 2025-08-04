/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.entities

import model.entities.PlayerError.{CannotUseItem, InventoryFull, ItemNotFound, NoLivesLeft}
import model.utils.{Inventory, Item, Slots, Usable}
import model.utils.Position.*

/**
 * Represents the direction where the player wants to move.
 */
enum Direction(val x: Int, val y: Int):
  case Up extends Direction(0, 1)
  case Down extends Direction(0, -1)
  case Right extends Direction(1, 0)
  case Left extends Direction(-1, 0)

/**
 * Represents errors associated with player entity.
 */
sealed trait PlayerError

object PlayerError:
  case object NoLivesLeft extends PlayerError
  case class NegativeScore(invalidScore: Int) extends PlayerError
  case class ItemNotFound(itemName: String) extends PlayerError
  case class CannotUseItem(itemName: String, reason: String) extends PlayerError
  case class InventoryFull(itemName: String, requested: Int) extends PlayerError
  case class Unexpected(errorMessage: String) extends PlayerError

type Result[A] = Either[PlayerError, A] 

/**
 * Represents the entity controlled by the user.
 */
trait Player extends Entity:

  /**
   * Provides user's remaining number of lives.
   * 
   * @return number of lives.
   */
  def lives: Int

  /**
   * The score associated with the player.
   * 
   * @return the score of the user.
   */
  def score: Int

  /**
   * Brings the player in a new position.
   * 
   * @param direction the direction of the move.
   * @return updated instance of the player.
   */
  def move(direction: Direction): Player

  /**
   * Decreases user's lives.
   * 
   * @return updated instance of the player.
   */
  def loseLife(): Result[Player]
  
  /**
   * Updates the score of the player.
   * 
   * @param points the points to add to the score.
   * @return updated instance of the player.
   */
  def addScore(points: Int): Result[Player]

  /**
   * Provides the current inventory.
   */
  def inventory: Inventory

  /**
   * Provides a copy of the player with the inventory replaced.
   */
  def withInventory(inv: Inventory): Player

  /**
   * Uses a specific item.
   *
   * @param item the item to use.
   * @param slots given parameter of type Slots[I].
   * @param usable given parameter of type Usable[I].
   * @tparam I a subtype of Item.
   * @return a new player instance if he can use the item,
   *         an error message otherwise.
   */
  def useItem[I <: Item](item: I)
                        (using slots: Slots[I])
                        (using usable: Usable[I]): Result[Player]

  /**
   * Records a temporary status for some turns.
   *
   * @param name the name of the status.
   * @param duration the number of turns.
   * @return a new instance of player.
   */
  def addStatus(name: String, duration: Int): Player

  /**
   * Decrements each status by one and removes those with zero turns.
   *
   * @return a new instance of player.
   */
  def decreaseStatusEffects(): Player

  /**
   * Checks if a status is still active.
   *
   * @param name the name of the status.
   * @return true if the status is active, false otherwise.
   */
  def hasStatus(name: String): Boolean

  /**
   * Takes an item.
   *
   * @param item the item to pick up.
   * @param quantity the quantity of the item.
   * @param slots given parameter of type Slots[I].
   * @tparam I a subtype of Item.
   * @return a new instance of player if he can pick up the item,
   *         an error message otherwise.
   */
  def pickUp[I <: Item](item: I, quantity: Int = 1)
                       (using slots: Slots[I]): Result[Player]

  /**
   * Checks if the inventory is full.
   *
   * @return true if the inventory is full, false otherwise.
   */
  def isInventoryFull: Boolean

/**
 * The companion object of player.
 */
object Player:

  /**
   * Generates a new instance of player.
   *
   * @param initialPosition player's starting position.
   * @param initialLives player's initial lives.
   * @param initialScore player's initial score.
   * @return new instance of player.
   */
  def apply(initialPosition: Position, initialLives: Int, initialScore: Int): Player =
    PlayerImpl(
      initialPosition,
      initialLives,
      initialScore,
      inventory = Inventory(capacity = 3),
      statusEffects = Map.empty)

private[entities] case class PlayerImpl(position: Position,
                                        lives: Int,
                                        score: Int,
                                        inventory: Inventory,
                                        statusEffects: Map[String, Int]) extends Player:
  override def move(direction: Direction): Player =
    copy(position = position.move(direction.x, direction.y))
  override def loseLife(): Result[Player] =
    if lives <= 0 then Left(NoLivesLeft)
    else Right(copy(lives = lives - 1))
  override def addScore(points: Int): Result[Player] =
    val newScore = score + points
    if newScore < 0 then
      Right(copy(score = 0))
    else Right(copy(score = newScore))
  override def withInventory(inv: Inventory): Player = copy(inventory = inv)
  override def useItem[I <: Item](item: I)
                                 (using slots: Slots[I])
                                 (using usable: Usable[I]): Result[Player] =
    if !inventory.has(item) then
      Left(ItemNotFound(item.name))
    else
      inventory.remove(item) match
        case Left(error) => Left(CannotUseItem(item.name, error.toString))
        case Right(newInv) =>
          val updatedPlayer = usable.use(item, this)
          Right(updatedPlayer.withInventory(newInv))
  override def addStatus(name: String, duration: Int): Player =
    copy(statusEffects = statusEffects + (name -> duration))
  override def decreaseStatusEffects(): Player =
    val updatedStatus = statusEffects.view.mapValues(_ - 1).toMap
    copy(statusEffects = updatedStatus.filter(_._2 > 0))
  override def hasStatus(name: String): Boolean = statusEffects.contains(name)
  override def pickUp[I <: Item](item: I, quantity: Int)
                                (using slots: Slots[I]): Result[Player] =
    inventory.add(item, quantity) match
      case Left(error) => Left(InventoryFull(item.name, quantity))
      case Right(newInv) => Right(copy(inventory = newInv))
  override def isInventoryFull: Boolean = inventory.isFull