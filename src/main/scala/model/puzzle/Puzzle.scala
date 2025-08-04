/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.puzzle

/**
 * Represents a generic puzzle.
 */
sealed trait Puzzle:

  /**
   * Puzzle's identification number.
   */
  def id: Int

  /**
   * Riddle's question.
   */
  def question: String

  /**
   * Valid solutions to the puzzle.
   *
   * @return a list of possible solutions.
   */
  def solutions: List[String]
  
  /**
   * Checks if the given answer is correct.
   *
   * @param answer solution provided by the user.
   * @return true if the answer is correct, false otherwise.
   */
  def checkAnswer(answer: String): Boolean = solutions.map(_.toLowerCase) contains answer.toLowerCase

/**
 * The following case classes are different puzzle types.
 */

/**
 * Represents a logic puzzle.
 *
 * @param id numeric identifier.
 * @param question query posed to the user.
 * @param solutions list of the possible correct answers to the question.
 */
case class LogicPuzzle(id: Int,
                       question: String,
                       solutions: List[String]
                      ) extends Puzzle

/**
 * Represents a riddle puzzle.
 *
 * @param id numeric identifier.
 * @param question query posed to the user.
 * @param solutions list of the possible correct answers to the question.
 */
case class RiddlePuzzle(id: Int,
                        question: String,
                        solutions: List[String]
                       ) extends Puzzle