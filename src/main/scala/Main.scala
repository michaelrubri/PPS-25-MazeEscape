/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

import model.*
import model.util.GameSettings
import view.*
import controller.*

object Main extends App:

  println("Maze Escape")

  private val gameSettings: GameSettings = GameSettings.fromDifficulty("easy")
  private val game: Game = Game(gameSettings)
  private val view: GameView = GameView(game)
  private val controller: Controller = Controller(view, game)