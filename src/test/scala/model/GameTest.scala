/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.map.{DoorCell, WallCell}
import model.puzzle.PuzzleRepository
import model.util.GameSettings
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import scala.util.Random

class GameTest:

  private val settings = GameSettings.fromDifficulty("easy")
  private val game = Game(settings)

  @BeforeEach
  def setup(): Unit = game.startGame()
  
  @Test
  def testValidMovement(): Unit =
    val newPosition =
      adjacentPosition(game.player.position).
      find((x, y) => game.maze.isWalkable(x, y)).
      get
    val result = game.movePlayerTo(newPosition)
    assertTrue(result.isRight)
    assertEquals(newPosition, game.player.position, "Player should be able to move towards an adjacent valid cell")

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
    assertEquals(doorCellCoords, game.player.position, "Player should be able to move towards an open door")

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
    assertEquals(floorCellAdjacentDoorCell, game.player.position, "Player should not be able to move towards a closed door")

  @Test
  def testInvalidMovementDistanceTooFar(): Unit =
    val newPositionTooFar =
      adjacentPosition(game.player.position).
      map((x, y) => (x + 1, y + 1)).
      find((x, y) => game.maze.isWalkable(x, y)).
      get
    val result = game.movePlayerTo(newPositionTooFar)
    assertTrue(result.isLeft)
    assertEquals("Invalid move", result.left.get, "Player should not be able to move to a non-adjacent cell")

  @Test
  def testInvalidMovementNonFloorCell(): Unit =
    val wallCellCoords =
      adjacentPosition(game.player.position).
      find((x, y) => game.maze.getCell(x, y).isInstanceOf[WallCell]).
      get
    val result = game.movePlayerTo(wallCellCoords)
    assertTrue(result.isLeft)
    assertEquals("Invalid move", result.left.get, "Player should not be able to move towards a non-floor cell")

  @Test
  def testInvalidMovementGameFinished(): Unit =
    game.endGame()
    val (newX, newY) =
      adjacentPosition(game.player.position).
      find((x, y) => game.maze.isWalkable(x, y)).
      get
    val result = game.movePlayerTo(newX, newY)
    assertTrue(result.isLeft)
    assertEquals("Game finished!", result.left.get, "Player should not be able to move if the game is finished")

  @Test
  def testOpenDoorWrongAnswer(): Unit =
    val doorCellCoords = findDoorCellCoords()
    val result = game.openDoor(doorCellCoords, "Wrong answer")
    assertTrue(result.isLeft)
    assertEquals("Puzzle failed", result.left.get)
    val doorCell = game.maze.getCell(doorCellCoords._1, doorCellCoords._2).asInstanceOf[DoorCell]
    assertFalse(doorCell.isOpen, "Door should be closed if the answer is wrong")

  @Test
  def testOpenDoorCorrectAnswer(): Unit =
    val doorCellCoords = findDoorCellCoords()
    val doorCell = game.maze.getCell(doorCellCoords._1, doorCellCoords._2).asInstanceOf[DoorCell]
    val puzzleSolution = doorCell.puzzle.solutions.head
    val result = game.openDoor(doorCellCoords, puzzleSolution)
    assertTrue(result.isRight)
    assertTrue(doorCell.isOpen, "Door should be open if the answer is correct")

  @Test
  def testOpenNonDoorCell(): Unit =
    val wallCellCoords = findWallCellCoords()
    val wallCell = game.maze.getCell(wallCellCoords._1, wallCellCoords._2)
    val result = game.openDoor(wallCellCoords, "Answer")
    assertTrue(result.isLeft)
    assertEquals("This is not a door", result.left.get)

  @RepeatedTest(10)
  def testFightLogicNonDeterministic(): Unit =
    val initialLives = game.player.lives
    val initialScore = game.player.score
    val answer = Seq(
      PuzzleRepository.randomPuzzle().solutions.head,
      "wrong answer"
    )(Random.nextInt(2))
    val result = game.fightLogic(answer)
    result match
      case Right(()) =>
        assertEquals(initialScore + 50, game.player.score, "In case of success score should be increased")
        assertEquals(initialLives, game.player.lives, "In case of success lives should not be decreased")
      case Left(error) =>
        assertEquals("Wrong answer, you lost a life", error)
        assertEquals(initialScore, game.player.score, "In case of failure score should not be increased")
        assertEquals(initialLives - 1, game.player.lives, "In case of failure lives should be decreased")
      case _ => fail("Unexpected path reached for method fightLogic()")

  @RepeatedTest(10)
  def testFightLuckNonDeterministic(): Unit =
    val initialLives = game.player.lives
    val initialScore = game.player.score
    val result = game.fightLuck()
    result match
      case Right(()) =>
        assertEquals(initialScore + 20, game.player.score, "In case of success score should be increased")
        assertEquals(initialLives, game.player.lives, "In case of success lives should not be decreased")
      case Left(error) =>
        assertEquals(initialScore, game.player.score, "In case of failure score should not be increased")
        assertEquals(initialLives - 1, game.player.lives, "In case of failure lives should be decreased")
      case _ => fail("Unexpected path reached for method fightLuck()")

  private val delta = List((1, 0), (-1, 0), (0, 1), (0, -1))
  private val size = settings.mazeSize

  private def adjacentPosition(position: (Int, Int)): List[(Int, Int)] =
    delta.
      map((x, y) => (position._1 + x, position._2 + y)).
      filter((x, y) => x >= 0 && x < size && y >= 0 && y < size)

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