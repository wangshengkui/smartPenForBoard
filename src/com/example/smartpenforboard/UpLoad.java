package com.example.smartpenforboard;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;






public class UpLoad {

	// 要上传的文件路径，理论上可以传输任何文件，实际使用时根据需要处理
	//  private String uploadFile = "/sdcard/testimg.jpg";
//	  private static String srcPath = "/sdcard/0945-0001-0000-0023-0003-0009-0022.jpg";
//	  private static  File file=new File(srcPath);
	  
	  
	  
	  // 服务器上接收文件的处理页面，这里根据需要换成自己的
	
	  /* 上传文件至Server，uploadUrl：接收文件的处理页面 */
	  public static  boolean uploadFile(String uploadUrl, String srcPath)
	  {
		  boolean uploadStaus=false;
//		  判断上传的图片是否存在
		  File file = new File(srcPath);
		  if (file.exists()) {
			Log.v("clickButton", srcPath);
		}
		  else {
			Log.v("clickButton", "文件不存在");
			uploadStaus=false;
			return uploadStaus;
		}
		  
	    String end = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "******";
	   
	    try
	    {
	    	
	      URL url = new URL(uploadUrl);
	      Log.v("clickButton", "123："+url);
//	      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	      HttpURLConnection con=(HttpURLConnection)url.openConnection();    
	      
	     
	           
	      // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
	      // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
//	      con.setChunkedStreamingMode(128 * 1024);// 128K
	      // 允许输入输出流
	      con.setDoInput(true);
	      con.setDoOutput(true);
	      con.setUseCaches(false);
	      // 使用POST方法
	      con.setRequestMethod("POST");
	      con.setRequestProperty("Connection", "Keep-Alive");
	      con.setRequestProperty("Charset", "UTF-8");
	      con.setRequestProperty("Content-Type",
	          "multipart/form-data;boundary=" + boundary);
//	      con.connect();

	      DataOutputStream dos = new DataOutputStream(
	    		  con.getOutputStream());
	      dos.writeBytes(twoHyphens + boundary + end);//发送分界符
	      dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
	          + srcPath.substring(srcPath.lastIndexOf("/") + 1)
	          + "\""
	          + end);
	      dos.writeBytes(end);//发送结束符
	      
	      Log.v("clickButton", "执行过链接代码啦");
	      
	   //   Log.v("clickButton", ""+dos.toString());

	      FileInputStream fis = new FileInputStream(file);//读取文件到内存的流
	     // Log.v("clickButton", "下面读取文件："+fis.toString());
	      byte[] buffer = new byte[1024]; // 8k
	      int count = 0;
	      // 读取文件
	      while ((count = fis.read(buffer)) != -1)
	      {
	   
		      Log.v("clickButton", "++++"+buffer.length);
		      Log.v("clickButton", buffer.toString());
	   dos.write(buffer, 0, count);
	  
	      //  Log.v("clickButton", "++++"+dos.size());
	      }
	      
	      Log.v("clickButton","++++"+dos.toString());
	      fis.close();
	      Log.v("clickButton","哈哈1");
	      
	      dos.writeBytes(end);
	      Log.v("clickButton","哈哈2");
	      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);//发送完成，下面的流是接受反馈
	
	      dos.flush();
	      Log.v("clickButton", "状态码："+con.getResponseCode());
	      InputStream is = con.getInputStream();//获得接收流
	      InputStreamReader isr = new InputStreamReader(is, "utf-8");
	      BufferedReader br = new BufferedReader(isr);
	      String result = br.readLine();
	      if(result.contains("has been uploaded")) {
	    	  uploadStaus=true;  
	    	  return uploadStaus;
	      }
	      Log.i("clickButton","result==="+result);   
//          Toast.makeText(this, "成功了", Toast.LENGTH_SHORT).show();
//	      Toast.makeText(, result, Toast.LENGTH_LONG).show();
	      
	      
	      
	      
	      
//        定义BufferedReader输入流来读取URL的响应
//       BufferedReader reader = new BufferedReader(new InputStreamReader(
//               con.getInputStream()));
//       String line = null;
//       while ((line = reader.readLine()) != null) {
//           System.out.println(line);
//           Log.i("clickButton",line);            }
	      
	      
	      
	      
	      dos.close();
	      is.close();
	      Log.v("clickButton","哈哈");
	      
	      

	      
	      
	      
	      
	    } catch (Exception e)
	    {
			uploadStaus=false;
			
	        Log.e("clickButton", "出错了:"+e);
	    	e.printStackTrace();
	    	return uploadStaus;
	 //     setTitle(e.getMessage());
	    }
	    return uploadStaus;
	  }	
	public UpLoad() {
		// TODO Auto-generated constructor stub
	}
	public UpLoad(String upLoadFileName){
		
	}
 	
	
	
	
}