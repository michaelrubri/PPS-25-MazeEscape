/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package controller

import controller.UserAction.FightLuck
import model.Game
import model.map.{DoorCell, FloorCell}
import view.GameView
import view.utils.*

class Controller(view: GameView, game: Game) extends UserActionHandler:

  view.updateView()
  initEventBus()
  
  private def initEventBus(): Unit = 
    EventBus.subscribe {
      case clickEvent: CellClickEvent =>
        val position = (clickEvent.getX, clickEvent.getY)
        onAction(UserAction.ClickCell(position))
      case _ => ()
    }

  override def onAction(action: UserAction): Unit = action match
    case UserAction.ClickCell(position) => handleClick(position)

    case UserAction.AttemptMove(position) =>
      game.movePlayerTo(position) match
        case Right(_) =>
          game.updateGameState()
          view.updateView()
          if game.finished() && game.victory() then view.showMessage("You escaped! Congratulations!")
          else
            game.guardianAtPlayer().headOption.foreach(guardian =>
              view.showFightChoice { choice =>
                if choice == "logic" then
                  val puzzle = game.startLogicChallenge()
                  view.showPuzzle(
                    puzzle.question,
                    userAnswer => {
                      onAction(UserAction.FightLogic(guardian, userAnswer))
                      scala.runtime.BoxedUnit.UNIT
                    }
                  )
                else onAction(FightLuck(guardian))
                scala.runtime.BoxedUnit.UNIT
              }
            )
        case Left(error) =>
          view.showMessage(error)

    case UserAction.AttemptOpenDoor(position, answer) =>
      game.openDoor(position, answer) match
        case Right(_) =>
          game.updateGameState()
          view.updateView()
        case Left(error) =>
          view.showMessage(error)

    case UserAction.FightLogic(guardian, answer) =>
      game.fightLogic(guardian, answer) match
        case Right(_) =>
          view.showMessage("Guardian defeated!")
          view.updateView()
        case Left(error) =>
          view.showMessage(error)
          view.updateView()

    case UserAction.FightLuck(guardian) =>
      game.fightLuck(guardian) match
        case Right(_) =>
          view.showMessage("You were lucky, you won!")
          view.updateView()
        case Left(error) =>
          view.showMessage(error)
          view.updateView()

    case UserAction.Restart =>
      game.startGame()
      view.updateView()

    case UserAction.InvalidAction(error) => view.showMessage(s"Invalid action: $error")

  private def handleClick(position: (Int, Int)): Unit =
    game.maze.getCell(position._1, position._2) match

      case _: FloorCell => onAction(UserAction.AttemptMove(position))

      case door: DoorCell if !door.isOpen =>
        view.showPuzzle(
          door.puzzle.question,
          userAnswer => {
            onAction(UserAction.AttemptOpenDoor(position, userAnswer))
            scala.runtime.BoxedUnit.UNIT
          }
        )

      case _: DoorCell => view.showMessage("Door is already open")

      case _ => view.showMessage("Cell unreachable.")