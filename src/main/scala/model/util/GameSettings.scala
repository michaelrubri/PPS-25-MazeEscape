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
 * @param levels number of intermediate levels to overcome to get out of the maze.
 * @param mazeSize maze's dimension.
 * @param numGuardians total number of guardians per level.
 * @param numLives number of player's lives.
 * @param lockDoorInTurns number of turns the user must wait if he fails to open the door.
 */
class GameSettings(
                    val difficulty: String,
                    val maxDuration: Int,
                    val levels: Int,
                    val mazeSize: Int,
                    val numGuardians: Int,
                    val numLives: Int,
                    val lockDoorInTurns: Int
                  ):

  /**
   * Gets the parameters of the actual game.
   *
   * @return the parameters of the game.
   */
  def getParameters: Map[String, Int] =
    Map(
      "maxDuration"     -> maxDuration,
      "levels"          -> levels,
      "mazeSize"        -> mazeSize,
      "numGuardians"    -> numGuardians,
      "numLives"        -> numLives,
      "lockDoorInTurns" -> lockDoorInTurns
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
      case "easy" => GameSettings("Easy", maxDuration = 90, levels = 1, mazeSize = 20, numGuardians = 2, numLives = 3, lockDoorInTurns = 2)
      case "medium" => GameSettings("Medium", maxDuration = 130, levels = 2, mazeSize = 20, numGuardians = 2, numLives = 4, lockDoorInTurns = 3)
      case "hard" => GameSettings("Hard", maxDuration = 150, levels = 3, mazeSize = 20, numGuardians = 3, numLives = 5, lockDoorInTurns = 4)
      case unknown => throw IllegalArgumentException(s"Difficulty level unknown: $unknown")

  /**
   * Creates custom game settings based on user choices.
   *
   * @param difficulty difficulty level of the game.
   * @param maxDuration maximum duration of the game in turns.
   * @param levels number of intermediate levels to overcome to get out of the maze.
   * @param mazeSize maze's dimension.
   * @param numGuardians total number of guardians per level.
   * @param numLives number of player's lives.
   * @param lockDoorInTurns number of turns the user must wait if he fails to open the door.
   * @return the configured game settings class.
   */
  def apply(
             difficulty: String,
             maxDuration: Int,
             levels: Int,
             mazeSize: Int,
             numGuardians: Int,
             numLives: Int,
             lockDoorInTurns: Int
           ): GameSettings =
    new GameSettings(difficulty, maxDuration, levels, mazeSize, numGuardians, numLives, lockDoorInTurns)