/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.map

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import scala.compiletime.uninitialized

class MazeTest:

  private val size = 10
  private var maze: Maze = uninitialized

  @BeforeEach
  def setup(): Unit =
    maze = Maze.generateBasic(size)

  @Test
  def testGridBordersAreWallOrDoor(): Unit =
    for
      x <- 0 until size
      y <- 0 until size
    do
      val cell = maze.getCell(x, y)
      val isBorder = x == 0 || y == 0 || x == size - 1 || y == size - 1
      if isBorder then
        if (x + y) % 7 == 0 then assertTrue(cell.isInstanceOf[DoorCell], s"Expected DoorCell at ($x, $y)")
        else assertTrue(cell.isInstanceOf[WallCell], s"Expected WallCell at ($x, $y)")
      else assertTrue(cell.isInstanceOf[FloorCell], s"Expected FloorCell at ($x, $y)")

  @Test
  def testIsWalkable(): Unit =
    for
      x <- 0 until size
      y <- 0 until size
    do
      val position = (x, y)
      val cell = maze.getCell(x, y)
      cell match
        case _: FloorCell   => assertTrue(maze.isWalkable(position), s"Expected walkable at $position")
        case _: WallCell    => assertFalse(maze.isWalkable(position), s"Expected not walkable at $position")
        case door: DoorCell =>
          assertFalse(door.isOpen, s"Door at $position should be initially closed")
          assertFalse(maze.isWalkable(position), s"Expected not walkable when door is closed at $position")
          door.unlock()
          assertTrue(maze.isWalkable(position), s"Expected walkable when door is open at $position")

  @Test
  def testIsExit(): Unit =
    val doorsPositionOpt = (for
      x <- 0 until size
      y <- 0 until size
      cell = maze.getCell(x, y)
      if cell.isInstanceOf[DoorCell]
    yield (x, y, cell.asInstanceOf[DoorCell])).find(!_._3.isOpen)
    assertTrue(doorsPositionOpt.isDefined, "Should have at least one closed DoorCell")

    val (x, y, door) = doorsPositionOpt.get
    val position = (x, y)
    assertFalse(maze.isExit(position), s"Door at $position should not be an exit when closed")
    door.unlock()
    assertTrue(maze.isExit(position), s"Door at $position should be an exit when open")

    val floorPosition = (1, 1)
    assertTrue(maze.getCell(floorPosition._1, floorPosition._2).isInstanceOf[FloorCell])
    assertFalse(maze.isExit(floorPosition), "FloorCell should never be an exit")

  @Test
  def testSpawnGuardians(): Unit =
    given Maze = maze
    val n = 2
    val guardians = Maze.spawnGuardians(n)
    assertEquals(n, guardians.size, "Should spawn correct number of guardians")
    assertEquals(n, guardians.toSet.size, "Guardian positions should be unique")
    for (x, y) <- guardians do
      assertTrue(x > 0 && x < size - 1, s"x=$x should be inside the maze")
      assertTrue(y > 0 && y < size - 1, s"y=$y should be inside the maze")
      assertTrue(maze.getCell(x, y).isInstanceOf[FloorCell], s"Cell at ($x, $y) should be FloorCell")