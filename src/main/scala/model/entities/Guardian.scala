/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.entities

import model.utils.Position.*
import model.utils.Position

/**
 * Represents the NPC character ruled by the system.
 */
trait Guardian extends Entity:

  /**
   * Defines the new position of the guardian, trying to reach the player.
   *
   * @param target the position of the player.
   * @return new set of coordinates.
   */
  def intercept(target: Position): Position

  /**
   * Used by the Game module to update the position of the guardian.
   *
   * @param newPosition the coordinates of the new position.
   * @return updated instance of the guardian.
   */
  private[model] def updatePosition(newPosition: Position): Guardian

/**
 * The companion object of guardian.
 */
object Guardian:

  /**
   * Generates a new instance of guardian.
   *
   * @param initialPosition guardian's starting position.
   * @return new instance of guardian.
   */
  def apply(initialPosition: Position): Guardian = GuardianImpl(initialPosition)

private case class GuardianImpl(position: Position) extends Guardian:
  override def intercept(target: Position): Position =
    val (dx, dy) = (target.row - position.row, target.col - position.col)
    (dx, dy) match
      case (dx, dy) if dx.abs + dy.abs <= 1 => Position(position.row, position.col)
      case (dx, dy) if dx.abs >= dy.abs => Position(position.row + dx.sign, position.col)
      case (_, dy) => Position(position.row, position.col + dy.sign)
  override private[model] def updatePosition(newPosition: Position): Guardian = copy(position = newPosition)