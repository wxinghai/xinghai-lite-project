package com.lite.gym.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author dbg
 *
 */
public class GeneratorUtil {

	public static void main(String[] args) {
		System.out.print("getRandomString = " + getRandomString(4) + getRandomNumString(6));
	}
	
    private static AtomicInteger orderIdCount = new AtomicInteger();

    private static final SimpleDateFormat ORDER_ID_FORMAT = new SimpleDateFormat("yyyyMMHHmmss");

    public static String genSixVerifyCode() {
        String time = System.nanoTime() + "";
        return time.substring(time.length() - 6);
    }

    public static String genSessionId() {
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }


    public static String genOrderId(String preffix) {
    	int ref = 1000;
        int i = orderIdCount.incrementAndGet() % ref;
        if (i < ref) {
            i += ref;
        }
        return preffix +"-"+ ORDER_ID_FORMAT.format(new Date()) +"-"+ i;
    }

    public static String genFileName(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String genUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String getRandomString(int length){
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
           int number=random.nextInt(3);
           long result=0;
           switch(number){
              case 0:
                  result=Math.round(Math.random()*25+65);
                  sb.append(String.valueOf((char)result));
                  break;
             case 1:
                 result=Math.round(Math.random()*25+97);
                 sb.append(String.valueOf((char)result));
                 break;
             case 2:     
                 sb.append(String.valueOf(new Random().nextInt(10)));
                 break;
            }


         }
         return sb.toString();
     }
    
	public static String getRandomNumString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(String.valueOf(new Random().nextInt(10)));
		}
		return sb.toString();
	}
}
