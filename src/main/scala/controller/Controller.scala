/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package controller

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
      case _ =>
    }

  override def onAction(action: UserAction): Unit = action match
    case UserAction.ClickCell(position) => handleClick(position)

    case UserAction.AttemptMove(position) =>
      game.movePlayerTo(position) match
        case Right(_) =>
          game.updateGameState()
          view.updateView()
        case Left(error) =>
          view.showMessage(error)

    case UserAction.AttemptOpenDoor(position, answer) =>
      game.openDoor(position, answer) match
        case Right(_) =>
          game.updateGameState()
          view.updateView()
        case Left(error) =>
          view.showMessage(error)

    case UserAction.FightLogic(answer) =>
      game.fightLogic(answer) match
        case Right(_) =>
          view.showMessage("Guardian defeated!")
          view.updateView()
        case Left(error) =>
          view.showMessage(error)
          view.updateView()

    case UserAction.FightLuck =>
      game.fightLuck() match
        case Right(_) =>
          view.showMessage("You are lucky, you have won!")
          view.updateView()
        case Left(error) =>
          view.showMessage(error)
          view.updateView()

    case UserAction.Restart =>
      game.startGame()
      view.updateView()

    case UserAction.InvalidAction(_) => view.showMessage("Invalid action")

  private def handleClick(position: (Int, Int)): Unit =
    game.maze.getCell(position._1, position._2) match

      case _: FloorCell => onAction(UserAction.AttemptMove(position))

      case door: DoorCell if !door.isOpen =>
        view.showPuzzle(
          door.puzzle.question,
          answer => {
            onAction(UserAction.AttemptOpenDoor(position, answer))
            scala.runtime.BoxedUnit.UNIT
          }
        )

      case _: DoorCell => view.showMessage("Door is already open")

      case _ if game.guardianAtPlayer().nonEmpty =>
        view.showFightChoice { choice =>
          if choice == "logic" then
            val puzzle = game.startLogicChallenge()
            view.showPuzzle(
              puzzle.question,
              (userAns: String) => {
                onAction(UserAction.FightLogic(userAns))
                scala.runtime.BoxedUnit.UNIT
              }
            )
          else
            onAction(UserAction.FightLuck)

          scala.runtime.BoxedUnit.UNIT
        }

      case _ => view.showMessage("Cell unreachable.")