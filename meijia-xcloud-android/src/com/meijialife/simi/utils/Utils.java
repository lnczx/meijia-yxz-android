package com.meijialife.simi.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static int height = -1;
    private static int width = -1;
    private static float densityDpi = -1;
    private static float density = -1;
    private static String deviceId = "";


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int getHeight(Context context) {
        if (height < 0) {
            getValues(context);
        }
        return height;
    }

    public static int getWidth(Context context) {
        if (width < 0) {
            getValues(context);
        }
        return width;
    }

    public static float getDensityDpi(Context context) {
        if (densityDpi < 0) {
            getValues(context);
        }
        return densityDpi;
    }

    public static float getDensity(Context context) {
        if (density < 0) {
            getValues(context);
        }
        return density;
    }

    private static void getValues(Context context) {

        DisplayMetrics display = new DisplayMetrics();
        display = context.getResources().getDisplayMetrics();
        width = display.widthPixels;
        height = display.heightPixels;
        densityDpi = display.densityDpi;
        density = display.density;
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);//
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
        }
    }


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


}
