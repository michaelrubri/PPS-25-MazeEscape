/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.map

import model.utils.Position
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import scala.compiletime.uninitialized

class MazeTest:

  private val size: Int = 10
  private var maze: Maze = uninitialized

  @BeforeEach
  def setup(): Unit = maze = Maze.generate(size)

  @Test
  def testIsWalkable(): Unit =
    for
      x <- 0 until size
      y <- 0 until size
    do
      val position = Position(x, y)
      val cell = maze.getCell(position)
      cell match
        case _: FloorCell   => assertTrue(maze.isWalkable(position), s"Expected walkable")
        case _: WallCell    => assertFalse(maze.isWalkable(position), s"Expected not walkable")
        case door: DoorCell =>
          assertFalse(maze.isWalkable(position), s"Expected not walkable when door is closed")
          val updatedMaze = maze.unlockDoorAt(door.position)
          assertTrue(updatedMaze.isWalkable(position), s"Expected walkable when door is open")

  @Test
  def testIsOnDoor(): Unit =
    val doors = maze.doorCells
    assertTrue(doors.nonEmpty, "Should have at least one door")
    val doorsNumber = doors.size
    assertEquals(doors.count(!_.isOpen), doorsNumber, "All doors should be initially closed")
    val doorPos = doors.head.position
    assertFalse(maze.isOnDoor(doorPos), "Door should not be an exit when closed")
    val updatedMaze = maze.unlockDoorAt(doorPos)
    assertTrue(updatedMaze.isOnDoor(doorPos), s"Door should be an exit when open")
    val floorPosition = updatedMaze.floorCells.head.position
    assertFalse(updatedMaze.isOnDoor(floorPosition), "Floor should never be an exit")

  @Test
  def testBlockDoorAndDecrementTurns(): Unit =
    val doorPos = maze.doorCells.head.position
    val updatedMaze1 = maze.blockDoorAt(doorPos, 3)
    val door1 = updatedMaze1.doorCells.head
    assertTrue(door1.isBlocked)
    assertEquals(3, door1.turnsLeft)
    val updatedMaze2 = updatedMaze1.decreaseTurnsLockedDoors
    val updatedDoor1 = updatedMaze2.doorCells.head
    assertEquals(2, updatedDoor1.turnsLeft)

  @Test
  def testSpawnGuardians(): Unit =
    given Maze = maze
    val guardians = maze.spawnGuardians(2)
    assertEquals(2, guardians.size, "Should spawn correct number of guardians")
    assertEquals(2, guardians.toSet.size, "Guardian positions should be unique")
    for position <- guardians do
      assertTrue(
        position.row >= 0 &&
        position.row < size &&
        position.col >= 0 &&
        position.col < size,
        "Guardians should be inside the maze"
      )
      assertTrue(maze.floorCells.map(_.position).contains(position), "Guardian should be spawned on a floor")