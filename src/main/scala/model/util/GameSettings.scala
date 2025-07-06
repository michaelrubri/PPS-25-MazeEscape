/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.util

/**
 * Defines the parameters of the game.
 *
 * @param difficulty difficulty level of the game.
 * @param maxDuration maximum duration of the game in turns.
 * @param mazeSize number of intermediate levels to overcome to get out of the maze.
 * @param numGuardians total number of guardians per level.
 * @param numLives number of player's lives.
 * @param failDoorBlockTurns number of turns the user must wait if he fails to open the door.
 */
class GameSettings(
                  val difficulty: String,
                  val maxDuration: Int,
                  val mazeSize: Int,
                  val numGuardians: Int,
                  val numLives: Int,
                  val failDoorBlockTurns: Int
                  ):

  /**
   * Gets the parameters of the actual game.
   *
   * @return the parameters of the game.
   */
  def getParameters: Map[String, Int] =
    Map(
      "maxDuration"         -> maxDuration,
      "mazeSize"            -> mazeSize,
      "numGuardians"        -> numGuardians,
      "numLives"            -> numLives,
      "failDoorBlockTurns"  -> failDoorBlockTurns
    )

object GameSettings:

  /**
   * Creates default game settings based on difficulty level.
   *
   * @param difficulty the difficulty level of the game.
   * @return the configured game settings class.
   */
  def fromDifficulty(difficulty: String): GameSettings =
    difficulty.toLowerCase match
      case "easy" => GameSettings("Easy", maxDuration = 90, mazeSize = 1, numGuardians = 2, numLives = 3, failDoorBlockTurns = 2)
      case "medium" => GameSettings("Medium", maxDuration = 130, mazeSize = 2, numGuardians = 2, numLives = 4, failDoorBlockTurns = 3)
      case "hard" => GameSettings("Hard", maxDuration = 150, mazeSize = 3, numGuardians = 3, numLives = 5, failDoorBlockTurns = 4)
      case unknown => throw IllegalArgumentException(s"Difficulty level unknown: $unknown")

  /**
   * Creates custom game settings based on user choices.
   *
   * @param difficulty difficulty level of the game.
   * @param maxDuration maximum duration of the game in turns.
   * @param mazeSize number of intermediate levels to overcome to get out of the maze.
   * @param numGuardians total number of guardians per level.
   * @param numLives number of player's lives.
   * @param failDoorBlockTurns number of turns the user must wait if he fails to open the door.
   * @return the configured game settings class.
   */
  def apply(
           difficulty: String,
           maxDuration: Int,
           mazeSize: Int,
           numGuardians: Int,
           numLives: Int,
           failDoorBlockTurns: Int
           ): GameSettings =
    new GameSettings(difficulty, maxDuration, mazeSize, numGuardians, numLives, failDoorBlockTurns)