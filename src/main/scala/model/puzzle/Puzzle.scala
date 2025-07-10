package model.puzzle

/**
 * Represents a generic puzzle.
 */
sealed trait Puzzle:
  def id: Int
  def question: String
  def solutions: List[String]
  var used: Boolean
  
  /**
   * Checks if the given answer is correct.
   *
   * @param answer solution provided by the user.
   * @return true if the answer is correct, false otherwise.
   */
  def checkAnswer(answer: String): Boolean = solutions.contains(answer)

    

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
case class LogicPuzzle(
                        id: Int,
                        question: String,
                        solutions: List[String],
                        var used: Boolean = false
                      ) extends Puzzle

/**
 * Represents a riddle puzzle.
 *
 * @param id numeric identifier.
 * @param question query posed to the user.
 * @param solutions list of the possible correct answers to the question.
 */
case class RiddlePuzzle(
                         id: Int,
                         question: String,
                         solutions: List[String],
                         var used: Boolean = false
                       ) extends Puzzle
