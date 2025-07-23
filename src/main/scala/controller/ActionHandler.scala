/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package controller

import model.entities.Guardian

/**
 * Represents all the actions a user can perform.
 */
sealed trait UserAction

/**
 * The companion object defines all the possible actions.
 */
object UserAction:

  /**
   * Select the new cell where to move.
   *
   * @param pos position of the cell.
   */
  case class ClickCell(pos: (Int, Int)) extends UserAction

  /**
   * Tries to move on another cell.
   *
   * @param pos the new position.
   */
  case class AttemptMove(pos: (Int, Int)) extends UserAction

  /**
   * Tries to open the door.
   *
   * @param pos position of the door.
   */
  case class AttemptOpenDoor(pos: (Int, Int), answer: String) extends UserAction

  /**
   * Represents the challenge with a guardian
   * based on solving a logical problem.
   */
  case class FightLogic(guardian: Guardian, answer: String) extends UserAction

  /**
   * Represents the challenge with a guardian
   * based on the roll of a die.
   */
  case class FightLuck(guardian: Guardian) extends UserAction

  /**
   * Used to restart a game.
   */
  case object Restart extends UserAction

  /**
   * Represents an invalid action performed by the user.
   *
   * @param detail defines why the action is invalid.
   */
  case class InvalidAction(detail: String) extends UserAction

/**
 * Used by the controller to check the validity of the action.
 */
trait UserActionHandler:

  /**
   * Represent an action performed by the user.
   *
   * @param action the action performed by the user.
   */
  def onAction(action: UserAction): Unit