package model.puzzle

import scala.util.Random

/**
 * All the possible puzzles are defined here.
 */
object PuzzleRepository:

  private val puzzles: Vector[Puzzle] = Vector(
    LogicPuzzle(1, "What is 3 x 3?", "9"),
    LogicPuzzle(2, "Determine x knowing that 3x = 12", "4"),
    RiddlePuzzle(1, "I'm always in front of you but you don't see me. Who am I?", "Future"),
    RiddlePuzzle(2,
      "It devours all things in its path-\n" +
      "The living, the beasts, the birds, the trees;\n" +
      "It topples kings, make cities fall,\n" +
      "It eats through iron, stone, and wall,\n" +
      "And turns great mountains into dust-\n" +
      "Relentless, silent, sure, and just",
      "Time")
  )

  /**
   * Randomly selects a puzzle from the existing ones.
   *
   * @return the puzzle chosen from those available.
   */
  def randomPuzzle(): Puzzle = puzzles(Random.nextInt(puzzles.size))

  /**
   * Selects the puzzle by the identifier.
   *
   * @param id numeric identifier.
   * @return the puzzle associated with the identifier if exists, none otherwise.
   */
  def getById(id: Int): Option[Puzzle] = puzzles.find(_.id == id)