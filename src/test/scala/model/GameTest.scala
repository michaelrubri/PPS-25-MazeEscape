/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.map.{DoorCell, WallCell}
import model.util.GameSettings
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class GameTest:

  private val settings = GameSettings.fromDifficulty("easy")
  private val game = Game(settings)

  @BeforeEach
  def setup(): Unit = game.startGame()

  private def findDoorCellCoords(): (Int, Int) =
    val size = settings.mazeSize
    val border = (0 until size).flatMap(x => Seq((x, 0), (x, size - 1))).
      concat((1 until size - 1).flatMap(y => Seq((0, y), (size - 1, y))))
    border.collectFirst {
      case (x, y) if game.maze.getCell(x, y).isInstanceOf[DoorCell] => (x, y)
    }.get

  @Test
  def testOpenDoorWrongAnswer(): Unit =
    val doorCellCoords = findDoorCellCoords()
    val result = game.openDoor(doorCellCoords, "Wrong answer")
    assertTrue(result.isLeft)
    assertEquals("Puzzle failed", result.left.get)
    val doorCell = doorCellCoords.asInstanceOf[DoorCell]
    assertFalse(doorCell.isOpen)

  @Test
  def testOpenDoorCorrectAnswer(): Unit =
    val doorCellCoords = findDoorCellCoords()
    val doorCell = game.maze.getCell(doorCellCoords._1, doorCellCoords._2).asInstanceOf[DoorCell]
    val puzzleSolution = doorCell.puzzle.solutions.head
    val result = game.openDoor(doorCellCoords, puzzleSolution)
    assertTrue(result.isRight)
    assertTrue(doorCell.isOpen)

  private def findWallCellCoords(): (Int, Int) =
    val size = settings.mazeSize
    val border = (0 until size).flatMap(x => Seq((x, 0), (x, size - 1))).
      concat((1 until size - 1).flatMap(y => Seq((0, y), (size - 1, y))))
    border.collectFirst {
      case (x, y) if game.maze.getCell(x, y).isInstanceOf[WallCell] => (x, y)
    }.get

  @Test
  def testOpenNonDoorCell(): Unit =
    val wallCellCoords = findWallCellCoords()
    val wallCell = game.maze.getCell(wallCellCoords._1, wallCellCoords._2).asInstanceOf[WallCell]
    val result = game.openDoor(wallCellCoords, "Answer")
    assertTrue(result.isLeft)
    assertEquals("This is not a door", result.left.map.toString())
