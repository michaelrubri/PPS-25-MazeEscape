package model.puzzle

/**
 * Represents a generic puzzle.
 */
sealed trait Puzzle:
  def id: Int
  def question: String
  def solution: String

  /**
   * Checks if the given answer is correct.
   *
   * @param answer solution provided by the user.
   * @return true if the answer is correct, false otherwise.
   */
  def checkAnswer(answer: String): Boolean = answer.trim.equalsIgnoreCase(solution)

/**
 * The following case classes are different puzzle types.
 */

/**
 * Represents a logic puzzle.
 *
 * @param id numeric identifier.
 * @param question query posed to the user.
 * @param solution correct answer to the question.
 */
case class LogicPuzzle(
                        id: Int,
                        question: String,
                        solution: String
                      ) extends Puzzle

/**
 * Represents a riddle puzzle.
 *
 * @param id numeric identifier.
 * @param question query posed to the user.
 * @param solution correct answer to the question.
 */
case class RiddlePuzzle(
                         id: Int,
                         question: String,
                         solution: String
                       ) extends Puzzle
