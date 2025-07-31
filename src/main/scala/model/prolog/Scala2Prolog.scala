package model.prolog

import alice.tuprolog.*

object Scala2Prolog:
  given Conversion[String, Term] = Term.createTerm(_)
  given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")

  def extractTerm(term: Term, index: Integer): Term =
    term.asInstanceOf[Struct].getArg(index).getTerm

  def mkPrologEngine(clauses: String*): Term => LazyList[Term] =
    val engine = Prolog()
    engine.setTheory(Theory(clauses mkString " "))
    goal =>
      new Iterable[Term]:
        override def iterator: Iterator[Term] = new Iterator[Term]:
          var solution = engine.solve(goal)
          override def hasNext: Boolean =
            solution.isSuccess || solution.hasOpenAlternatives
          override def next(): Term =
            try solution.getSolution finally solution = engine.solveNext()
    .to(LazyList)
