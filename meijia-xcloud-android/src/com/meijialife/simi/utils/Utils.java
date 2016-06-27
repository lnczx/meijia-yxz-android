package com.meijialife.simi.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

public class Utils {

	/**
	 * @Description: 验证手机号是否合法
	 * @param mobile
	 * @return
	 */
	public static boolean verifyPhoneNo(String mobile) {
		Pattern p = Pattern.compile("^(13|15|18)\\d{9}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	public static int getDeviceHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	/**
	 * 根据手机版本，获取dialog风格
	 *
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static int getDialogTheme() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.style.Theme_Holo_Dialog
				: android.R.style.Theme_Dialog;
	}

	/**
	 * 获取APK当前版本号
	 *
	 * @return
	 */
	public static String getCurVerName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					"com.meijialife.simi", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	/**
     * 判断sdcard是否挂载
     * 
     * @return
     */
    public static boolean isExistSD() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// sdcard是否挂载
            return true;

        }
        return false;
    }
    
    
    /**
     * 写到Sdcard文件上
     * @param fileName
     * @param message
     */
    public static void writeFileSdcard(Context context,String fileName,String message){ 

        try{ 
        FileOutputStream fout = new FileOutputStream(fileName);
          byte [] bytes = message.getBytes(); 
   Toast.makeText(context, ""+bytes.toString(),Toast.LENGTH_LONG).show();
          fout.write(bytes); 
          fout.close(); 
         } 
        catch(Exception e){ 

         e.printStackTrace(); 
        } 
    }    

    /**
     * 从SD卡读取文件
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFileSdcardFile(String fileName) throws IOException{   
        String res="";   
        try{   
               FileInputStream fin = new FileInputStream(fileName);   
        
               int length = fin.available();   
        
               byte [] buffer = new byte[length];   
               fin.read(buffer);       
        
               res = EncodingUtils.getString(buffer, "UTF-8");   
        
               fin.close();       
              }   
        
              catch(Exception e){   
               e.printStackTrace();   
              }   
              return res;   
      }   
    
}
