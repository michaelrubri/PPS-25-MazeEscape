/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.map.{DoorCell, Maze}
import model.puzzle.{Puzzle, PuzzleRepository}
import model.util.GameSettings
import scala.compiletime.uninitialized
import scala.util.Random

class Game(val settings: GameSettings):
  given maze: Maze = Maze.generate(settings.mazeSize)
  val player: Player = Player(maze.randomFloorCell(), settings.numLives, 0)
  var guardians: List[Guardian] = Maze.spawnGuardians(settings.numGuardians).map{case (x, y) => Guardian(x, y)}
  private var doors: List[DoorCell] = uninitialized
  private var currentTurn: Int = uninitialized
  private var isFinished: Boolean = uninitialized
  private var isVictory: Boolean = uninitialized
  private var currentPuzzle: Option[Puzzle] = uninitialized

  def finished(): Boolean = isFinished

  def victory(): Boolean = isVictory

  def startGame(): Unit =
    doors = maze.doorCells
    currentTurn = 0
    isFinished = false
    isVictory = false
    currentPuzzle = None

  private inline def unless(condition: => Boolean)(block: => Unit): Unit =
    if !condition then block

  def updateGameState(): Unit =
    unless(isFinished) {
      val entitiesPositionAfterTurn = guardians.foldLeft(Set(player.position)) { (occupied, guardian) =>
        val newPosition = guardian.intercept(player.position)
        if maze.isWalkable(newPosition) && !occupied(newPosition) then guardian.updatePosition(newPosition)
        occupied + guardian.position
      }
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

  def movePlayerTo(toPosition: (Int, Int)): Either[String, Unit] =
    if isFinished then Left("(Game) Game finished!")
    else
      val fromPosition = player.position
      val validMove =
        isAdjacent(fromPosition, toPosition)
        && !isCellOccupied(toPosition)
        && maze.isWalkable(toPosition)
      if validMove then
        directionBetween(fromPosition, toPosition).foreach(player.move)
        Right(())
      else Left("(Game) Invalid move")

  private def isCellOccupied(position: (Int, Int)): Boolean = guardians.exists(_.position == position)

  def openDoor(at: (Int, Int), userAnswer: String): Either[String, String] =
    // if !isAdjacent(player.position, at) then Left("(Game) Player should be adjacent to the door")
    // else
      maze.getCell(at._1, at._2) match
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
            player.addScore(50)
            Right("(Game) Guardian defeated!")
          else
            player.loseLife()
            Left("(Game) Wrong answer, you lost a life")
        guardians = guardians.filterNot(_ == guardian)
        result
      case None => Left("(Game) No active puzzle")

  def fightLuck(guardian: Guardian): Either[String, String] =
    val win = Random.nextBoolean()
    val result =
      if win then
        player.addScore(20)
        Right("(Game) Guardian defeated!")
      else
        player.loseLife()
        Left("(Game) You were unlucky, you lost the fight")
    guardians = guardians.filterNot(_ == guardian)
    result

  def guardianAtPlayer(): List[Guardian] =
    guardians.filter(guardian => isAdjacent(guardian.position, player.position))

  def isAdjacent(from: (Int, Int), to: (Int, Int)): Boolean =
    val (dx, dy) = ((from._1 - to._1).abs, (from._2 - to._2).abs)
    (dx == 1 && dy == 0) || (dx == 0 && dy == 1)

  private def directionBetween(from: (Int, Int), to: (Int, Int)): Option[Direction] =
    (to._1 - from._1, to._2 - from._2) match
      case (1, 0) => Some(Direction.Right)
      case (-1, 0) => Some(Direction.Left)
      case (0, 1) => Some(Direction.Up)
      case (0, -1) => Some(Direction.Down)
      case _ => None