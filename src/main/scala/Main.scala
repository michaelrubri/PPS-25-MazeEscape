/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

import model.*
import model.utils.GameSettings
import view.*
import controller.*
import java.util.function.Consumer

object Main extends App:
     new MenuView((difficulty: String) => {
       val gameSettings: GameSettings = GameSettings.fromDifficulty(difficulty)
       val game: Game = Game(gameSettings)
       game.startGame()
       val view: GameView = GameView(game)
       val controller: Controller = Controller(view, game)
     })