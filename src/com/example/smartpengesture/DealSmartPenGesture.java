package com.example.smartpengesture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import com.example.smartpenforboard.MainActivity;
import com.example.smartpenforboard.Position;
import com.example.smartpenforboard.R;
import com.example.smartpenforboard.RecordingService;
import com.example.smartpenforboard.RecordingService.MyBinder;
import com.example.smartpenforboard.UpLoad;
import com.google.common.collect.ArrayListMultimap;
import com.tqltech.tqlpencomm.Dot;

import android.R.integer;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class DealSmartPenGesture {
	com.example.smartpenforboard.MainActivity activity;
    public com.example.smartpenforboard.RecordingService.MyBinder recordService;
    public Intent intent; 
    public int PageID=-1;
   public  ArrayList<Integer> tag=null;
   public String gestureFinalName="手势名称";
   public String  gestureResponce="响应方式";
    private ArrayList<Integer> itemsContainer=new ArrayList<Integer>();//布置作业的时候的题目容器
    private ParseXml parseXml=null;
    private volatile long lastItemTime=0; //老师最后一次选择题目的当前系统时间(正常是为了布置作业)
    public void setPageID( int PageID) {
    	this.PageID=PageID;
		if (parseXml==null) {
			parseXml=new ParseXml();
		}
	int parseXmlStatus=parseXml.setParseXml(PageID);
	switch (parseXmlStatus) {
	case 0:
		parseXml=null;
		break;
	case -1:
		parseXml=null;
		break;
	case 1:
		return;
	default:
		break;
	}	
	}
    public final ServiceConnection recordConnection=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			recordService=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			recordService=(MyBinder) service;
		}
	};

	public void setDealSmartPenGesture(MainActivity activity){
		this.activity=activity;
         intent = new Intent(activity, RecordingService.class);
	}
	
	
	
	private ArrayListMultimap<String, GesturePlaceAndResource> gesturePlaceContainer= ArrayListMultimap.create(); // Book=100笔迹数据
	private float delt=15;//判断手势是否在同一个地方的手势边框的冗余量；
	


	public String recogniseSmartPenGesture(SmartPenGesture gesture){
//	GestureLibrary gestureLibrary=GestureLibraries.fromFile("/sdcard/zgmgesture");
	if (activity.gestureLibrary.load()) {
//		Log.e("zgm", "1210：手势文件装载成功");
	Set<String> aSet=activity.gestureLibrary.getGestureEntries();
/*	for (String string : aSet) {
		Log.e("zgm", "0113:"+string);
	}*/
/*	Gesture firstGesture=new Gesture();
	firstGesture.addStroke(gesture.getStrokes().get(0));*/
		ArrayList<Prediction> predictions=activity.gestureLibrary.recognize(gesture);
        ArrayList<GestureScore> gestureScores=new ArrayList<DealSmartPenGesture.GestureScore>();
        for (Prediction prediction :predictions) {
			if (prediction.score>2.5) {
				gestureScores.add(new GestureScore(prediction.name, prediction.score));
			}
		}
        String gestureName = getHightestScoreGesture(gestureScores);
        return gestureName;
	    }else {
			return null;
		}
	}

/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：手势识别主逻辑
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-24 下午4:08:41
 * @param currentSmartPenGesture
 */
	public void dealWithGesture(SmartPenGesture currentSmartPenGesture) {
		String gestureName = null;
		if (currentSmartPenGesture.getStrokesCount()==1) {
        gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
			if (gestureName==null) {
//				activity.updateUsingInfo("未识别的手势",activity.ORDERSTATE);
				gestureFinalName="未知手势";
				return;
			}
//			if (gestureName.equals("录音")&&tag.get(1)!=2) {//不等于题号区
			if (gestureName.equals("录音")) {
				activity.showTextView(activity.textView,"截屏");
				final SmartPenGesture tempSmartPenGesture=currentSmartPenGesture;
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						RectF disTailContorlGestureBoundingBox=	getControlGestureBody(activity.temContair);
						int delt=10;
						if(disTailContorlGestureBoundingBox.bottom-disTailContorlGestureBoundingBox.top>20||disTailContorlGestureBoundingBox.right-disTailContorlGestureBoundingBox.left>20) {
//							Log.e("zgm","bottom-top:"+(disTailContorlGestureBoundingBox.bottom-disTailContorlGestureBoundingBox.top));
//							Log.e("zgm","right-left:"+(disTailContorlGestureBoundingBox.right-disTailContorlGestureBoundingBox.left ));
					activity.showSharePicture(activity.saveScreen(activity.drawView,disTailContorlGestureBoundingBox));
//					boolean statsu=	UpLoad.uploadFile("http://118.24.109.3/Public/smartpen/upload.php","/sdcard/-1/"
//							+ activity.sharePictureName);	
/*				if (statsu) {
					activity.showSound(R.raw.upload_sucess);					
//					Toast.makeText(getBaseContext(), "上传成功", Toast.LENGTH_SHORT).show();
				}
				else {
					activity.showSound(R.raw.upload_fail);	
//					Toast.makeText(getBaseContext(), "上传失败", Toast.LENGTH_SHORT).show();
				}*/
						return;
						}
						
						
						
//						activity.showTextView(activity.textView,"控制指令");
//						Log.e("zgm", "gestureName:"+gestureName);
//						String audioName="001-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-"+tag.get(0)+".mp3";
/*						String audioName="123.mp3";
						Log.e("zgm", "audioName:"+audioName);
						File file=new File("/sdcard/xyz/"+audioName);
						if (file.exists()) {
							Log.e("zgm", "音频文件存在");
							playAudio("/sdcard/xyz/"+audioName);
							return;
						}*/
						recordAudioGestureProcess(disTailContorlGestureBoundingBox);//录音文件不存在，则启动录音						
					}}).start();

				return ;
			}
			if (gestureName.equals("对")) {
				//正常这里不应该进行处理直接返回
//				activity.updateUsingInfo("您画的是判题类手势:对",activity.ORDERSTATE);	
				activity.showTextView(activity.textView,"您画的是判题类手势:对");
				gestureFinalName="对";
				gestureResponce="语音响应";
				return;
			}
			//手势只有一笔，且不是录音和选题，交给wsk处理
			activity.showTextView(activity.textView,"未识别的一笔手势");
			return;
		}
		else {//手势笔画数大于1,对每一笔都进行识别,看是否有对号，录音符号
			SmartPenGesture tempGesture=new SmartPenGesture();
			int tempIndex=-1;
			int situation=-1;
			for (int i = 0; i < currentSmartPenGesture.getStrokesCount(); i++) {//拆分手势，进行单笔识别
				tempGesture.SmartPenGestureClearAllStroke();
				tempGesture.SmartPenGestureClearmBoundingBox();
				tempGesture.addStroke( currentSmartPenGesture.getStrokes().get(i));//将每一笔手势都重新放入临时手势中进行识别
				gestureName=recogniseSmartPenGesture(tempGesture);
				if (gestureName==null) {
					continue;
				}
				if (gestureName.equals("录音")) {
					situation=1;
					tempIndex=i;
					break;
				}
				if (gestureName.equals("对")) {
					situation=2;
//					tempIndex=i;
					break;
				}
			}
			
			switch (situation) {
			case 1://其中一笔是录音手势
//				重新封装剩下的笔画，用其他方法识别
				tempGesture.SmartPenGestureClearAllStroke();
				for (int i = 0; i <currentSmartPenGesture.getStrokesCount(); i++) {
					if (i==tempIndex) {
						continue;
					}
					tempGesture.addStroke(currentSmartPenGesture.getStrokes().get(i));
				}
				//将剩下的手势进行重新处理
				Bitmap bitmap=tempGesture.toBitmap(32, 32, 2, Color.WHITE);
			String simble=	matrix(bitmap);//概率小于0.6则返回null
			if (simble==null) {
                activity.showTextView(activity.textView,"您画的是投票类手势：但我不知道选了什么");
				gestureFinalName="未知手势";
				return;
			}
			activity.showTextView(activity.textView,"选择："+simble);
				break;
			case 2://其中一笔是对
				switch (currentSmartPenGesture.getStrokesCount()) {
				case 2:
					gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
					if (gestureName!=null&&gestureName.equals("错")) {
//						activity.updateUsingInfo("您画的是判题类手势:错",activity.ORDERSTATE);	
//						activity.textView.setText("您画的是判题类手势:错");
						activity.showTextView(activity.textView,"您画的是判题类手势:错");
						gestureResponce="语音响应";
						gestureFinalName="错";
						return;
					}
//					activity.updateUsingInfo("您画的是判题类手势:半对",activity.ORDERSTATE);
//					activity.textView.setText("您画的是判题类手势:半对");
					activity.showTextView(activity.textView,"您画的是判题类手势:半对");
					gestureFinalName="半对";
					return;
				case 3:
//					activity.textView.setText("您画的是判题类手势:半对2");
					activity.showTextView(activity.textView,"您画的是判题类手势:半对2");
//					activity.updateUsingInfo("您画的是判题类手势:半对2",activity.ORDERSTATE);
					gestureResponce="语音响应";
					gestureFinalName="半对2";
					return;
				case 4:
//					activity.textView.setText("您画的是判题类手势:半对3");
					activity.showTextView(activity.textView,"您画的是判题类手势:半对3");
//					activity.updateUsingInfo("您画的是判题类手势:半对3",activity.ORDERSTATE);
					gestureResponce="语音响应";
					gestureFinalName="半对3";
					return;
				default:
					gestureFinalName="未知手势";
					break;
				}
//				activity.textView.setText("您画的手势我没有识别");
				activity.showTextView(activity.textView,"您画的手势我没有识别");
//				activity.updateUsingInfo("您画的手势我没有识别",activity.ORDERSTATE);
				gestureFinalName="未知手势";
				break;
			case -1:
				gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
				if (gestureName!=null&&gestureName.equals("错")) {
//					activity.textView.setText("您画的是判题类手势:错");
					activity.showTextView(activity.textView,"您画的是判题类手势:错");
//					activity.updateUsingInfo("您画的是判题类手势:错",activity.ORDERSTATE);
					gestureResponce="语音响应";
					gestureFinalName="错";
					return;
				}
/*				Bitmap mbitmap=currentSmartPenGesture.toBitmap(32, 32, 2, Color.WHITE);
				String msimble=	matrix(mbitmap);
				if (msimble==null) {
					if (currentSmartPenGesture.getStrokesCount()==2) {
						gestureName=recogniseSmartPenGesture(currentSmartPenGesture);

						
					}*/
//					activity.updateUsingInfo("您画的手势我没有识别",activity.ORDERSTATE);
//				}
//				activity.updateUsingInfo("您画的手势是："+msimble,activity.ORDERSTATE);
				
//				activity.updateUsingInfo("您画的手势我没有识别",activity.ORDERSTATE);
				activity.showTextView(activity.textView,"您画的是判题类手势:错");
//				activity.textView.setText("您画的是判题类手势:错");
				gestureFinalName="未知手势";
				break;
			default:
				gestureFinalName="未知手势";
				break;
			}
		}
	}	
	
public String getHightestScoreGesture(ArrayList<GestureScore> arrayList){
	if (arrayList.size()==0) {
		return null;
	}
	double hightestScore=-5;
	String  gestureName=null;
	for (int i=0;i<arrayList.size();i++) {
		if (arrayList.get(i).getGestureScore()>hightestScore) {
			hightestScore=arrayList.get(i).getGestureScore();
			gestureName=arrayList.get(i).getGestureNmae();
		}
	}
	return gestureName;	
}	
private	 boolean isAlmostEqual(RectF rectF1,RectF rectF2){
	float centerPointX1=(rectF1.right+rectF1.left)/2;
	float centerPointY1=(rectF1.bottom+rectF1.top)/2;
	float centerPointX2=(rectF2.right+rectF2.left)/2;
	float centerPointY2=(rectF2.bottom+rectF2.top)/2;
/*	float leftDistance=Math.abs(rectF1.left-rectF2.left);
//	float topDistance=Math.abs(rectF1.top-rectF2.top);
//	float rightDistance=Math.abs(rectF1.right-rectF2.right);
	float bottomDistance=Math.abs(rectF1.bottom-rectF2.bottom);
	*/
	if (Math.abs(centerPointX1-centerPointX2)<delt&&Math.abs(centerPointY1-centerPointY2)<delt) {
		return true;
	}
	return false;	
}

public class GestureScore{
	String gestureName="";
	double gestureScore=0;
	public GestureScore(String gestureName, double score) {
		// TODO Auto-generated constructor stub
		this.gestureName=gestureName;
		this.gestureScore=score;
	}
	public String getGestureNmae() {
		return gestureName;
		
	}
	public double getGestureScore(){
		return gestureScore;
		
	}
}	

private class GesturePlaceAndResource{
	String gestureNameString="";
	String resourcePathString="";
	long    resourceElpased=0;
    final	RectF rectF;
	
	GesturePlaceAndResource(String gestureNameString,String resourcePathString,long resourceElpased,RectF rectF){
		this.gestureNameString=gestureNameString;
		this.resourcePathString=resourcePathString;
		this.resourceElpased=resourceElpased;//播放时长
		this.rectF=rectF;
	}
public String getGestureNameString(){
	return gestureNameString;
}
public String getResourcePath(){
	return resourcePathString;
}
public RectF getGesturePlace(){
	return this.rectF;
}

public long getresourceElpased(){
	return resourceElpased;
}
}

private void onRecord(boolean start) {
    if (start) {

//        Toast.makeText(activity, "开始录音...", Toast.LENGTH_SHORT).show();
        File folder = new File(Environment.getExternalStorageDirectory() + "/penconNativeRecord");
        if (!folder.exists()) {
            //folder /SoundRecorder doesn't exist, create the folder
            folder.mkdir();
        }

        //start Chronometer
//        mChronometerTime.setBase(SystemClock.elapsedRealtime());
//        mChronometerTime.start();
        //start RecordingService
        activity.startService(intent);
        if (activity.bindService(intent,recordConnection,Service.BIND_AUTO_CREATE)) {
			Log.e("zgm", "1217:绑定成功！");
		}else {
			Log.e("zgm", "1217:绑定失败！ "+activity.getClass().getName());
		}
//        
        
        //keep screen on while recording
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    } else {
//        Toast.makeText(activity, "录音结束...", Toast.LENGTH_SHORT).show();
        activity.unbindService(recordConnection);
        activity.stopService(intent);
    }
}

public void playAudio(GesturePlaceAndResource gesturePlaceAndResource){
//	activity.updateUsingInfo("播放相关资源"+gesturePlaceAndResource.getResourcePath(),activity.ORDERSTATE);
Log.e("zgm","1218：播放相关资源"+gesturePlaceAndResource.getResourcePath());
final MediaPlayer mMediaPlayer = new MediaPlayer();
try {
    mMediaPlayer.setDataSource( Environment.getExternalStorageDirectory().getAbsolutePath()+"/SoundRecorder/" + "recordAudio"+ ".mp3");
    mMediaPlayer.prepare();

} catch (IOException e) {
    Log.e("zgm", "prepare() failed");
}
mMediaPlayer.start();
new Thread(new Runnable() {
	
	@Override
	public void run() {
		long tempStartTime=System.currentTimeMillis();
		while (System.currentTimeMillis()-tempStartTime<3*1000) {
			//空循环
		}
//		activity.updateUsingInfo("留言播放结束",activity.ORDERSTATE);
//		activity.textView.setText("留言播放结束");
		activity.showTextView(activity.textView,"留言播放结束");
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        activity.isDealPenPoint=true;
	}
}).start();
return;
/*						com.example.pencon.RecordingItem recordingItem = new com.example.pencon.RecordingItem();
//播放相关资源
recordingItem.setLength((int) gesturePlaceAndResource.getresourceElpased());
recordingItem.setFilePath(gesturePlaceAndResource.getResourcePath());
com.example.pencon.PlaybackDialogFragment fragmentPlay = com.example.pencon.PlaybackDialogFragment.newInstance(recordingItem);
fragmentPlay.show(activity.getSupportFragmentManager(), com.example.pencon.PlaybackDialogFragment.class.getSimpleName());
*/	

}


public  void  playAudio(final String path){
	 final MediaPlayer mMediaPlayer = new MediaPlayer();
		try {
		    mMediaPlayer.setDataSource(path);
		    mMediaPlayer.prepare();

		} catch (IOException e) {
		    Log.e("zgm", "prepare() failed");
		}
		mMediaPlayer.start();
	new Thread(new Runnable() {
		
		@Override
		public void run() {
while(mMediaPlayer.isPlaying()) {
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}
if (!mMediaPlayer.isPlaying()) {
	mMediaPlayer.release();
}



		}
	}).start();


	
}
public void recordAndio(final RectF boundingBox,boolean isjustforname){
	String gestureNameString="录音";
	GesturePlaceAndResource gesturePlaceAndResource;
//	 Log.e("zgm","/sdcard/xyz/001-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-"+tag.get(1)+".mp3");
	 
	if (isjustforname) {
		
		 if(boundingBox!=null){
			 RectF tempRectF=new RectF(boundingBox);
    		  gesturePlaceAndResource=new GesturePlaceAndResource(gestureNameString,"/sdcard/xyz/001-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-"+tag.get(1)+".mp3",2000,tempRectF);
    		  Log.e("zgm","/sdcard/xyz/001-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-"+tag.get(1)+".mp3");
    		 gesturePlaceContainer.put(gestureNameString, gesturePlaceAndResource);//将手势和相关的信息加入gesturePlaceContainer中;
return;
		 }		
	}
//	activity.updateUsingInfo("请录音",activity.ORDERSTATE);
//	activity.textView.setText("请录音");
	activity.showTextView(activity.textView,"请录音");
		onRecord(true);

	
//	activity.updateUsingInfo("录音当中，录音3s，请留言",activity.ORDERSTATE);
//	activity.textView.setText("录音当中，录音3s，请留言");
	activity.showTextView(activity.textView,"录音当中，录音3s，请留言");
	new Thread(new Runnable() {		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long temStartTime=System.currentTimeMillis();
			while (System.currentTimeMillis()-temStartTime<3*1000) {
			}
			onRecord(false);//结束录音
//			activity.showSound(R.raw.endrecord);
//			activity.showVibrator();// 震动
			String filePath=recordService.getService().getMFilePath();
			activity.soundPathString=filePath;
    		if (filePath==null) {
//				activity.updateUsingInfo("没有录音",activity.ORDERSTATE);
//				activity.textView.setText("没有录音");
				activity.showTextView(activity.textView,"没有录音");
				
				return;
				
			}
			 long elpased =recordService.getService().getMFileElpased();
			 String gestureNameString="录音";
			 if(boundingBox!=null){
				 RectF tempRectF=new RectF(boundingBox);
	    		 GesturePlaceAndResource gesturePlaceAndResource=new GesturePlaceAndResource(gestureNameString,filePath,elpased,tempRectF);
	    		 gesturePlaceContainer.put(gestureNameString, gesturePlaceAndResource);//将手势和相关的信息加入gesturePlaceContainer中;
 
				 
				 
			 }
//			 activity.textView.setText("录音完成，音频文件路径："+filePath);
				activity.showTextView(activity.textView,"录音完成，音频文件路径："+filePath);
//    		 activity.updateUsingInfo("录音完成，音频文件路径："+filePath,activity.ORDERSTATE);
    		 activity.isDealPenPoint=true;
		}
	}).start();	
	
	
}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-23 下午10:33:44
 * @param gestureName
 * @param gestureStroke
 * @return:返回的是点的序号（从零开始），因此返回值不会大于gestureStroke.points.length/2-1;
 */

public int  getTailIndex(String gestureName,ArrayList<Position> temContair){
	if (!gestureName.equals("录音")) {
		return -1;
	}
	if ( temContair.size()<8) {
		return -1;
	}
	boolean first=true;
	boolean second=false,third=false,forth=false;
	
	int index=-1;
	float x1,y1,x2,y2;

for (int i = 0; i < temContair.size()-1; i++) {
	x1=temContair.get(i).x;
	y1=temContair.get(i).y;
	x2=temContair.get(i+1).x;
	y2=temContair.get(i+1).y;
	if(first){
		if ((x2-x1)>1&&(y2-y1)>1) {//从左上到右下变化趋势
			first=false;
			second=true;
		}
	}
	if(second) {
		if ((x2-x1)>=1&&(y2-y1)<=-1) {//从左下到右上
			second=false;
			third=true;
			}				
	}
		if(third) {
			if ((x2-x1)<=-1&&(y2-y1)>=-1) {//从右上到左下

				third=false;
				forth=true;				
			}
	}
		if (forth) {
			if ((x2-x1)>1&&(y2-y1)<1) {//从左下到右上
               index=i;
               return index;
			}		
	}
}	
	return -1;	
}

public RectF getBoundingBox(int endIndex,ArrayList<Position> temContaier){
	if (endIndex<0||temContaier.size()==0) {
		return null;
	}
	RectF boundingBox=new RectF();
	Dot tempDot;
	for (int i = 0;i < temContaier.size(); i++) {
		
		if (i<=endIndex) {
			if (i==0) {
				boundingBox.left=temContaier.get(i).x;
				boundingBox.right=temContaier.get(i).x;
				boundingBox.top=temContaier.get(i).y;
				boundingBox.bottom=temContaier.get(i).y;
				continue;
			}
			boundingBox.union(temContaier.get(i).x,temContaier.get(i).y);
			continue;
		}
		break;
	}
	return boundingBox;
}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：还未进行调试(可能有bug,暂时还没有使用)，对点序列中的相邻两个点直接进行插值，使其x坐标连续(每一个整数都能对应一个x坐标).
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-20 上午11:26:58
 * @param gestureStroke
 * @return
 */

public void recordAudioGestureProcess(RectF disTailContorlGestureBoundingBox){
	if(activity==null){  return; }
	String gestureName="录音";
	/*
	 * 以下代码主要是获得录音手势去掉尾巴的的边界矩形
	 */
//	RectF mainBoundingBox=null;
/*
 * 注意录音手势应该只有一笔，这里就认为其只有一笔，所以下面的代码只适用于只有一笔的录音手势
 */
/*	    int endIndex= getTailIndex( gestureName,currentSmartPenGesture.getStrokes().get(0));
	    if (endIndex==-1) {
	    	endIndex=currentSmartPenGesture.getStrokes().get(0).points.length/2-1;
		}
	    mainBoundingBox=getBoundingBox(endIndex, currentSmartPenGesture.getStrokes().get(0));
*/	/*
	 * 以上代码主要是获得录音手势去掉尾巴的的边界矩形
	 */
/*			if(gesturePlaceContainer.get(gestureName).size()==0){
		//还没有录过音，开始第一个录音
	return;
	}else */
	{//代码块，匹配手势用
		
		activity.isDealPenPoint=false;
		Log.e("zgm", "1218:"+gestureName);
		List<GesturePlaceAndResource> a = gesturePlaceContainer.get(gestureName);
		if (a.size()>0) {
			Log.e("zgm","0108:"+a.size()+":"+a.get(0).rectF);	
		}

		for (GesturePlaceAndResource gesturePlaceAndResource : gesturePlaceContainer.get(gestureName)) {
			if (isAlmostEqual(disTailContorlGestureBoundingBox,gesturePlaceAndResource.getGesturePlace())) {
				playAudio(gesturePlaceAndResource);
                  return;
				
			}
		}
	}
	
	{//代码块，没有匹配到手势，那么就开始录音相关的操作
	Log.e("zgm","1216：没有匹配到录音手势");

		recordAndio( disTailContorlGestureBoundingBox,!activity.doSomeworkIsOK);

	}//代码块完	
}

 public float[]  linearInterpolation(GestureStroke gestureStroke){
	ArrayList<Float> interpolatedPoints=new ArrayList<Float>();
	if (gestureStroke.points.length<4) {
		return gestureStroke.points;
	}
	int x1,y1,x2,y2;
	for (int i = 0; i < gestureStroke.points.length/2-1; i=i+1) {
		x1=Math.round(gestureStroke.points[i*2]);
		y1=Math.round(gestureStroke.points[i*2+1]);
		x2=Math.round(gestureStroke.points[(i+1)*2]);
		y2=Math.round(gestureStroke.points[(i+1)*2+1]);
		if (i==0) {
			interpolatedPoints.add((float) x1);
			interpolatedPoints.add((float) y1);
		}
		if (x1==x2&&y1==y2) {//情况1
			interpolatedPoints.add((float) x2);
			interpolatedPoints.add((float) y2);
			continue;
		}
		if (x1==x2&&y1!=y2) {//情况2
			int tempy=y1;
			if (tempy<y2) {
				while(tempy<y2){
					interpolatedPoints.add((float) x1);
					interpolatedPoints.add((float) tempy+1);
					tempy=tempy+1;
				}
				continue;
			}
			if (tempy>y2) {
				while(tempy<y2){
					interpolatedPoints.add((float) x1);
					interpolatedPoints.add((float) tempy-1);
					tempy=tempy-1;
				}
				continue;
			}
		}
		if (x1!=x2&&y1==y2) {//情况3
			int tempx=x1;
			if (tempx<x2) {
				while(tempx<x2){
					interpolatedPoints.add((float) tempx+1);
					interpolatedPoints.add((float) y1);
					tempx=tempx+1; 
				}
				continue;
			}
			if (tempx>x2) {
				while(tempx>x2){
					interpolatedPoints.add((float) tempx-1);
					interpolatedPoints.add((float) y1);
					tempx=tempx-1;
				}
				continue;
			}
		}
		if (x1!=x2&&y1!=y2) {//情况4
		float bx1x2 =y1*(x2-x1)-x1*(y2-y1);
		int tempx=x1;
		float tempy;
		if (tempx<x2) {
			while (tempx<x2) {
				tempy=((tempx+1)*(y2-y1)+bx1x2)/(x2-x1);
				interpolatedPoints.add((float) tempx+1);
				interpolatedPoints.add((float) y1);	
				tempx=tempx+1;
			}
           continue;
		}
		if (tempx>x2) {
			while (tempx>x2) {
				tempy=((tempx-1)*(y2-y1)+bx1x2)/(x2-x1);
				interpolatedPoints.add((float) tempx-1);
				interpolatedPoints.add((float) y1);	
				tempx=tempx-1;
			}
           continue;
		}
		}
		
		
		
	}
	return null;
	
	
	
	
}         
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：求得手势的中心点坐标(所有笔画的中心)
 * @date：2019-1-23 下午9:05:28
 * @param currentSmartPenGesture
 * @return:返回手势的中心点坐标
 */
public float[] getGestureCenture(SmartPenGesture currentSmartPenGesture){
	float[] averges=new float[2];
	averges[0]=0;//averges[0]存放的是中心点x的坐标
	averges[1]=0;//averges[1]存放的是中心点y的坐标
	int counter=0;
	for (GestureStroke gStroke:currentSmartPenGesture.getStrokes()) {
		for (int i =0;i< gStroke.points.length/2; i++) {
			counter++;
			averges[0]=averges[0]+gStroke.points[2*i];
			averges[1]=averges[1]+gStroke.points[2*i+1];
		}
	}
	averges[0]=averges[0]/counter;
	averges[1]=averges[1]/counter;
	return averges;	
}

public double[][] layerout(double[][] a,double[][] b,double[][]c){
	double[][] d=new double[a.length][b[0].length];
	if(a[0].length==b.length) {
		for(int i=0;i<a.length;i++) {
			for(int j=0;j<b[0].length;j++) {
				for(int k=0;k<a[0].length;k++) {
					d[i][j]+=a[i][k]*b[k][j];
				}
		   }
	    }
    }
	if(d.length==c.length&&d[0].length==c[0].length) {
		for(int i=0;i<d.length;i++) {
			for(int j=0;j<d[0].length;j++) {
				d[i][j]=d[i][j]+c[i][j];
			}
		}
	}
	for(int i=0;i<c.length;i++) {
		d[i][0]=1.0/(1+1/Math.exp(d[i][0]));
	}
	return d;
}

public double[][] in_array(String string,int wigth,int height) throws IOException {
	BufferedReader in = new BufferedReader(new FileReader(string));
	double[][] arr = new double [height][wigth];
	String line = "";
	int row=0;
	while((line =in.readLine())!=null) {
		Log.i("di", "tmp"+row);
		String[] tmp = line.split(" ");
//   		Log.i("di", "tmp"+tmp[25]);
		for(int i=0;i<tmp.length;i++) {
//   			Log.i("di", "tmpi"+i);
			arr[row][i]= Double.valueOf(tmp[i]);
		}
		row++;
		
	}
	in.close();
	return arr;
}

private String matrix(Bitmap bitmap) {
	

	String picName = System.currentTimeMillis()+".jpg";
	File file = new File( "/sdcard/classify/"+picName);
	FileOutputStream out = null;
	try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
			System.out.println("___________保存__sd___下_______________________");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			Log.e("zgm", ":"+e);
			e.printStackTrace();
		}
    double[][] arr=new double[1024][1];
    int r=0;
    for(int i=0;i<32;i++) {
    	for(int j=0;j<32;j++) {
    		Log.i("di", "pixel"+bitmap.getPixel(j, i));
    		if(bitmap.getPixel(j,i)==0) {
    			arr[r][0]=1;
    		}
    		else {
    			arr[r][0]=0;
    		}
//     		Log.i("di", "a"+r);
    		if(arr[r][0]==0) {
   // 		Log.i("di", "a"+r);
    		}
    		r++;
    		      		
    	}
    }
	if (!bitmap.isRecycled()) {
		bitmap.recycle();
    }
    try {
		double[][]w=in_array("/sdcard/classify/w.txt",28,5);
		double[][]b=in_array("/sdcard/classify/b.txt",1,5);
		double[][]w_h=in_array("/sdcard/classify/w_h.txt",1024,28);
		double[][]b_h=in_array("/sdcard/classify/b_h.txt",1,28);
		double[][] hid=layerout(w_h,arr,b_h);
		double[][] pre=layerout(w,hid,b);
		String[] a = {"A","B","C","D","E"};
		double max=0;
		
		for (int i=0;i<pre.length;i++) {
			Log.i("di", "type"+pre[i][0]);
			if(pre[i][0]>max) {
				max=pre[i][0];
			}		
		}
		if (max<0.9) {
			return null;	
		}
		for (int i=0;i<pre.length;i++) {				
			if(pre[i][0]==max) {

				//showInftTextView.setText("字母：" + a[i]);
				return a[i];

			}

		}
    }
		catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Log.e("zgm", "读取参数矩阵失败:"+e);
		return null;
	}
	return null;
}

/**
 * 判断某个手势是否是单击
 * @param currentSmartPenGesture
 * @return true：是单击；false:不是单击
 */
public boolean isclick(SmartPenGesture currentSmartPenGesture) {
if (currentSmartPenGesture.getStrokesCount()!=1) {
	return false;
}
RectF rectF=currentSmartPenGesture.getGestureBoundBoxRect();
	if (Math.abs(rectF.right-rectF.left)<1&&Math.abs(rectF.bottom-rectF.top)<1) {
		return true;
	}
	
	return false;
	
	
}

//wsk 2019.1.26
//读题目和三维语义
/*public void ReadTiMu(int pageID)
{
	ArrayList<Integer> tag = new ArrayList<Integer>();
	
	tag = ReadQuestion.ReceiveQuestionDots(point_number1, point_number2, bihua, pageID);
	
	if(tag == null)
	{
		return;
	}
	
	else
	{
		//读题
		if(tag.get(1) == 0)
		{
			switch(tag.get(0))
			{
			case 1:showSound(R.raw.onetigan);
			break;
			case 2:showSound(R.raw.twotigan);
			break;
			case 3:showSound(R.raw.threetigan);
			break;
			case 4:showSound(R.raw.fourtigan);
			break;
			case 5:showSound(R.raw.fivetigan);
			break;
			case 6:showSound(R.raw.sixtigan);
			break;
			case 7:showSound(R.raw.seventigan);
			break;
			case 8:showSound(R.raw.eigthttigan);
			break;
			case 9:showSound(R.raw.ninetigan);
			break;
			default:break;
			}
		}
		
		else if(tag.get(1) == 1)
		{
			//读要求
			showSound(R.raw.sanweiyuyi);
		}
		
		else return;
	}
}
*/

public List<points> singleSmartPenGestureToPointsList(SmartPenGesture currentSmartPenGesture){
	List<points> points;
	
	
	return null;
	
}
/**
 * 
 * @param currentSmartPenGesture 一笔手势，多笔不做处理
 * @return: tag[2],tag=null:不在任何区域 ;tag[0]:题号；tag[1]:题号对应题目的某个区域
 */
public ArrayList<Integer> getChirographyPositionInfo(SmartPenGesture currentSmartPenGesture,int mPageID ) {
/*	if (currentSmartPenGesture.getStrokesCount()!=1) {//不是一笔，直接返回null
		return null;
	}*/
	float[] averages=getGestureCenture(currentSmartPenGesture);//averages[0]:x平均值;averages[1]:x平均值
	//得到区域-题干区
	averages[0]=(float) (averages[0]/138.14*1519)+20;
	averages[1]=(float) (averages[1]/194.296*2151)+100;
	ArrayList<Integer> tag=testxml.test( averages[0], averages[1],activity.gCurBookID);
	return tag;
}
/**
 * 
* @Title: getControlGestureBody 
* @Description: TODO :获取控制指令符号的主体(去掉最后的尾巴)
* @return 
*@return:RectF(返回类型) 
* @throws：异常描述
*
* @version: v1.0.0
* @author: lfgm
* @date: 2019年4月25日 下午4:04:15
 */
public RectF getControlGestureBody(ArrayList<Position> temContainer) {
	/*
	 * 注意录音手势应该只有一笔，这里就认为其只有一笔，所以下面的代码只适用于只有一笔的录音手势
	 */
		    int endIndex= getTailIndex("录音",temContainer);
		    
		    if (endIndex==-1) {
		    	endIndex=temContainer.size();
			}

		    RectF mainBoundingBox = getBoundingBox(endIndex,temContainer);
		/*
		 * 以上代码主要是获得录音手势去掉尾巴的的边界矩形
		 */
//activity.drawRectF(mainBoundingBox);
activity.updateInfo(getsignallength(endIndex,temContainer), activity.TAILLENGTH);//控制符号尾巴长度		    
activity.updateInfo(getsignallength(0,temContainer), activity.GESTURELENGTH);//控制符号整体长度
activity.updateInfo(mainBoundingBox.right-mainBoundingBox.left, activity.BOUNDINGBOXWIDTH);//控制符号主体边框宽
activity.updateInfo(mainBoundingBox.bottom-mainBoundingBox.top, activity.BOUNDINGBOXHEIGHT);//控制符号主体边框高
activity.updateInfo(changeTimes(endIndex,temContainer), activity.TAILECHANGETIMES);//控制符号尾巴变化次数
activity.updateInfo(temContainer.size()-endIndex,activity.TAILPOINTCOUNTER);//控制符号尾巴变化次数
ArrayList<Position> tepmCountainer1=new ArrayList<Position>();
//int[] temIndexs=getLongestLineArea(endIndex,temContainer);
//for (int i = temIndexs[0]; i <temIndexs[1]+1; i++) {
	for (int i =endIndex; i <temContainer.size(); i++) {
	tepmCountainer1.add(temContainer.get(i));
}

float[] a_b=getKandB(tepmCountainer1);
activity.updateInfo(a_b[0],activity.TAILSLOPE);//控制符号尾巴变化次数
float temp =temContainer.get(temContainer.size()-1).x;

//activity.drawLine(temContainer.get(temIndexs[0]).x,temContainer.get(temIndexs[0]).y, temp, a_b[0]*temp+a_b[1]);
activity.drawLine(temContainer.get(endIndex).x,temContainer.get(endIndex).y,temp,a_b[0]*temp+a_b[1]);

	return mainBoundingBox;
}
public float getsignallength(int startIndex,ArrayList<Position> temContainer) {
	float sum=0;
	float x=0,y=0,tmpx=0,tmpy=0;
	for (int i = startIndex; i < temContainer.size(); i++) {
  	  if(i==startIndex) {
		  x=temContainer.get(i).x;
    	  y=temContainer.get(i).y;
    	  tmpx=x;
    	  tmpy=y;
    	  sum+=Math.sqrt((x-tmpx)*(x-tmpx)+(y-tmpy)*(y-tmpy));
	}else {
		  x=temContainer.get(i).x;
    	  y=temContainer.get(i).y;
  	  sum+=Math.sqrt((x-tmpx)*(x-tmpx)+(y-tmpy)*(y-tmpy));
  	  tmpx=x;
  	  tmpy=y;
	  } 	
}
	return sum;
}	

public int changeTimes(int startIndex,ArrayList<Position> temContainer) {
	int changeTimes=0;
	float x0=0,y0=0,x1=0,y1=0;
	int lastStateX=-1;
	int currentStateX=-1;
	int lastStateY=-1;
	int currentStateY=-1;
	for (int i =startIndex; i < temContainer.size()-1; i++) {
		x0=temContainer.get(i).x;
		y0=temContainer.get(i).y;
		x1=temContainer.get(i+1).x;
		y1=temContainer.get(i+1).y;		
if (x1-x0>1) {//x增加
	currentStateX=0;
}
if (x1-x0<-1) {//x减少
	currentStateX=1;
}
if (x1-x0>-1&&x1-x0<1) {//x持平
	currentStateX=2;
}
if (y1-y0>1) {//x增加
	currentStateY=0;
}
if (y1-y0<-1) {//x减少
	currentStateY=1;
}
if (y1-y0>-1&&y1-y0<1) {//x持平
	currentStateY=2;
}
if(currentStateX!=lastStateX||currentStateY!=lastStateY) {
	changeTimes++;
	if (currentStateX!=lastStateX) {
		lastStateX=currentStateX;
	}
	if (currentStateY!=lastStateY) {
		lastStateY=currentStateY;
	}
}
	}	
	return changeTimes;	
}

public float[] getKandB(ArrayList<Position> posList){
	//List<Position>posList = new ArrayList();
	float[] result = new float[2];
	float k;
	float b;
	float x_y = 0;
	float x = 0;
	float y = 0;
	float x2 = 0;
	float x_mean;
	float y_mean;
	for(Position pos:posList){
		x_y+=pos.x*pos.y;
		x+=pos.x;
		y+=pos.y;
		x2+=pos.x*pos.x;
		
	}
	x_mean=x/posList.size();
	y_mean=y/posList.size();
//	b=y_mean-x_mean;
	if(x2-x*x/posList.size()!=0){
	k=(x_y-x*y/posList.size())/(x2-x*x/posList.size());
	b=y_mean-x_mean*k;
	result[0]=k;//斜率
	result[1]=b;//截距
	}
	else{
		result[0]=Float.MAX_VALUE;//斜率
	    result[1]=0;//截距
	}
	return result;
	}
public  int[] getLongestLineArea(int startIndex,ArrayList<Position> positions) {
	int[] mIndex=new int[2];
	int tempStartIndex=startIndex;
	double length=0;
	double sum=0;
	double d=0;
	float x0=0,y0=0,x1=0,y1=0;
	int lastStateX=-1;
	int currentStateX=-1;
	int lastStateY=-1;
	int currentStateY=-1;
	for (int i =startIndex; i < positions.size()-1; i++) {
		x0= positions.get(i).x;
		y0= positions.get(i).y;
		x1= positions.get(i+1).x;
		y1= positions.get(i+1).y;
		d=Math.sqrt((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0));
//		sum=sum+d;
if (x1-x0>1) {//x增加
	currentStateX=0;
}
if (x1-x0<-1) {//x减少
	currentStateX=1;
}
if (x1-x0>-1&&x1-x0<1) {//x持平
	currentStateX=2;
}
if (y1-y0>1) {//x增加
	currentStateY=0;
}
if (y1-y0<-1) {//x减少
	currentStateY=1;
}
if (y1-y0>-1&&y1-y0<1) {//x持平
	currentStateY=2;
}
if(currentStateX!=lastStateX||currentStateY!=lastStateY) {
if (sum>length) {
	length=sum;
	sum=0;
	sum=+d;
	mIndex[0]=tempStartIndex;
	mIndex[1]=i;
	tempStartIndex=i;
}

	if (currentStateX!=lastStateX) {
		lastStateX=currentStateX;
	}
	if (currentStateY!=lastStateY) {
		lastStateY=currentStateY;
	}
}else {
	sum=+d;
}

	}
	if (sum>length) {
		length=sum;
		mIndex[0]=tempStartIndex;
		mIndex[1]= positions.size()-1;
	}
	return mIndex;
	
}
}
