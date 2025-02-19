package org.paracite.glframework.core;

import java.util.ArrayList;
import java.util.List;

import org.paracite.glframework.core.Input.TouchEvent;
import org.paracite.glframework.core.Pool.PoolObjectFactory;

import android.view.MotionEvent;
import android.view.View;

public class HandlerMultiTouch implements HandlerTouch {
	boolean[] isTouched = new boolean[20];
	int[] touchX = new int[20];
	int[] touchY = new int[20];
	Pool<TouchEvent> touchEventPool;
	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	float scaleX;
	float scaleY;

	public HandlerMultiTouch(View view, float scaleX, float scaleY) {
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		};
		touchEventPool = new Pool<TouchEvent>(factory, 100);
		view.setOnTouchListener(this);
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	public boolean onTouch(View v, MotionEvent event) {
		
		/*
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		
		synchronized (this) {
			int action = event.getAction() & MotionEvent.ACTION_MASK;
			int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			int pointerId = event.getPointerId(pointerIndex);
			TouchEvent touchEvent;
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				touchEvent = touchEventPool.newObject();
				touchEvent.type = TouchEvent.TOUCH_DOWN;
				touchEvent.pointer = pointerId;
				touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex) * scaleX);
				touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex) * scaleY);
				isTouched[pointerId] = true;
				touchEventsBuffer.add(touchEvent);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				touchEvent = touchEventPool.newObject();
				touchEvent.type = TouchEvent.TOUCH_UP;
				touchEvent.pointer = pointerId;
				touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex) * scaleX);
				touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex) * scaleY);
				isTouched[pointerId] = false;
				touchEventsBuffer.add(touchEvent);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_CANCEL:
				break;
			case MotionEvent.ACTION_MOVE:
				int pointerCount = event.getPointerCount();
				for (int i = 0; i < pointerCount; i++) {
					pointerIndex = i;
					pointerId = event.getPointerId(pointerIndex);
					touchEvent = touchEventPool.newObject();
					touchEvent.type = TouchEvent.TOUCH_DRAGGED;
					touchEvent.pointer = pointerId;
					touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex) * scaleX);
					touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex) * scaleY);
					touchEventsBuffer.add(touchEvent);
				}
				break;
			}
			return true;
		}
	}

	public boolean isTouchDown(int pointer) {
		synchronized (this) {
			if (pointer < 0 || pointer >= 20)
				return false;
			else
				return isTouched[pointer];
		}
	}

	public int getTouchX(int pointer) {
		synchronized (this) {
			if (pointer < 0 || pointer >= 20)
				return 0;
			else
				return touchX[pointer];
		}
	}

	public int getTouchY(int pointer) {
		synchronized (this) {
			if (pointer < 0 || pointer >= 20)
				return 0;
			else
				return touchY[pointer];
		}
	}

	public List<TouchEvent> getTouchEvents() {
		synchronized (this) {
			int len = touchEvents.size();
			for (int i = 0; i < len; i++)
				touchEventPool.free(touchEvents.get(i));
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			return touchEvents;
		}
	}

}
