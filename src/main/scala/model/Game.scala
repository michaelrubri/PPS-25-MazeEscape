/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.entities.{Direction, Guardian, Player}
import model.map.{DoorCell, Maze}
import model.puzzle.{Puzzle, PuzzleRepository}
import model.utils.Position._
import model.utils.{GameSettings, Position}
import scala.compiletime.uninitialized
import scala.util.Random

class Game(val settings: GameSettings):
  /*given maze: Maze = Maze.generate(settings.mazeSize)
  var player: Player = Player(maze.randomFloorCell(), settings.numLives, 0)
  var guardians: List[Guardian] =
    Maze.spawnGuardians(settings.numGuardians).map{case Position(x, y) => Guardian(Position(x, y))}*/
  private var maze: Maze = uninitialized
  var player: Player = uninitialized
  var guardians: List[Guardian] = uninitialized
  private var doors: List[DoorCell] = uninitialized
  private var currentTurn: Int = uninitialized
  private var isFinished: Boolean = uninitialized
  private var isVictory: Boolean = uninitialized
  private var currentPuzzle: Option[Puzzle] = uninitialized

  def startGame(): Unit =
    maze = Maze.generate(settings.mazeSize)
    player = Player(maze.randomFloorCell(), settings.numLives, 0)
    guardians = Maze.spawnGuardians(settings.numGuardians).map { case Position(x, y) => Guardian(Position(x, y)) }
    doors = maze.doorCells
    currentTurn = 0
    isFinished = false
    isVictory = false
    currentPuzzle = None

  implicit def givenMaze: Maze = maze

  def finished(): Boolean = isFinished

  def victory(): Boolean = isVictory
  
  def getMaze: Maze = maze

  private inline def unless(condition: => Boolean)(block: => Unit): Unit =
    if !condition then block

  def updateGameState(): Unit =
    unless(isFinished) {
      val positionsOccupied = Set(player.position)
      val (updatedGuardians, _) = guardians.foldLeft((List.empty[Guardian], positionsOccupied)) {
        case ((acc, occ), guardian) => 
          val newPosition = guardian.intercept(player.position)
          if maze.isWalkable(newPosition) && !positionsOccupied(newPosition) then
            val updatedGuardian = guardian.updatePosition(newPosition)
            (acc :+ updatedGuardian, occ + newPosition)
          else (acc :+ guardian, occ)
      }
      guardians = updatedGuardians
      currentTurn += 1
      doors.foreach(_.decrementTurns())
      isFinished =
        player.lives <= 0 ||
        currentTurn >= settings.maxDuration ||
        maze.isExit(player.position)
      isVictory = maze.isExit(player.position)
    }
      
  def endGame(): Boolean =
    isFinished = true
    isVictory

  def movePlayerTo(toPosition: Position): Either[String, Unit] =
    if isFinished then Left("(Game) Game finished!")
    else
      val fromPosition = player.position
      val validMove =
        isAdjacent(fromPosition, toPosition)
        && !isCellOccupied(toPosition)
        && maze.isWalkable(toPosition)
      if validMove then
        directionBetween(fromPosition, toPosition).foreach(direction => player = player.move(direction))
        Right(())
      else Left("(Game) Invalid move")

  private def isCellOccupied(position: Position): Boolean = guardians.exists(_.position == position)

  def openDoor(at: Position, userAnswer: String): Either[String, String] =
    if !isAdjacent(player.position, at) then Left("(Game) Player should be adjacent to the door")
    else
      maze.getCell(at.x, at.y) match
        case door: DoorCell if door.isOpen => Left("(Game) Door is already open")
        case door: DoorCell if door.isBlocked => Left(s"(Game) Door is blocked for ${door.turnsLeft} turns")
        case door: DoorCell =>
          if door.puzzle.checkAnswer(userAnswer) then
            door.unlock()
            Right("(Game) Door opened")
          else
            door.blockFor(settings.lockDoorInTurns)
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

  def guardianAtPlayer(): List[Guardian] =
    guardians.filter(guardian => isAdjacent(guardian.position, player.position))

  def isAdjacent(from: Position, to: Position): Boolean =
    val (dx, dy) = ((from.x - to.x).abs, (from.y - to.y).abs)
    (dx == 1 && dy == 0) || (dx == 0 && dy == 1)

  private def directionBetween(from: Position, to: Position): Option[Direction] =
    (to.x - from.x, to.y - from.y) match
      case (1, 0)   => Some(Direction.Right)
      case (-1, 0)  => Some(Direction.Left)
      case (0, 1)   => Some(Direction.Up)
      case (0, -1)  => Some(Direction.Down)
      case _ => None