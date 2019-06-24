package com.example.readAndSave;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.Dot.DotType;

import android.R.integer;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

public class SmartPenPage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final int bookeId;
	public final int pageNumber;
	public  int pageSize;
	public transient RectF  temRectF;
	private int owner=-1;//批改作业时指定作业的所有者
	//PagePoint[][] mPagePixels=new PagePoint[200][140];
	private long ChirographyCount=0;//从1开始计数，初始化为0
	private ArrayListMultimap<Long, Dot> pageChirographys = ArrayListMultimap
	.create();
	private transient ArrayList<RectF> chirographyBoundingBoxArrayList=new ArrayList<RectF>();
//	private ArrayList<int[]> chirographyColoraAndWidth=new ArrayList<int[]>();
	public SmartPenPage(int pageNumber, int bookeId,int pageSize ){
		this.bookeId=bookeId;
		this.pageNumber=pageNumber;
		this.pageSize=pageSize;
	}
	public SmartPenPage(int pageNumber, int bookeId,int pageSize,int owner){
		this.bookeId=bookeId;
		this.pageNumber=pageNumber;
		this.pageSize=pageSize;
		this.owner=owner;
	}
	public SmartPenPage(int pageNumber, int bookeId) {
		this.bookeId=bookeId;
		this.pageNumber=pageNumber;
		// TODO Auto-generated constructor stub
	}
	public void addStrokePoint(Dot dot){
//		Log.i("rectf",""+dot.type);
		if (temRectF==null) {//有的时候第一次运行APP,第一个dot的dot.type!=DotType.PEN_DOWN,temRectF==null程序会崩掉
			temRectF=new RectF();

		}
		if (dot.type==DotType.PEN_DOWN&&dot.force>0) {
			temRectF=new RectF();
			temRectF.setEmpty();
			temRectF.left=dot.x+dot.fx/100;
			temRectF.right=temRectF.left;
			temRectF.top=dot.y+dot.fy/100;
			temRectF.bottom=temRectF.top;

		}
		pageChirographys.put(ChirographyCount, dot);
		Log.e("zgm", "123-dot.BookID:"+dot.BookID);
		temRectF.union(dot.x+dot.fx/100, dot.y+dot.fy/100);
		if (dot.type==DotType.PEN_UP&&dot.force<0) {
//			temRectF.union(dot.x+dot.fx/100, dot.y+dot.fy/100);
			ChirographyCount++;
			chirographyBoundingBoxArrayList.add(temRectF);
			temRectF.setEmpty();
			return;
		}
	}
	public void getAllStrokeBoundingBoxArrayList(){
		if (chirographyBoundingBoxArrayList!=null) {
			return;
		}
	    temRectF=new RectF();
		for (int i = 1; i <=ChirographyCount; i++) {
		for (Dot dot : pageChirographys.get(ChirographyCount)) {
			if (dot.type==DotType.PEN_DOWN&&dot.force>0) {
				temRectF.setEmpty();
				temRectF.left=dot.x+dot.fx/100;
				temRectF.right=temRectF.left;
				temRectF.top=dot.y+dot.fy/100;
				temRectF.bottom=temRectF.top;
				return;
			}
			temRectF.union(dot.x+dot.fx/100, dot.y+dot.fy/100);
			if (dot.type==DotType.PEN_UP&&dot.force<0) {
//				temRectF.union(dot.x+dot.fx/100, dot.y+dot.fy/100);
				chirographyBoundingBoxArrayList.add(temRectF);
				temRectF.setEmpty();
				return;
			}

			
			
		}
		}
	}
	public String getPageFileName(){
		String filenameString=owner+"/"+"zgm1"+bookeId+"_"+pageNumber+"_"+pageSize+".page";
		return filenameString;
		
		
		
	}
	public ArrayListMultimap<Long, Dot> getAllPoints() {
		// TODO Auto-generated method stub
		return pageChirographys;
	}
	public long getStrokesCount(){
		return ChirographyCount;
	}
}

