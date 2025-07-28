/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

import controller.Controller
import model.Game
import model.utils.GameSettings
import view.{GameView, MenuView}

object Main extends App:

  new MenuView(difficulty => {
    val gameSettings = GameSettings.fromDifficulty(difficulty)
    val game = Game(gameSettings)
    game.startGame()
    val view = GameView(game)
    val controller = Controller(view, game)
  })