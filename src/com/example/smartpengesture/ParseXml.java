package com.example.smartpengesture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.R.integer;
import android.graphics.pdf.PdfDocument.Page;

public class ParseXml {
	Document doc = null;
	int totalQuestion=-1;
	int pageid=-1;
	String filepath="/sdcard/";
	Elements elements;
	Elements x1s;     
	Elements y1s;    
	Elements x2s;     
	Elements y2s;     
	Elements x3s;    
	Elements y3s;     
	Elements x4s;     
	Elements y4s;     
	Elements x5s;     
	Elements y5s;     
	Elements x6s;     
	Elements y6s;     
	Elements x7s;     
	Elements y7s;     
	Elements x8s;     
	Elements y8s;     
	Elements x9s;     
	Elements y9s;     
	Elements x10s;    
	Elements y10s;    
	Elements x11s;    
	Elements y11s;    
	Elements x12s;    
	Elements y12s;    
    
	Elements x13s;    
	Elements y13s;    
	Elements x14s;    
	Elements y14s; 
	Elements QuestionTypes;
	Elements abilitys;    
	Elements skills;       
	Elements knowledges;   
   public int setParseXml(int pageid){
		int status=0;//
		/**
		 * status=0,代表没有制作模板;
		 * status=1代表制作过模板，且解析成功;
		 * status=-1代表制作过模板，但解析失败(模板没有在指定路径处，或者其他原因)
		 */
		this.pageid=pageid;
	String xmlNameString = null;

	switch (pageid) {
	case 10:
		xmlNameString= "page_51.xml";
		break;
	case 11:
		xmlNameString="page_52.xml";
		break;
	case 0:
		xmlNameString= "page_51.xml";
		break;		

	default:
		
		return status=0;
	}

	File file = new File(filepath+xmlNameString);
	try 
	{
		doc = Jsoup.parse(file, "UTF-8");
	
	 }
	catch (IOException e) 
	{
		e.printStackTrace();
		return status=-1;
	}

	Elements itemnumbers = doc.getElementsByTag("itemnumber");
	totalQuestion = itemnumbers.size();//根据题号标签获得题目总数    
	 elements = doc.getElementsByTag("itemnumber");
	 x1s      = doc.getElementsByTag("x1");
	 y1s      = doc.getElementsByTag("y1");
	 x2s      = doc.getElementsByTag("x2");
	 y2s      = doc.getElementsByTag("y2");
	 x3s      = doc.getElementsByTag("x3");
	 y3s      = doc.getElementsByTag("y3");
	 x4s      = doc.getElementsByTag("x4");
	 y4s      = doc.getElementsByTag("y4");
	 x5s      = doc.getElementsByTag("x5");
	 y5s      = doc.getElementsByTag("y5");
	 x6s      = doc.getElementsByTag("x6");
	 y6s      = doc.getElementsByTag("y6");
	 x7s      = doc.getElementsByTag("x7");
	 y7s      = doc.getElementsByTag("y7");
	 x8s      = doc.getElementsByTag("x8");
	 y8s      = doc.getElementsByTag("y8");
	 x9s      = doc.getElementsByTag("x9");
	 y9s      = doc.getElementsByTag("y9");
	 x10s     = doc.getElementsByTag("x10");
	 y10s     = doc.getElementsByTag("y10");
	 x11s     = doc.getElementsByTag("x11");
	 y11s     = doc.getElementsByTag("y11");
	 x12s     = doc.getElementsByTag("x12");
	 y12s     = doc.getElementsByTag("y12");
	 x13s     = doc.getElementsByTag("x13");
	 y13s     = doc.getElementsByTag("y13");
	 x14s     = doc.getElementsByTag("x14");
	 y14s     = doc.getElementsByTag("y14");
		
	QuestionTypes = doc.getElementsByTag("itemtype");
	abilitys      = doc.getElementsByTag("ability");
	skills        = doc.getElementsByTag("skill");
	knowledges    = doc.getElementsByTag("knowledge");
	return status=1;
}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @date：2019-1-23 下午9:14:51
 * @param x
 * @param y
 * @return：ArrayList<Integer>的第一位(0)代表点所在的题目的编号，第二位(1)代表题目的具体区域
 */
   public ArrayList<Integer> getAreaInfo( float x,float y ){
	 if (totalQuestion==-1||totalQuestion==0) {//模板未解析成功或者模板中没有题目
		return null;
	}
 	double RatioX = 0;//横坐标比例
 	double RatioY = 0;//纵坐标比例
 	float RatioX1 = 0;
 	float RatioY1 = 0;
 	double RatioPaperY = 0;//纸屏比例
 	double RatioPaperX = 0;
 	double temp,tempx;
 	boolean IsItem = false;
 	RatioX = (double)595/(double)1600;//595为pdf的宽度，1600为屏幕的宽度
 	RatioY = (double)842/(double)2500;//842为pdf的长度,2500为屏幕的长度
 	RatioX1 = (float)1600/(float)595;//595为pdf的宽度，1600为屏幕的宽度
 	RatioY1 = (float)2500/(float)842;//842为pdf的长度,2500为屏幕的长度
 	RatioPaperY = (double)2500/(double)2082;
 	RatioPaperX = (double)1600/(double)1433;
 	temp = (double)(y-140)*RatioPaperY;
 	tempx = (double)(x-50)*RatioPaperX;
 	
 	ArrayList<Integer> tag = new ArrayList<Integer>(); 	
	//2019.01.18，判断是否在题号上圈题
	if(tempx*RatioX>80 && tempx*RatioX<100)
	{
		for(int j = 0;j<totalQuestion;j++)
		{
			if(QuestionTypes.get(j).text().toString().equals("页眉") == true)
			{
				continue;
			}
			
			else
			{
				if(temp*RatioY > Double.valueOf(y1s.get(j).text().toString()) && temp*RatioY < Double.valueOf(y5s.get(j).text().toString()))
				{
					tag.add(j);
					tag.add(0);
					IsItem = true;
					break;
				}
			}
		}
	}
	//从头到尾遍历xml模板中的题目
	for(int i = 0;i<totalQuestion;i++)
	{
		if(IsItem == true) 
		{
			break;
		}
		
		else if(QuestionTypes.get(i).text().toString().equals("页眉") == true)
		{
			if(temp*RatioY > Double.valueOf(y1s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y3s.get(i).text().toString()))
			{
				tag.add(i);
				tag.add(1);
				break;
			}
		}
		else if(QuestionTypes.get(i).text().toString().equals("选择题") == true)
		{
			if(temp*RatioY > Double.valueOf(y13s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y14s.get(i).text().toString()))
			{
				if(temp*RatioY > Double.valueOf(y1s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y3s.get(i).text().toString()) && x*RatioX>Double.valueOf(x1s.get(i).text().toString()) && x*RatioX<Double.valueOf(x3s.get(i).text().toString()))
				{
					//runOnUIThread("题干区");
					tag.add(i);
					tag.add(2);
					break;
				}
				else if (temp*RatioY > Double.valueOf(y1s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y5s.get(i).text().toString()) && x*RatioX>Double.valueOf(x1s.get(i).text().toString()) && x*RatioX<Double.valueOf(x5s.get(i).text().toString())) 
				{
					//runOnUIThread("题干区");
					tag.add(i);
					tag.add(2);
					break;
				}
				else if (temp*RatioY > Double.valueOf(y6s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y8s.get(i).text().toString()) && x*RatioX>Double.valueOf(x8s.get(i).text().toString()) && x*RatioX<Double.valueOf(x6s.get(i).text().toString())) 
				{
					//runOnUIThread("题干区");
					tag.add(i);
					tag.add(2);
					break;
				}
				else if(temp*RatioY > Double.valueOf(y4s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y10s.get(i).text().toString()) && x*RatioX>Double.valueOf(x4s.get(i).text().toString()) && x*RatioX<Double.valueOf(x10s.get(i).text().toString()))
				{
					//runOnUIThread("答题区");
					tag.add(i);
					tag.add(3);
					break;
				}
				else if(temp*RatioY > Double.valueOf(y13s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y8s.get(i).text().toString()) && x*RatioX>Double.valueOf(x13s.get(i).text().toString()) && x*RatioX<Double.valueOf(x8s.get(i).text().toString()))
				{
					//runOnUIThread("三维语义区");
					tag.add(i);
					tag.add(4);
					break;
				}
				else 
				{
					//runOnUIThread("留白区");
					tag.add(i);
					tag.add(4);
					break;
				}
			}
			else continue;
		}
		
		else if (QuestionTypes.get(i).text().toString().equals("填空题") == true) 
		{
			if(temp*RatioY > Double.valueOf(y11s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y12s.get(i).text().toString()))
			{
				if(temp*RatioY > Double.valueOf(y1s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y3s.get(i).text().toString()) && x*RatioX>Double.valueOf(x1s.get(i).text().toString()) && x*RatioX<Double.valueOf(x3s.get(i).text().toString()))
				{
					//runOnUIThread("题干区");
					tag.add(i);
					tag.add(2);
					break;
				}
				if(temp*RatioY > Double.valueOf(y1s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y5s.get(i).text().toString()) && x*RatioX>Double.valueOf(x1s.get(i).text().toString()) && x*RatioX<Double.valueOf(x5s.get(i).text().toString()))
				{
					//runOnUIThread("题干区");
					tag.add(i);
					tag.add(2);
					break;
				}
				else if(temp*RatioY > Double.valueOf(y4s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y8s.get(i).text().toString()) && x*RatioX>Double.valueOf(x4s.get(i).text().toString()) && x*RatioX<Double.valueOf(x8s.get(i).text().toString()))
				{
					//runOnUIThread("答题区");
					tag.add(i);
					tag.add(3);
					break;
				}
				else if(temp*RatioY > Double.valueOf(y11s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y6s.get(i).text().toString()) && x*RatioX>Double.valueOf(x11s.get(i).text().toString()) && x*RatioX<Double.valueOf(x6s.get(i).text().toString()))
				{
					//runOnUIThread("三维语义区");
					tag.add(i);
					tag.add(4);
					break;
				}
				else 
				{
					//runOnUIThread("留白区");
					tag.add(i);
					tag.add(4);
					break;
				}
			}
			else 
			{
				continue;
			}
		}
		
		else 
		{
			if(temp*RatioY > Double.valueOf(y10s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y11s.get(i).text().toString())) 
			{
				if(temp*RatioY > Double.valueOf(y1s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y3s.get(i).text().toString()) && x*RatioX>Double.valueOf(x1s.get(i).text().toString()) && x*RatioX<Double.valueOf(x3s.get(i).text().toString()))
				{
					//runOnUIThread("题干区");
					tag.add(i);
					tag.add(2);
					break;
				}
				else if(temp*RatioY > Double.valueOf(y1s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y5s.get(i).text().toString()) && x*RatioX>Double.valueOf(x1s.get(i).text().toString()) && x*RatioX<Double.valueOf(x5s.get(i).text().toString()))
				{
					//runOnUIThread("题干区");
					tag.add(i);
					tag.add(2);
					break;
				}
				else if(temp*RatioY > Double.valueOf(y10s.get(i).text().toString()) && temp*RatioY < Double.valueOf(y9s.get(i).text().toString()) && x*RatioX>Double.valueOf(x10s.get(i).text().toString()) && x*RatioX<Double.valueOf(x9s.get(i).text().toString()))
				{
					//runOnUIThread("三维语义区");
					tag.add(i);
					tag.add(4);
					break;
				}
				else 
				{
					//runOnUIThread("答题区");
					tag.add(i);
					tag.add(3);
					break;
				}
			}
		}
	}
	if(tag.size() == 2)
	{
		return tag;
	}
     return null;
 
	 
	 
 }  
   
   
   
}
