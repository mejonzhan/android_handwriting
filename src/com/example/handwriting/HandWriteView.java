package com.example.handwriting;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HandWriteView extends View {

	private static final String TAG = "HandWriteView";

	private static final float TOUCH_TOLERANCE = 5.f;
	
	private float mLastX;
	private float mLastY;
	
	private Paint mPaint;
	
	private ArrayList<Path> mPaths = new ArrayList<Path>();
	private ArrayList<Path> mBackPaths = new ArrayList<Path>();
	
	public HandWriteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	public HandWriteView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public HandWriteView(Context context) {
		this(context, null);
	}
	
	private void init() {
		
		mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		mPaint.setStrokeWidth(5);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setStyle(Style.STROKE);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		
		for (int i = 0; i < mPaths.size(); i++) {
			Path path = mPaths.get(i);
			if (path != null) {
				canvas.drawPath(path, mPaint);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float x = event.getX();
        float y = event.getY();
        
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDown(x, y);
			invalidate();
			break;
			
		case MotionEvent.ACTION_MOVE:
			touchMove(x, y);
			invalidate();
			break;
			
		case MotionEvent.ACTION_UP:
			touchUp(x, y);
			invalidate();
			break;
		}
		
		return true;
	}
	
	private void touchDown(float x, float y) {
		
		Path path = new Path();
		path.moveTo(x, y);
		
		mLastX = x;
		mLastY = y;
		
		mPaths.add(path);
	}
	
	private void touchMove(float x, float y) {
		
		float diffX = Math.abs(x - mLastX);
		float diffY = Math.abs(y - mLastY);
		
		if (diffX > TOUCH_TOLERANCE || diffY > TOUCH_TOLERANCE) {
			Path path = getTopPath(mPaths);
			path.quadTo(mLastX, mLastY, (mLastX + x)/2, (mLastY + y)/2);
			mLastX = x;
			mLastY = y;
		}
	}
	
	private void touchUp(float x, float y) {
		
		Path path = getTopPath(mPaths);
		path.lineTo(x, y);
	}
	
	/**
	 * get the top path to continue draw
	 * @return
	 */
	private Path getTopPath(ArrayList<Path> list) {
		
		Path path = null;
		
		int size = list.size();
		if (size > 0) {
			path = list.get(size - 1);
		}
		
		return path;
	}
	
	private void popPath() {
		
		Path path = getTopPath(mPaths);
		
		if (path != null) {
			mPaths.remove(path);
			mBackPaths.add(path);
		}
	}
	
	private void pushPath() {
		
		Path path = getTopPath(mBackPaths);
		
		if (path != null) {
			mBackPaths.remove(path);
			mPaths.add(path);
		}
	}
	
	public void undo() {
		
		popPath();
		invalidate();
	}
	
	public void redo() {
		
		pushPath();
		invalidate();
	}
	
	public boolean isCanUndo() {
		return mPaths.size() > 0;
	}
	
	public boolean isCanRedo() {
		return mBackPaths.size() > 0;
	}
	
}
