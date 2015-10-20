package com.example.handwriting;

import java.util.Date;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AnalogClockView extends View {

	private static final String TAG = "AnalogClockView";

	private static final int SEC_STROKE_WIDTH = 2;
	private static final int MIN_STROKE_WIDTH = 4;
	private static final int HOUR_STROKE_WIDTH = 6;
	private static final int CIRCEL_STROKE_WIDTH = 5;

	private Paint mSecPaint;
	private Paint mMinPaint;
	private Paint mHourPaint;
	
	private int mSec;
	private float mMin;
	private float mHour;

	private int mRadius;

	private int mCenterX;
	private int mCenterY;
	
	public AnalogClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	public AnalogClockView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public AnalogClockView(Context context) {
		this(context, null);
	}

	private void init() {

		mSecPaint = getPaint(Color.RED, SEC_STROKE_WIDTH);
		mMinPaint = getPaint(Color.RED, MIN_STROKE_WIDTH);
		mHourPaint = getPaint(color.black, HOUR_STROKE_WIDTH);

		getTime();
		
		mHandler.post(mTimeTickRunnable);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpec = MeasureSpec.getMode(heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpec == MeasureSpec.UNSPECIFIED) {
			width = getSuggestedMinimumWidth();

			if (width == 0) {
				width = height;
			} else {
				if (heightSpec == MeasureSpec.AT_MOST) {
					height = width;
				}
			}
		}

		if (heightSpec == MeasureSpec.UNSPECIFIED) {
			height = getSuggestedMinimumWidth();

			if (height == 0) {
				height = width;
			} else {
				if (widthSpec == MeasureSpec.AT_MOST) {
					width = height;
				}
			}
		}

		if (widthSpec == MeasureSpec.EXACTLY) {
			if (heightSpec == MeasureSpec.UNSPECIFIED) {
				height = width;
			} else if (heightSpec == MeasureSpec.AT_MOST) {
				height = Math.min(width, height);
			}
		}

		if (heightSpec == MeasureSpec.EXACTLY) {
			if (width == MeasureSpec.UNSPECIFIED) {
				width = height;
			} else if (widthSpec == MeasureSpec.AT_MOST) {
				width = Math.min(width, height);
			}
		}

		if (widthSpec == MeasureSpec.AT_MOST) {
			width = Math.min(width, height);
		}

		if (heightSpec == MeasureSpec.AT_MOST) {
			height = Math.min(width, height);
		}

		mRadius = Math.min(width, height) / 2 - CIRCEL_STROKE_WIDTH;

		mCenterX = width / 2;
		mCenterY = height / 2;

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		drawCircel(canvas);
		drawHoure(canvas);
		Point point = getPoint(mHour, (int) (mRadius * 0.6), mCenterX, mCenterY);
		canvas.drawLine(mCenterX, mCenterY, point.x, point.y, mHourPaint);
		
		point = getPoint(mMin, (int) (mRadius * 0.8), mCenterX, mCenterY);
		canvas.drawLine(mCenterX, mCenterY, point.x, point.y, mMinPaint);
		
		point = getPoint(mSec - 1, (int) (mRadius * 0.9), mCenterX, mCenterY);
		canvas.drawLine(mCenterX, mCenterY, point.x, point.y, mSecPaint);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
	}
	
	private void getTime() {
		
		Date time = new Date();
		int sec = time.getSeconds();
		
		if (sec != mSec) {
			mSec = sec;
			mMin = time.getMinutes() + mSec / 60f;
			mHour = time.getHours() % 12 * 5 + mMin / 12f;
		}
	}

	private Paint getPaint(int color, int width) {

		Paint paint = new Paint();
		paint.setStrokeWidth(width);

		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setShadowLayer(8, 5, 5, color);

		return paint;
	}

	private void drawCircel(Canvas canvas) {

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.DKGRAY);
		canvas.drawCircle(mCenterX, mCenterY, mRadius, paint);

		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(CIRCEL_STROKE_WIDTH);
		canvas.drawCircle(mCenterX, mCenterY, mRadius, paint);
	}

	private void drawHoure(Canvas canvas) {

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(CIRCEL_STROKE_WIDTH);
		paint.setAntiAlias(true);
		canvas.save();

		int lenght = (int) (mRadius * 0.05);
		for (int i = 0; i < 12; i++) {
			canvas.drawLine(mCenterX, mCenterY - mRadius, mCenterX, mCenterY
					- mRadius + lenght, paint);
			canvas.rotate(30, mCenterX, mCenterY);
		}

		canvas.restore();

		TextPaint textPaint = new TextPaint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize((int) (mRadius * 0.2));

		int textRadius = (int) (mRadius * 0.8);
		for (int i = 0; i < 12; i++) {
			Point p = getPoint(i * 5, textRadius, mCenterX, mCenterY);
			int hour = i % 12;
			String text = String.valueOf(hour == 0 ? 12 : hour);

			Rect textRect = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), textRect);
			canvas.drawText(text, p.x - textRect.width() / 2,
					p.y + textRect.height() / 2, textPaint);

		}
	}

	private Point getPoint(float minutes, int radius, int cx, int cy) {

		double angle = Math.toRadians(minutes * 6 - 90);

		int x = cx + (int) (Math.cos(angle) * radius);
		int y = cy + (int) (Math.sin(angle) * radius);

		return new Point(x, y);
	}
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			getTime();
			invalidate();
			postDelayed(mTimeTickRunnable, 1000);
		};
	};
	
	private Runnable mTimeTickRunnable = new Runnable() {
		
		@Override
		public void run() {
			Log.i("mejonzhan", "runnable");
			mHandler.sendEmptyMessage(0);
		}
	};
}
