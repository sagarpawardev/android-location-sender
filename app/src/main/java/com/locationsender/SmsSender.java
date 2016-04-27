package com.locationsender;

import java.util.ArrayList;

import android.telephony.SmsManager;

public class SmsSender{

	final static String RESULT_OK = "RESULT_OK";

	public static String send(String contactNo,String msg){
        try{
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> smsString = sms.divideMessage(msg);
            sms.sendMultipartTextMessage(contactNo, null, smsString, null, null);
            return RESULT_OK;
        }
        catch(Exception e){
        	e.printStackTrace();
        	return e.getMessage();
        }
    }

}