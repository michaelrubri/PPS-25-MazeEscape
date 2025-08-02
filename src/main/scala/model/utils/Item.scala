/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.utils

import model.entities.Player

/**
 * Represents a standard item.
 */
sealed trait Item:
  def id: String
  def name: String
  def description: String

/**
 * Represents a stackable item.
 */
trait Stackable[I]:
  def maxStack(item: I): Int

/**
 * Represents a usable item.
 */
trait Usable[I]:
  def use(item: I, player: Player): Player

case object SealOfTheChallenge extends Item:
  val id = "special-key"
  val name = "Seal of the challenge"
  val description = "Unlock access to the Mysterious Man"

case class InvisibilityPotion(turns: Int = 3) extends Item:
  val id = "inv-potion"
  val name = "Invisibility potion"
  val description = s"Makes invisible for $turns turns"
  
given Stackable[InvisibilityPotion] with
  override def maxStack(item: InvisibilityPotion): Int = 5
  
given Usable[InvisibilityPotion] with
  override def use(item: InvisibilityPotion, player: Player): Player =
    player.addStatus("invisible", item.turns)