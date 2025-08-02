package controller

import model.Game
import model.entities.Player
import model.utils.*
import model.utils.Position.*
import model.map.{DoorCell, FloorCell, Maze}
import model.entities.Guardian
import view.*
import view.utils.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue

import scala.compiletime.uninitialized

class ControllerTest:

  private val settings = GameSettings.fromDifficulty("easy")
  private val game = Game(settings)
  private var maze: Maze = uninitialized
  private var view: GameView = uninitialized
  private var controller: Controller = uninitialized
  given Maze = maze

  @BeforeEach
  def setup(): Unit =
    game.startGame()
    view = new GameView(game)
    controller = Controller(view, game)
    maze = game.getMaze


  @Test
  def testAttemptMoveToValidFloor(): Unit =
    val current = game.player.position
    val adjacent =
      Position.adjacentPositions(current).find(p =>
        maze.isWalkable(p) &&
          !game.guardians.exists(_.position == p)
      )
    assumeTrue(adjacent.nonEmpty)
    controller.onAction(UserAction.AttemptMove(adjacent.get))
    assertEquals(adjacent.get, game.player.position)

  @Test
  def testAttemptMoveToInvalidCell(): Unit =
    val current = game.player.position
    val invalid =
      Position.adjacentPositions(current)
        .find(p => !maze.isWalkable(p))
    controller.onAction(UserAction.AttemptMove(invalid.get))
    assertEquals(current, game.player.position)

  @Test
  def testAttemptToOpenBlockedDoor(): Unit =
    val door = game.getMaze.doorCells.head
    val adj = Position.adjacentPositions(door.position)
      .find(p => game.getMaze.isWalkable(p)).get
    game.setPlayerPosition(adj)
    val current = game.player.position
    game.maze = game.getMaze.blockDoorAt(door.position, 2)
    controller.onAction(UserAction.ClickCell(door.position))
    assertEquals(current, game.player.position)

  @Test
  def testAttemptToOpenUnblockedDoor(): Unit =
    val door = game.getMaze.doorCells.head
    val adj = Position.adjacentPositions(door.position)
      .find(p => game.getMaze.isWalkable(p)).get
    game.setPlayerPosition(adj)
    game.maze = game.getMaze.unlockDoorAt(door.position)
    controller.onAction(UserAction.ClickCell(door.position))
    assertEquals(door.position, game.player.position)


  @Test
  def testRestartAction(): Unit =
    game.setPlayerLives(1)
    controller.onAction(UserAction.Restart)
    assertEquals(game.settings.numLives, game.player.lives)
