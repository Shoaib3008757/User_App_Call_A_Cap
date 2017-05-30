package come.texi.driver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import come.texi.driver.AllTripActivity;
import come.texi.driver.ChangePasswordActivity;
import come.texi.driver.HomeActivity;
import come.texi.driver.LoginActivity;
import come.texi.driver.LoginOptionActivity;
import come.texi.driver.R;
import come.texi.driver.RateCardActivity;
import come.texi.driver.SignUpActivity;
import come.texi.driver.UserProfileActivity;
import come.texi.driver.gpsLocation.GPSTracker;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by techintegrity on 04/07/16.
 */
public class Common {

    public static String device_token = "";
    public static JSONArray CabDetail;
    public static String Currency = "";
    public static String Country = "";
    public static String StartDayTime = "";
    public static String EndDayTime = "";
    public static AllTripFeed allTripFeeds;
    public static String ActiveActivity = "book my trips";
    public static int is_pusnotification = 0;
    public static int user_InActive = 0;
    public static String InActive_msg = "";

    RelativeLayout layout_book_my_trip;
    RelativeLayout layout_my_trip;
    RelativeLayout layout_rate_card;
    RelativeLayout layout_cahnge_password;
    RelativeLayout layout_footer_logout;

    double PickupLongtude;
    double PickupLatitude;

    public static void showLoginRegisterMkError(final Activity act,String message)
    {
        if(!act.isFinishing()){

            Animation slideUpAnimation;

            final Dialog MKInfoPanelDialog = new Dialog(act,android.R.style.Theme_Translucent_NoTitleBar);

            MKInfoPanelDialog.setContentView(R.layout.mkinfopanel);
            MKInfoPanelDialog.show();
            slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(),
                    R.anim.slide_up_map);

            RelativeLayout layout_info_panel = (RelativeLayout) MKInfoPanelDialog.findViewById(R.id.layout_info_panel);
            layout_info_panel.startAnimation(slideUpAnimation);

            TextView subtitle = (TextView)MKInfoPanelDialog.findViewById(R.id.subtitle);
            subtitle.setText(message);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(MKInfoPanelDialog.isShowing() && !act.isFinishing())
                            MKInfoPanelDialog.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);

        }
    }

    public static void LoginMkError(final Activity act,String message,String code){

        Typeface OpenSans_Bold = Typeface.createFromAsset(act.getAssets(), "fonts/OpenSans-Bold_0.ttf");
        Typeface OpenSans_Regular = Typeface.createFromAsset(act.getAssets(), "fonts/OpenSans-Regular_0.ttf");

        final Dialog LoginErrorDialog = new Dialog(act,android.R.style.Theme_Translucent_NoTitleBar);
        LoginErrorDialog.setContentView(R.layout.login_error_dialog);
        TextView txt_invalid_login = (TextView)LoginErrorDialog.findViewById(R.id.txt_invalid_login);
        TextView txt_error_msg = (TextView)LoginErrorDialog.findViewById(R.id.txt_error_msg);
        Log.d("code","code = "+code);
        if(code.toString().toLowerCase().equals("invalid login")){
            txt_invalid_login.setText(act.getResources().getString(R.string.invalid_login));
            txt_error_msg.setText(act.getResources().getString(R.string.correct_login_detail));
        }else{
            txt_invalid_login.setText(act.getResources().getString(R.string.recheck_your_login_detail_title));
            txt_error_msg.setText(act.getResources().getString(R.string.recheck_your_login_detail));
        }
        txt_invalid_login.setTypeface(OpenSans_Bold);
        txt_error_msg.setTypeface(OpenSans_Regular);

        RelativeLayout layout_ok = (RelativeLayout)LoginErrorDialog.findViewById(R.id.layout_ok);
        layout_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginErrorDialog.cancel();
            }
        });

        LoginErrorDialog.show();
    }

    public static void showMkError(final Activity act, final String error_code)
    {
        String message = "";
        if(!act.isFinishing()){
            Log.d("error_code","error_code = "+error_code);
            if(error_code.equals("2")){
                message = act.getResources().getString(R.string.enter_correct_login_detail);
            }else if(error_code.equals("7")){
                message = act.getResources().getString(R.string.email_username_mobile_exit);
            }else if(error_code.equals("8")){
                message = act.getResources().getString(R.string.email_username_exit);
            }else if(error_code.equals("9")){
                message = act.getResources().getString(R.string.email_mobile_exit);
            }else if(error_code.equals("10")){
                message = act.getResources().getString(R.string.mobile_username_exit);
            }else if(error_code.equals("11")){
                message = act.getResources().getString(R.string.email_exit);
            }else if(error_code.equals("12")){
                message = act.getResources().getString(R.string.user_exit);
            }else if(error_code.equals("13")){
                message = act.getResources().getString(R.string.mobile_exit);
            }else if(error_code.equals("14")){
                message = act.getResources().getString(R.string.somthing_worng);
            }else if(error_code.equals("15") || error_code.equals("16")){
                message = act.getResources().getString(R.string.data_not_found);
            }else if(error_code.equals("19")){
                message = act.getResources().getString(R.string.vehicle_numbet_exits);
            }else if(error_code.equals("20")){
                message = act.getResources().getString(R.string.license_numbet_exits);
            }else if(error_code.equals("22")){
                message = act.getResources().getString(R.string.dublicate_booking);
            }else{
                message = error_code;
            }

            final SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(act);

            Animation slideUpAnimation;

           final Dialog MKInfoPanelDialog = new Dialog(act,android.R.style.Theme_Translucent_NoTitleBar);

            MKInfoPanelDialog.setContentView(R.layout.mkinfopanel);
            MKInfoPanelDialog.show();
            slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(),
                    R.anim.slide_up_map);

            RelativeLayout layout_info_panel = (RelativeLayout) MKInfoPanelDialog.findViewById(R.id.layout_info_panel);
            layout_info_panel.startAnimation(slideUpAnimation);

            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) act.getResources().getDimension(R.dimen.height_40));
            buttonLayoutParams.setMargins(0, (int) act.getResources().getDimension(R.dimen.height_50), 0, 0);
            layout_info_panel.setLayoutParams(buttonLayoutParams);

            TextView subtitle = (TextView)MKInfoPanelDialog.findViewById(R.id.subtitle);
            subtitle.setText(message);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(MKInfoPanelDialog.isShowing() && !act.isFinishing())
                            MKInfoPanelDialog.cancel();

                        if(error_code.equals("1") || error_code.equals("5") ){
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.clear();
                            editor.commit();

                            Intent logInt = new Intent(act, LoginOptionActivity.class);
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            act.startActivity(logInt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);

        }
    }

    public static void showMkSucess(final Activity act,String message,String isHeader)
    {
        if(!act.isFinishing()){

            Animation slideUpAnimation;

            final Dialog MKInfoPanelDialog = new Dialog(act,android.R.style.Theme_Translucent_NoTitleBar);

            MKInfoPanelDialog.setContentView(R.layout.mkinfopanel);
            MKInfoPanelDialog.show();
            slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(),
                    R.anim.slide_up_map);
            slideUpAnimation.setFillAfter(true);
            slideUpAnimation.setDuration(2000);

            RelativeLayout layout_info_panel = (RelativeLayout) MKInfoPanelDialog.findViewById(R.id.layout_info_panel);
            layout_info_panel.setBackgroundResource(R.color.sucess_color);
            layout_info_panel.startAnimation(slideUpAnimation);

            if(isHeader.equals("yes")) {
                RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) act.getResources().getDimension(R.dimen.height_50));
                buttonLayoutParams.setMargins(0, (int) act.getResources().getDimension(R.dimen.height_50), 0, 0);
                layout_info_panel.setLayoutParams(buttonLayoutParams);
            }

            TextView subtitle = (TextView)MKInfoPanelDialog.findViewById(R.id.subtitle);
            subtitle.setText(message);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (MKInfoPanelDialog.isShowing() && !act.isFinishing())
                            MKInfoPanelDialog.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);

        }
    }

    public static boolean isNetworkAvailable(Activity act){

        ConnectivityManager connMgr = (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            return true;
        } else {
            // display error
            return false;
        }

    }

    public static void showInternetInfo(final Activity act,String message)
    {
        if(!act.isFinishing()){
            final InternetInfoPanel mk = new InternetInfoPanel(act, InternetInfoPanel.InternetInfoPanelType.MKInfoPanelTypeInfo, "SUCCESS!",message, 2000);
            mk.show();
            mk.getIv_ok().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        if(mk.isShowing() && !act.isFinishing())
                            mk.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static boolean ShowHttpErrorMessage(Activity activity,String ErrorMessage){

        Log.d("ErrorMessage", "ErrorMessage = " + ErrorMessage);
        boolean Status = true;
        if (ErrorMessage != null && !ErrorMessage.equals("")) {
            if (ErrorMessage.contains("Connect to")) {
                Common.showInternetInfo(activity, "");
                Status = false;
            }else if(ErrorMessage.contains("failed to connect to")){
                Common.showInternetInfo(activity, "network not available");
                Status = false;
            }else if(ErrorMessage.contains("Internal Server Error")){
                Common.showMkError(activity, "Internal Server Error");
                Status = false;
            }else if(ErrorMessage.contains("Request Timeout")){
                Common.showMkError(activity, "Request Timeout");
                Status = false;
            }
        }else{
            Toast.makeText(activity, "Server Time Out", Toast.LENGTH_LONG).show();
            Status = false;
        }
        return Status;
    }

    public static class LoginSocialUserHttp extends AsyncTask<String, Integer, String>{

        HttpEntity entity;
        String SocialUrl;
        String facebook_id;
        String twitter_id;
        private String content =  null;
        Activity activity;
        SharedPreferences userPref;
        double PickupLongtude;
        double PickupLatitude;

        public LoginSocialUserHttp(String SUrl,String f_id, String t_id,Activity act){

            SocialUrl = SUrl;
            facebook_id = f_id;
            twitter_id = t_id;
            activity = act;

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            if(!facebook_id.equals(""))
                entityBuilder.addTextBody("facebook_id", facebook_id);
            else if(!twitter_id.equals(""))
                entityBuilder.addTextBody("twitter_id", twitter_id);
            entity = entityBuilder.build();

            userPref = PreferenceManager.getDefaultSharedPreferences(activity);

            GPSTracker gpsTracker = new GPSTracker(activity);
            PickupLatitude = gpsTracker.getLatitude();
            PickupLongtude = gpsTracker.getLongitude();
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpParams HttpParams = client.getParams();
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000);
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000);
                Log.d("SocialUrl","SocialUrl = "+SocialUrl+"=="+facebook_id+"=="+twitter_id);
                HttpPost post = new HttpPost(SocialUrl);
                post.setEntity(entity);
                client.execute(post, new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

                        HttpEntity httpEntity = httpResponse.getEntity();
                        content = EntityUtils.toString(httpEntity);
                        Log.d("Result >>>","Result One"+ content);

                        return null;
                    }
                });

            } catch(Exception e)
            {
                e.printStackTrace();
                Log.d("Indiaries", "Result error" + e);
                return e.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            boolean isStatus = Common.ShowHttpErrorMessage(activity,result);
            if(isStatus) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    Log.d("Social Register resObj", "Social Register resObj = " + resObj);
                    if (resObj.getString("status").equals("success")) {

                        JSONArray cabDtlAry = new JSONArray(resObj.getString("cabDetails"));
                        Common.CabDetail = cabDtlAry;

                        /*set Start Currency*/

                        JSONArray currencyArray = new JSONArray(resObj.getString("country_detail"));
                        for (int ci = 0; ci < currencyArray.length(); ci++) {
                            JSONObject startEndTimeObj = currencyArray.getJSONObject(ci);
                            Common.Currency = startEndTimeObj.getString("currency");
                            Common.Country = startEndTimeObj.getString("country");
                        }

                /*set Start And End Time*/
                        JSONArray startEndTimeArray = new JSONArray(resObj.getString("time_detail"));
                        for (int si = 0; si < startEndTimeArray.length(); si++) {
                            JSONObject startEndTimeObj = startEndTimeArray.getJSONObject(si);
                            Common.StartDayTime = startEndTimeObj.getString("day_start_time");
                            Common.EndDayTime = startEndTimeObj.getString("day_end_time");
                        }

                /*User Detail*/
                        JSONObject userDetilObj = new JSONObject(resObj.getString("userdetail"));

                        SharedPreferences.Editor id = userPref.edit();
                        id.putString("id", userDetilObj.getString("id").toString());
                        id.commit();

                        SharedPreferences.Editor name = userPref.edit();
                        name.putString("name", userDetilObj.getString("name").toString());
                        name.commit();

                        SharedPreferences.Editor passwordPre = userPref.edit();
                        passwordPre.putString("password", "");
                        passwordPre.commit();

                        SharedPreferences.Editor username = userPref.edit();
                        username.putString("username", userDetilObj.getString("username").toString());
                        username.commit();

                        SharedPreferences.Editor mobile = userPref.edit();
                        mobile.putString("mobile", userDetilObj.getString("mobile").toString());
                        mobile.commit();

                        SharedPreferences.Editor email = userPref.edit();
                        email.putString("email", userDetilObj.getString("email").toString());
                        email.commit();

                        SharedPreferences.Editor isLogin = userPref.edit();
                        isLogin.putString("isLogin", "1");
                        isLogin.commit();

                        SharedPreferences.Editor userImage = userPref.edit();
                        userImage.putString("userImage", userDetilObj.getString("image").toString());
                        userImage.commit();

                        SharedPreferences.Editor dob = userPref.edit();
                        dob.putString("date_of_birth", userDetilObj.getString("dob").toString());
                        dob.commit();


                        SharedPreferences.Editor facebook_id = userPref.edit();
                        facebook_id.putString("facebook_id", userDetilObj.getString("facebook_id").toString());
                        facebook_id.commit();

                        SharedPreferences.Editor twitter_id = userPref.edit();
                        twitter_id.putString("twitter_id", userDetilObj.getString("twitter_id").toString());
                        twitter_id.commit();

                        SharedPreferences.Editor gender = userPref.edit();
                        gender.putString("gender", userDetilObj.getString("gender").toString());
                        gender.commit();

                        //Common.showMkSucess(activity, resObj.getString("message").toString(), "no");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent hi = new Intent(activity, HomeActivity.class);
                                hi.putExtra("PickupLatitude", PickupLatitude);
                                hi.putExtra("PickupLongtude", PickupLongtude);
                                activity.startActivity(hi);
                                activity.finish();
                            }
                        }, 2000);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class LoginCallHttp extends AsyncTask<String, Integer, String>{

        Activity activity;
        Dialog ProgressDialog;
        RotateLoading cusRotateLoading;
        String activityName;
        private String content =  null;
        SharedPreferences userPref;
        String password;
        double PickupLongtude;
        double PickupLatitude;
        String LoginUrl;

        public LoginCallHttp(Activity act,Dialog ProgressDialog, RotateLoading cusRotateLoading, String password, String activityName,String LoginUrl){
            activity = act;

            this.ProgressDialog = ProgressDialog;
            this.cusRotateLoading = cusRotateLoading;
            this.activityName = activityName;
            this.password = password;
            this.LoginUrl = LoginUrl;

            userPref = PreferenceManager.getDefaultSharedPreferences(activity);

            GPSTracker gpsTracker = new GPSTracker(activity);
            PickupLatitude = gpsTracker.getLatitude();
            PickupLongtude = gpsTracker.getLongitude();

        }
        @Override
        protected void onPreExecute() {
            Log.d("Start", "start");
            if(ProgressDialog != null) {
                ProgressDialog.show();
                cusRotateLoading.start();
            }

        }

        @Override
        protected String doInBackground(String... params) {
/*
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpParams HttpParams = client.getParams();
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000);
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000);
                Log.d("LoginUrl", "LoginUrl = " + LoginUrl);
                HttpPost post = new HttpPost(LoginUrl);

                client.execute(post, new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

                        HttpEntity httpEntity = httpResponse.getEntity();
                        content = EntityUtils.toString(httpEntity);
                        Log.d("Result >>>","Result One"+ content);

                        return null;
                    }
                });

            }
            catch(Exception e)
            {
                e.printStackTrace();
                Log.d("Naquil", "Result error" + e);
                return e.getMessage();
            }
*/
            String urlLogin = LoginUrl;
            Log.d("urlLogin", "urlLogin = " + urlLogin);
            Ion.with(activity)
                    .load(urlLogin)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                cusRotateLoading.stop();
                            }
                            catch (Exception e1)
                            {
                                e1.printStackTrace();
                            }
                            try {
                                // do stuff with the result or error
                                if (e != null) {
                                    // Toast.makeText(LoginActivity.this, "Login Error"+e, Toast.LENGTH_LONG).show();
                                    Common.showMkError(activity, e.getMessage());
                                    return;
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            try {
                                content = result.toString();
                                Log.d("content", "onCompleted: "+content);
                                ValidateLogin(content);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }



                    });
            return content;
        }

        private void ValidateLogin(String value)
        {
            if(ProgressDialog != null) {
                cusRotateLoading.stop();
                ProgressDialog.cancel();
            }
            String result = value;
            boolean isStatus = Common.ShowHttpErrorMessage(activity,result);
            Log.d("LoginUrl", "LoginUrl result= " + result+"=="+isStatus);
            if(isStatus) {
                try {
                    Log.d("loginUrl", "loginUrl two= " + result);
                    final JSONObject resObj = new JSONObject(result);
                    Log.d("loginUrl", "loginUrl two= " + resObj);
                    if (ProgressDialog != null) {
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                    }
                    Log.d("loginUrl Status","loginUrl Status"+resObj.getString("status"));

                    if (resObj.getString("status").equals("success")) {

                        JSONArray cabDtlAry = new JSONArray(resObj.getString("cabDetails"));
                        Common.CabDetail = cabDtlAry;

                        /*set Start Currency*/

                        JSONArray currencyArray = new JSONArray(resObj.getString("country_detail"));
                        for (int ci = 0; ci < currencyArray.length(); ci++) {
                            JSONObject startEndTimeObj = currencyArray.getJSONObject(ci);
                            Common.Currency = startEndTimeObj.getString("currency");
                            Common.Country = startEndTimeObj.getString("country");
                        }

                /*set Start And End Time*/
                        JSONArray startEndTimeArray = new JSONArray(resObj.getString("time_detail"));
                        for (int si = 0; si < startEndTimeArray.length(); si++) {
                            JSONObject startEndTimeObj = startEndTimeArray.getJSONObject(si);
                            Common.StartDayTime = startEndTimeObj.getString("day_start_time");
                            Common.EndDayTime = startEndTimeObj.getString("day_end_time");
                        }

                /*User Detail*/
                        JSONObject userDetilObj = new JSONObject(resObj.getString("userdetail"));

                        SharedPreferences.Editor id = userPref.edit();
                        id.putString("id", userDetilObj.getString("id").toString());
                        id.commit();

                        SharedPreferences.Editor name = userPref.edit();
                        name.putString("name", userDetilObj.getString("name").toString());
                        name.commit();

                        SharedPreferences.Editor passwordPre = userPref.edit();
                        passwordPre.putString("password", password);
                        passwordPre.commit();

                        SharedPreferences.Editor username = userPref.edit();
                        username.putString("username", userDetilObj.getString("username").toString());
                        username.commit();

                        SharedPreferences.Editor mobile = userPref.edit();
                        mobile.putString("mobile", userDetilObj.getString("mobile").toString());
                        mobile.commit();

                        SharedPreferences.Editor email = userPref.edit();
                        email.putString("email", userDetilObj.getString("email").toString());
                        email.commit();

                        SharedPreferences.Editor isLogin = userPref.edit();
                        isLogin.putString("isLogin", "1");
                        isLogin.commit();

                        SharedPreferences.Editor userImage = userPref.edit();
                        userImage.putString("userImage", userDetilObj.getString("image").toString());
                        userImage.commit();

                        SharedPreferences.Editor dob = userPref.edit();
                        dob.putString("date_of_birth", userDetilObj.getString("dob").toString());
                        dob.commit();

                        SharedPreferences.Editor gender = userPref.edit();
                        gender.putString("gender", userDetilObj.getString("gender").toString());
                        gender.commit();

//                        if (!activityName.equals("SplashScreen")) {
//                            Common.showMkSucess(activity, resObj.getString("message"),"no");
//                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent hi = new Intent(activity, HomeActivity.class);
                                hi.putExtra("PickupLatitude", PickupLatitude);
                                hi.putExtra("PickupLongtude", PickupLongtude);
                                activity.startActivity(hi);
                                activity.finish();
                            }
                        }, 2000);
                    }else if(resObj.getString("status").equals("failed")){
                        Common.LoginMkError(activity, resObj.getString("error code"), resObj.getString("code"));
                        if (activityName.equals("SplashScreen")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent hi = new Intent(activity, LoginActivity.class);
                                    activity.startActivity(hi);
                                    activity.finish();
                                }
                            }, 2000);
                        }
                    }else if(resObj.getString("status").equals("false")){
                        Log.d("Result","Result failed"+resObj.getString("status"));
                        if(resObj.getString("Isactive").equals("Inactive")){

                            //Common.showLoginRegisterMkError(activity, resObj.getString("message"));
                            Common.user_InActive = 1;
                            Common.InActive_msg = resObj.getString("message");
                            //if (activityName.equals("SplashScreen")) {
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.clear();
                            editor.commit();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent logInt = new Intent(activity, LoginOptionActivity.class);
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(logInt);
                                }
                            }, 500);
                            //}
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(ProgressDialog != null) {
                cusRotateLoading.stop();
                ProgressDialog.cancel();
            }
            result = content;
            boolean isStatus = Common.ShowHttpErrorMessage(activity,result);
            Log.d("LoginUrl", "LoginUrl result= " + result+"=="+isStatus);
            if(isStatus) {
                try {
                    Log.d("loginUrl", "loginUrl two= " + result);
                    final JSONObject resObj = new JSONObject(result);
                    Log.d("loginUrl", "loginUrl two= " + resObj);
                    if (ProgressDialog != null) {
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                    }
                    Log.d("loginUrl Status","loginUrl Status"+resObj.getString("status"));

                    if (resObj.getString("status").equals("success")) {

                        JSONArray cabDtlAry = new JSONArray(resObj.getString("cabDetails"));
                        Common.CabDetail = cabDtlAry;

                        /*set Start Currency*/

                        JSONArray currencyArray = new JSONArray(resObj.getString("country_detail"));
                        for (int ci = 0; ci < currencyArray.length(); ci++) {
                            JSONObject startEndTimeObj = currencyArray.getJSONObject(ci);
                            Common.Currency = startEndTimeObj.getString("currency");
                            Common.Country = startEndTimeObj.getString("country");
                        }

                /*set Start And End Time*/
                        JSONArray startEndTimeArray = new JSONArray(resObj.getString("time_detail"));
                        for (int si = 0; si < startEndTimeArray.length(); si++) {
                            JSONObject startEndTimeObj = startEndTimeArray.getJSONObject(si);
                            Common.StartDayTime = startEndTimeObj.getString("day_start_time");
                            Common.EndDayTime = startEndTimeObj.getString("day_end_time");
                        }

                /*User Detail*/
                        JSONObject userDetilObj = new JSONObject(resObj.getString("userdetail"));

                        SharedPreferences.Editor id = userPref.edit();
                        id.putString("id", userDetilObj.getString("id").toString());
                        id.commit();

                        SharedPreferences.Editor name = userPref.edit();
                        name.putString("name", userDetilObj.getString("name").toString());
                        name.commit();

                        SharedPreferences.Editor passwordPre = userPref.edit();
                        passwordPre.putString("password", password);
                        passwordPre.commit();

                        SharedPreferences.Editor username = userPref.edit();
                        username.putString("username", userDetilObj.getString("username").toString());
                        username.commit();

                        SharedPreferences.Editor mobile = userPref.edit();
                        mobile.putString("mobile", userDetilObj.getString("mobile").toString());
                        mobile.commit();

                        SharedPreferences.Editor email = userPref.edit();
                        email.putString("email", userDetilObj.getString("email").toString());
                        email.commit();

                        SharedPreferences.Editor isLogin = userPref.edit();
                        isLogin.putString("isLogin", "1");
                        isLogin.commit();

                        SharedPreferences.Editor userImage = userPref.edit();
                        userImage.putString("userImage", userDetilObj.getString("image").toString());
                        userImage.commit();

                        SharedPreferences.Editor dob = userPref.edit();
                        dob.putString("date_of_birth", userDetilObj.getString("dob").toString());
                        dob.commit();

                        SharedPreferences.Editor gender = userPref.edit();
                        gender.putString("gender", userDetilObj.getString("gender").toString());
                        gender.commit();

//                        if (!activityName.equals("SplashScreen")) {
//                            Common.showMkSucess(activity, resObj.getString("message"),"no");
//                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent hi = new Intent(activity, HomeActivity.class);
                                hi.putExtra("PickupLatitude", PickupLatitude);
                                hi.putExtra("PickupLongtude", PickupLongtude);
                                activity.startActivity(hi);
                                activity.finish();
                            }
                        }, 2000);
                    }else if(resObj.getString("status").equals("failed")){
                        Common.LoginMkError(activity, resObj.getString("error code"), resObj.getString("code"));
                        if (activityName.equals("SplashScreen")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent hi = new Intent(activity, LoginActivity.class);
                                    activity.startActivity(hi);
                                    activity.finish();
                                }
                            }, 2000);
                        }
                    }else if(resObj.getString("status").equals("false")){
                        Log.d("Result","Result failed"+resObj.getString("status"));
                        if(resObj.getString("Isactive").equals("Inactive")){

                            //Common.showLoginRegisterMkError(activity, resObj.getString("message"));
                            Common.user_InActive = 1;
                            Common.InActive_msg = resObj.getString("message");
                            //if (activityName.equals("SplashScreen")) {
                                SharedPreferences.Editor editor = userPref.edit();
                                editor.clear();
                                editor.commit();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent logInt = new Intent(activity, LoginOptionActivity.class);
                                        logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        activity.startActivity(logInt);
                                    }
                                }, 500);
                            //}
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void SlideMenuDesign(final SlidingMenu slidingMenu, final Activity activity, final String clickMenu) {

        final SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(activity);

        Typeface Roboto_Regular =Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular_0.ttf");
        Typeface Roboto_Bold =Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Bold.ttf");

        TextView txt_user_name = (TextView)slidingMenu.findViewById(R.id.txt_user_name);
        txt_user_name.setTypeface(Roboto_Regular);
        txt_user_name.setText(userPref.getString("username",""));
        TextView txt_user_number = (TextView)slidingMenu.findViewById(R.id.txt_user_number);
        txt_user_number.setTypeface(Roboto_Regular);
        txt_user_number.setText(userPref.getString("mobile", ""));

        TextView txt_book_my_trip = (TextView)slidingMenu.findViewById(R.id.txt_book_my_trip);
        txt_book_my_trip.setTypeface(Roboto_Bold);
        TextView txt_my_trip = (TextView)slidingMenu.findViewById(R.id.txt_my_trip);
        txt_my_trip.setTypeface(Roboto_Bold);
        TextView txt_rate_card = (TextView)slidingMenu.findViewById(R.id.txt_rate_card);
        txt_rate_card.setTypeface(Roboto_Bold);
        TextView txt_cahnge_password = (TextView)slidingMenu.findViewById(R.id.txt_cahnge_password);
        txt_cahnge_password.setTypeface(Roboto_Bold);
        TextView txt_sign_out = (TextView)slidingMenu.findViewById(R.id.txt_sign_out);
        txt_sign_out.setTypeface(Roboto_Bold);

        layout_book_my_trip = (RelativeLayout)slidingMenu.findViewById(R.id.layout_book_my_trip);
        layout_my_trip = (RelativeLayout)slidingMenu.findViewById(R.id.layout_my_trip);
        layout_rate_card = (RelativeLayout)slidingMenu.findViewById(R.id.layout_rate_card);
        layout_cahnge_password = (RelativeLayout)slidingMenu.findViewById(R.id.layout_cahnge_password);
        layout_footer_logout = (RelativeLayout)slidingMenu.findViewById(R.id.layout_footer_logout);

        RelativeLayout layout_user = (RelativeLayout)slidingMenu.findViewById(R.id.layout_user);
        if(Common.ActiveActivity.equals("book my trips")){
            layout_book_my_trip.setBackgroundResource(R.drawable.active_opt_bg);
        }
        layout_book_my_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();

                layout_my_trip.setBackgroundResource(0);
                layout_my_trip.setBackgroundResource(0);
                layout_cahnge_password.setBackgroundResource(0);
                Common.ActiveActivity = "book my trips";
                if(!clickMenu.equals("home")) {
                    Intent mi = new Intent(activity, HomeActivity.class);
                    activity.startActivity(mi);
                    activity.finish();
                }

            }
        });

        if(Common.ActiveActivity.equals("my trips")){
            layout_my_trip.setBackgroundResource(R.drawable.active_opt_bg);
        }
        layout_my_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
                layout_book_my_trip.setBackgroundResource(0);
                layout_rate_card.setBackgroundResource(0);
                layout_cahnge_password.setBackgroundResource(0);
                Common.ActiveActivity = "my trips";
                if(!clickMenu.equals("all trip")) {
                    Intent mi = new Intent(activity, AllTripActivity.class);
                    activity.startActivity(mi);
                    activity.finish();
                }
            }
        });

        if(Common.ActiveActivity.equals("rate card")){
            layout_rate_card.setBackgroundResource(R.drawable.active_opt_bg);
        }
        layout_rate_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Common.ActiveActivity = "rate card";
                slidingMenu.toggle();
                layout_my_trip.setBackgroundResource(0);
                layout_book_my_trip.setBackgroundResource(0);
                layout_cahnge_password.setBackgroundResource(0);

                if(!clickMenu.equals("rate card")) {
                    Intent ri = new Intent(activity, RateCardActivity.class);
                    activity.startActivity(ri);
                    activity.finish();
                }
            }
        });

        if(Common.ActiveActivity.equals("change password")){
            layout_cahnge_password.setBackgroundResource(R.drawable.active_opt_bg);
        }
        layout_cahnge_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
                Common.ActiveActivity = "change password";
                layout_rate_card.setBackgroundResource(0);
                layout_my_trip.setBackgroundResource(0);
                layout_book_my_trip.setBackgroundResource(0);

                if(!clickMenu.equals("change password")) {
                    Intent mi = new Intent(activity, ChangePasswordActivity.class);
                    activity.startActivity(mi);
                    activity.finish();
                }
            }
        });


        layout_footer_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();

                new AlertDialog.Builder(activity)
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.clear();
                            editor.commit();

                            Intent logInt = new Intent(activity, LoginOptionActivity.class);
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(logInt);

                        }
                    })
                    .setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            }
        });

        layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
                Intent mi = new Intent(activity, UserProfileActivity.class);
                activity.startActivity(mi);
            }
        });

        ImageView img_user = (ImageView)slidingMenu.findViewById(R.id.img_user);
        String facebook_id = userPref.getString("facebook_id","");
        Log.d("facebook_id","facebook_id = "+facebook_id);
        if(facebook_id != null && !facebook_id.equals("") && userPref.getString("userImage", "").equals("")) {
            String facebookImage = Url.FacebookImgUrl + facebook_id + "/picture?type=large";
            Log.d("facebookImage","facebookImage = "+facebookImage);
            Picasso.with(activity)
                    .load(facebookImage)
                    .placeholder(R.drawable.avatar_placeholder)
                    .resize(200, 200)
                    .transform(new  CircleTransform())
                    .into(img_user);
        }else {
            Log.d("userImage","user Image = "+Url.userImageUrl+userPref.getString("userImage",""));
            Picasso.with(activity)
                    .load(Uri.parse(Url.userImageUrl + userPref.getString("userImage", "")))
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.mail_defoult)
                    .into(img_user);
        }


    }


    public static Float getTotalPrice(String intailrate,float FirstKm,Float distance,String fromintailrate,String ride_time_rate,int totalTime){
        Float totlePrice;
        Float firstPrice = Float.parseFloat(intailrate);
        Float secoundPrice = null;
        Log.d("fromintailrate","fromintailrate FirstKm= "+FirstKm+"=="+distance);
        if(FirstKm < distance) {
            Float afterkm = distance - FirstKm;
            Log.d("fromintailrate","fromintailrate distance= "+fromintailrate+"=="+afterkm);
            if(fromintailrate.equals(""))
                fromintailrate = "0";
            secoundPrice = Float.parseFloat(fromintailrate) * afterkm;
            Log.d("total price","total price = "+distance+"=="+FirstKm+"=="+afterkm);
        }

        Log.d("totalTime","totalTime = "+totalTime+"=="+ride_time_rate);
        float driverprice = Float.parseFloat(ride_time_rate) * totalTime;

        if(secoundPrice != null)
            totlePrice = firstPrice+secoundPrice+driverprice;
        else
            totlePrice = firstPrice+driverprice;

        return totlePrice;
    }

    public static int getDisplayHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

//    public static void HintFunction(final Activity activity, final EditText editText, final String HitMessage){
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus)
//                {
//                    if(editText.getText().length() == 0)
//                        Common.showMkHitMessage(activity, HitMessage);
//                }
//            }
//        });
//    }

    public static void showMkHitMessage(final Activity act,String message)
    {
        if(!act.isFinishing()){

            Animation slideUpAnimation;

            final Dialog MKInfoPanelDialog = new Dialog(act,android.R.style.Theme_Translucent_NoTitleBar);

            MKInfoPanelDialog.setContentView(R.layout.mkinfopanel);
            MKInfoPanelDialog.show();
            slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(),
                    R.anim.slide_up_map);

            RelativeLayout layout_info_panel = (RelativeLayout) MKInfoPanelDialog.findViewById(R.id.layout_info_panel);
            layout_info_panel.startAnimation(slideUpAnimation);

            layout_info_panel.setBackgroundResource(R.color.yellow);

            TextView subtitle = (TextView)MKInfoPanelDialog.findViewById(R.id.subtitle);
            subtitle.setText(message);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(MKInfoPanelDialog.isShowing() && !act.isFinishing())
                            MKInfoPanelDialog.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);

        }
    }

    public static class CallUnSubscribeTaken extends AsyncTask<String, Void, String > {


        private SharedPreferences userPref;
        String DeviceToken;
        Activity activity;
        public CallUnSubscribeTaken(Activity activity,String dt){
            DeviceToken = dt;
            userPref = PreferenceManager.getDefaultSharedPreferences(activity);
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... args) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 10000);
            HttpConnectionParams.setSoTimeout(myParams, 10000);

            JSONObject JSONResponse = null;
            InputStream contentStream = null;
            String resultString = "";

            try {

                JSONObject passObj = new JSONObject();
                passObj.put("user","u_"+userPref.getString("id",""));
                passObj.put("type","android");
                passObj.put("token",DeviceToken);

                Log.d("passObj","response passObj = "+passObj);

                HttpPost httppost = new HttpPost(Url.unsubscribeUrl);
                httppost.setHeader("Content-Type", "application/json");
                httppost.setHeader("Accept", "application/json");

//                StringEntity se = new StringEntity(passObj.toString());
//                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                httppost.setEntity(se);
                httppost.setEntity(new StringEntity(passObj.toString(), HTTP.UTF_8));

                for (int i=0;i<httppost.getAllHeaders().length;i++) {
                    Log.v("set header", httppost.getAllHeaders()[i].getValue());
                }

                HttpResponse response = httpclient.execute(httppost);

                // Do some checks to make sure that the request was processed properly
                Header[] headers = response.getAllHeaders();
                HttpEntity entity = response.getEntity();
                contentStream = entity.getContent();

                Log.d("response","response = "+response.toString()+"=="+entity+"=="+contentStream);
                resultString = response.toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d("Error","response Error one = "+e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Error", "response Error two = " + e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Error", "response Error three = " + e.getMessage());
            }


            return resultString;
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(activity,"sucess = "+result,Toast.LENGTH_LONG).show();

            if(result.contains("HTTP/1.1 200 OK")){
                new Common.CallDeviceTaken(activity,Common.device_token).execute();

            }
        }

    }

    public static class CallDeviceTaken extends AsyncTask<String, Void, String > {


        private SharedPreferences userPref;
        String DeviceToken;
        public CallDeviceTaken(Activity activity,String dt){
            DeviceToken = dt;
            userPref = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        @Override
        protected String doInBackground(String... args) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpParams myParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(myParams, 10000);
            HttpConnectionParams.setSoTimeout(myParams, 10000);

            JSONObject JSONResponse = null;
            InputStream contentStream = null;
            String resultString = "";

            try {

                JSONObject passObj = new JSONObject();
                passObj.put("user","u_"+userPref.getString("id",""));
                passObj.put("type","android");
                passObj.put("token",DeviceToken);

                Log.d("passObj","response passObj = "+passObj);

                HttpPost httppost = new HttpPost(Url.subscribeUrl);
                httppost.setHeader("Content-Type", "application/json");
                httppost.setHeader("Accept", "application/json");

//                StringEntity se = new StringEntity(passObj.toString());
//                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                httppost.setEntity(se);
                httppost.setEntity(new StringEntity(passObj.toString(), HTTP.UTF_8));

                for (int i=0;i<httppost.getAllHeaders().length;i++) {
                    Log.v("set header", httppost.getAllHeaders()[i].getValue());
                }

                HttpResponse response = httpclient.execute(httppost);

                // Do some checks to make sure that the request was processed properly
                Header[] headers = response.getAllHeaders();
                HttpEntity entity = response.getEntity();
                contentStream = entity.getContent();

                Log.d("response","response = "+response.toString()+"=="+entity+"=="+contentStream);
                resultString = response.toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d("Error","response Error one = "+e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Error", "response Error two = " + e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Error", "response Error three = " + e.getMessage());
            }


            return resultString;
        }

        @Override
        protected void onPostExecute(String result) {

            if(result.contains("HTTP/1.1 200 OK")){
                SharedPreferences.Editor isDeviceToken = userPref.edit();
                isDeviceToken.putString("id_device_token", "1");
                isDeviceToken.commit();
            }
        }
    }

    public static void showMKPanelError(final Activity act, String message, final RelativeLayout rlMainView, TextView tvTitle, Typeface typeface) {
        if (!act.isFinishing() && (rlMainView.getVisibility() == View.GONE)) {

            Log.d("rlMainView", "rlMainView = " + rlMainView.getVisibility() + "==" + View.GONE);
            if ((rlMainView.getVisibility() == View.GONE)) {
                rlMainView.setVisibility(View.VISIBLE);
            }

            rlMainView.setBackgroundResource(R.color.dialog_error_color);
            tvTitle.setText(message);

            tvTitle.setTypeface(typeface);
            Animation slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(), R.anim.slide_up_map);
            rlMainView.startAnimation(slideUpAnimation);

        }
    }

    public static void ValidationGone(final Activity activity, final RelativeLayout rlMainView, EditText edt_reg_username){
        edt_reg_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("charSequence","charSequence = "+charSequence.length()+"=="+rlMainView.getVisibility()+"=="+View.VISIBLE);
                if(charSequence.length() > 0 && rlMainView.getVisibility() == View.VISIBLE){
                    if(!activity.isFinishing()){
                        TranslateAnimation slideUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -100);
                        slideUp.setDuration(10);
                        slideUp.setFillAfter(true);
                        rlMainView.startAnimation(slideUp);
                        slideUp.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                rlMainView.setVisibility(View.GONE);
                            }
                        });

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
