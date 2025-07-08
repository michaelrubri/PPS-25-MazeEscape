package controller

import model.{Game, Guardian}
import model.map.{DoorCell, FloorCell, Maze}

class GameController(/*view: GameView,*/ game: Game, maze: Maze) extends UserActionHandler:

  override def onAction(action: UserAction): Unit =
    action match
      case UserAction.ClickCell(pos) =>
        // logica di routing: spostamento, apertura porta o incontro
        def handleClick(pos: (Int, Int)): Unit =
          maze.getCell(pos._1, pos._2) match
            case _: FloorCell    => onAction(UserAction.AttemptMove(pos))
            case _: DoorCell     => onAction(UserAction.AttemptOpenDoor(pos))
            case _: Guardian     =>
              // bisogna implementare anche la view
              view.showFightChoice { choice =>
                val cmd = if choice == "logic" then UserAction.FightLogic else UserAction.FightLuck
                onAction(cmd)
              }

      case UserAction.AttemptMove(pos) =>
        game.movePlayerTo(pos)
        view.render(game)

// altri metodi in actionHandler