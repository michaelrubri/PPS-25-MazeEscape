package model.prolog

import model.map.Maze
import model.utils.Position

object MazePrologTheory:

  def apply(maze: Maze): String =
    val facts =
      (for
        x <- 0 until maze.size
        y <- 0 until maze.size
        if maze.isWalkable(Position(x,y))
      yield s"walkable($x,$y).").mkString("\n")

    val theory = """
      % Adjacent cells
      neighbor(X,Y,NX,Y) :- NX is X+1, walkable(NX,Y).
      neighbor(X,Y,NX,Y) :- NX is X-1, walkable(NX,Y).
      neighbor(X,Y,X,NY) :- NY is Y+1, walkable(X,NY).
      neighbor(X,Y,X,NY) :- NY is Y-1, walkable(X,NY).

      % path from (SX,SY) to (GX,GY) returning a list of positions
      path(SX,SY,GX,GY,Path) :-
        bfs([[[SX,SY]]], [GX,GY], RevPath),
        reverse(RevPath, Path).

      bfs([[[GX,GY]|Rest]|_], [GX,GY], [[GX,GY]|Rest]).

      bfs([CurrentPath|Queue], Goal, Path) :-
        CurrentPath = [[CX,CY]|_],
        findall(
          [[NX,NY]|CurrentPath],
          (neighbor(CX,CY,NX,NY), \+ member([NX,NY], CurrentPath)),
          NextPaths
        ),
        append(Queue, NextPaths, NewQueue),
        bfs(NewQueue, Goal, Path).

      next_move(SX,SY,GX,GY,NX,NY) :- path(SX,SY,GX,GY,[[SX,SY],[NX,NY]|_]).
    """

    s"""
    $facts
    $theory
    """.trim