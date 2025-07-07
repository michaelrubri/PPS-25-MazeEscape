/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.map

import scala.util.Random

/**
 * The generic cell used to generate the maze.
 */
sealed trait Cell:
  def position: (Int, Int)

/**
 * Represents a single wall cell.
 *
 * @param position the position of the wall cell.
 */
case class WallCell(position: (Int, Int)) extends Cell

/**
 * Represents a single floor cell.
 *
 * @param position the position of the floor cell.
 */
case class FloorCell(position: (Int, Int)) extends Cell

/**
 * Represents a single door cell.
 *
 * @param position the position of the wall cell.
 */
case class DoorCell(
                   position: (Int, Int),
                   private var open: Boolean = false,
                   private var blockedTurns: Int = 0
                   ) extends Cell:

  /**
   * Checks if the door is open.
   *
   * @return true if the door is open, false otherwise.
   */
  def isOpen: Boolean = open

  /**
   * Opens a blocked door.
   */
  def unlock(): Unit = open = true

  /**
   * Locks the door for a defined number of turns.
   *
   * @param turns number of turns the door is locked.
   */
  def blockFor(turns: Int): Unit = blockedTurns = turns

  /**
   * Checks if the door is blocked.
   *
   * @return true if the door is blocked, false otherwise.
   */
  def isBlocked: Boolean = blockedTurns > 0

  /**
   * Decreases the number of turns the door is locked.
   */
  def decrementBlock(): Unit = if blockedTurns > 0 then blockedTurns -= 1

/**
 * The class used to model the maze.
 *
 * @param size the size of the maze
 * @param grid set of cells used to build the maze.
 */
class Maze private (val size: Int, val grid: Vector[Vector[Cell]]):

  /**
   * Provides the cell based on spatial coordinates.
   *
   * @param x the x-axis.
   * @param y the y-axis.
   * @return the cell based on the specified coordinates.
   */
  def getCell(x: Int, y: Int): Cell = grid(x)(y)

  /**
   * Determines whether the cell can be walked on.
   *
   * @param position the coordinates of the cell.
   * @return true if the cell can be walked on, false otherwise.
   */
  def isWalkable(position: (Int, Int)): Boolean =
    getCell(position._1, position._2) match
      case _: FloorCell   => true
      case door: DoorCell => door.isOpen
      case _              => false

  /**
   * Checks if the user has opened the door successfully and moved to that cell.
   *
   * @param position the coordinates of the cell
   * @return
   */
  def isExit(position: (Int, Int)): Boolean =
    getCell(position._1, position._2) match
      case door: DoorCell => door.isOpen
      case _              => false

/**
 * The companion object of class Maze. It has the responsibility to create the maz
 * and generates the guardians.
 */
object Maze:

  /**
   * Creates a custom maze based on the size and the grid passed as arguments.
   *
   * @param size the size of the maze.
   * @param grid contains the cells of the maze.
   * @return the custom maze.
   */
  def apply(size: Int, grid: Vector[Vector[Cell]]): Maze = new Maze(size, grid)

  /**
   * Generates a basic maze with floor cells, wall cells and doors.
   *
   * @param size the size of the maze.
   * @return the basic maze.
   */
  def generateBasic(size: Int): Maze =
    val seed = new Random()
    val grid = Vector.tabulate(size, size) { (x, y) =>
      val position = (x, y)
      val isBorder = x == 0 || y == 0 || x == size - 1 || y == size - 1
      if isBorder && (x + y) % 7 == 0 then DoorCell(position)
      else if isBorder then WallCell(position)
      else FloorCell(position)
    }
    Maze(size, grid)

  /**
   * Generates guardian entities randomly on the map.
   *
   * @param guardiansNumber the number of guardians to spawn.
   * @param maze context parameter to get the maze in the scope.
   * @return a list of guardians.
   */
  def spawnGuardians(guardiansNumber: Int)(using maze: Maze): List[(Int, Int)] =
    val guardiansPosition = for
      x <- 1 until maze.size - 1
      y <- 1 until maze.size - 1
      guardianPosition = (x, y)
      if maze.getCell(x, y).isInstanceOf[FloorCell]
    yield guardianPosition
    Random.shuffle(guardiansPosition.toList).take(guardiansNumber)