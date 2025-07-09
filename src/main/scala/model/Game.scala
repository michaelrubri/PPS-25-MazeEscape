/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model

import model.map.{DoorCell, Maze}
import model.puzzle
import model.puzzle.{Puzzle, PuzzleRepository}
import model.util.GameSettings
import scala.compiletime.uninitialized
import scala.util.Random

class Game(val settings: GameSettings):
  val maze: Maze = Maze.generate(settings.mazeSize)
  val player: Player = Player((0, 0), settings.numLives, 0)
  val guardians: List[Guardian] = Maze.spawnGuardians(settings.numGuardians).map{ case (x, y) => Guardian(x, y)}
  private var currentTurn: Int = uninitialized
  private var isFinished: Boolean = uninitialized
  private var isVictory: Boolean = uninitialized
  private var currentPuzzle: Option[Puzzle] = uninitialized

  given instanceMaze: Maze = maze

  def startGame(): Unit =
    currentTurn = 0
    isFinished = false
    isVictory = false
    currentPuzzle = None

  def updateGameState(): Unit =
    if isFinished then return

    guardians.foreach(_.intercept(player.position))
    currentTurn += 1

    isFinished =
      player.lives <= 0 ||
      currentTurn >= settings.maxDuration ||
      maze.isExit(player.position)

    isVictory = maze.isExit(player.position)
      
  def endGame(): Boolean =
    isFinished = true
    isVictory

  def movePlayerTo(position: (Int, Int)): Either[String, Unit] =
    val from = player.position
    val validMove = isAdjacent(from, position) && maze.isWalkable(position)
    if isFinished then Left("Game finished!")
    else if validMove then
      directionBetween(from, position).foreach(player.move)
      Right(())
    else Left("Invalid move")

  private def isAdjacent(from: (Int, Int), to: (Int, Int)): Boolean =
    val (dx, dy) = ((from._1 - to._1).abs, (from._2 - to._2).abs)
    (dx == 1 && dy == 0) || (dx == 0 && dy == 1)

  private def directionBetween(from: (Int, Int), to: (Int, Int)): Option[Direction] =
    (to._1 - from._1, to._2 - from._2) match
      case (1, 0) => Some(Direction.Right)
      case (-1, 0) => Some(Direction.Left)
      case (0, 1) => Some(Direction.Down)
      case (0, -1) => Some(Direction.Up)
      case _ => None

  private def startLogicChallenge(): Puzzle =
    val puzzle = PuzzleRepository.randomPuzzle()
    currentPuzzle = Some(puzzle)
    puzzle

  def attemptFightLogic(answer: String): Either[String, Unit] =
    val puzzle = startLogicChallenge()
    currentPuzzle match
      case Some(puzzle) if puzzle.checkAnswer(answer) =>
        currentPuzzle = None
        player.addScore(50)
        Right(())
      case Some(puzzle) =>
        currentPuzzle = None
        player.loseLife()
        Left("Wrong answer, you lost a life")
      case None => Left("No active puzzle")

  def attemptFightLuck(): Either[String, Unit] =
    val win = Random.nextBoolean()
    if win then
      player.addScore(20)
      Right(())
    else
      player.loseLife()
      Left("You were unlucky, you lost the fight")