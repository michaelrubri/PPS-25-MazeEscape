/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.map

import model.puzzle.{Puzzle, PuzzleRepository}
import scala.util.Random

/**
 * The generic cell used to generate the maze.
 */
abstract class Cell:
  def toString: String

/**
 * Represents a wall cell.
 */
case class WallCell() extends Cell:
  override def toString: String = "#"

/**
 * Represents a floor cell.
 */
case class FloorCell() extends Cell:
  override def toString: String = " "

/**
 * Represents a door cell.
 */
case class DoorCell(
                   puzzle: Puzzle,
                   private var open: Boolean = false,
                   private var blockedTurns: Int = 0,
                   ) extends Cell:

  /**
   * Checks if the door is open.
   *
   * @return true if the door is open, false otherwise.
   */
  def isOpen: Boolean = open

  /**
   * Opens a locked door.
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

  override def toString: String = "->"

/**
 * The class used to model the maze.
 *
 * @param size the size of the maze.
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
   * @param position the coordinates of the cell.
   * @return true if the user has walked on an open door, false otherwise.
   */
  def isExit(position: (Int, Int)): Boolean =
    getCell(position._1, position._2) match
      case door: DoorCell => door.isOpen
      case _              => false    

  def printMaze(): Unit =
  for row <- grid do
    for cell <- row do
      print(cell.toString)
    println()
      
/**
 * The companion object of class Maze. It has the responsibility to create the maze
 * and generates the guardians.
 */
object Maze:
  
  def generate(size: Int): Maze =
    val rand = new scala.util.Random()
  
    // All walls at first
    var grid = Vector.fill(size, size)(WallCell(): Cell)
  
    def isInBounds(x: Int, y: Int): Boolean =
      x > 0 && y > 0 && x < size - 1 && y < size - 1
  
    def carve(x: Int, y: Int): Unit =
      grid = grid.updated(y, grid(y).updated(x, FloorCell()))
  
      val directions = rand.shuffle(List((2, 0), (-2, 0), (0, 2), (0, -2)))
      for (dx, dy) <- directions do
        val nx = x + dx
        val ny = y + dy
        if isInBounds(nx, ny) && grid(ny)(nx) == WallCell then
          grid = grid.updated(y + dy / 2, grid(y + dy / 2).updated(x + dx / 2, FloorCell()))
          carve(nx, ny)
  
    // Comenzar desde celda impar
    carve(1, 1)
  
    // Entrance and Exit
    grid = grid.updated(1, grid(1).updated(0, FloorCell()))
    grid = grid.updated(size - 2, grid(size - 2).updated(size - 1, DoorCell(PuzzleRepository.randomPuzzle())))
    
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