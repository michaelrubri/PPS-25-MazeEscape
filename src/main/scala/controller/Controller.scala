package controller

import model.Game
import model.map.{DoorCell, FloorCell}
import view.GameView
import view.utils.*

class Controller(view: GameView, game: Game) extends UserActionHandler:

  view.updateView()
  initEventBus()
  
  private def initEventBus(): Unit = 
    EventBus.subscribe { event =>
      event match 
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
        case Left(err) =>
          view.showMessage(err)

    case UserAction.FightLogic(ans) =>
      game.fightLogic(ans) match
        case Right(_) =>
          view.showMessage("Guardian defeated!")
          view.updateView()
        case Left(err) =>
          view.showMessage(err)
          view.updateView()

    case UserAction.FightLuck =>
      game.fightLuck() match
        case Right(_) =>
          view.showMessage("You are lucky, you have won!")
          view.updateView()
        case Left(err) =>
          view.showMessage(err)
          view.updateView()

    case UserAction.Restart =>
      game.startGame()
      view.updateView()

    case controller.UserAction.InvalidAction(_) => ???
  
  
  private def handleClick(pos: (Int, Int)): Unit =
    game.maze.getCell(pos._1, pos._2) match

      case _: FloorCell => onAction(UserAction.AttemptMove(pos))

      case door: DoorCell if !door.isOpen =>
        view.showPuzzle(
          door.puzzle.question,
          answer => {
            onAction(UserAction.AttemptOpenDoor(pos, answer))
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
    
    