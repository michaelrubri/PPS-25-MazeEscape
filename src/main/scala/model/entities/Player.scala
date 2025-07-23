/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.entities

import model.utils.Position

/**
 * Represents the direction where the player wants to move.
 */
enum Direction(val dx: Int, val dy: Int):
  case Up extends Direction(0, 1)
  case Down extends Direction(0, -1)
  case Right extends Direction(1, 0)
  case Left extends Direction(-1, 0)

/**
 * ADT to represent errors associated with PLayer entity.
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

object Player:
  def apply(initialPosition: Position, initialLives: Int, initialScore: Int): Player =
    PlayerImpl(initialPosition, initialLives, initialScore)

private[entities] case class PlayerImpl(position: Position,
                                        lives: Int,
                                        score: Int) extends Player:
  override def move(direction: Direction): Player = copy(position = position.move(direction.dx, direction.dy))
  override def loseLife(): Result[Player] =
    if lives <= 0 then Left(PlayerError.NoLivesLeft)
    else Right(copy(lives = lives - 1))
  override def addScore(points: Int): Result[Player] =
    val newScore = score + points
    if newScore < 0 then Left(PlayerError.NegativeScore(points))
    else Right(copy(score = newScore))