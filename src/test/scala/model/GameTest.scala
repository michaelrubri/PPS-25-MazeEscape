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
  
  @Test
  def testValidMovement(): Unit =
    val (newX, newY) =
      adjacentPosition(game.player.position).
      find((x, y) => game.maze.isWalkable(x, y)).
      get
    val result = game.movePlayerTo(newX, newY)
    assertTrue(result.isRight)
    assertEquals((newX, newY), game.player.position)

  @Test
  def testValidMovementOpenDoor(): Unit =
    val doorCellCoords = findDoorCellCoords()
    val doorCell = game.maze.getCell(doorCellCoords._1, doorCellCoords._2).asInstanceOf[DoorCell]
    val floorCellAdjacentDoorCell =
      adjacentPosition(doorCellCoords).
      find((x, y) => game.maze.isWalkable(x, y)).
      get
    game.player._setPosition(floorCellAdjacentDoorCell)
    doorCell.unlock()
    val result = game.movePlayerTo(doorCellCoords)
    assertTrue(result.isRight)
    assertEquals(doorCellCoords, game.player.position)

  @Test
  def testInvalidMovementClosedDoor(): Unit =
    val doorCellCoords = findDoorCellCoords()
    val floorCellAdjacentDoorCell =
      adjacentPosition(doorCellCoords).
      find((x, y) => game.maze.isWalkable(x, y)).
      get
    game.player._setPosition(floorCellAdjacentDoorCell)
    val result = game.movePlayerTo(doorCellCoords)
    assertTrue(result.isLeft)
    assertEquals("Invalid move", result.left.get)
    assertEquals(floorCellAdjacentDoorCell, game.player.position)

  @Test
  def testInvalidMovementDistanceTooFar(): Unit =
    val (tooFarX, tooFarY) =
      adjacentPosition(game.player.position).
      map((x, y) => (x + 1, y + 1)).
      find((x, y) => game.maze.isWalkable(x, y)).
      get
    val result = game.movePlayerTo(tooFarX, tooFarY)
    assertTrue(result.isLeft)
    assertEquals("Invalid move", result.left.get)

  @Test
  def testInvalidMovementNonFloorCell(): Unit =
    val (wallCellX, wallCellY) =
      adjacentPosition(game.player.position).
      find((x, y) => game.maze.getCell(x, y).isInstanceOf[WallCell]).
      get
    val result = game.movePlayerTo(wallCellX, wallCellY)
    assertTrue(result.isLeft)
    assertEquals("Invalid move", result.left.get)

  @Test
  def testInvalidMovementGameFinished(): Unit =
    game.endGame()
    val (newX, newY) =
      adjacentPosition(game.player.position).
      find((x, y) => game.maze.isWalkable(x, y)).
      get
    val result = game.movePlayerTo(newX, newY)
    assertTrue(result.isLeft)
    assertEquals("Game finished!", result.left.get)

  @Test
  def testOpenDoorWrongAnswer(): Unit =
    val doorCellCoords = findDoorCellCoords()
    val result = game.openDoor(doorCellCoords, "Wrong answer")
    assertTrue(result.isLeft)
    assertEquals("Puzzle failed", result.left.get)
    val doorCell = game.maze.getCell(doorCellCoords._1, doorCellCoords._2).asInstanceOf[DoorCell]
    assertFalse(doorCell.isOpen)

  @Test
  def testOpenDoorCorrectAnswer(): Unit =
    val doorCellCoords = findDoorCellCoords()
    val doorCell = game.maze.getCell(doorCellCoords._1, doorCellCoords._2).asInstanceOf[DoorCell]
    val puzzleSolution = doorCell.puzzle.solutions.head
    val result = game.openDoor(doorCellCoords, puzzleSolution)
    assertTrue(result.isRight)
    assertTrue(doorCell.isOpen)

  @Test
  def testOpenNonDoorCell(): Unit =
    val wallCellCoords = findWallCellCoords()
    val wallCell = game.maze.getCell(wallCellCoords._1, wallCellCoords._2)
    val result = game.openDoor(wallCellCoords, "Answer")
    assertTrue(result.isLeft)
    assertEquals("This is not a door", result.left.get)

  private val delta = List((1, 0), (-1, 0), (0, 1), (0, -1))

  private def adjacentPosition(position: (Int, Int)): List[(Int, Int)] =
    delta.
      map((x, y) => (position._1 + x, position._2 + y)).
      filter((x, y) => x >= 0 && x < size && y >= 0 && y < size)

  private val size = settings.mazeSize

  private val border = (0 until size).flatMap(x => Seq((x, 0), (x, size - 1))).
    concat((1 until size - 1).flatMap(y => Seq((0, y), (size - 1, y))))

  private def findDoorCellCoords(): (Int, Int) =
    // border.find()
    border.collectFirst {
      case (x, y) if game.maze.getCell(x, y).isInstanceOf[DoorCell] => (x, y)
    }.get

  private def findWallCellCoords(): (Int, Int) =
    border.collectFirst {
      case (x, y) if game.maze.getCell(x, y).isInstanceOf[WallCell] => (x, y)
    }.get