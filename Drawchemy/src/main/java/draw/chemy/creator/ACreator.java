/*
 * This file is part of the Drawchemy project - https://github.com/PPilmeyer/drawchemy
 *
 * Copyright (c) 2015 Pilmeyer Patrick
 *
 * Drawchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Drawchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Drawchemy.  If not, see <http://www.gnu.org/licenses/>.
 */

package draw.chemy.creator;

import android.graphics.Paint;

import draw.chemy.DrawManager;

public abstract class ACreator {

  private final DrawManager fManager;

  public ACreator(DrawManager aManager) {
    fManager = aManager;
  }

  protected final void redraw() {
    fManager.redraw();
  }

  protected final Paint getPaint() {
    return fManager.getPaintState().getPaint();
  }

  public abstract IDrawingOperation startDrawingOperation(float x, float y);

  public abstract void updateDrawingOperation(float x, float y);

  public abstract void endDrawingOperation();

  // Clear the creator state (used when clearing the canvas)
  public void clear() {
  }
}
