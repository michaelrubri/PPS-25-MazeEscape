/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.entities.{Guardian, Player}
import model.map.{DoorCell, Maze, WallCell}
import model.strategies.GuardianStrategy
import model.utils.Position.*
import model.utils.{GameSettings, Position}
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue

class GameTest:

  private val settings = GameSettings.fromDifficulty("easy")
  private val game = Game(settings)
  private lazy val maze: Maze = game.getMaze
  given Maze = maze

  private def isInBounds(pos: Position): Boolean =
    pos.row >= 0 && pos.col >= 0 && pos.row < maze.size && pos.col < maze.size

  @BeforeEach
  def setup(): Unit = game.startGame()

  private def setStrategy(strat: GuardianStrategy): Unit =
    val f = classOf[Game].getDeclaredField("guardianStrategy")
    f.setAccessible(true)
    f.set(game, strat)

  private def getGuardians: List[Guardian] =
    val f = classOf[Game].getDeclaredField("guardians")
    f.setAccessible(true)
    f.get(game).asInstanceOf[List[Guardian]]

  @RepeatedTest(10)
  def testGuardianMovesToFreeCell(): Unit =
    val guardians = getGuardians
    assertFalse(guardians.isEmpty)
    val guardian = guardians.head
    val occupied = Set(game.player.position) concat guardians.map(_.position)
    val neighbors =
      adjacentPositions(guardian.position).
      filter(pos => game.getMaze.isWalkable(pos) && !occupied(pos))
    assumeTrue(neighbors.nonEmpty, "No cells available")
    val target = neighbors.head
    val stubStrat = new GuardianStrategy(null) {
      override def nextMove(gx: Int, gy: Int, px: Int, py: Int): Position = target
    }
    setStrategy(stubStrat)
    val f = classOf[Game].getDeclaredField("guardians")
    f.setAccessible(true)
    f.set(game, List(guardian))
    game.updateGameState()
    val updated = getGuardians.head
    assertEquals(target, updated.position)

  @RepeatedTest(10)
  def testGuardiansDontOverlap(): Unit =
    val original = getGuardians
    val guardian0 = original.head
    val guardian1 = original(1)
    val common =
      (adjacentPositions(guardian0.position).toSet intersect adjacentPositions(guardian1.position).toSet)
      .filter(game.getMaze.isWalkable)
    assumeTrue(common.nonEmpty, "Test skipped, no guardian nearby")
    val target = common.head
    val stubStrat = new GuardianStrategy(null) {
      override def nextMove(gx: Int, gy: Int, px: Int, py: Int): Position = target
    }
    setStrategy(stubStrat)
    game.updateGameState()
    val updated = getGuardians
    assertEquals(target, updated.head.position)
    assertEquals(
      guardian1.position,
      updated(1).position,
      "The second guardian cannot move on an occupied cell"
    )

  @RepeatedTest(10)
  def testValidMovement(): Unit =
    val freeNeighbors =
      adjacentPositions(game.player.position).
      find(pos =>
        game.getMaze.isWalkable(pos) &&
        !game.guardians.exists(_.position == pos))
    assumeTrue(freeNeighbors.nonEmpty, "Test skipped: no cell available")
    val newPosition = freeNeighbors.head
    val result = game.movePlayerTo(newPosition)
    assertTrue(result.isRight)
    assertEquals(
      newPosition,
      game.player.position,
      "Player should be able to move towards an adjacent walkable cell"
    )

  @Test
  def testValidMovementOpenDoor(): Unit =
    val doorPosition = maze.doorCells.head.position
    val floorCellAdjacentDoorCell =
      adjacentPositions(doorPosition).
      find(pos => game.getMaze.isWalkable(pos)).
      get
    game.player = Player(floorCellAdjacentDoorCell, game.player.lives, game.player.score)
    val updatedMaze = maze.unlockDoorAt(doorPosition)
    game.maze = updatedMaze
    val result = game.movePlayerTo(doorPosition)
    assertTrue(result.isRight)
    assertEquals(
      doorPosition,
      game.player.position,
      "Player should be able to move towards an open door"
    )

  @Test
  def testInvalidMovementClosedDoor(): Unit =
    val doorPosition = maze.doorCells.head.position
    val floorCellAdjacentDoorCell =
      adjacentPositions(doorPosition).
      find(pos => game.getMaze.isWalkable(pos)).
      get
    game.player = Player(floorCellAdjacentDoorCell, game.player.lives, game.player.score)
    val result = game.movePlayerTo(doorPosition)
    assertTrue(result.isLeft)
    assertEquals(
      floorCellAdjacentDoorCell,
      game.player.position,
      "Player should not be able to move towards a closed door"
    )

  @RepeatedTest(10)
  def testInvalidMovementDistanceTooFar(): Unit =
    val playerPos = game.player.position
    val candidatesPos =
      Seq(
        Position(playerPos.row + 2, playerPos.col),
        Position(playerPos.row - 2, playerPos.col),
        Position(playerPos.row, playerPos.col + 2),
        Position(playerPos.row, playerPos.col - 2)
      )
    val findPosTooFar =
      candidatesPos.
        filter(isInBounds).
        find(game.getMaze.isWalkable)
    assumeTrue(findPosTooFar.isDefined, "Test skipped: no cell available")
    val newPositionTooFar = findPosTooFar.get
    val result = game.movePlayerTo(newPositionTooFar)
    assertTrue(result.isLeft)
    assertEquals(
      playerPos,
      game.player.position,
      "Player should not be able to move towards a non-adjacent cell"
    )

  @Test
  def testInvalidMovementNonFloorCell(): Unit =
    val wallPosition =
      adjacentPositions(game.player.position).
      find(pos => game.getMaze.getCell(pos).isInstanceOf[WallCell]).
      get
    val playerPosition = game.player.position
    val result = game.movePlayerTo(wallPosition)
    assertTrue(result.isLeft)
    assertEquals(
      playerPosition,
      game.player.position,
      "Player should not be able to move towards a non-walkable cell")

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
    assertEquals(
      playerPosition,
      game.player.position,
      "Player should not be able to move if the game is finished"
    )

  @Test
  def testOpenDoorWrongAnswer(): Unit =
    val doorPosition = maze.doorCells.head.position
    val floorCellAdjacentDoorCell =
      adjacentPositions(doorPosition).
        find(pos => game.getMaze.isWalkable(pos)).
        get
    game.player = Player(floorCellAdjacentDoorCell, game.player.lives, game.player.score)
    val result = game.openDoor(doorPosition, "Wrong answer")
    assertTrue(result.isLeft)
    val doorCell = game.getMaze.getCell(doorPosition).asInstanceOf[DoorCell]
    assertFalse(doorCell.isOpen, "Door should be closed if the answer provided is wrong")

  @Test
  def testOpenDoorCorrectAnswer(): Unit =
    val doorPosition = maze.doorCells.head.position
    val floorCellAdjacentDoorCell =
      adjacentPositions(doorPosition).
        find(pos => game.getMaze.isWalkable(pos)).
        get
    game.player = Player(floorCellAdjacentDoorCell, game.player.lives, game.player.score)
    val doorCellBefore = game.getMaze.getCell(doorPosition).asInstanceOf[DoorCell]
    val puzzleSolution = doorCellBefore.puzzle.solutions.head
    val result = game.openDoor(doorPosition, puzzleSolution)
    assertTrue(result.isRight)
    val doorCellAfter = game.getMaze.getCell(doorPosition).asInstanceOf[DoorCell]
    assertTrue(doorCellAfter.isOpen, "Door should be open if the answer is correct")

  @Test
  def testOpenNonDoorCell(): Unit =
    val wallCell = maze.wallCells.head
    val wallCellCoords = wallCell.position
    val floorCellAdjacentWallCell =
      adjacentPositions(wallCellCoords).
        find(pos => game.getMaze.isWalkable(pos)).
        get
    game.player = Player(floorCellAdjacentWallCell, game.player.lives, game.player.score)
    val result = game.openDoor(wallCellCoords, "Answer")
    assertTrue(result.isLeft)
    assertEquals("(Game) This is not a door", result.left.get)

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