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

  /*def generate(size: Int): Maze = {

    val rand = new scala.util.Random()
    var grid = Vector.fill(size, size)(WallCell(): Cell)

    def isInBounds(x: Int, y: Int): Boolean =
      x >= 0 && y >= 0 && x < size && y < size

    def carve(x: Int, y: Int): Unit =
      grid = grid.updated(y, grid(y).updated(x, FloorCell()))

      val directions = rand.shuffle(List((2, 0), (-2, 0), (0, 2), (0, -2)))

      directions.foreach { case (dx, dy) =>
        val nx = x + dx
        val ny = y + dy
        val midX = x + dx / 2
        val midY = y + dy / 2

        if isInBounds(nx, ny) && grid(ny)(nx) == WallCell() then
          grid = grid.updated(midY, grid(midY).updated(midX, FloorCell()))
          carve(nx, ny)

      }


    // Generamos múltiples puntos de inicio para mejor conectividad
    val startPoints = List((1, 1), (size - 2, size - 2), (1, size - 2), (size - 2, 1))
    startPoints.foreach { case (x, y) =>
      if isInBounds(x, y) && grid(y)(x) == WallCell() then carve(x, y)
    }

    // Aseguramos entrada y salida conectadas
    grid = grid.updated(1, grid(1).updated(0, FloorCell())) // Entrada

    // Conectamos la salida al laberinto principal
    val exitX = size - 2
    val exitY = size - 2
    if (grid(exitY)(exitX) == WallCell()) {
      // Buscamos celdas adyacentes conectadas
      val adjacent = List((exitX - 1, exitY), (exitX, exitY - 1))
        .filter { case (x, y) => isInBounds(x, y) && grid(y)(x) == FloorCell() }

      if (adjacent.nonEmpty) {
        val (connX, connY) = adjacent.head
        grid = grid.updated(exitY, grid(exitY).updated(exitX, FloorCell()))
        grid = grid.updated(connY, grid(connY).updated(connX, FloorCell()))
      }
    }

    grid = grid.updated(size - 2, grid(size - 2).updated(size - 1,
      DoorCell(PuzzleRepository.randomPuzzle())))

    new Maze(size, grid)
  }*/



  def generate(size: Int): Maze = {

    val rand = new scala.util.Random()
    var grid = Vector.tabulate(size, size)((x, y) =>
      if (x % 2 == 1 && y % 2 == 1) FloorCell() else WallCell()
    )

    // Algoritmo de Prim mejorado
    val walls = collection.mutable.ArrayBuffer[(Int, Int, Int, Int)]()

    // Generar paredes horizontales
    for {
      x <- 1 until size by 2
      y <- 1 until size - 1 by 2
    } walls += ((x, y, x, y + 2))

    // Generar paredes verticales
    for {
      x <- 1 until size - 1 by 2
      y <- 1 until size by 2
    } walls += ((x, y, x + 2, y))

    rand.shuffle(walls).foreach { case (x1, y1, x2, y2) =>
      val (mx, my) = ((x1 + x2) / 2, (y1 + y2) / 2)
      if (grid(y1)(x1) == FloorCell() ^ grid(y2)(x2) == FloorCell()) {
        grid = grid.updated(my, grid(my).updated(mx, FloorCell()))
        if (grid(y1)(x1) == WallCell()) grid = grid.updated(y1, grid(y1).updated(x1, FloorCell()))
        if (grid(y2)(x2) == WallCell()) grid = grid.updated(y2, grid(y2).updated(x2, FloorCell()))
      }
    }

    // Entrada y salida garantizadas
    grid = grid.updated(1, grid(1).updated(0, FloorCell())) // Entrada (0,1)

    // Conectar salida al laberinto
    val exitX = size - 2
    val exitY = size - 2
    if (grid(exitY)(exitX) == WallCell()) {
      grid = grid.updated(exitY, grid(exitY).updated(exitX, FloorCell()))
      grid = grid.updated(exitY, grid(exitY).updated(exitX - 1, FloorCell()))
    }

    grid = grid.updated(exitY, grid(exitY).updated(size - 1,
      DoorCell(PuzzleRepository.randomPuzzle())) )// Salida (size-1, size-2)

    new Maze(size, grid)
  }

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