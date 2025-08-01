/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.entities

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
   * Uses a specific item.
   *
   * @param item the item to use.
   * @param slots given parameter of type Slots[T].
   * @param usable given parameter of type Usable[T].
   * @tparam T a subtype of Item.
   * @return a new player instance if he can use the item,
   *         an error message otherwise.
   */
  def useItem[T <: Item](item: T)
                        (using slots: Slots[T])
                        (using usable: Usable[T]): Either[String, Player]

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
      inventory = Inventory(capacity = 5),
      statusEffects = Map.empty)

private[entities] case class PlayerImpl(position: Position,
                                        lives: Int,
                                        score: Int,
                                        inventory: Inventory,
                                        statusEffects: Map[String, Int]) extends Player:
  override def move(direction: Direction): Player =
    copy(position = position.move(direction.x, direction.y))
  override def loseLife(): Result[Player] =
    if lives <= 0 then Left(PlayerError.NoLivesLeft)
    else Right(copy(lives = lives - 1))
  override def addScore(points: Int): Result[Player] =
    val newScore = score + points
    if newScore < 0 then
      copy(score = 0)
      Left(PlayerError.NegativeScore(points))
    else Right(copy(score = newScore))
  override def useItem[T <: Item](item: T)
                                 (using slots: Slots[T])
                                 (using usable: Usable[T]): Either[String, Player] =
    if !inventory.has(item) then
      Left(s"Item ${item.name} not available")
    else
      inventory.remove(item) match
        case Left(error) => Left(error)
        case Right(newInv) =>
          val updatedPlayer = usable.use(item, this)
          Right(updatedPlayer.asInstanceOf[PlayerImpl].copy(inventory = newInv))
  override def addStatus(name: String, duration: Int): Player =
    copy(statusEffects = statusEffects + (name -> duration))
  override def decreaseStatusEffects(): Player =
    val updatedStatus = statusEffects.view.mapValues(_ - 1).toMap
    copy(statusEffects = updatedStatus.filter(_._2 > 0))
  override def hasStatus(name: String): Boolean = statusEffects.contains(name)  