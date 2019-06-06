package com.example.killerpad.color_picker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.example.killerpad.R;

public class ColorPickerDialog extends Dialog {

    private OnColorChangedListener listener;
    private int initialColor;

    private static class ColorPickerView extends View {
        private final int CENTER_X = dpToPx(126);
        private final int CENTER_Y = dpToPx(126);
        private final int CENTER_RADIUS = dpToPx(40);
        private final int MARGIN = dpToPx(8);
        private final float PI = 3.1415926f;

        private Paint outerCirclePaint;
        private Paint innerCirclePaint;
        private final int[] mColors;
        private OnColorChangedListener colorListener;
        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        ColorPickerView(Context context, OnColorChangedListener listener, int color) {
            super(context);
            colorListener = listener;

            //Hex colors with alpha channel (red, pink, blue, cyan, green, yellow, red)
            mColors = new int[] {
                    0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
                    0xFFFFFF00, 0xFFFF0000
            };

            //draws a sweep gradient around a center point.
            //cx: x coordinate of center
            //cy; y coordinate of center
            //colors to be distributed around the center
            //positions: null means spaced evenly around
            Shader shader = new SweepGradient(0, 0, mColors, null);
            outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            outerCirclePaint.setShader(shader);
            outerCirclePaint.setStyle(Paint.Style.STROKE);
            outerCirclePaint.setStrokeWidth(dpToPx(55));

            innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            //The center is painted with the selected color
            innerCirclePaint.setColor(color);
            innerCirclePaint.setStrokeWidth(5);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float radius = CENTER_X - outerCirclePaint.getStrokeWidth()*0.5f;

            //Position at center of screen
            canvas.translate(CENTER_X, CENTER_Y);

            //Draw outer circle
            canvas.drawOval(new RectF(-radius + MARGIN,
                    -radius + MARGIN, radius - MARGIN, radius - MARGIN), outerCirclePaint);

            //Draw inner circle
            canvas.drawCircle(0, 0, CENTER_RADIUS, innerCirclePaint);

            //If color is being selected
            if (mTrackingCenter) {
                int color = innerCirclePaint.getColor();
                innerCirclePaint.setStyle(Paint.Style.STROKE);

                if (mHighlightCenter) {
                    innerCirclePaint.setAlpha(0xFF); //completely opaque
                } else {
                    innerCirclePaint.setAlpha(0x80); //semi transparent (half of 0xFF)
                }

                //Color in the center circle
                canvas.drawCircle(0, 0,
                        CENTER_RADIUS + innerCirclePaint.getStrokeWidth(),
                        innerCirclePaint);

                innerCirclePaint.setStyle(Paint.Style.FILL);
                innerCirclePaint.setColor(color);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(CENTER_X*2, CENTER_Y*2);
        }

        private int ave(int s, int d, float p) {
            return s + java.lang.Math.round(p * (d - s));
        }

        private int interpColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int)p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i+1];
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);

            return Color.argb(a, r, g, b);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;
            boolean inCenter = Math.hypot(x, y) <= CENTER_RADIUS;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    if (inCenter) {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter) {
                        if (mHighlightCenter != inCenter) {
                            mHighlightCenter = inCenter;
                            invalidate();
                        }
                    } else {
                        float angle = (float)Math.atan2(y, x);
                        // need to turn angle [-PI ... PI] into unit [0....1]
                        float unit = angle/(2*PI);
                        if (unit < 0) {
                            unit += 1;
                        }
                        innerCirclePaint.setColor(interpColor(mColors, unit));
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter) {
                        if (inCenter) {
                            colorListener.colorChanged(innerCirclePaint.getColor());
                        }
                        mTrackingCenter = false;    // so we draw w/o halo
                        invalidate();
                    }
                    break;
            }
            return true;
        }

        private int dpToPx(int dp) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    }

    public ColorPickerDialog(Context context,
                             OnColorChangedListener listener,
                             int initialColor) {
        super(context);
        this.listener = listener;
        this.initialColor = initialColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ColorPickerView(getContext(), listener, initialColor));
        getWindow().setBackgroundDrawable(
                new ColorDrawable(getContext().getResources().getColor(R.color.semiTransparentWhite)));
    }
}