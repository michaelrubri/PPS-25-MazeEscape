/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

/**
 * Represents the direction where the player wants to move.
 */

enum Direction:
  case Up, Left, Down, Right

/**
 * Represents the entity controlled by the user.
 */
trait Player extends Entity:

  /**
   * The current position of the player.
   */
  def position: (Int, Int)

  /**
   * The user's remaining number of lives.
   */
  def lives: Int

  /**
   * The score associated with the player.
   */
  def score: Int

  /**
   * Brings the player in a new position.
   * 
   * @param direction the direction of the move.
   */
  def move(direction: Direction): Unit

  /**
   * Decreases user's lives
   */
  def loseLife(): Unit

  /**
   * Updates the score of the player.
   * 
   * @param points points to add to the score.
   */
  def addScore(points: Int): Unit

object Player:
  def apply(initialPosition: (Int, Int), initialLives: Int, initialScore: Int): Player =
    PlayerImpl(initialPosition, initialLives, initialScore)

private case class PlayerImpl(private var _position: (Int, Int),
                              private var _lives: Int,
                              private var _score: Int) extends Player:
  override def position: (Int, Int) = _position
  override def lives: Int = _lives
  override def score: Int = _score
  override def move(direction: Direction): Unit =
    direction match
      case Direction.Up => _position = (_position._1, _position._2 - 1)
      case Direction.Down => _position = (_position._1, _position._2 + 1)
      case Direction.Left => _position = (_position._1 - 1, _position._2)
      case Direction.Right => _position = (_position._1 + 1, _position._2)
  override def loseLife(): Unit = _lives -= 1
  override def addScore(points: Int): Unit = _score += points
