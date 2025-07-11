/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.map.Maze

/**
 * Represents the NPC character ruled by the system.
 */
trait Guardian extends Entity:

  /**
   * The current position of the guardian.
   */
  def position: (Int, Int)

  /**
   * Defines the new position of the guardian, trying to reach the player.
   *
   * @param target the position of the player.
   */
  def intercept(target: (Int, Int)): (Int, Int)

  /**
   * Used by the Game module to update the position of the guardian.
   *
   * @param newPosition the coordinates of the new position.
   */
  private[model] def updatePosition(newPosition: (Int, Int)): Unit

object Guardian:
  def apply(initialPosition: (Int, Int)): Guardian =
    GuardianImpl(initialPosition)

private case class GuardianImpl(private var _position: (Int, Int)) extends Guardian:
  override def position: (Int, Int) = _position
  override def intercept(target: (Int, Int)): (Int, Int) =
    val (dx, dy) = (target._1 - _position._1, target._2 - _position._2)
    (dx, dy) match
      case (dx, dy) if dx.abs <= 1 && dy.abs <= 1 => (_position._1, _position._2)
      case (dx, dy) if dx.abs == dy.abs => (_position._1 + dx.sign, _position._2 + dy.sign)
      case (dx, dy) if dx.abs > dy.abs => (_position._1 + dx.sign, _position._2)
      case (_, dy) => (_position._1, _position._2 + dy.sign)

  override private[model] def updatePosition(newPosition: (Int, Int)): Unit = _position = newPosition