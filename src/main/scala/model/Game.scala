/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.entities.{Guardian, Player}
import model.map.{DoorCell, Maze}
import model.prolog.{MazePrologTheory, Scala2Prolog}
import model.puzzle.{Puzzle, PuzzleRepository}
import model.strategies.GuardianStrategy
import model.utils.Position.*
import model.utils.{GameSettings, Position}
import scala.compiletime.uninitialized
import scala.util.Random

class Game(val settings: GameSettings):
  var player: Player = uninitialized
  var guardians: List[Guardian] = uninitialized
  var maze: Maze = uninitialized
  private var guardianStrategy: GuardianStrategy = uninitialized
  private var doors: List[DoorCell] = uninitialized
  private var currentTurn: Int = uninitialized
  private var isFinished: Boolean = uninitialized
  private var isVictory: Boolean = uninitialized
  private var currentPuzzle: Option[Puzzle] = uninitialized
  private var currentLevel: Int = uninitialized
  private val maxLevels: Int = settings.levelsToWin

  private def resetLevel(level: Int, 
                         preservingLives: Int, 
                         preservingScore: Int): Unit =
    maze = Maze.generate(settings.mazeSize)
    val theory = MazePrologTheory(maze)
    val engine = Scala2Prolog.mkPrologEngine(theory)
    guardianStrategy = new GuardianStrategy(engine)
    player = Player(maze.randomFloorCell(), preservingLives, preservingScore)
    guardians = maze.spawnGuardians(settings.numGuardians).map(pos => Guardian(pos))
    doors = maze.doorCells
    currentTurn = 0
    currentPuzzle = None
    currentLevel = level
    isFinished = false
    isVictory = false

  def startGame(): Unit = resetLevel(1, settings.numLives, 0)

  /*def startGame(): Unit =
    maze = Maze.generate(settings.mazeSize)
    player = Player(maze.randomFloorCell(), settings.numLives, 0)
    guardians = Maze.spawnGuardians(settings.numGuardians).map { case Position(row, col) => Guardian(Position(row, col)) }
    val theory = model.prolog.MazePrologTheory(maze)
    val engine = Scala2Prolog.mkPrologEngine(theory)
    guardianStrategy = new GuardianStrategy(engine)
    doors = maze.doorCells
    currentTurn = 0
    isFinished = false
    isVictory = false
    currentPuzzle = None*/

  implicit def givenMaze: Maze = maze

  def finished(): Boolean = isFinished

  def victory(): Boolean = isVictory

  def getMaze: Maze = maze

  private inline def unless(condition: => Boolean)(block: => Unit): Unit =
    if !condition then block

  def updateGameState(): Unit =
    unless(isFinished) {
      val cellsOccupied = Set(player.position)
      val (updatedGuardians, _) = guardians.foldLeft((List.empty[Guardian], cellsOccupied)) {
        case ((acc, occupied), guardian) =>
          val (gx, gy) = (guardian.position.row, guardian.position.col)
          val (px, py) = (player.position.row, player.position.col)
          // val (nx, ny) = guardianStrategy.nextMove(gx, gy, px, py)
          // val newPos = Position(nx, ny)
          val newPos = guardianStrategy.nextMove(gx, gy, px, py)
          val updatedGuardian =
            if maze.isWalkable(newPos) && !occupied(newPos) then
              guardian.updatePosition(newPos)
            else
              guardian
          (acc :+ updatedGuardian, occupied + updatedGuardian.position)
      }
      guardians = updatedGuardians
      currentTurn += 1
      // doors.foreach(_.decrementTurns())
      maze = maze.decreaseTurnsLockedDoors

      /*isFinished =
        player.lives <= 0 ||
        currentTurn >= settings.maxDuration ||
        maze.isExit(player.position)
      isVictory = maze.isExit(player.position)*/

      if maze.isExit(player.position) then
        if currentLevel < maxLevels then
          resetLevel(currentLevel + 1, player.lives, player.score)
          return
        else
          isFinished = true
          isVictory = true
          return

      if player.lives <= 0 || currentTurn >= settings.maxDuration then
        isFinished = true
        isVictory = false
    }

  def endGame(): Boolean =
    isFinished = true
    isVictory

  def movePlayerTo(toPosition: Position): Either[String, Unit] =
    if isFinished then Left("(Game) Game finished!")
    else
      val fromPosition = player.position
      val validMove =
        fromPosition.isAdjacent(toPosition)
        && !isCellOccupied(toPosition)
        && maze.isWalkable(toPosition)
      if validMove then
        fromPosition.directionBetween(toPosition).foreach(direction => player = player.move(direction))
        Right(())
      else Left("(Game) Invalid move")

  private def isCellOccupied(position: Position): Boolean = guardians.exists(_.position == position)

  def openDoor(doorPosition: Position, userAnswer: String): Either[String, String] =
    if !player.position.isAdjacent(doorPosition) then Left("(Game) Player should be adjacent to the door")
    else
      maze.getCell(doorPosition) match
        case door: DoorCell if door.isOpen => Left("(Game) Door is already open")
        case door: DoorCell if door.isBlocked => Left(s"(Game) Door is blocked for ${door.turnsLeft} turns")
        case door: DoorCell =>
          if door.puzzle.checkAnswer(userAnswer) then
            maze = maze.unlockDoorAt(doorPosition)
            Right("(Game) Door opened")
          else
            maze = maze.blockDoorAt(doorPosition, settings.lockDoorInTurns)
            Left("(Game) Puzzle failed")
        case _ => Left("(Game) This is not a door")

  def startLogicChallenge(): Puzzle =
    val puzzle = PuzzleRepository.randomPuzzle()
    currentPuzzle = Some(puzzle)
    puzzle

  def fightLogic(guardian: Guardian, answer: String): Either[String, String] =
    currentPuzzle match
      case Some(puzzle) =>
        currentPuzzle = None
        val result =
          if puzzle.checkAnswer(answer) then
            player.
              addScore(50).
              fold (
                error => Left(s"(Game) Score update failed: $error"),
                newPlayer =>
                  player = newPlayer
                  Right("(Game) Guardian defeated!")
              )
          else
            player.
              loseLife().
              fold(
                error => Left(s"(Game) Lose life error: $error"),
                newPlayer =>
                  player = newPlayer
                  Left("(Game) Wrong answer, you lost a life")
              )
        guardians = guardians.filterNot(_ == guardian)
        result
      case None => Left("(Game) No active puzzle")

  def fightLuck(guardian: Guardian): Either[String, String] =
    val win = Random.nextBoolean()
    val result =
      if win then
        player.
          addScore(20).
          fold(
            error => Left(s"(Game) Score update failed: $error"),
            newPlayer =>
              player = newPlayer
              Right("(Game) Guardian defeated!")
          )
      else
        player.
          loseLife().
          fold(
            error => Left(s"(Game) Lose life error: $error"),
            newPlayer =>
              player = newPlayer
              Right("(Game) You were unlucky, you lost the fight")
          )
    guardians = guardians.filterNot(_ == guardian)
    result

  def guardiansAtPlayer(): List[Guardian] =
    guardians.filter(guardian => player.position.isAdjacent(guardian.position))