package model.utils

case class Position(x: Int, y: Int):
  def move(dx: Int, dy: Int): Position = Position(dx + x, dy + y)

object Position:
  def create(x: Int, y: Int): Position = Position(x, y)