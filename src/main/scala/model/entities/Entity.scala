/*
 * Copyright (c) 2025 "Maze Escape"
 * Licensed under the MIT License
 */

package model.entities

import model.utils.Position

/**
 * Represents a generic entity
 */
trait Entity:

  /**
   * Provides the current position of the entity.
   *
   * @return the entity position.
   */
  def position: Position