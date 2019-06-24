package com.example.smartpengesture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import android.widget.FrameLayout;

public class testxml {
	
	static Document doc;
    static String pagexml;
    /**
     * 
     * @param x
     * @param y
     * @param pageid
     * @return :tag[2],tag=null:不在任何区域 tag[0]:题号；tag[1]:题号对应题目的某个区域
     */
    public static ArrayList<Integer> test(float x,float y, int pageid)
    {
    	Log.e("zgm", "page:"+pageid);
    	switch (pageid) {
		case 0:
			pagexml = "page_0.xml";
			break;
		case 1:
			pagexml = "page_1.xml";
			break;
		default:
			Log.e("wsk", "0415+pageid不对");
			return null;
		}

    	int totalQuestion = 0;//题目总数
    	double RatioX = 0;//横坐标比例
    	double RatioY = 0;//纵坐标比例
    	float RatioX1 = 0;
    	float RatioY1 = 0;
    	double RatioPaperY = 0;//纸屏比例
    	double RatioPaperX = 0;
    	double tempy,tempx;
    	RatioX = (double)595/(double)1600;//595为pdf的宽度，1600为屏幕的宽度
    	RatioY = (double)842/(double)2500;//842为pdf的长度,2500为屏幕的长度
    	RatioX1 = (float)1600/(float)595;//595为pdf的宽度，1600为屏幕的宽度
    	RatioY1 = (float)2500/(float)842;//842为pdf的长度,2500为屏幕的长度
    	RatioPaperY = (double)2500/(double)2082;
    	RatioPaperX = (double)1600/(double)1433;
    	tempy = (double)(y-140)*RatioPaperY;
    	tempx = (double)(x-30)*RatioPaperX;
    	int count = 2;//计数器,页眉和组只有quyu属性，在使用其他属性时要减去模板中的页眉和组的数量
    	int counttigan = 0;
    	boolean IsEnd = false;
    	
    	ArrayList<Integer> tag = new ArrayList<Integer>();
    	
    	File file = new File("/sdcard/" +pagexml);
    	try 
    	{
    		doc = Jsoup.parse(file, "UTF-8");
    	
    	 }
    	catch (IOException e) 
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
    	Elements item = doc.getElementsByTag("item");
    	Elements type = doc.getElementsByTag("type");
    	Elements quyu = doc.getElementsByTag("quyu");
    	Elements tihaoqu = doc.getElementsByTag("tihaoqu");
    	Elements tiganqu = doc.getElementsByTag("tiganqu");
    	Elements datiqu = doc.getElementsByTag("datiqu");
    	Elements itemson;
    	Elements liubaiqu;
    	Elements tiganquson;
    	
    	
    	//0-页眉  1-组  2-题号区  3-答题区  4-题干区  5-留白区
    	for(int i = 0;i<item.size();i++)
    	{
    		
    		//根据区域坐标判断属于哪个区域（题目/页眉/组）
    		if(tempy*RatioY>Double.valueOf(quyu.get(i).getElementsByTag("y1").text().toString()) && tempy*RatioY<Double.valueOf(quyu.get(i).getElementsByTag("y2").text().toString()) && tempx*RatioX>Double.valueOf(quyu.get(i).getElementsByTag("x1").text().toString()) && tempx*RatioX<Double.valueOf(quyu.get(i).getElementsByTag("x2").text().toString()))
    		{
    			//判断页眉
    			if(type.get(i).text().toString().equals("页眉") == true)
    			{
    				tag.add(i);
    				tag.add(0);
    				break;
    			}
    			
    			//判断组
    			else if(type.get(i).text().toString().equals("组") == true)
    			{
    				tag.add(i);
    				tag.add(1);
    				break;
    			}
    			
    			//判断题目区域
    			else
    			{
    				//判断题号区
    				if(tempx*RatioX>Double.valueOf(tihaoqu.get(i-count).getElementsByTag("x1").text().toString()) && tempx*RatioX<Double.valueOf(tihaoqu.get(i-count).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(tihaoqu.get(i-count).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(tihaoqu.get(i-count).getElementsByTag("y2").text().toString()))
    				{
    					tag.add(i);
        				tag.add(2);
    					break;
    				}
    				
    				//判断答题区：选择题只有一个空，两个坐标可以确定矩形范围
    				else if (type.get(i).text().toString().equals("选择题") == true) 
    				{
    					//选择题答题区
						if(tempx*RatioX>Double.valueOf(datiqu.get(i-count).getElementsByTag("x1").text().toString()) && tempx*RatioX<Double.valueOf(datiqu.get(i-count).getElementsByTag("x2").text().toString()) && tempy*RatioY>Double.valueOf(datiqu.get(i-count).getElementsByTag("y1").text().toString()) && tempy*RatioY<Double.valueOf(datiqu.get(i-count).getElementsByTag("y2").text().toString()))
						{
							tag.add(i);
		    				tag.add(3);
							break;
						}
					}
    				
    				//判断答题区：填空题有多个空，根据<count/>元素值，判断由几个坐标确定
    				else if(type.get(i).text().toString().equals("填空题") == true)
    				{
    					//两个坐标--一个空
    					if(Double.valueOf(datiqu.get(i-count).getElementsByTag("count").text().toString()) == 2)
						{
							if(tempx*RatioX > Double.valueOf(datiqu.get(i-count).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(datiqu.get(i-count).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(datiqu.get(i-count).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(datiqu.get(i-count).getElementsByTag("y2").text().toString()))
							{
								tag.add(i);
			    				tag.add(3);
								break;
							}
						}
    					
    					//四个坐标--两个空
    					else if(Double.valueOf(datiqu.get(i-count).getElementsByTag("count").text().toString()) == 4)
    					{
    						if(tempx*RatioX > Double.valueOf(datiqu.get(i-count).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(datiqu.get(i-count).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(datiqu.get(i-count).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(datiqu.get(i-count).getElementsByTag("y2").text().toString()))
							{
    							tag.add(i);
    		    				tag.add(3);
								break;
							}
    						
    						else if(tempx*RatioX > Double.valueOf(datiqu.get(i-count).getElementsByTag("x3").text().toString()) && tempx*RatioX < Double.valueOf(datiqu.get(i-count).getElementsByTag("x4").text().toString()) && tempy*RatioY > Double.valueOf(datiqu.get(i-count).getElementsByTag("y3").text().toString()) && tempy*RatioY < Double.valueOf(datiqu.get(i-count).getElementsByTag("y4").text().toString()))
							{
    							tag.add(i);
    		    				tag.add(3);
								break;
							}
    					}
    					
    					
    					//六个坐标--三个空
    					else if(Double.valueOf(datiqu.get(i-count).getElementsByTag("count").text().toString()) == 6)
    					{
    						if(tempx*RatioX > Double.valueOf(datiqu.get(i-count).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(datiqu.get(i-count).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(datiqu.get(i-count).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(datiqu.get(i-count).getElementsByTag("y2").text().toString()))
							{
    							tag.add(i);
    		    				tag.add(3);
								break;
							}
    						
    						else if(tempx*RatioX > Double.valueOf(datiqu.get(i-count).getElementsByTag("x3").text().toString()) && tempx*RatioX < Double.valueOf(datiqu.get(i-count).getElementsByTag("x4").text().toString()) && tempy*RatioY > Double.valueOf(datiqu.get(i-count).getElementsByTag("y3").text().toString()) && tempy*RatioY < Double.valueOf(datiqu.get(i-count).getElementsByTag("y4").text().toString()))
							{
    							tag.add(i);
    		    				tag.add(3);
								break;
							}
    						
    						else if(tempx*RatioX > Double.valueOf(datiqu.get(i-count).getElementsByTag("x5").text().toString()) && tempx*RatioX < Double.valueOf(datiqu.get(i-count).getElementsByTag("x6").text().toString()) && tempy*RatioY > Double.valueOf(datiqu.get(i-count).getElementsByTag("y5").text().toString()) && tempy*RatioY < Double.valueOf(datiqu.get(i-count).getElementsByTag("y6").text().toString()))
							{
    							tag.add(i);
    		    				tag.add(3);
								break;
							}
    					}
    				}
    				
    				//题干区
    				if(IsEnd == false)
    				{
    					if(type.get(i).text().toString().equals("选择题") == true || type.get(i).text().toString().equals("填空题") == true)
    					{
    						if(Double.valueOf(tiganqu.get(i-count).getElementsByTag("count").text().toString()) == 2)
    						{
    							if(tempx*RatioX > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y2").text().toString()))
    							{
    								IsEnd = true;
    								counttigan++;
    								tag.add(i);
    			    				tag.add(4);
    								break;
    							}
    						}
    						
    						else if(Double.valueOf(tiganqu.get(i-count).getElementsByTag("count").text().toString()) == 4)
    						{
    							if(tempx*RatioX > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y2").text().toString()))
    							{
    								IsEnd = true;
    								counttigan++;
    								tag.add(i);
    			    				tag.add(4);
    								break;
    							}
    							
    							else if(tempx*RatioX > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x3").text().toString()) && tempx*RatioX < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x4").text().toString()) && tempy*RatioY > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y3").text().toString()) && tempy*RatioY < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y4").text().toString()))
    							{
    								IsEnd = true;
    								counttigan++;
    								tag.add(i);
    			    				tag.add(4);
    								break;
    							}
    						}
    						
        					
    						else if(Double.valueOf(tiganqu.get(i-count).getElementsByTag("count").text().toString()) == 6)
    						{
    							if(tempx*RatioX > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y2").text().toString()))
    							{
    								IsEnd = true;
    								tag.add(i);
    			    				tag.add(4);
    								break;
    							}
    							
    							else if(tempx*RatioX > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x3").text().toString()) && tempx*RatioX < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x4").text().toString()) && tempy*RatioY > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y3").text().toString()) && tempy*RatioY < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y4").text().toString()))
    							{
    								IsEnd = true;
    								tag.add(i);
    			    				tag.add(4);
    								break;
    							}
    							
    							else if(tempx*RatioX > Double.valueOf(tiganqu.get(i-count).getElementsByTag("x5").text().toString()) && tempx*RatioX < Double.valueOf(tiganqu.get(i-count).getElementsByTag("x6").text().toString()) && tempy*RatioY > Double.valueOf(tiganqu.get(i-count).getElementsByTag("y5").text().toString()) && tempy*RatioY < Double.valueOf(tiganqu.get(i-count).getElementsByTag("y6").text().toString()))
    							{
    								IsEnd = true;
    								tag.add(i);
    			    				tag.add(4);
    								break;
    							}
    						}
    					}
    					
    					else
    					{
    						itemson = item.get(i).getElementsByTag("itemson");
        					for(int j = 0;j<itemson.size();j++)
        					{
        						tiganquson = itemson.get(j).getElementsByTag("tiganquson");
        						if(Double.valueOf(tiganquson.get(0).getElementsByTag("count").text().toString()) == 2)
        						{
        							if(tempx*RatioX > Double.valueOf(tiganquson.get(0).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(tiganquson.get(0).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(tiganquson.get(0).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(tiganquson.get(0).getElementsByTag("y2").text().toString()))
        							{
        								IsEnd = true;
        								
        								tag.add(i);
        			    				tag.add(4);
        			    				tag.add(j);
        								break;
        							}
        						}
        						
        						else if(Double.valueOf(tiganquson.get(0).getElementsByTag("count").text().toString()) == 4)
        						{
        							if(tempx*RatioX > Double.valueOf(tiganquson.get(0).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(tiganquson.get(0).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(tiganquson.get(0).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(tiganquson.get(0).getElementsByTag("y2").text().toString()))
        							{
        								IsEnd = true;
        								
        								tag.add(i);
        			    				tag.add(4);
        			    				tag.add(j);
        								break;
        							}
        							
        							else if(tempx*RatioX > Double.valueOf(tiganquson.get(0).getElementsByTag("x3").text().toString()) && tempx*RatioX < Double.valueOf(tiganquson.get(0).getElementsByTag("x4").text().toString()) && tempy*RatioY > Double.valueOf(tiganquson.get(0).getElementsByTag("y3").text().toString()) && tempy*RatioY < Double.valueOf(tiganquson.get(0).getElementsByTag("y4").text().toString()))
        							{
        								IsEnd = true;
        								
        								tag.add(i);
        			    				tag.add(4);
        			    				tag.add(j);
        								break;
        							}
        						}
        						
            					
        						else if(Double.valueOf(tiganquson.get(0).getElementsByTag("count").text().toString()) == 6)
        						{
        							if(tempx*RatioX > Double.valueOf(tiganquson.get(0).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(tiganquson.get(0).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(tiganquson.get(0).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(tiganquson.get(0).getElementsByTag("y2").text().toString()))
        							{
        								IsEnd = true;
        								tag.add(i);
        			    				tag.add(4);
        			    				tag.add(j);
        								break;
        							}
        							
        							else if(tempx*RatioX > Double.valueOf(tiganquson.get(0).getElementsByTag("x3").text().toString()) && tempx*RatioX < Double.valueOf(tiganquson.get(0).getElementsByTag("x4").text().toString()) && tempy*RatioY > Double.valueOf(tiganquson.get(0).getElementsByTag("y3").text().toString()) && tempy*RatioY < Double.valueOf(tiganquson.get(0).getElementsByTag("y4").text().toString()))
        							{
        								IsEnd = true;
        								tag.add(i);
        			    				tag.add(4);
        			    				tag.add(j);
        								break;
        							}
        							
        							else if(tempx*RatioX > Double.valueOf(tiganquson.get(0).getElementsByTag("x5").text().toString()) && tempx*RatioX < Double.valueOf(tiganquson.get(0).getElementsByTag("x6").text().toString()) && tempy*RatioY > Double.valueOf(tiganquson.get(0).getElementsByTag("y5").text().toString()) && tempy*RatioY < Double.valueOf(tiganquson.get(0).getElementsByTag("y6").text().toString()))
        							{
        								IsEnd = true;
        								tag.add(i);
        			    				tag.add(4);
        			    				tag.add(j);
        								break;
        							}
        						}
        					}
    					}
    				}
    				
    				if(IsEnd == false)
    				{
    					//判断答题区：
        				if(type.get(i).text().toString().equals("解答题") == true)
        				{
        					itemson = item.get(i).getElementsByTag("itemson");
        					for(int j = 0;j<itemson.size();j++)
        					{
        						//留白区
        						liubaiqu = itemson.get(j).getElementsByTag("liubaiqu");
        						if(tempx*RatioX > Double.valueOf(liubaiqu.get(j).getElementsByTag("x1").text().toString()) && tempx*RatioX < Double.valueOf(liubaiqu.get(j).getElementsByTag("x2").text().toString()) && tempy*RatioY > Double.valueOf(liubaiqu.get(j).getElementsByTag("y1").text().toString()) && tempy*RatioY < Double.valueOf(liubaiqu.get(j).getElementsByTag("y2").text().toString()))
        						{
        							IsEnd = true;
        							tag.add(i);
    			    				tag.add(5);
    			    				tag.add(j);
        							break;
        						}
        					}	
        					
        					//答题区
        					if(IsEnd == false)
        					{
        						//答题区
        						tag.add(i);
			    				tag.add(3);
        						break;
        					}
        				}
    				}
    				
    				//留白区
    				tag.add(i);
    				tag.add(5);
    			}
    		}
    	}
    	
    	if(tag.size()<2)
    	{
    		Log.e("wsk", "0415:"+tag.size());
    		return null;
    	}
    	
    	else return tag;
    }

}
