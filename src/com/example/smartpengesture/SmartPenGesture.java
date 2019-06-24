package com.example.smartpengesture;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class SmartPenGesture extends Gesture{
	private transient RectF gestureBoundBoxRect=null;
    private static final int BITMAP_RENDERING_WIDTH = 2;

    private static final boolean BITMAP_RENDERING_ANTIALIAS = true;
    private static final boolean BITMAP_RENDERING_DITHER = true;	
	
public  SmartPenGesture(){
	super();
}	
public void SmartPenGestureClearAllStroke( ) {
            getStrokes().clear();
}
public void SmartPenGestureClearmBoundingBox( ) {
    getBoundingBox().setEmpty();
}
public RectF getGestureBoundBoxRect() {
	// TODO Auto-generated method stub
	return getBoundingBox();
}

public Bitmap toBitmap(int width, int height, int inset, int color) {
    final Bitmap bitmap = Bitmap.createBitmap(width, height,
            Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(bitmap);

    final Paint paint = new Paint();
    paint.setAntiAlias(BITMAP_RENDERING_ANTIALIAS);
    paint.setDither(BITMAP_RENDERING_DITHER);
    paint.setColor(color);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setStrokeWidth(BITMAP_RENDERING_WIDTH);

    final Path path = toPath();
    final RectF bounds =this.getBoundingBox();
    path.computeBounds(bounds, true);
/*    
    
    Bitmap originalBitmap=Bitmap.createBitmap((int) Math.ceil(bounds.width()), (int)Math.ceil(bounds.height()),
            Bitmap.Config.ARGB_8888);*/
    
   
    
    

    final float sx = (width - 2 * inset) / bounds.width();
    final float sy = (height - 2 * inset) / bounds.height();
//    final float sx = width/ (bounds.width()+2*inset);
//    final float sy = height/ (bounds.height()+2*inset);
    final float scale = sx < sy ? sx:sy;
    paint.setStrokeWidth(2.0f / scale);

/*    path.offset(-bounds.left + (width - bounds.width() * scale) / 2.0f,
            -bounds.top + (height - bounds.height() * scale) / 2.0f);*/
    path.offset(-bounds.left,
            -bounds.top);
    canvas.translate(inset, inset);
    canvas.scale(scale, scale);

    canvas.drawPath(path, paint);

    return bitmap;
}	

public Path toPath() {
    return toPath(null);
}

public Path toPath(Path path) {
    if (path == null) path = new Path();

    final ArrayList<GestureStroke> strokes = getStrokes();
    final int count = strokes.size();

    for (int i = 0; i < count; i++) {
        path.addPath(strokes.get(i).getPath());
    }

    return path;
}	



}
