package com.example.smartpenforboard;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



//新建一个类继承View
public class DrawView extends View{
	private int mov_x;//声明起点坐标
	private int mov_y;
	public Paint paint;//声明画笔
	public Canvas canvas;//画布
	public Bitmap bitmap;//位图

	private int blcolor=Color.RED;
	private int bwidth=3;


	public MainActivity mcontext;
	private int lastX;
	private int lastY;
	int oriX=0;//上次屏幕的点的x;
	int oriY=0;//上次屏幕的点的Y;
	int left;
	int right;
	int top;
	int bottom;
	private String TAG="123";
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mcontext = (MainActivity) context;
		createBitmap(mcontext.mWidth,mcontext.mHeight);
		initDraw();
//		this.mcontext = context;
		// TODO Auto-generated constructor stub
	}
	public DrawView(MainActivity context)
	{
		super(context);
		this.mcontext = context;
		createBitmap(context.mWidth,context.mHeight);
		initDraw();
	}

	public DrawView(MainActivity context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);		
		this.mcontext = context;
		createBitmap(mcontext.mWidth,mcontext.mHeight);
		// TODO Auto-generated constructor stub
	}
//画位图
@Override
protected void onDraw(Canvas canvas)
{
super.onDraw(canvas);

canvas.drawBitmap(bitmap, 0, 0, paint);
}

public void initDraw()
{
	paint=null;
	//bitmap=null;
	canvas=null;
	paint=new Paint(Paint.DITHER_FLAG);//创建一个画笔]
	//	bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888); //设置位图的宽高
	//bitmap = Bitmap.createBitmap(1200, 1824, Bitmap.Config.ARGB_8888); //设置位图的宽高
    //bitmap = Bitmap.createBitmap(1550, 2320, Bitmap.Config.ARGB_8888); //设置位图的宽高
/*    if(bitmap == null){
//		bitmap = Bitmap.createBitmap(1900, 2300, Bitmap.Config.ARGB_8888); //设置位图的宽高
//		bitmap = Bitmap.createBitmap(1430, 1725, Bitmap.Config.ARGB_8888); //设置位图的宽高
//		bitmap = Bitmap.createBitmap((int) 200, 300, Bitmap.Config.ARGB_8888); //设置位图的宽高
//		bitmap = Bitmap.createBitmap(794, 1122, Bitmap.Config.ARGB_8888);
//		bitmap = Bitmap.createBitmap(2850,3000, Bitmap.Config.ARGB_4444);
//  	bitmap=((BitmapDrawable)getResources().getDrawable(R.drawable.pagebg)).getBitmap();
    }*/
//	  paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	canvas=new Canvas();
//	paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//	canvas.drawPaint(paint);
//	paint.setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
	///canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
	canvas.setBitmap(bitmap);
//	bitmap=((BitmapDrawable)getResources().getDrawable(R.drawable.pagebg)).getBitmap();
//	canvas.drawBitmap(bitmap,0,0, paint);
	paint.setStyle(Style.STROKE);//设置非填充
	//  paint.setStrokeWidth(bwidth);//笔宽5像素
	paint.setColor(blcolor);//设置为红笔
	paint.setAntiAlias(true);//锯齿不显示
	paint.setDither(true);  //防抖动
//	canvas.drawCircle(100,100, 100, paint);
	invalidate();
}
public void setVcolor(int vlcolor)
{
	blcolor=vlcolor;
 	paint.setColor(blcolor);
}
public void setVwidth(int vwidth)
{
	bwidth=vwidth;
	paint.setStrokeWidth(bwidth);
}

public void DrawDestroy(){
	if(bitmap != null && !bitmap.isRecycled()){        
		bitmap.recycle(); 
        bitmap = null; 
	}

	paint=null;
	canvas=null;
}

//@Override
//protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    int width;
//    int height;
//    float textWidth=bitmap.getWidth();
//    Log.e("zgm", "0411:"+textWidth);
//    width = (int) (getPaddingLeft() + textWidth + getPaddingRight());    
//    float textHeight =bitmap.getHeight();
//    height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
//    //保存测量宽度和测量高度
//    setMeasuredDimension(width, height);
//}

/*public void resetOnMeasure() {	
	Measure(0, 0);
}*/
/*
//触摸事件
@Override
public boolean onTouchEvent(MotionEvent event) {
if (event.getAction()==MotionEvent.ACTION_MOVE) {//如果拖动
 canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);//画线
 invalidate();
}
if (event.getAction()==MotionEvent.ACTION_DOWN) {//如果点击
 mov_x=(int) event.getX();
 mov_y=(int) event.getY();
 Log.d("USB_HOST", "x:"+mov_x+"y:"+mov_y);
 canvas.drawPoint(mov_x, mov_y, paint);//画点
 invalidate();
}
mov_x=(int) event.getX();
mov_y=(int) event.getY();
//return true;
return super.onTouchEvent(event);
}
*/

//public boolean onTouchEvent(MotionEvent event) {
//    //检测到触摸事件后 第一时间得到相对于父控件的触摸点坐标 并赋值给x,y
//    int x = (int) event.getX();
//    int y = (int) event.getY();
// 
//    switch (event.getAction()) {
//      //触摸事件中绕不开的第一步，必然执行，将按下时的触摸点坐标赋值给 lastX 和 last Y
//      case MotionEvent.ACTION_DOWN:
//    	 left=getLeft();
//    	 right=getRight();
//    	 top=getTop();
//    	 bottom=getBottom();
//        lastX = x;
//        lastY = y;
//        oriX=x;//上次屏幕的点的x;
//        oriY=y;
//        break;
//      //触摸事件的第二步，这时候的x,y已经随着滑动操作产生了变化，用变化后的坐标减去首次触摸时的坐标得到 相对的偏移量
//      case MotionEvent.ACTION_MOVE:
///*    	  if (Math.abs(x-oriX)<3&&Math.abs(y-oriY)<3) {
//			return  true;
//		}*/
//        int offsetX = x - lastX;
//        int offsetY = y - lastY;
///*        oriX=x;
//        oriY=y;*/
//        layout(left + offsetX, top + offsetY, right + offsetX,bottom + offsetY);			
//        //使用 layout 进行重新定位
//        break;
//    }
//    return true;
//  }

public void createBitmap(float mWidth, float mHeight) {
	int mSize= (int) (mWidth<mHeight?mHeight:mWidth);
	if(mSize<=0)
		mSize=2560;	
	this.bitmap=Bitmap.createBitmap(mSize,mSize, Bitmap.Config.ARGB_4444);
}


}
