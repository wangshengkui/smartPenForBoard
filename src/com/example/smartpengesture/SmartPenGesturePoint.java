package com.example.smartpengesture;

import android.gesture.GesturePoint;
/*
 * SmartPenGesturePoint和GesturePoint没有什么区别，只是数据来源的区别
 * SmartPenGesturePoint的点（x，y）来源于智能笔
 * GesturePoint的点(x,y)来源于屏幕坐标
 */
public class SmartPenGesturePoint extends GesturePoint {

	public SmartPenGesturePoint(float x, float y, long t) {
		super(x, y, t);
		// TODO Auto-generated constructor stub
	}

}
