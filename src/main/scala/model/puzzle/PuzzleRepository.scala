/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.puzzle

import scala.util.Random

/**
 * All the possible puzzles are defined here.
 */
object PuzzleRepository:
  
  private val allPuzzles: Vector[Puzzle] = Vector(
    LogicPuzzle(
      1,
      "Five people were eating apples,\n" +
      "A finished before B, but behind C.\n" +
      "D finished before E, but behind B.\n" +
      "What was the finishing order?",
      List("CABDE")),
    LogicPuzzle(
      2,
      "A novel has at least 300 pages.\n" +
      "A short story has at most 100 pages.\n" +
      "A story book has at most 500 pages.\n" +
      "What can you say about a book that has 400 pages?",
      List("novel, story book")),
    RiddlePuzzle(
      1,
      "What has roots as nobody sees,\n" +
      "Is taller than trees,\n" +
      "Up, up it goes,\n" +
      "And yet never grows?",
      List("Mountain")),
    RiddlePuzzle(
      2,
      "Thirty white horses on a red hill,\n" +
      "First they champ,\n" +
      "Then they stamp,\n" +
      "Then they stand still.",
      List("Teeth")),
    RiddlePuzzle(
      3,
      "This thing all things devours:\n" +
      "Birds, beasts, trees, flowers;\n" +
      "Gnaws iron, bites steel;\n" +
      "Grinds hard stones to meal;\n" +
      "Slays king, ruins town,\n" +
      "And beats high mountain down.",
      List("Time")),
    RiddlePuzzle(
      4,
      "A box without hinges, key, or lid,\n" +
      "Yet golden treasure inside is hid.",
      List("Egg"))
  )
  
  private var availablePuzzles = allPuzzles

  /**
   * Randomly selects a puzzle from the existing ones.
   *
   * @return the puzzle chosen from those available.
   */
  def randomPuzzle(): Puzzle =
    if availablePuzzles.isEmpty then resetPuzzles()
    val randomIndex = Random.nextInt(availablePuzzles.size)
    val selectedPuzzle = availablePuzzles(randomIndex)
    availablePuzzles = availablePuzzles.filterNot(_ == selectedPuzzle)
    selectedPuzzle

  /**
   * Resets all the puzzles to NOT USED
   */
  private def resetPuzzles(): Unit = availablePuzzles = allPuzzles
  
  /**
   * Selects the puzzle by the identifier.
   *
   * @param id numeric identifier.
   * @return the puzzle associated with the identifier if exists, none otherwise.
   */
  def getById(id: Int): Option[Puzzle] = allPuzzles.find(_.id == id)