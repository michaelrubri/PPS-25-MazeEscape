/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.map

import model.puzzle.{Puzzle, PuzzleRepository}
import model.utils.Position
import model.utils.Position.*
import scala.reflect.ClassTag
import scala.util.Random

/**
 * Helper to update and read the grid.
 */
object MazeUtils:

  /**
   * Provides a new grid with the cell in the specified position
   * replaced by a new cell.
   *
   * @param grid the grid that shapes the maze.
   * @param position the selected position.
   * @param newCell the new cell.
   * @return the updated grid.
   */
  def updateCell(grid: Vector[Vector[Cell]],
                 position: Position,
                 newCell: Cell): Vector[Vector[Cell]] =
    grid.updated(position.row, grid(position.row).updated(position.col, newCell))

  /**
   * Provides the cell at the specified position.
   *
   * @param grid the grid that shapes the maze.
   * @param position the selected position.
   * @return the specified cell.
   */
  def getCell(grid: Vector[Vector[Cell]], position: Position): Cell =
    grid(position.row)(position.col)

/**
 * The generic cell used to generate the maze.
 */
sealed trait Cell:

  /**
   * Provides cell's position.
   */
  def position: Position

  override def toString: String

/**
 * Represents a wall cell.
 */
case class WallCell(position: Position) extends Cell:
  override def toString: String = "#"

/**
 * Represents a floor cell.
 */
case class FloorCell(position: Position) extends Cell:
  override def toString: String = " "

/**
 * Represents a door cell.
 */
case class DoorCell(position: Position,
                    puzzle: Puzzle,
                    open: Boolean = false,
                    blockedTurns: Int = 0) extends Cell:
  
  /**
   * Checks if the door is open.
   *
   * @return true if the door is open, false otherwise.
   */
  def isOpen: Boolean = open

  /**
   * Opens a locked door.
   */
  def unlock(): DoorCell = copy(open = true)

  /**
   * Locks the door for a defined number of turns.
   *
   * @param turns number of turns the door is locked.
   */
  def blockFor(turns: Int): DoorCell = copy(blockedTurns = turns)

  /**
   * Provides the remaining turns in which the door is locked.
   * 
   * @return number of turns to wait.
   */
  def turnsLeft: Int = blockedTurns

  /**
   * Checks if the door is locked.
   *
   * @return true if the door is blocked, false otherwise.
   */
  def isBlocked: Boolean = blockedTurns > 0

  /**
   * Decreases the number of turns the door is locked.
   */
  def decrementTurns(): DoorCell =
    if blockedTurns > 0 then copy(blockedTurns = blockedTurns - 1)
    else this

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
   * @param position the position of the cell.
   * @return the cell based on the specified coordinates.
   */
  // def getCell(position: Position): Cell = grid(position.row)(position.col)

  def getCell(position: Position): Cell = MazeUtils.getCell(grid, position)

  /**
   * Provides the cells of the same type.
   *
   * @return a list of cells of type A, subtype of Cell.
   */
  private def cellsOfType[A <: Cell : ClassTag]: List[A] = grid.flatten.collect { case a: A => a }.toList

  /**
   * Provides all the door cells of the maze.
   *
   * @return a list of door cells.
   */
  def doorCells: List[DoorCell] = cellsOfType[DoorCell]

  /**
   * Provides all the wall cells of the maze.
   *
   * @return a list of wall cells.
   */
  def wallCells: List[WallCell] = cellsOfType[WallCell]

  /**
   * Provides all the floor cells of the maze.
   *
   * @return a list of floor cells.
   */
  def floorCells: List[FloorCell] = cellsOfType[FloorCell]

  /**
   * Provides all the cells of the maze.
   *
   * @return a list of generic cells.
   */
  def cells: List[Cell] = grid.flatten.toList

  /**
   * Determines whether the cell can be walked on.
   *
   * @param position the coordinates of the cell.
   * @return true if the cell can be walked on, false otherwise.
   */
  def isWalkable(position: Position): Boolean =
    getCell(position) match
      case _: FloorCell                   => true
      case door: DoorCell if door.isOpen  => true
      case _                              => false

  /**
   * Checks if the user has opened the door successfully and moved to that cell.
   * The door is considered an exit only if the door belongs to the last level of the maze.
   *
   * @param position the coordinates of the cell.
   * @return true if the user has walked on an open door, false otherwise.
   */
  def isExit(position: Position): Boolean =
    getCell(position) match
      case door: DoorCell => door.isOpen
      case _              => false
  
  /**
   * Selects a random floor cell of the maze.
   *
   * @return the position of the floor cell.
   */
  /*def randomFloorCell(): Position =
    val rand = new Random()
    val floorCells =
      for
        row <- 0 until size/2
        col <- 0 until size/2
        if getCell(Position(row, col)).isInstanceOf[FloorCell]
      yield Position(row, col)
    floorCells(rand.nextInt(floorCells.length))*/

  def randomFloorCell(): Position =
    val allFloorCells = floorCells
    if allFloorCells.isEmpty then throw new NoSuchElementException("No floor cell available")
    allFloorCells(Random.nextInt(allFloorCells.length)).position

  /**
   * Generates guardian entities randomly on the map.
   *
   * @param guardiansNumber the number of guardians to spawn.
   * @param maze            context parameter to get the maze in the scope.
   * @return a list of guardians' position.
   */
  /*def spawnGuardians(guardiansNumber: Int)(using maze: Maze): List[Position] =
    val guardiansPosition =
      for
        row <- 0 until maze.size
        col <- 0 until maze.size
        if maze.getCell(Position(row, col)).isInstanceOf[FloorCell]
      yield Position(row, col)
    Random.shuffle(guardiansPosition.toList).take(guardiansNumber)*/

  def spawnGuardians(guardiansNumber: Int)(using maze: Maze): List[Position] =
    Random.shuffle(maze.floorCells.map(_.position)).take(guardiansNumber)

/**
 * The companion object of class Maze. It has the responsibility to create the maze
 * and generates the guardians.
 */
object Maze:

  def generate(size: Int): Maze =
    val rand = new scala.util.Random()
    var grid: Vector[Vector[Cell]] =
      Vector.tabulate(size, size) { (row, col) =>
        WallCell(Position(row, col))
      }

    def isInBounds(row: Int, col: Int): Boolean =
      row >= 0 && col >= 0 && row < size && col < size

    def carve(row: Int, col: Int): Unit =
      grid = grid.updated(row, grid(row).updated(col, FloorCell(Position(row, col))))

      val directions = rand.shuffle(List((2, 0), (-2, 0), (0, 2), (0, -2)))

      for (dRow, dCol) <- directions do
        val newRow = row + dRow
        val newCol = col + dCol
        val midRow = row + dRow / 2
        val midCol = col + dCol / 2

        if isInBounds(newRow, newCol) && grid(newRow)(newCol).isInstanceOf[WallCell] then
          grid = grid.updated(midRow, grid(midRow).updated(midCol, FloorCell(Position(midRow, midCol))))
          carve(newRow, newCol)

    // Defining exit door
    val exitRow = size - 2
    val exitCol = size - 1
    carve(exitRow, exitCol)

    // Add exit door
    grid =
      grid.updated(
        exitRow,
        grid(exitRow).updated(exitCol, DoorCell(Position(exitRow, exitCol), PuzzleRepository.randomPuzzle())))

    new Maze(size, grid)

  extension (maze: Maze)

    /**
     * Performs an action on the door.
     *
     * @param position the position of the door.
     * @param f the function to call.
     * @return a new instance of maze.
     */
    private def updateDoorAt(position: Position)(f: DoorCell => DoorCell): Maze =
      maze.getCell(position) match
        case door: DoorCell =>
          val newCell = f(door)
          Maze(maze.size, MazeUtils.updateCell(maze.grid, position, newCell))
        case _ => maze

    /**
     * Decreases the turns of all locked doors.
     *
     * @return a new instance of maze.
     */
    def decreaseTurnsLockedDoors: Maze =
      maze.doorCells.foldLeft(maze) { (maze, door) =>
        maze.updateDoorAt(door.position)(_.decrementTurns())
      }

    /**
     * Locks a door.
     *
     * @param position the position of the door.
     * @param turns number of turns it is locked in.
     * @return a new instance of maze.
     */
    def blockDoorAt(position: Position, turns: Int): Maze =
      maze.updateDoorAt(position)(_.blockFor(turns))

    /**
     * Unlocks a door.
     *
     * @param position the position of the door.
     * @return a new instance of maze.
     */
    def unlockDoorAt(position: Position): Maze =
      maze.updateDoorAt(position)(_.unlock())
