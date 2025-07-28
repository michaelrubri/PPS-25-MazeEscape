package model.strategies

import alice.tuprolog.Term
import alice.tuprolog.Struct
import model.prolog.Scala2Prolog

class GuardianStrategy(engine: Term => LazyList[Term]):

  /**
   * Returns the guardian's next position given its position and
   * the player's position.
   *
   * @param gx first coordinate of guardian's position.
   * @param gy second coordinate of guardian's position.
   * @param px first coordinate of player's position.
   * @param py second coordinate of player's position.
   * @return the new coordinates of guardian's position.
   */
  def nextMove(gx: Int, gy: Int, px: Int, py: Int): (Int, Int) =
    val goal = Term.
      createTerm(s"next_move($gx, $gy, $px, $py, NX, NY)").
      asInstanceOf[Struct]
    engine(goal).headOption match
      case Some(solution) =>
        val nx = Scala2Prolog.extractTerm(solution, 4).toString.toInt
        val ny = Scala2Prolog.extractTerm(solution, 5).toString.toInt
        (nx, ny)
      case None => (gx, gy)
