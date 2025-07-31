/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package controller

import controller.UserAction.FightLuck
import model.Game
import model.map.{DoorCell, FloorCell}
import model.utils.Position.*
import model.utils.Position
import view.GameView
import view.utils.*

class Controller(view: GameView, game: Game) extends UserActionHandler:

  // game.startGame()
  view.updateView()
  initEventBus()
  
  private def initEventBus(): Unit = 
    EventBus.subscribe {
      case clickEvent: CellClickEvent =>
        val position = Position(clickEvent.getX, clickEvent.getY)
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
          if game.finished() then
            view.showEndGameMenu(
              game.victory(),
              choice =>
                if choice == "restart" then onAction(UserAction.Restart) 
                else System.exit(0)
                scala.runtime.BoxedUnit.UNIT
            )
          else
            game.guardiansAtPlayer().headOption.foreach(guardian =>
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
        case Right(message) =>
          game.updateGameState()
          view.showMessage(message)
          view.updateView()
        case Left(error) =>
          view.showMessage(error)

    case UserAction.FightLogic(guardian, answer) =>
      game.fightLogic(guardian, answer) match
        case Right(message) =>
          view.showMessage(message)
          view.updateView()
        case Left(error) =>
          view.showMessage(error)
          view.updateView()

    case UserAction.FightLuck(guardian) =>
      game.fightLuck(guardian) match
        case Right(message) =>
          view.showMessage(message)
          view.updateView()
        case Left(error) =>
          view.showMessage(error)
          view.updateView()

    case UserAction.Restart =>
      game.startGame()
      view.updateView()
      scala.runtime.BoxedUnit.UNIT

    case UserAction.InvalidAction(error) => view.showMessage(s"(Controller) Invalid action: $error")

  private def handleClick(position: Position): Unit =
    game.getMaze.getCell(position) match

      case _: FloorCell => onAction(UserAction.AttemptMove(position))

      case door: DoorCell if door.isOpen => onAction(UserAction.AttemptMove(position))

      case door: DoorCell if !door.isOpen && door.isBlocked =>
        view.showMessage(s"(Controller) Door is blocked for ${door.turnsLeft} turns")

      case door: DoorCell if !door.isOpen =>
        view.showPuzzle(
          door.puzzle.question,
          userAnswer => {
            onAction(UserAction.AttemptOpenDoor(position, userAnswer))
            scala.runtime.BoxedUnit.UNIT
          }
        )

      case _ => view.showMessage("(Controller) Cell unreachable.")