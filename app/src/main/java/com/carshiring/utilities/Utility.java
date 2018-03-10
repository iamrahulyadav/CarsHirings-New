package com.carshiring.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.widget.Toast;

/*
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
*/

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.security.MessageDigest;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class Utility {
    static ProgressDialog progressDialog;
    public Bitmap getCircularBitmap(Bitmap bitmap)
    {
        final Bitmap circularimage=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(circularimage);
        int mColor= Color.RED;
        Paint paint=new Paint();
        Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        RectF rectF=new RectF(rect);
        paint.setAntiAlias(true);
        paint.setColor(mColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawARGB(0,0,0,0);
        canvas.drawOval(rectF,paint);
        canvas.drawBitmap(bitmap,rect,rect,paint);
        bitmap.recycle();

        return bitmap;
    }

    public static String  getRandomString(int length) {
        String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * VALID_CHARS.length());
            salt.append(VALID_CHARS.charAt(index));
        }
        return salt.toString();
    }

    public static String getSHA256Hash(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static Boolean isNetworkConnected(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnectedOrConnecting())
            return true;
        return false;
    }
    public static Boolean checkemail(final String emai)
    {
        if(emai!=null)
        {
            Pattern pattern= Patterns.EMAIL_ADDRESS;
            if(pattern.matcher(emai).matches())
            {
                return pattern.matcher(emai).matches();
            }
        }
        return false;

    }


    public static Boolean checkphone(final String phone)
    {
        if(phone!=null)
        {
            Pattern pattern= Patterns.PHONE;
            if(pattern.matcher(phone).matches())
            {
                return pattern.matcher(phone).matches();
            }
        }
        return false;

    }
    public static void showloadingPopup(Activity activity)
    {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
        progressDialog=new ProgressDialog(activity);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
    }

    public static void showLoading(Activity activity,String message)
    {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
        progressDialog=new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public  static void hidepopup()
    {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
        progressDialog=null;
    }
    public static void message(Context context,String Msg)
    {
        Toast.makeText(context,Msg,Toast.LENGTH_SHORT).show();
    }

    public static boolean checkGooglePlayService(Activity activity)
    {
        int checkGooglePlayService= GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        int Requestcode=200;
        if(checkGooglePlayService!= ConnectionResult.SUCCESS)
        {
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayService,activity,Requestcode);
            Toast.makeText(activity," not working",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }



}
