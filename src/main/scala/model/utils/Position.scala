/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.utils

import model.entities.Direction
import model.map.Maze

/**
 * Represents the position of a generic entity.
 */
object Position:

  opaque type Position = (Int, Int)

  /**
   * Provides all adjacent movements.
   */
  private val delta = List(Position(1, 0), Position(-1, 0), Position(0, 1), Position(0, -1))

  /**
   * Generates a new position.
   * 
   * @param row the row index.
   * @param col the column index.
   * @return new type position.
   */
  def apply(row: Int, col: Int): Position = (row, col)

  def deltas: List[Position] = delta

  /**
   * Provides the cells adjacent to a given position.
   *
   * @param initPos initial position.
   * @param maze context parameter.
   * @return a list of adjacent cells.
   */
  def adjacentPositions(initPos: Position)(using maze: Maze): List[Position] =
    delta.
      map(pos => Position(initPos.row + pos.row, initPos.col + pos.col)).
      filter(pos => pos.row >= 0 && pos.row < maze.size && pos.col >= 0 && pos.col < maze.size)

  /**
   * Determines whether two positions are adjacent
   * horizontally or vertically.
   *
   * @param from first position to compare.
   * @param to second position to compare.
   * @return true if the two positions are adjacent, false otherwise.
   */
  def isAdjacentTwoArgs(from: Position, to: Position): Boolean =
    from.isAdjacent(to)

  extension (position: Position)

    /**
     * Provides the row index.
     */
    def row: Int = position._1

    /**
     * Provides the column index.
     */
    def col: Int = position._2

    /**
     * Generates a new position from a starting position.
     *
     * @param dRow the amount to add to the row index.
     * @param dCol the amount to add to the column index.
     * @return the updated position.
     */
    def move(dRow: Int, dCol: Int): Position = Position(position.row + dRow, position.col + dCol)

    /**
     * Determines whether two positions are adjacent
     * horizontally or vertically.
     *
     * @param to position to compare.
     * @return true if the two positions are adjacent, false otherwise.
     */
    def isAdjacent(to: Position): Boolean =
      val (dRow, dCol) = ((position.row - to.row).abs, (position.col - to.col).abs)
      (dRow == 1 && dCol == 0) || (dRow == 0 && dCol == 1)

    /**
     * Provides the direction of movement.
     *
     * @param to position to compare.
     * @return the direction between one position and the next if exists, none otherwise.
     */
    def directionBetween(to: Position): Option[Direction] =
      (to.row - position.row, to.col - position.col) match
        case (1, 0) => Some(Direction.Right)
        case (-1, 0) => Some(Direction.Left)
        case (0, 1) => Some(Direction.Up)
        case (0, -1) => Some(Direction.Down)
        case _ => None

    /**
     * Prints the position.
     */
    def show: String = s"Position(${position.row}, ${position.col})"

  def unapply(position: Position): Option[(Int, Int)] = Some((position.row, position.col))