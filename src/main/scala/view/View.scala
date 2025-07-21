/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package view

/**
 * Defines all the actions performed on the GUI.
 */
trait View:

  /**
   * Renders the current state of the game.
   */
  def updateView(): Unit

  /**
   * Shows a simple informational popup.
   *
   * @param message information to be printed.
   */
  def showMessage(message: String): Unit

  /**
   * Displays a puzzle or a question and returns the answer.
   *
   * @param question query posed to the user.
   * @param answer solution provided.
   */
  def showPuzzle(question: String)(answer: String => Unit): Unit

  /**
   * Shows the choice “Logic” or “Luck” used to fight a guardian.
   *
   * @param choice the user's choice of combat type.
   */
  def showFightChoice(choice: String => Unit): Unit