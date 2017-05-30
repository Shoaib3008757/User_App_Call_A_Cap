package come.texi.driver;

import android.animation.StateListAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import come.texi.driver.utils.Common;

import come.texi.driver.utils.RegistrationIntentService;
import come.texi.driver.utils.Url;

import static come.texi.driver.utils.Common.isNetworkAvailable;
import static come.texi.driver.utils.Common.showInternetInfo;

public class SplashActivity extends AppCompatActivity {

    ImageView img_location;

    SharedPreferences userPref;

    Common common = new Common();

    TranslateAnimation translation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);


        userPref = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);


        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        //boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        Log.d("gps_enabled", "gps_enabled = " + gps_enabled);
        if(!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
            dialog.setTitle("Improve location accurancy?");
            dialog.setMessage("This app wants to change your device setting:");
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(myIntent, 1);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        }else{




                    if(userPref.getString("isLogin","").equals("1")){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (isNetworkAvailable(SplashActivity.this)) {

                                    if(!userPref.getString("facebook_id", "").equals("") || !userPref.getString("twitter_id", "").equals("")){
                                        Log.d("facebook id","facebook id = "+userPref.getString("facebook_id", ""));

                                        String SocialUrl = "";
                                        if(!userPref.getString("facebook_id", "").equals("")) {
                                                SocialUrl = Url.facebookLoginUrl;
                                        }else if(!userPref.getString("twitter_id", "").equals("")) {
                                            SocialUrl = Url.twitterLoginUrl;
                                        }

                                        new Common.LoginSocialUserHttp(SocialUrl,userPref.getString("facebook_id", ""),userPref.getString("twitter_id", ""),SplashActivity.this).execute();
                                    }else {
                                        String loginUrl = null;
                                        try {
                                            loginUrl = Url.loginUrl + "?email=" + URLEncoder.encode(userPref.getString("email", ""), "UTF-8") + "&password=" + userPref.getString("password", "");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }

                                        Log.d("loginUrl", "loginUrl " + loginUrl);

                                        new Common.LoginCallHttp(SplashActivity.this, null, null, userPref.getString("password", ""), "SplashScreen", loginUrl).execute();
                                    }
                                } else {
                                    showInternetInfo(SplashActivity.this, "");
                               }

                            }
                        }, 100);

                    }else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SplashActivity.this, LoginOptionActivity.class));
                                finish();
                            }
                        }, 2500);
                    }
                }
            }

    public int getDisplayHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if (isNetworkAvailable(SplashActivity.this)) {

                String loginUrl = null;
                try {
                    loginUrl = Url.loginUrl+"?email="+ URLEncoder.encode(userPref.getString("email", ""), "UTF-8")+"&password="+userPref.getString("password", "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("loginUrl", "loginUrl " + loginUrl);
                new Common.LoginCallHttp(SplashActivity.this, null, null, userPref.getString("password", ""), "SplashScreen", loginUrl).execute();
            } else {
                showInternetInfo(SplashActivity.this, "");
            }
        }
    }
}
