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
import android.graphics.RectF;

import java.util.LinkedList;

import draw.chemy.DrawManager;
import draw.chemy.DrawUtils;

public class BallCreator extends ACreator {

  public static final int MIN_FLOW = 1;
  public static final int MAX_FLOW = 10;

  public static final float MIN_SIZE = 8.f;
  public static final float MAX_SIZE = 30.f;

  public static final int MIN_RADIUS = 1;
  public static final int MAX_RADIUS = 10;

  private int fCount;

  private int fFlow = 3;
  private float fSize = 18;
  private int fRadius = 5;

  private BallOperation fCurrentOperation;

  public BallCreator(DrawManager aManager) {
    super(aManager);
  }

  @Override
  public IDrawingOperation startDrawingOperation(float x, float y) {
    fCurrentOperation = new BallOperation(getPaint());
    fCount = 0;
    return fCurrentOperation;
  }

  @Override
  public void updateDrawingOperation(float x, float y) {

    if (fCount++ % fFlow == 0) {
      ball(x, y);
      redraw();
    }
  }

  private void ball(float x, float y) {
    Ball b = new Ball(x, y, fRadius);
    for (int i = 0; i < fRadius; i++) {
      b.addRadius(DrawUtils.RANDOM.nextFloat() * (4.f * fSize / (float) fRadius));
    }
    fCurrentOperation.addBall(b);
  }

  @Override
  public void endDrawingOperation() {
    fCurrentOperation = null;
  }

  public int getFlow() {
    return fFlow;
  }

  public void setFlow(int aFlow) {
    fFlow = aFlow;
  }

  public int getRadius() {
    return fRadius;
  }

  public void setRadius(int aRadius) {
    fRadius = aRadius;
  }

  public float getSize() {
    return fSize;
  }

  public void setSize(float aSize) {
    fSize = aSize;
  }

  private class Ball {

    private float fX, fY;
    private float fRadius[];
    private int fStep = 0;

    public Ball(float aX, float aY, int aNumber) {
      fX = aX;
      fY = aY;
      fStep = 0;
      fRadius = new float[aNumber];
    }

    public void addRadius(float aRadius) {
      if (fStep == 0) {
        fRadius[0] = aRadius;

      } else {
        fRadius[fStep] = fRadius[fStep - 1] + aRadius;
      }
      fStep++;
    }
  }

  private class BallOperation implements IDrawingOperation {

    LinkedList<Ball> fBalls;
    Paint fPaint;
    private RectF fBounds;

    public BallOperation(Paint aPaint) {
      fBalls = new LinkedList<Ball>();
      fPaint = aPaint;
      fBounds = null;
    }

    @Override
    public synchronized void draw(Canvas aCanvas) {
      for (Ball ball : fBalls) {
        for (float r : ball.fRadius) {
          aCanvas.drawCircle(ball.fX, ball.fY, r, fPaint);
        }
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

    public synchronized void addBall(Ball b) {
      fBalls.addLast(b);
      float x = b.fX;
      float y = b.fY;
      float r = b.fRadius[b.fRadius.length - 1];

      if (fBounds == null) {
        fBounds = new RectF(x - r, y - r, x + r, y + r);
      } else {
        fBounds.union(x - r, y - r);
        fBounds.union(x + r, y + r);
      }
    }
  }
}
