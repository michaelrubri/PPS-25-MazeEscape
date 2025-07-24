/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.utils
/*
/**
 * Represents the position of a generic entity.
 *
 * @param x the x-coordinate.
 * @param y the y-coordinate.
 */
case class Position(x: Int, y: Int):

  /**
   * Generates a new position from a starting position.
   *
   * @param dx the x-coordinate.
   * @param dy the y-coordinate.
   * @return the updated position.
   */
  def move(dx: Int, dy: Int): Position = Position(dx + x, dy + y)

/**
 * The companion object of position.
 */
object Position:

  /**
   * Generates a new instance of position.
   *
   * @param x the x-coordinate.
   * @param y the y-coordinate.
   * @return new instance of position.
   */
  def create(x: Int, y: Int): Position = Position(x, y)
*/

/**
 * Represents the position of a generic entity.
 */
object Position:

  opaque type Position = (Int, Int)

  /**
   * Generates a new position.
   * 
   * @param x the x-coordinate.
   * @param y the y-coordinate.
   * @return new type position.
   */
  def apply(x: Int, y: Int): Position = (x, y)

  extension (position: Position)

    /**
     * Provides the x-coordinate.
     */
    def x: Int = position._1

    /**
     * Provides the y-coordinate.
     */
    def y: Int = position._2

    /**
     * Generates a new position from a starting position.
     *
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @return the updated position.
     */
    def move(x: Int, y: Int): Position = Position(position.x + x, position.y + y)

    /**
     * Prints the position.
     */
    def show: String = s"Position(${position.x}, ${position.y})"

  def unapply(position: Position): Option[(Int, Int)] = Some((position.x, position.y))