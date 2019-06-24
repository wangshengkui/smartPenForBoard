package com.example.smartpenforboard;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.example.smartpengesture.DealSmartPenGesture;
import com.example.smartpengesture.SmartPenGesture;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder; 
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public String sharePictureName="";
	private Context mContext;
	private static final int REQUEST_SELECT_DEVICE = 1; // 蓝牙扫描
	private BluetoothLEService mService = null; // 蓝牙服务
	private PenCommAgent bleManager;
	public TextView textView;
	private float pointX;
	private float pointY;
	private int pointZ;//在这里是指压力值
	private int tmp;
	public DrawView drawView;
	private float g_x0;
	private float g_y0;
	private float g_x1;
	private float g_y1;
	public int gCurPageID = -1; // 当前PageID
	public int gCurBookID = -1; // 当前BookID
	private float gScale = 1; // 笔迹缩放比例
	private int gColor = 6; // 笔迹颜色
	private int gWidth = 5; // 笔迹粗细
	private float gOffsetX = 0; // 笔迹x偏移
	private float gOffsetY = 0; // 笔迹y偏移
	DisplayMetrics dm;
	public static float mWidth; // 屏幕宽，实际像素值
	public static float mHeight; // 屏幕高，实际像素值
	private String TAG = "MainActivity";
	private int A4_x = 136;// 是读取的码点坐标
	private int A4_y = 193;
	private int A3_x = 136;
	private int A3_y = 193;
	private int A2_x = 276;
	private int A2_y = 391;
	private float k = 1;
	private int drawType=0;//画图时，该值决定了是是否是起点(0为起点，1不是起点)
	private float gOffsetXOnPaper = 0; // 笔迹x偏移
	private float gOffsetYOnPaper = 0; // 笔迹y偏移
	private PopupMenu mPopupMenu = null;
//	private TextView hintTextView=null;
	private boolean isJiaoZhun = false;
	private int choosePaperSizeItemId = 0;
	private ImageView bgImageView;
	public AlertDialog builder;
	public  boolean firstOccur=true;
	public boolean doSomeworkIsOK=true;
	public String studentNumber="0995";
	MediaPlayer mediaPlayer = null;
	public boolean isDealPenPoint=false;
	public String soundPathString;
	private boolean ismSmartPenStrokeBufferNeedClear = true;
	private int intPenStrokeCount=0;
	RelativeLayout	relativeLayout=null;
	public DealSmartPenGesture dealSmartPenGesture = new DealSmartPenGesture();
	public GestureLibrary gestureLibrary = GestureLibraries.fromFile("/sdcard/zgmgesture");
	private SmartPenGesture currentSmartPenGesture = null;
	private volatile boolean firstPenChi = true;
	private volatile long penUpTime;
	private volatile boolean firstpen = true;
	protected volatile long curTime;
	private float startx=0;
	private float starty=0;
	private RectF GestureBoundingBox=null;
	 
	String informatrion="";
public	String taillength="";
public	String gesturelength="";
public	String boundingBoxWidth="";
public	String boundingBoxheight="";
public	String taileChangeTimes="";
public	String tailPointCounter="";
public	String tailSlope="";
public final int TAILLENGTH=1;
public final int GESTURELENGTH=2;
public final int BOUNDINGBOXWIDTH=3;
public final int BOUNDINGBOXHEIGHT=4;
public final int TAILECHANGETIMES=5;
public final int TAILPOINTCOUNTER=6;
public final int TAILSLOPE=7;
//	private Dot dotContainer;
//	BlockingQueue<Dot> queue = new LinkedBlockingQueue<Dot>();
	public Handler mHandler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
		switch(msg.what) {
		case 1://分享图片下载完成
			File file=new File("/sdcard/xyz/"+sharePictureName);
			if(file.exists()) {
				Bitmap bm=BitmapFactory.decodeFile("/sdcard/xyz/"+sharePictureName);
				showSharePicture(bm);
			}else {
				Toast.makeText(mContext, "没有检测到分享来的图片", Toast.LENGTH_SHORT).show();
			}
			break;
		
		}	
			
			
		}
	};
	private final ArrayList<GesturePoint> SmartPenStrokeBuffer = new ArrayList<GesturePoint>();
	public ArrayList<Position> temContair=new ArrayList<Position>();
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(final ComponentName className, IBinder rawBinder) {
//			updateUsingInfo(penName, PENNAME);
			mService = ((BluetoothLEService.LocalBinder) rawBinder).getService();
			Log.d("mService:", "onServiceConnected mService= " + mService);
			if (!mService.initialize()) {
				finish();
			}
//			penName = ApplicationResources.mPenName + "";
			mService.setOnDataReceiveListener(new BluetoothLEService.OnDataReceiveListener() {

				@Override
				public void onDataReceive(final Dot dot) {
/*					try {
						queue.put(dot);
						Log.e("zgm", "加入点了");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							switch (choosePaperSizeItemId) {
							case 0:
//								Toast.makeText(getBaseContext(), "请选择书写幅面，现在为默认A4", Toast.LENGTH_SHORT).show();
								processEachDot(dot);
								break;
							case R.id.A4:
								processEachDot(dot);
								break;
							case R.id.A3:
								processEachDot(dot);
								break;
							case R.id.A4_16:
								Process16A4EachDot(dot);
								break;
							case R.id.a2_4:
								Process4A2EachDot(dot);
								break;
							case R.id.a2_8:
								Process8A2EachDot(dot);
								break;
							case R.id.a2_4_picture:
								Process4A2HavePictureEachDot(dot);
//								A24PoictureProcessEachDot(dot);
								break;

							default:
								break;
							}

						}
					});
				}

				@Override
				public void onOfflineDataReceive(final Dot dot) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
//							ProcessDots(dot);
						}
					});
				}

				@Override
				public void onFinishedOfflineDown(boolean success) {
					// Log.i(TAG, "---------onFinishedOfflineDown--------" +
					// success);
					/*
					 * layout.setVisibility(View.GONE); bar.setProgress(0);
					 */
				}

				@Override
				public void onOfflineDataNum(final int num) {
					// Log.i(TAG, "---------onOfflineDataNum1--------" + num);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// textView.setText("离线数量有" +
									// Integer.toString(num * 10) + "bytes");
									/*
									 * 
									 * //if (num == 0) { // return; //}
									 * 
									 * Log.e("zgm","R.id.dialog1"+R.id.dialog); dialog =
									 * (RelativeLayout)findViewById(R.id .dialog);
									 * Log.e("zgm","dialog"+dialog.getId()); dialog.setVisibility(View.VISIBLE);
									 * textView = (TextView) findViewById(R.id.textView2); textView.setText("离线数量有"
									 * + Integer.toString(num * 10) + "bytes"); confirmBtn = (Button)
									 * findViewById(R.id.but); confirmBtn.setOnClickListener(new
									 * View.OnClickListener() {
									 * 
									 * @Override public void onClick(View view) { dialog.setVisibility(View.GONE); }
									 * });
									 */
								}

							});
						}
					});
				}

				@Override
				public void onReceiveOIDSize(final int OIDSize) {// 这里接收点读数据
				}

				@Override
				public void onReceiveOfflineProgress(final int i) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							/*
							 * if (startOffline) {
							 * 
							 * layout.setVisibility(View.VISIBLE); text.setText("开始缓存离线数据");
							 * bar.setProgress(i); Log.e(TAG, "onReceiveOfflineProgress----" + i); if (i ==
							 * 100) { layout.setVisibility(View.GONE); bar.setProgress(0); } } else {
							 * layout.setVisibility(View.GONE); bar.setProgress(0); }
							 */
						}

					});
				}

				@Override
				public void onDownloadOfflineProgress(final int i) {

				}

				@Override
				public void onReceivePenLED(final byte color) {

				}

				@Override
				public void onOfflineDataNumCmdResult(boolean success) {
					// Log.i(TAG, "onOfflineDataNumCmdResult---------->" +
					// success);
				}

				@Override
				public void onDownOfflineDataCmdResult(boolean success) {
					// Log.i(TAG, "onDownOfflineDataCmdResult---------->" +
					// success);
				}

				@Override
				public void onWriteCmdResult(int code) {
					// Log.i(TAG, "onWriteCmdResult---------->" + code);
				}

				@Override
				public void onReceivePenType(int type) {
					// Log.i(TAG, "onReceivePenType type---------->" + type);
//					penType = type;
				}
			});
		}

		public void onServiceDisconnected(ComponentName classname) {
//			showSound(R.raw.smartpemdisconnect);	
			mService = null;
		}
	};
	protected boolean penIsUp=true;
	private boolean ShowBackground=true;
//	protected boolean penIsDown;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		 relativeLayout=(RelativeLayout) findViewById(R.id.mrelativeLayout);
		 relativeLayout.setVisibility(View.GONE);
		bgImageView = (ImageView) findViewById(R.id.bgimageview);
//		bgImageView.setImageResource(R.drawable.bgimageview);
		bgImageView.setImageResource(R.drawable.bgimageviewfora4);
		
//		bgImageView.setVisibility(View.GONE);
		textView = (TextView) findViewById(R.id.textView);
		drawView = (com.example.smartpenforboard.DrawView) findViewById(R.id.drawView);
		{// 低功耗蓝牙绑定服务
			Intent gattServiceIntent = new Intent(this, BluetoothLEService.class);
			boolean bBind = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		}
		bleManager = PenCommAgent.GetInstance(getApplication());// 低功耗蓝牙(智能笔)初始化
		bleManager.setPenBeepMode(true);

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
		float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = dm.densityDpi; // 屏幕密度dpi（120 / 160 / 240）
		Log.e(TAG, "density=======>" + density + ",densityDpi=======>" + densityDpi);
		// 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
		int screenWidth = (int) (mWidth / density); // 屏幕宽度(dp)
		int screenHeight = (int) (mHeight / density);// 屏幕高度(dp)
		k = mWidth / A4_y;
//		k=mWidth/(A2_x*4);
		Log.e(TAG, "width=======>" + screenWidth);
		Log.e(TAG, "height=======>" + screenHeight);

		Log.e(TAG, "-----screen pixel-----width:" + mWidth + ",height:" + mHeight);
		dealSmartPenGesture.setDealSmartPenGesture(this);
		builder = new AlertDialog.Builder(MainActivity.this).create();
		mContext=this.getBaseContext();
		sharePictureName="001-"+studentNumber+"-01-01-0.jpg";
/*		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						dotContainer = queue.take();
						if(queue.isEmpty())
						{
							penIsUp=true;
//							penIsDown=true;
						}
//						dotContainer=dot;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						Message msg = new Message();
						msg.what = 10;
						msg.obj = e.toString();
						mHandler.sendMessage(msg);
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							switch (choosePaperSizeItemId) {
							case 0:
//								Toast.makeText(getBaseContext(), "请选择书写幅面，现在为默认A4", Toast.LENGTH_SHORT).show();
								processEachDot(dotContainer);
								break;
							case R.id.A4:
								processEachDot(dotContainer);
								break;
							case R.id.A3:
								processEachDot(dotContainer);
								break;
							case R.id.A4_16:
								Process16A4EachDot(dotContainer);
								break;
							case R.id.a2_4:
								Process4A2EachDot(dotContainer);
								break;
							case R.id.a2_8:
								Process8A2EachDot(dotContainer);
								break;
							case R.id.a2_4_picture:
								Process4A2HavePictureEachDot(dotContainer);
//								A24PoictureProcessEachDot(dot);
								break;

							default:
								break;
							}


						}
					});

				}
			}
		}).start();	*/
	
	
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_SELECT_DEVICE:
			if (resultCode == Activity.RESULT_OK && data != null) {
				String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
				try {
					boolean flag = mService.connect(deviceAddress);
					if (flag) {
						Log.e("smartPenConnect", "智能笔连接成功!");
					} else {
						Log.e("smartPenConnect", "智能笔连接失败!");
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		// 0-free format;1-for A4;2-for A3
		// Log.i(TAG, "-----------setDataFormat-------------");
//		bleManager.setXYDataFormat(1);// 设置码点输出规格

		switch (item.getItemId()) {

		case R.id.scan_smartpen:// 扫描智能笔菜单
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, SelectDeviceActivity.class);
			startActivityForResult(serverIntent, REQUEST_SELECT_DEVICE);
			return true;
		default:
			break;

		}

		return false;
	}
	protected void onDestroy() {
		unbindService(dealSmartPenGesture.recordConnection);
//		SmartPenUnitils.save(smartPenPage);
//		wifiUtil.closeWifiAp();
		super.onDestroy();
	}
	private void processEachDot(Dot dot) {
//		Log.i("zgm", "111 ProcessEachDot=" + dot.toString());
		int counter = 0;
		pointZ = dot.force;
		counter = dot.Counter;
		/*
		 * Log.i("zgm", "BookID:  " + dot.BookID); Log.i("zgm", "Counter: " +
		 * dot.Counter); Log.i("zgm", "Counter: " + dot.force);
		 */
		tmp = dot.x;
		pointX = dot.fx;
		pointX /= 100.0;
		pointX += tmp;

		tmp = dot.y;
		pointY = dot.fy;
		pointY /= 100.0;
		pointY += tmp;
		textView.setText("x=" + pointX + "--y=" + pointY + "\n" + "BookId:"+dot.PageID+"--PageId:"+dot.PageID);
		pointX *= k;
		pointY *= k;
/*		if (pointZ > 0) {
			if (dot.type == Dot.DotType.PEN_DOWN) {
				if (dot.PageID < 0 || dot.BookID < 0) {
					// 谨防笔连接不切页的情况
					return;
				}

				drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);

				return;
			}

			if (dot.type == Dot.DotType.PEN_MOVE) {

				if (dot.Counter <= 15) {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);
				} else {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 1);
//					
				}

			}
		} else if (dot.type == Dot.DotType.PEN_UP) {
			drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 2);
//			bDrawl[0].invalidate();
		} // dot.type == Dot.DotType.PEN_UP代码块结束
*/
		accrodingDotTypeDrawChirography(pointX,pointY,dot);		
	}

	public void Process8A2EachDot(Dot dot) {
//		Log.i("zgm", "111 ProcessEachDot=" + dot.toString());
//		int counter = 0;
//		pointZ = dot.force;
//		counter = dot.Counter;
		/*
		 * Log.i("zgm", "BookID:  " + dot.BookID); Log.i("zgm", "Counter: " +
		 * dot.Counter); Log.i("zgm", "Counter: " + dot.force);
		 */
		tmp = dot.x;
		pointX = dot.fx;
		pointX /= 100.0;
		pointX += tmp;

		tmp = dot.y;
		pointY = dot.fy;
		pointY /= 100.0;
		pointY += tmp;
		textView.setText("8张A2:x=" + pointX + "---y=" + pointY + "/n" + "bookid:" + dot.BookID + "----pageid:" + dot.PageID);
		switch (dot.PageID / 2 % 8) {
		case 0:
			if (isJiaoZhun) {
				gOffsetXOnPaper = pointX;
				gOffsetYOnPaper = pointY;
				return;
			}
//只有偏移量
			break;
		case 1:
			pointX = pointX + A2_x;
			if (isJiaoZhun) {
				gOffsetXOnPaper = pointX;
				gOffsetYOnPaper = pointY;
				return;
			}
			break;
		case 2:
			pointY = pointY + A2_y;
			if (isJiaoZhun) {
				gOffsetXOnPaper = pointX;
				gOffsetYOnPaper = pointY;
				return;
			}
			break;
		case 3:
			pointX = pointX + A2_x;
			pointY = pointY + A2_y;
			if (isJiaoZhun) {
				gOffsetXOnPaper = pointX;
				gOffsetYOnPaper = pointY;
				return;
			}
			break;

		case 4:
			pointX = pointX + A2_x * 2;
			if (isJiaoZhun) {
				gOffsetXOnPaper = pointX;
				gOffsetYOnPaper = pointY;
				return;
			}
			break;
		case 5:
			pointX = pointX + A2_x * 3;
			if (isJiaoZhun) {
				gOffsetXOnPaper = pointX;
				gOffsetYOnPaper = pointY;
				return;
			}
			break;
		case 6:
			pointX = pointX + A2_x * 2;
			pointY = pointY + A2_y;
			if (isJiaoZhun) {
				gOffsetXOnPaper = pointX;
				gOffsetYOnPaper = pointY;
				return;
			}
			break;
		case 7:
			pointX = pointX + A2_x * 3;
			pointY = pointY + A2_y;
			if (isJiaoZhun) {
				gOffsetXOnPaper = pointX;
				gOffsetYOnPaper = pointY;
				return;
			}
			break;
		default:
			break;
		}
		pointX = pointX - gOffsetXOnPaper;
		pointY = pointY - gOffsetYOnPaper;
		pointX *= k;
		pointY *= k;
/*		pointX*=1.04;
//		pointY*=1.0623;
		pointY*=1.040;*/
		pointX*=0.99;
//		pointY*=1.0623;
		pointY*=0.99;
		if (gOffsetXOnPaper != 0 || gOffsetYOnPaper != 0) {
			pointX = pointX + 100;// 画图偏移量
			pointY = pointY + 100;//// 画图偏移量
		}
		if (pointX < 0 || pointY < 0) {
			return;
		}
		accrodingDotTypeDrawChirography(pointX,pointY,dot);	
/*		if (pointZ > 0) {
			if (dot.type == Dot.DotType.PEN_DOWN) {
				if (dot.PageID < 0 || dot.BookID < 0) {
					// 谨防笔连接不切页的情况
					return;
				}
				
				 * if (PageID != gCurPageID || BookID != gCurBookID) { gbSetNormal = false;
				 * SetBackgroundImage(BookID, PageID); //
				 * gImageView.setVisibility(View.VISIBLE); bIsOfficeLine = true; gCurPageID =
				 * PageID; gCurBookID = BookID; drawInit();
				 * 
				 * DrawExistingStroke(gCurBookID, gCurPageID); }
				 

				drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);

				return;
			}

			if (dot.type == Dot.DotType.PEN_MOVE) {

				if (dot.Counter <= 15) {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);
				} else {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 1);
//				
				}

			}
		} else if (dot.type == Dot.DotType.PEN_UP) {
			drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 2);
//		bDrawl[0].invalidate();
		}
*/	}

	public void Process4A2EachDot(Dot dot) {
//		Log.i("zgm", "111 ProcessEachDot=" + dot.toString());
//		int counter = 0;
//		pointZ = dot.force;
//		counter = dot.Counter;
		/*
		 * Log.i("zgm", "BookID:  " + dot.BookID); Log.i("zgm", "Counter: " +
		 * dot.Counter); Log.i("zgm", "Counter: " + dot.force);
		 */
		tmp = dot.x;
		pointX = dot.fx;
		pointX /= 100.0;
		pointX += tmp;

		tmp = dot.y;
		pointY = dot.fy;
		pointY /= 100.0;
		pointY += tmp;
		textView.setText("x=" + pointX + "---y=" + pointY + "\n" + "bookid:" + dot.BookID + "----pageid:" + dot.PageID);
		float temp = A2_x - pointX;
		pointX = pointY;
		pointY = temp;

		switch (dot.PageID / 2 % 4) {
		case 0:
			pointY = pointY + A2_x;

			break;
		case 1:
			// 只有偏移量
			break;
		case 2:
			pointX = pointX + A2_y;
			pointY = pointY + A2_x;

			break;
		case 3:
			pointX = pointX + A2_y;

			break;
		default:
			break;
		}
		/*
		 * pointX=pointX-gOffsetXOnPaper; pointY=pointY-gOffsetYOnPaper;
		 */
		pointX *= k;
		pointY *= k;
		/*
		 * pointX=pointX+100;//画图偏移量 pointY=pointY+100;////画图偏移量
		 */ if (pointX < 0 || pointY < 0) {
			return;
		}
accrodingDotTypeDrawChirography(pointX,pointY,dot);	
/*		 
		if (pointZ > 0) {
			if (dot.type == Dot.DotType.PEN_DOWN) {
				if (dot.PageID < 0 || dot.BookID < 0) {
					// 谨防笔连接不切页的情况
					return;
				}
				
				 * if (PageID != gCurPageID || BookID != gCurBookID) { gbSetNormal = false;
				 * SetBackgroundImage(BookID, PageID); //
				 * gImageView.setVisibility(View.VISIBLE); bIsOfficeLine = true; gCurPageID =
				 * PageID; gCurBookID = BookID; drawInit();
				 * 
				 * DrawExistingStroke(gCurBookID, gCurPageID); }
				 

				drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);

				return;
			}

			if (dot.type == Dot.DotType.PEN_MOVE) {

				if (dot.Counter <= 15) {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);
				} else {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 1);
//				
				}

			}
		} else if (dot.type == Dot.DotType.PEN_UP) {
			drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 2);
//		bDrawl[0].invalidate();
		}
*/	}

	public void Process4A2HavePictureEachDot(Dot dot) {
//		Log.i("zgm", "111 ProcessEachDot=" + dot.toString());
//		int counter = 0;
//		pointZ = dot.force;
//		counter = dot.Counter;
		/*
		 * Log.i("zgm", "BookID:  " + dot.BookID); Log.i("zgm", "Counter: " +
		 * dot.Counter); Log.i("zgm", "Counter: " + dot.force);
		 */
		tmp = dot.x;
		pointX = dot.fx;
		pointX /= 100.0;
		pointX += tmp;

		tmp = dot.y;
		pointY = dot.fy;
		pointY /= 100.0;
		pointY += tmp;
		textView.setText("x=" + pointX + "---y=" + pointY + "\n" + "bookid:" + dot.BookID + "----pageid:" + dot.PageID);
		switch (dot.PageID / 2 % 4) {
		case 0:
			break;
		case 1:
			pointX = pointX + A2_x;
			// 只有偏移量
			break;
		case 2:
			pointY = pointY + A2_y;
			break;
		case 3:
			pointX = pointX + A2_x;
			pointY = pointY + A2_y;
			break;
		default:
			break;
		}
		/*
		 * pointX=pointX-gOffsetXOnPaper; pointY=pointY-gOffsetYOnPaper;
		 */
		pointX *= k;
		pointY *= k;
		/*
		 * pointX=pointX+100;//画图偏移量 pointY=pointY+100;////画图偏移量
		 */ if (pointX < 0 || pointY < 0) {
			return;
		}
		 accrodingDotTypeDrawChirography(pointX,pointY,dot);	
/*		if (pointZ > 0) {
			if (dot.type == Dot.DotType.PEN_DOWN) {
				if (dot.PageID < 0 || dot.BookID < 0) {
					// 谨防笔连接不切页的情况
					return;
				}
				
				 * if (PageID != gCurPageID || BookID != gCurBookID) { gbSetNormal = false;
				 * SetBackgroundImage(BookID, PageID); //
				 * gImageView.setVisibility(View.VISIBLE); bIsOfficeLine = true; gCurPageID =
				 * PageID; gCurBookID = BookID; drawInit();
				 * 
				 * DrawExistingStroke(gCurBookID, gCurPageID); }
				 

				drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);

				return;
			}

			if (dot.type == Dot.DotType.PEN_MOVE) {

				if (dot.Counter <= 15) {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);
				} else {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 1);
//				
				}

			}
		} else if (dot.type == Dot.DotType.PEN_UP) {
			drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 2);
//		bDrawl[0].invalidate();
		}
*/	}

	public void Process16A4EachDot(Dot dot) {
/*		Log.i("zgm", "111 ProcessEachDot=" + dot.toString());
		int counter = 0;
		pointZ = dot.force;
		counter = dot.Counter;*/
		/*
		 * Log.i("zgm", "BookID:  " + dot.BookID); Log.i("zgm", "Counter: " +
		 * dot.Counter); Log.i("zgm", "Counter: " + dot.force);
		 */
		tmp = dot.x;
		pointX = dot.fx;
		pointX /= 100.0;
		pointX += tmp;

		tmp = dot.y;
		pointY = dot.fy;
		pointY /= 100.0;
		pointY += tmp;
		textView.setText("x=" + pointX + "---y=" + pointY + "\n" + "bookid:" + dot.BookID + "----pageid:" + dot.PageID);
		switch (dot.PageID) {
		case 0:
//只有偏移量
			break;
		case 2:
			pointX = pointX + A4_x;
			break;
		case 4:
			pointX = pointX + A4_x * 2;
			break;
		case 6:
			pointX = pointX + A4_x * 3;
			break;
		case 8:
			pointY = pointY + A4_y;
			break;

		case 10:
			pointX = pointX + A4_x;
			pointY = pointY + A4_y;
			break;
		case 12:
			pointX = pointX + A4_x * 2;
			pointY = pointY + A4_y;
			break;
		case 14:
			pointX = pointX + A4_x * 3;
			pointY = pointY + A4_y;
			break;

		case 32:
			pointY = pointY + A4_y * 2;
			break;
		case 34:
			pointX = pointX + A4_x;
			pointY = pointY + A4_y * 2;
			break;
		case 36:
			pointX = pointX + A4_x * 2;
			pointY = pointY + A4_y * 2;
			break;
		case 38:
			pointX = pointX + A4_x * 3;
			pointY = pointY + A4_y * 2;
			break;
		case 40:
			pointY = pointY + A4_y * 3;
			break;
		case 42:
			pointX = pointX + A4_x;
			pointY = pointY + A4_y * 3;
			break;
		case 44:
			pointX = pointX + A4_x * 2;
			pointY = pointY + A4_y * 3;
			break;
		case 46:
			pointX = pointX + A4_x * 3;
			pointY = pointY + A4_y * 3;
			break;

		default:
			break;
		}
		{	
		}

		pointX *= k;
		pointY *= k;
		if (pointX < 0 || pointY < 0) {
			return;
		}
		accrodingDotTypeDrawChirography(pointX,pointY,dot);	
/*		if (pointZ > 0) {
			if (dot.type == Dot.DotType.PEN_DOWN) {
				if (dot.PageID < 0 || dot.BookID < 0) {
					// 谨防笔连接不切页的情况
					return;
				}
				
				 * if (PageID != gCurPageID || BookID != gCurBookID) { gbSetNormal = false;
				 * SetBackgroundImage(BookID, PageID); //
				 * gImageView.setVisibility(View.VISIBLE); bIsOfficeLine = true; gCurPageID =
				 * PageID; gCurBookID = BookID; drawInit();
				 * 
				 * DrawExistingStroke(gCurBookID, gCurPageID); }
				 

				drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);

				return;
			}

			if (dot.type == Dot.DotType.PEN_MOVE) {

				if (dot.Counter <= 15) {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);
				} else {
					drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 1);
//				
				}

			}
		} else if (dot.type == Dot.DotType.PEN_UP) {
			
			drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 2);
//		bDrawl[0].invalidate();
		}
*/	}

	private void drawSubFountainPen2(DrawView DV, float scale, float offsetX, float offsetY, int penWidth, float x,
			float y, int force, int ntype) {
//		Log.e("zgm", "执行函数drawSubFountainPen2");
		if (ntype == 0) {
			g_x0 = x;
			g_y0 = y;
			g_x1 = x;
			g_y1 = y;
			// Log.i(TAG, "--------draw pen down-------");
		}
		if (ntype == 2) {
			g_x1 = x;
			g_y1 = y;
			Log.i("TEST", "--------draw pen up--------");
		} else {
			g_x1 = x;
			g_y1 = y;
			// Log.i(TAG, "--------draw pen move-------");
		}

		DV.paint.setStrokeWidth(penWidth);

//		DV.paint.setColor(Color.RED);
//		DV.canvas.saveLayer(new RectF(DV.canvas.getClipBounds()), DV.paint, Canvas.ALL_SAVE_FLAG);
//		DV.canvas.save(Canvas.ALL_SAVE_FLAG);
		DV.canvas.drawLine(g_x0, g_y0, g_x1, g_y1, DV.paint);
//		DV.canvas.restore();
		DV.invalidate();
		g_x0 = g_x1;
		g_y0 = g_y1;

		return;
	}

	public void buttonClick(View v) {
		switch (v.getId()) {
		case R.id.buttonMenu:
			Log.e("buttonmenu", "你点击了ButtonMenu");
			mPopupMenu = new PopupMenu(this, v);
			getMenuInflater().inflate(R.menu.main, mPopupMenu.getMenu());
			mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					return onPopupMenuItemClick(item);
					// TODO Auto-generated method stub
//					return false;
				}
			});
			mPopupMenu.show();
			break;
		case R.id.chooseSize:
			mPopupMenu = new PopupMenu(this, v);
			getMenuInflater().inflate(R.menu.choosesize, mPopupMenu.getMenu());
			mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					return onPopupMenuItemClick(item);
					// TODO Auto-generated method stub
//					return false;
				}
			});
			mPopupMenu.show();
			break;

		default:
			break;
		}
	}

	public void sanforBlooth() {
		Intent serverIntent = new Intent(this, SelectDeviceActivity.class);
		startActivityForResult(serverIntent, REQUEST_SELECT_DEVICE);
	}

	public boolean onPopupMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan_smartpen:
			sanforBlooth();
			break;
		case R.id.jiaozhun:
			isJiaoZhun = true;
			Log.e("校准", "" + isJiaoZhun);
			gOffsetXOnPaper = 0;
			gOffsetYOnPaper = 0;
			textView.setText("坐标偏移量已经置零,请点击图中的十字图标进行校准");
			break;
		case R.id.exitjiaozhun:
			isJiaoZhun = false;
			textView.setText("已经退出校准状态");
			break;
		case R.id.showpicture:
			if(ShowBackground) {
				bgImageView.setVisibility(View.GONE);
				ShowBackground=false;
			}else {
				ShowBackground=true;
			}
			break;
		case R.id.A4:
			if(ShowBackground){
				bgImageView.setImageResource(R.drawable.bgimageviewfora4);
				bgImageView.setVisibility(View.VISIBLE);
			}
			drawView.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);// 清除画布
			k = mWidth / A4_x;
			choosePaperSizeItemId = item.getItemId();
			Log.e("buttonmenu", "A4:" + choosePaperSizeItemId);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			break;
		case R.id.A3:
			if(ShowBackground){
				bgImageView.setVisibility(View.GONE);
			}
//			bgImageView.setVisibility(View.GONE);
			drawView.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);// 清除画布
//		k=mHeight/(A4_x*2);
			k = mWidth / A4_y;
			choosePaperSizeItemId = item.getItemId();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			Log.e("buttonmenu", "A3");
			break;
		case R.id.A4_16:
			if(ShowBackground){
				bgImageView.setVisibility(View.GONE);
			}
//			bgImageView.setVisibility(View.GONE);
			k = mWidth / (A4_x * 4);
			choosePaperSizeItemId = item.getItemId();
			drawView.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);// 清除画布
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			Log.e("buttonmenu", "A4_16");
			break;
		case R.id.a2_4:
			if(ShowBackground){
				bgImageView.setVisibility(View.GONE);
			}
//			bgImageView.setVisibility(View.GONE);
			choosePaperSizeItemId = item.getItemId();
			drawView.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);// 清除画布
			Log.e("buttonmenu", "A2_4");
			k = mWidth / (A2_x * 2);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case R.id.a2_8:
			if(ShowBackground){
				bgImageView.setVisibility(View.GONE);
			}
//			bgImageView.setVisibility(View.GONE);
			drawView.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);// 清除画布
			Log.e("buttonmenu", "A2_8");
			drawcrossline();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			k = mHeight / (A2_x * 4);
			choosePaperSizeItemId = item.getItemId();
			break;
		case R.id.a2_4_picture:
			if(ShowBackground){
//				bgImageView.setImageResource(R.drawable.bgimageview);
				Bitmap mBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.bgimageview);
//				bgImageView.setImageResource(R.drawable.bgimageviewfora4);
				bgImageView.setImageBitmap(mBitmap);
				bgImageView.setVisibility(View.VISIBLE);	
//				Log.e("0505", "显示图片啊");
				}
			drawView.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);// 清除画布
			Log.e("buttonmenu", "A2_8");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			k = mWidth / (A2_x * 2);
			choosePaperSizeItemId = item.getItemId();
			break;
		case R.id.refreshinfo:
			doDownLoadWork();
			break;
		case R.id.traingesture:
			if (currentSmartPenGesture == null) {
				return true;
			}
			saveGesture(currentSmartPenGesture);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int orientation = newConfig.orientation;
		if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			Log.i(TAG, "-------------横屏-------------");
		} else {
			Log.i(TAG, "-------------竖屏-------------");
		}
		Log.i(TAG, "onConfigurationChanged: " + orientation);
	}

	public void drawcrossline() {
		drawView.paint.setColor(Color.BLACK);
		drawView.paint.setStrokeWidth(gWidth);
		drawView.canvas.drawLine(60, 100, 140, 100, drawView.paint);
		drawView.canvas.drawLine(100, 60, 100, 140, drawView.paint);
		drawView.paint.setColor(Color.RED);
	}
public void accrodingDotTypeDrawChirography(float x,float y,Dot tempDot) {
	if (x < 0 || y < 0) {//超出屏幕区域直接返回
		return;
	}
//	pointZ=tempDot.force;
	if (tempDot.force > 0) {
		if (tempDot.type == Dot.DotType.PEN_DOWN) {
			if (tempDot.PageID < 0 || tempDot.BookID< 0) {
				// 谨防笔连接不切页的情况
				return;
			}
			drawType=0;
			penIsUp=false;
//			penIsUp=false;
//			penIsDown=true;
/*			if(!penIsUp) {
				drawType=1;	
				
			}*/
		
			if (ismSmartPenStrokeBufferNeedClear) {
//				penIsUp=false;
				SmartPenStrokeBuffer.clear();
				temContair.clear();
			}
			/*
			 * if (PageID != gCurPageID || BookID != gCurBookID) { gbSetNormal = false;
			 * SetBackgroundImage(BookID, PageID); //
			 * gImageView.setVisibility(View.VISIBLE); bIsOfficeLine = true; gCurPageID =
			 * PageID; gCurBookID = BookID; drawInit();
			 * 
			 * DrawExistingStroke(gCurBookID, gCurPageID); }
			 */

//			drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);

//			return;
			startx=x;
			starty=y;
		}

		if (tempDot.type == Dot.DotType.PEN_MOVE) {

			if (tempDot.Counter <= 5) {
				drawType=0;
//				drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);
			} else {
				drawType=1;
//				drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 1);
		if(penIsUp) {
			drawType=0;
//			penIsDown=true;
			penIsUp=false;
			if (ismSmartPenStrokeBufferNeedClear) {
				SmartPenStrokeBuffer.clear();
				temContair.clear();
			}
			
		}
			}
			startx=x;
			starty=y;
		}
	}
	else if (tempDot.type == Dot.DotType.PEN_UP) {
		penIsUp=true;
//		penIsDown=false;
		drawType=2;
//		intPenStrokeCount++;
//	bDrawl[0].invalidate();
        if ( tempDot.x == 0||tempDot.y == 0) {
            pointX = startx;
            pointY = starty;
        }
        
	}
	drawSubFountainPen2(drawView, gScale, gOffsetX, gOffsetY, gWidth, x, y, tempDot.force, drawType);	
	SmartPenStrokeBuffer.add(new GesturePoint((float) (tempDot.x + tempDot.fx / 100.0), (float) (tempDot.y + tempDot.fy / 100.0), System.currentTimeMillis()));
	temContair.add(new Position(x,y));
	penUpTime = System.currentTimeMillis();
	if (firstpen) {
		firstpen = false;
		new Thread(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				firstpen = false;
				curTime = System.currentTimeMillis();
//Log.e("di","1129:hah计时开始："+System.currentTimeMillis());				
				while (curTime - penUpTime < 800) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					curTime = System.currentTimeMillis();
				}
				{
					firstpen = true;
					firstPenChi = true;
					penIsUp=true;
					transferToGesture(SmartPenStrokeBuffer);
					dealSmartPenGesture.dealWithGesture(currentSmartPenGesture);
//				Log.e("di", "1129:计时完成："+System.currentTimeMillis());
				}
			}

		}).start();
	}	
}
 private void transferToGesture(ArrayList<GesturePoint> mSmartPenStrokeBuffer2) {
	// TODO Auto-generated method stub
	/*
	 * if (mCurrentGesture==null) { mCurrentGesture= new MGesture(new
	 * MGestureStroke(mSmartPenStrokeBuffer2)); return; }else { if (true) {
	 * mCurrentGesture.clearMGestureStroke(); mCurrentGesture.addMGestureStroke(new
	 * MGestureStroke(mSmartPenStrokeBuffer2));
	 * 
	 * } }
	 */

	if (currentSmartPenGesture == null) {
		currentSmartPenGesture = new SmartPenGesture();
		currentSmartPenGesture.addStroke(new GestureStroke(mSmartPenStrokeBuffer2));
		firstPenChi = false;
		return;
	} else {
		if (firstPenChi) {
			firstPenChi = false;
			currentSmartPenGesture.SmartPenGestureClearAllStroke();
//			Log.e("zgm", "0108:" + currentSmartPenGesture.getBoundingBox());
			currentSmartPenGesture.SmartPenGestureClearmBoundingBox();
		}
//		Log.e("zgm", "01181:" + currentSmartPenGesture.getStrokesCount());
		currentSmartPenGesture.addStroke(new GestureStroke(mSmartPenStrokeBuffer2));
//		Log.e("zgm", "01182:" + currentSmartPenGesture.getStrokesCount());
	}

}
/**
 * 语音提示
 * 
 * @param raw
 */
public void showSound(int raw) {
	if(mediaPlayer==null) {
		mediaPlayer=new MediaPlayer();
	}
	if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
		mediaPlayer.stop();
	}
	Context aContext = getApplicationContext();
	mediaPlayer = MediaPlayer.create(getApplicationContext(), raw);
	mediaPlayer.setVolume(1.0f, 1.0f);
	mediaPlayer.start();


}
public void runOnUIThread(final String title, final String Message) {
	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			builder.setTitle(title);
			builder.setMessage(Message);
			if (!builder.isShowing()) {
				builder.show();
			}
			// TODO Auto-generated method stub
//			builder.show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					firstOccur = true;
				}
			}, 1500);
			/*
			 * new Handler().postDelayed(new Runnable() {
			 * 
			 * @Override public void run() { builder.dismiss(); } }, 2000);
			 */
		}

	});

}


public void showTextView(final TextView textView,final String textViewString) {
	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			textView.setText(textViewString);
		}});
	
	
}
public void drawRectF(final RectF rectf) {
	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
/*			rectf.bottom*=k;
			rectf.left*=k;
			rectf.right*=k;
			rectf.top*=k;*/
			drawView.canvas.drawRect(rectf, drawView.paint);
			drawView.invalidate();
		}});
}
public 	Bitmap saveScreen( View view,RectF rectf) {

	String fname = "/sdcard/-1/"+sharePictureName;
	View dView = view.getRootView();
//	dView.getRootView();
	dView.setDrawingCacheEnabled(true);
//	dView.buildDrawingCache();
//	Bitmap bitmap=;
//	dView.setDrawingCacheEnabled(false);
	Bitmap bitmap =	Bitmap.createBitmap(dView.getDrawingCache() , (int)(rectf.left),  (int)(rectf.top), (int)(( rectf.right-rectf.left)), (int)(( rectf.bottom-rectf.top)));
//	Bitmap bitmap =	Bitmap.createBitmap( tempBitmap , 400,  400, 400, 400);
/*	View dview  = getWindow().getDecorView();
    Bitmap tempBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(tempBitmap);
    dview.draw(canvas);
    */
//    Bitmap bitmap =	Bitmap.createBitmap(tempBitmap , (int)(rectf.left*k),  (int)(rectf.top*k), (int)(( rectf.right-rectf.left)*k), (int)(( rectf.bottom-rectf.top)*k));
  	if (bitmap != null) {
		System.out.println("bitmap got!");
		try {
			FileOutputStream out = new FileOutputStream(fname);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
			System.out.println("file " + fname + "outputdonezhuan .");
		} catch (Exception e) {
			e.printStackTrace();
		}
	} else {
		System.out.println("bitmap is NULL!");
	}
	Uri data = Uri.parse("file://storage/emulated/0/");
	dView.setDrawingCacheEnabled(false);
	this.sendBroadcast(new Intent(
			Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
	return bitmap;
}
public void updateInfo(float f, int category) {
	switch (category) {
	case 1:
		 taillength=f/k+"";
		break;
	case 2:
		gesturelength=f/k+"";
		break;
	case 3:
		boundingBoxWidth=f/k+"";
		break;
	case 4:
		boundingBoxheight=f/k+"";
		break;
	case 5:
		taileChangeTimes=(int) f+"";
		break;
	case 6:
		tailPointCounter=(int) f+"";
		break;
	case 7:
		tailSlope= -f+"";
		break;
	default:
		break;
	}
	informatrion="尾巴长度:"+taillength+"\n"+"手势整体长度："+gesturelength+"\n"+"主体边框的宽："+boundingBoxWidth
			+"\n"+"主体边框的高："+boundingBoxheight+"\n"+"尾巴的斜率:"+tailSlope+"\n"+
//			"尾巴的变化次数："+taileChangeTimes+"\n"+
			"尾巴的点的个数："+tailPointCounter;
	runOnUiThread(new Runnable() {		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			textView.setText(informatrion);
		}
	});
	
}
public void onTouch(View v) {
switch(v.getId()) {
case R.id.mrelativeLayout:
	 relativeLayout.setVisibility(View.GONE);
	break;

}
	
}

public void showSharePicture(final Bitmap bm){
	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			 relativeLayout.setVisibility(View.VISIBLE);
			relativeLayout.setBackgroundColor(0xcc333333);
       ImageView img=(ImageView) findViewById(R.id.shareedPicture);
       img.setImageBitmap(bm);
		}});

//ImageView im=new ImageView(this.getApplicationContext());
//im.setImageResource("/sdcard/-1/hh.jpg"));
//RelativeLayout.LayoutParams asd=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//relativeLayout.addView(im, asd);
}
private void doDownLoadWork() {
/*		DownLoaderTask task = new DownLoaderTask("http://" + inputIp
			+ "/Public/Uploads/"+filename, "/sdcard/xyz/",
			this);*/
	Log.e("zgm", "studentdoen:"+studentNumber);
	DownLoaderTask task = new DownLoaderTask("http://118.24.109.3/Public/smartpen/download.php?sid="+studentNumber, "/sdcard/xyz/",
			this);
	// DownLoaderTask task = new
	// DownLoaderTask("http://192.168.9.155/johnny/test.h264",
	// getCacheDir().getAbsolutePath()+"/", this);
	task.execute();
}
public void showUnzipDialog() {
	new AlertDialog.Builder(this).setTitle("确认").setMessage("是否解压？")
	.setPositiveButton("是", new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			Log.d(TAG, "onClick 1 = " + which);
			doZipExtractorWork();				
		}
	}).setNegativeButton("否", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.d(TAG, "onClick 2 = " + which);
				}
			}).show();
}
public void doZipExtractorWork() {
	// ZipExtractorTask task = new
	// ZipExtractorTask("/storage/usb3/system.zip",
	// "/storage/emulated/legacy/", this, true);
	ZipExtractorTask task = new ZipExtractorTask("/storage/emulated/0/xyz/download.zip", "/storage/emulated/0/xyz",
			this, true);
	task.execute();
	Uri data = Uri.parse("file://storage/emulated/0/");
	this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
}
public void saveGesture(final SmartPenGesture mgesture) {
	if (mgesture == null) {
		return;
	}
	View saveViewDialog = getLayoutInflater().inflate(R.layout.show_gesture, null);
	ImageView imageView = (ImageView) saveViewDialog.findViewById(R.id.show);
	final EditText gestureNam = (EditText) saveViewDialog.findViewById(R.id.name);
	Bitmap bitmap = mgesture.toBitmap(128, 128, 10, 0xffff0000);
	imageView.setImageBitmap(bitmap);
//	final EditText gestureNam = (EditText) saveViewDialog.findViewById(R.id.name);
/*		View saveViewDialog = getLayoutInflater().inflate(R.layout.gestureinfor, null);
	ImageView imageView = (ImageView) saveViewDialog.findViewById(R.id.show);
	Bitmap bitmap = mgesture.toBitmap(128, 128, 10, 0xffff0000);
	imageView.setImageBitmap(bitmap);
	TextView gestureOwner=(TextView) saveViewDialog.findViewById(R.id.gesture_owner);
	gestureOwner.setText("使 用者："+mNameString);
	TextView  gesturePosition=(TextView) saveViewDialog.findViewById(R.id.gesture_position);
	EditText  gestureYuyi= (EditText) saveViewDialog.findViewById(R.id.gesture_yuyi);
	TextView gestureName=(TextView)   saveViewDialog.findViewById(R.id.gesture_name);
	TextView gestureResponce=(TextView) saveViewDialog.findViewById(R.id.gesture_responce);
	if (dealSmartPenGesture!=null) {
		if (dealSmartPenGesture.tag!=null) {
			gesturePosition.setText("位  置：第"+dealSmartPenGesture.tag.get(0)+"题题干区");	
		}
		gestureName.setText("手势名称："+dealSmartPenGesture.gestureFinalName);
		gestureResponce.setText("响应方式："+dealSmartPenGesture.gestureResponce);
	}*/

	new AlertDialog.Builder(MainActivity.this).setView(saveViewDialog)
			.setPositiveButton("保存", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
//                         GestureLibrary gestureLibrary=GestureLibraries.fromFile("/sdcard/zgmgesture");

					gestureLibrary.addGesture(gestureNam.getText().toString(), mgesture);
//						Log.e("zgm", "1210:gestureNam.getText().toString:"+gestureNam.getText().toString());
					gestureLibrary.save();
					gestureLibrary = GestureLibraries.fromFile("/sdcard/zgmgesture");
//					Log.e("zgm", "1210:"+gestureLibrary.save());

				}

			}).setNegativeButton("取消", null).show();

}

public Bitmap getBitmap(String filePath ) {
	File file=new File(filePath);
	if(file.exists()) {
		Bitmap bm=BitmapFactory.decodeFile(file.getAbsolutePath());
		return bm;
//		showSharePicture(bm);
	}else {
		Toast.makeText(mContext, "没有检测到的图片", Toast.LENGTH_SHORT).show();
		return null;
	}
//
//	
//	return null;	
}
public  String getCurrentPageName(String path, int bookid,int pageid){
	String bookID = ""+bookid;
	String pageID = ""+pageid;
	String fileName = null;
	File[] fileArray = null;

	String file;
	String[] id = null;
	File files = new File(path);
	fileArray = files.listFiles();
	for(int i=0;i<fileArray.length;i++){
		String filepathname = fileArray[i].getPath();
		String ori = filepathname.substring(filepathname.lastIndexOf("/"));
		Log.i("getfile", "ori====="+ori);
		ori = ori.substring(1,ori.length());
	    file  = ori.substring(0,ori.lastIndexOf("."));
		id = file.split("-");
		
	    if(id[0].equals("NONE")&&bookID.equals(id[2])&&pageID.equals(id[3])){
	    	fileName = ori;
	    	
	    	break;
	    }
	    else fileName ="1111-9999-111-111-0.page";
	}
	Log.i("getfile", "filename====="+fileName);
	return fileName;

}

public void drawLine(final float x0, final float y0,final float x1, final float y1) {
	
	runOnUiThread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			drawView.paint.setColor(Color.BLUE);
			drawView.canvas.drawLine(x0, y0, x1, y1, drawView.paint);
			drawView.invalidate();
//			drawView.canvas.drawLine(60, 100, 140, 100, drawView.paint);
			drawView.paint.setColor(Color.RED);
		}
	});
	
}

}
