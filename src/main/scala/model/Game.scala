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

  /**
   * Resets the class' variable.
   */
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

  /**
   * Used to start the game.
   */
  def startGame(): Unit = resetLevel(1, settings.numLives, 0)

  implicit def givenMaze: Maze = maze

  /**
   * Checks if the game is finished.
   */
  def finished(): Boolean = isFinished

  /**
   * Checks if the user has won.
   */
  def victory(): Boolean = isVictory

  def getMaze: Maze = maze

  private inline def unless(condition: => Boolean)(block: => Unit): Unit =
    if !condition then block

  /**
   * Updates the current game state.
   */
  def updateGameState(): Unit =
    unless(isFinished) {
      val cellsOccupied = Set(player.position)
      val (updatedGuardians, _) = guardians.foldLeft((List.empty[Guardian], cellsOccupied)) {
        case ((acc, occupied), guardian) =>
          val (gx, gy) = (guardian.position.row, guardian.position.col)
          val (px, py) = (player.position.row, player.position.col)
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
      maze = maze.decreaseTurnsLockedDoors

      if maze.isOnDoor(player.position) then
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

  /**
   * Moves the player in a new position.
   *
   * @param toPosition the destination of the movement.
   * @return nothing in case of success, an error message otherwise.
   */
  def movePlayerTo(toPosition: Position): Either[String, Unit] =
    if isFinished then Left("Game finished!")
    else
      val fromPosition = player.position
      val validMove =
        fromPosition.isAdjacent(toPosition)
        && !isCellOccupied(toPosition)
        && maze.isWalkable(toPosition)
      if validMove then
        fromPosition.directionBetween(toPosition).foreach(direction => player = player.move(direction))
        Right(())
      else Left("Invalid move")

  private def isCellOccupied(position: Position): Boolean = guardians.exists(_.position == position)

  /**
   * Opens a door.
   *
   * @param doorPosition position of the door.
   * @param userAnswer the answer provided by the user.
   * @return a message of success, otherwise a message of failure.
   */
  def openDoor(doorPosition: Position, userAnswer: String): Either[String, String] =
    if !player.position.isAdjacent(doorPosition) then Left("Player should be adjacent to the door")
    else
      maze.getCell(doorPosition) match
        case door: DoorCell if door.isOpen => Left("Door is already open")
        case door: DoorCell if door.isBlocked => Left(s"Door is blocked for ${door.turnsLeft} turns")
        case door: DoorCell =>
          if door.puzzle.checkAnswer(userAnswer) then
            maze = maze.unlockDoorAt(doorPosition)
            Right("Door opened")
          else
            maze = maze.blockDoorAt(doorPosition, settings.lockDoorInTurns)
            Left("Puzzle failed")
        case _ => Left("This is not a door")

  /**
   * Provides a puzzle.
   */
  def startLogicChallenge(): Puzzle =
    val puzzle = PuzzleRepository.randomPuzzle()
    currentPuzzle = Some(puzzle)
    puzzle

  /**
   * Computes the fight against a guardian using logic.
   *
   * @param guardian the guardian to defeat.
   * @param answer the answer of the riddle.
   * @return a message of success, otherwise a message of failure.
   */
  def fightLogic(guardian: Guardian, answer: String): Either[String, String] =
    currentPuzzle match
      case Some(puzzle) =>
        currentPuzzle = None
        val result =
          if puzzle.checkAnswer(answer) then
            player.
              addScore(50).
              fold (
                error => Left(s"Score update failed: $error"),
                newPlayer =>
                  player = newPlayer
                  Right("Guardian defeated!")
              )
          else
            player.
              loseLife().
              fold(
                error => Left(s"Lose life error: $error"),
                newPlayer =>
                  player = newPlayer
                  Left("Wrong answer, you lost a life")
              )
        guardians = guardians.filterNot(_ == guardian)
        result
      case None => Left("No active puzzle")

  /**
   * Computes the fight against a guardian using luck.
   *
   * @param guardian the guardian to defeat.
   * @return a message of success, otherwise a message of failure.
   */
  def fightLuck(guardian: Guardian): Either[String, String] =
    val win = Random.nextBoolean()
    val result =
      if win then
        player.
          addScore(20).
          fold(
            error => Left(s"Score update failed: $error"),
            newPlayer =>
              player = newPlayer
              Right("Guardian defeated!")
          )
      else
        player.
          loseLife().
          fold(
            error => Left(s"Lose life error: $error"),
            newPlayer =>
              player = newPlayer
              Right("You were unlucky, you lost the fight")
          )
    guardians = guardians.filterNot(_ == guardian)
    result

  def guardiansAtPlayer(): List[Guardian] =
    guardians.filter(guardian => player.position.isAdjacent(guardian.position))

  def setPlayerPosition(position: Position): Unit =
    player = Player(position, player.lives, player.score)

  def setPlayerLives(lives: Int): Unit =
    player = Player(player.position, lives, player.score)