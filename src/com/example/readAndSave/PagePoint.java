package com.example.readAndSave;

import java.io.Serializable;
import java.util.ArrayList;

import android.R.integer;
import android.graphics.Color;

public class PagePoint implements Serializable{
	private ArrayList<integer> PagePointValue=new ArrayList<integer>();//笔迹在页像素点的颜色
	private ArrayList<Byte> chirographyNmber=new ArrayList<Byte>();//该页中的笔迹的序数
}
