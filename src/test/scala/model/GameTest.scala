/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.entities.Player
import model.map.{DoorCell, Maze, WallCell}
import model.utils.Position.*
import model.utils.{GameSettings, Position}
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class GameTest:

  private val settings = GameSettings.fromDifficulty("easy")
  private val game = Game(settings)
  given maze: Maze = game.getMaze

  @BeforeEach
  def setup(): Unit = game.startGame()
  
  @Test
  def testValidMovement(): Unit =
    val newPosition =
      adjacentPositions(game.player.position).
      find(pos => game.getMaze.isWalkable(pos)).
      get
    val result = game.movePlayerTo(newPosition)
    assertTrue(result.isRight)
    assertEquals(newPosition, game.player.position, "Player should be able to move towards an adjacent walkable cell")

  @Test
  def testValidMovementOpenDoor(): Unit =
    // val doorCellCoords = maze.findDoorCellCoordsOnBorder()
    // val doorCell = maze.doorCells.head
    val doorCell = maze.doorCells.head
    val doorCellCoords = doorCell.position
    assertSame(doorCell, maze.getCell(doorCellCoords))
    val floorCellAdjacentDoorCell =
      adjacentPositions(doorCellCoords).
      find(pos => game.getMaze.isWalkable(pos)).
      get
    game.player = Player(floorCellAdjacentDoorCell, game.player.lives, game.player.score)
    doorCell.unlock()
    val result = game.movePlayerTo(doorCellCoords)
    assertTrue(result.isRight)
    assertEquals(doorCellCoords, game.player.position, "Player should be able to move towards an open door")

  @Test
  def testInvalidMovementClosedDoor(): Unit =
    val doorCellCoords = maze.doorCells.head.position
    val floorCellAdjacentDoorCell =
      adjacentPositions(doorCellCoords).
      find(pos => game.getMaze.isWalkable(pos)).
      get
    game.player = Player(floorCellAdjacentDoorCell, game.player.lives, game.player.score)
    val result = game.movePlayerTo(doorCellCoords)
    assertTrue(result.isLeft)
    assertEquals(floorCellAdjacentDoorCell, game.player.position, "Player should not be able to move towards a closed door")

  @Test
  def testInvalidMovementDistanceTooFar(): Unit =
    val newPositionTooFar =
      adjacentPositions(game.player.position).
      map(pos => Position(pos.row + 1, pos.col + 1)).
      find(pos => game.getMaze.isWalkable(pos)).
      get
    val playerPosition = game.player.position
    val result = game.movePlayerTo(newPositionTooFar)
    assertTrue(result.isLeft)
    assertEquals(playerPosition, game.player.position, "Player should not be able to move towards a non-adjacent cell")

  @Test
  def testInvalidMovementNonFloorCell(): Unit =
    val wallCellCoords =
      adjacentPositions(game.player.position).
      find(pos => game.getMaze.getCell(pos).isInstanceOf[WallCell]).
      get
    val playerPosition = game.player.position
    val result = game.movePlayerTo(wallCellCoords)
    assertTrue(result.isLeft)
    assertEquals(playerPosition, game.player.position, "Player should not be able to move towards a non-walkable cell")

  @Test
  def testInvalidMovementGameFinished(): Unit =
    game.endGame()
    val newPosition =
      adjacentPositions(game.player.position).
      find(pos => game.getMaze.isWalkable(pos)).
      get
    val playerPosition = game.player.position
    val result = game.movePlayerTo(newPosition)
    assertTrue(result.isLeft)
    assertEquals(playerPosition, game.player.position, "Player should not be able to move if the game is finished")

  @Test
  def testOpenDoorWrongAnswer(): Unit =
    val doorCellCoords = maze.doorCells.head.position
    val floorCellAdjacentDoorCell =
      adjacentPositions(doorCellCoords).
        find(pos => game.getMaze.isWalkable(pos)).
        get
    game.player = Player(floorCellAdjacentDoorCell, game.player.lives, game.player.score)
    val result = game.openDoor(doorCellCoords, "Wrong answer")
    assertTrue(result.isLeft)
    val doorCell = game.getMaze.getCell(doorCellCoords).asInstanceOf[DoorCell]
    assertFalse(doorCell.isOpen, "Door should be closed if the answer provided is wrong")

  @Test
  def testOpenDoorCorrectAnswer(): Unit =
    val doorCellCoords = maze.doorCells.head.position
    val floorCellAdjacentDoorCell =
      adjacentPositions(doorCellCoords).
        find(pos => game.getMaze.isWalkable(pos)).
        get
    game.player = Player(floorCellAdjacentDoorCell, game.player.lives, game.player.score)
    val doorCell = game.getMaze.getCell(doorCellCoords).asInstanceOf[DoorCell]
    val puzzleSolution = doorCell.puzzle.solutions.head
    val result = game.openDoor(doorCellCoords, puzzleSolution)
    assertTrue(result.isRight)
    assertTrue(doorCell.isOpen, "Door should be open if the answer is correct")

  @Test
  def testOpenNonDoorCell(): Unit =
    val wallCell = maze.wallCells.head
    val wallCellCoords = wallCell.position
    val floorCellAdjacentWallCell =
      adjacentPositions(wallCellCoords).
        find(pos => game.getMaze.isWalkable(pos)).
        get
    game.player = Player(floorCellAdjacentWallCell, game.player.lives, game.player.score)
    // val wallCell = game.getMaze.getCell(wallCellCoords.x, wallCellCoords.y)
    val result = game.openDoor(wallCellCoords, "Answer")
    assertTrue(result.isLeft)
    assertEquals("(Game) This is not a door", result.left.get)

  /*@RepeatedTest(10)
  def testFightLogicNonDeterministic(): Unit =
    val initialLives = game.player.lives
    val initialScore = game.player.score
    val puzzle = game.startLogicChallenge()
    val answer = Seq(
      puzzle.solutions.head,
      "wrong answer"
    )(Random.nextInt(2))
    val guardian = game.guardians.head
    val result = game.fightLogic(guardian, answer)
    result match
      case Right(msg) =>
        assertEquals("(Game) Guardian defeated!", msg)
        assertEquals(initialScore + 50, game.player.score, "In case of success score should be increased")
        assertEquals(initialLives, game.player.lives, "In case of success lives should not be decreased")
      case Left(error) =>
        assertEquals("(Game) Wrong answer, you lost a life", error)
        assertEquals(initialScore, game.player.score, "In case of failure score should not be increased")
        assertEquals(initialLives - 1, game.player.lives, "In case of failure lives should be decreased")
      case _ => fail("Unexpected path reached for method fightLogic()")*/

  @Test
  def testFightLogicNoActivePuzzle(): Unit =
    val guardian = game.guardians.head
    val initialLives = game.player.lives
    val initialScore = game.player.score
    val initialGuardiansNumber = game.guardians.size
    val result = game.fightLogic(guardian, "any answer")
    assertTrue(result.isLeft)
    assertEquals("(Game) No active puzzle", result.left.getOrElse(""))
    assertEquals(initialLives, game.player.lives, "Lives should not change")
    assertEquals(initialScore, game.player.score, "Score should not change")
    assertEquals(initialGuardiansNumber, game.guardians.size, "No guardian should be removed")

  @Test
  def testFightLogicCorrectAnswer(): Unit =
    val guardian = game.guardians.head
    val initialLives = game.player.lives
    val initialScore = game.player.score
    val puzzle = game.startLogicChallenge()
    val correctAnswer = puzzle.solutions.head
    val result = game.fightLogic(guardian, correctAnswer)
    assertTrue(result.isRight)
    assertEquals("(Game) Guardian defeated!", result.getOrElse(""))
    assertEquals(initialScore + 50, game.player.score, "In case of success score should be increased")
    assertEquals(initialLives, game.player.lives, "In case of success lives should not be decreased")
    assertFalse(game.guardians.contains(guardian), "The guardian should be removed from the list")

  @Test
  def testFightLogicWrongAnswer(): Unit =
    val guardian = game.guardians.head
    val initialLives = game.player.lives
    val initialScore = game.player.score
    val puzzle = game.startLogicChallenge()
    val wrongAnswer = "wrong answer"
    val result = game.fightLogic(guardian, wrongAnswer)
    assertTrue(result.isLeft)
    assertEquals("(Game) Wrong answer, you lost a life", result.left.getOrElse(""))
    assertEquals(initialScore, game.player.score, "In case of failure score should not be increased")
    assertEquals(initialLives - 1, game.player.lives, "In case of failure lives should be decreased")
    assertFalse(game.guardians.contains(guardian), "The guardian should be removed")

  @RepeatedTest(20)
  def testFightLuckNonDeterministic(): Unit =
    val initialLives = game.player.lives
    val initialScore = game.player.score
    val guardian = game.guardians.head
    val result = game.fightLuck(guardian)
    result match
      case Right("(Game) Guardian defeated!") =>
        assertEquals(initialScore + 20, game.player.score, "In case of success score should be increased")
        assertEquals(initialLives, game.player.lives, "In case of success lives should not be decreased")
      case Right("(Game) You were unlucky, you lost the fight") =>
        assertEquals(initialScore, game.player.score, "In case of failure score should not be increased")
        assertEquals(initialLives - 1, game.player.lives, "In case of failure lives should be decreased")
      case Right(other) => fail(s"Unexpected message: $other")
      case Left(msg) if msg.startsWith("(Game) Score update failed:") => assertEquals(0, game.player.score)
      case Left(msg) if msg.startsWith("(Game) Lose life error:") => assertEquals(0, game.player.lives)
      case Left(other) => fail(s"Unexpected message: $other")

  /*@Test
  def testFightLuckWin(): Unit =
    val guardian = game.guardians.head
    val initialScore = game.player.score
    val initialLives = game.player.lives
    Random.setSeed(1L)
    val result = game.fightLuck(guardian)
    assertTrue(result.isRight)
    assertEquals("(Game) Guardian defeated!", result.getOrElse(""))
    assertEquals(initialScore + 20, game.player.score, "In case of success score should be increased")
    assertEquals(initialLives, game.player.lives, "In case of success lives should not be changed")
    assertFalse(game.guardians.contains(guardian), "The guardian should be removed")

  @Test
  def testFightLuckLose(): Unit =
    val guardian = game.guardians.head
    val initialScore = game.player.score
    val initialLives = game.player.lives
    Random.setSeed(0L)
    val result = game.fightLuck(guardian)
    assertTrue(result.isRight)
    assertEquals("(Game) You were unlucky, you lost the fight", result.getOrElse(""))
    assertEquals(initialScore, game.player.score, "In case of failure score should not be increased")
    assertEquals(initialLives - 1, game.player.lives, "In case of failure lives should be decreased")
    assertFalse(game.guardians.contains(guardian), "The guardian should be removed")*/