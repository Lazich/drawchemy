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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Pair;

import java.util.LinkedList;

import draw.chemy.DrawManager;

import static draw.chemy.DrawUtils.RANDOM;
import static draw.chemy.DrawUtils.getProbability;

public class SplatterCreator extends ACreator {

  public static final float MIN_SIZE = 5.f;
  public static final float MAX_SIZE = 30.f;

  public static final int MIN_DRIPS = 1;
  public static final int MAX_DRIPS = 15;

  private static final float PUSH = 0.5f;
  private static final float NEW_SIZE_INFLUENCE = 0.7f;

  // Parameters;
  private float fMaxLineWidth = 120.f;
  private int fDrips = 12;

  private float fSize;
  private float startX, startY;
  private float endX, endY;
  private SplatterDrawOp fCurrentOperation;

  public SplatterCreator(DrawManager aManager) {
    super(aManager);
  }

  @Override
  public IDrawingOperation startDrawingOperation(float x, float y) {

    startX = x;
    startY = y;

    endX = x;
    endY = y;

    fSize = 1;
    fCurrentOperation = new SplatterDrawOp(getPaint());
    return fCurrentOperation;
  }

  @Override
  public void updateDrawingOperation(float x, float y) {
    float mX = startX + (endX - startX) * (1.f + PUSH);
    float mY = startY + (endY - startY) * (1.f + PUSH);

    startX = endX;
    startY = endY;

    endX = x;
    endY = y;

    float dst = dist(startX, startY, endX, endY);

    if (dst < 1.5f) {
      dst = 1.5f;
    }

    float newSize = fMaxLineWidth / (dst);
    fSize = newSize * (1.f - NEW_SIZE_INFLUENCE) + fSize * NEW_SIZE_INFLUENCE;

    splat(new PointF(startX, startY), new PointF(endX, endY), new PointF(mX, mY), fSize);
    redraw();
  }

  @Override
  public void endDrawingOperation() {
  }

  public int getDrips() {
    return fDrips;
  }

  public void setDrips(int fDrips) {
    this.fDrips = fDrips;
  }

  public float getSize() {
    return fMaxLineWidth / 10.f;
  }

  public void setSize(float aSize) {
    this.fMaxLineWidth = aSize * 10;
  }

  private static class SplatterDrawOp implements IDrawingOperation {

    LinkedList<Pair<Path, Float>> fPaths;
    private Paint fPaint;
    private RectF fBounds = null;
    private RectF fBoundsTemp;

    public SplatterDrawOp(Paint aPaint) {
      fPaths = new LinkedList<Pair<Path, Float>>();
      fPaint = aPaint;
      fPaint.setStrokeCap(Paint.Cap.ROUND);
      fPaint.setStyle(Paint.Style.STROKE);
      fPaint.setShader(null);
      fBoundsTemp = new RectF();
    }

    @Override
    public synchronized void draw(Canvas aCanvas) {
      for (Pair<Path, Float> path : fPaths) {
        fPaint.setStrokeWidth(path.second);
        aCanvas.drawPath(path.first, fPaint);
      }
    }

    public synchronized void addPath(Path aPath, float aStrokeWidth) {
      if (fBounds == null) {
        fBounds = new RectF();
        aPath.computeBounds(fBounds, true);
      } else {
        aPath.computeBounds(fBoundsTemp, true);
        fBounds.union(fBoundsTemp);
      }
      fPaths.add(new Pair<Path, Float>(aPath, aStrokeWidth));
    }

    @Override
    public Paint getPaint() {
      return fPaint;
    }

    @Override
    public synchronized void computeBounds(RectF aBoundSFCT) {
      if (fBounds != null) {
        aBoundSFCT.set(fBounds);
      }
    }

    @Override
    public void undo() {
    }

    @Override
    public void redo() {
    }

    @Override
    public void complete() {
    }
  }

  private void splat(PointF start, PointF end, PointF mid, float d) {

    Path firstPath = new Path();
    if (d < 0) {
      d = 0;
    }

    firstPath.moveTo(start.x, start.y);
    firstPath.quadTo(mid.x, mid.y, end.x, end.y);
    fCurrentOperation.addPath(firstPath, d);

    float dst = dist(start.x, start.y, end.x, end.y);
    int quarterDrips = fDrips / 4;
    int nbDrips = quarterDrips + RANDOM.nextInt(fDrips);

    for (int i = 0; i < nbDrips; i++) {
      // positioning of splotch varies between ±4dd, tending towards 0

      float x4 = dst * getProbability(0.5f);
      float y4 = dst * getProbability(0.5f);
      // direction of splotch varies between ±0.5
      float x5 = getProbability(0.5f);
      float y5 = getProbability(0.5f);

      float dd = Math.min(d * (RANDOM.nextFloat() + 0.4f), d);
      Path subPath = new Path();
      subPath.moveTo(start.x + x4, start.y + y4);
      subPath.lineTo(start.x + x4 + x5, start.y + y4 + y5);
      fCurrentOperation.addPath(subPath, dd);
    }
  }

  private float dist(float ax, float ay, float bx, float by) {
    return (float) Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
  }
}
