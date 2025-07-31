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
   * Updates the view based on the current state of the game.
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

  /**
   * Shows to the user the end-game menu allowing him to start a new game or exit.
   *
   * @param victory flag that defines whether the player has won.
   * @param choice the user's choice.
   */
  def showEndGameMenu(victory: Boolean)(choice: String => Unit): Unit