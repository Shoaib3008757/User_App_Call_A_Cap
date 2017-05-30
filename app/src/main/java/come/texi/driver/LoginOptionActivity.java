package come.texi.driver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import come.texi.driver.gpsLocation.GPSTracker;
import come.texi.driver.utils.Common;
import come.texi.driver.utils.Url;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.util.EntityUtils;

import static come.texi.driver.utils.Common.showMKPanelError;

public class LoginOptionActivity extends AppCompatActivity {

    ImageView img_logo,img_background;
    ImageView img_facebook;
    ImageView img_twitter;
    TextView txt_sign_in_with,txt_new_user_signup,txt_signin;
    RelativeLayout layout_option_main,layout_new_user_signup,layout_signin;

    Typeface OpenSans_Regular,regularRoboto,Roboto_Bold;

    CallbackManager callbackManager;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;

    TwitterLoginButton twitterLoginBtn;
    int socialFlg;
    SharedPreferences userPref;

    double PickupLongtude;
    double PickupLatitude;

    //Error Alert
    RelativeLayout rlMainView;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_option);

        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        regularRoboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        GPSTracker gpsTracker = new GPSTracker(LoginOptionActivity.this);
        PickupLatitude = gpsTracker.getLatitude();
        PickupLongtude = gpsTracker.getLongitude();

        //Error Alert
        rlMainView=(RelativeLayout)findViewById(R.id.rlMainView);
        tvTitle=(TextView)findViewById(R.id.tvTitle);

        layout_option_main = (RelativeLayout)findViewById(R.id.layout_option_main);
        img_logo = (ImageView)findViewById(R.id.img_logo);

        img_facebook = (ImageView)findViewById(R.id.img_facebook);
        img_twitter = (ImageView)findViewById(R.id.img_twitter);
        txt_sign_in_with = (TextView)findViewById(R.id.txt_sign_in_with);
        layout_new_user_signup = (RelativeLayout)findViewById(R.id.layout_new_user_signup);
        layout_signin = (RelativeLayout)findViewById(R.id.layout_signin);
        txt_new_user_signup = (TextView)findViewById(R.id.txt_new_user_signup);
        txt_new_user_signup.setTypeface(Roboto_Bold);
        txt_signin = (TextView)findViewById(R.id.txt_signin);
        txt_signin.setTypeface(Roboto_Bold);

        userPref = PreferenceManager.getDefaultSharedPreferences(LoginOptionActivity.this);

        ProgressDialog = new Dialog(LoginOptionActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);

        callbackManager = CallbackManager.Factory.create();



        txt_sign_in_with.setTypeface(OpenSans_Regular);

        Picasso.with(LoginOptionActivity.this)
                .load(R.drawable.facebook_btn)
                .into(img_facebook);


        Picasso.with(LoginOptionActivity.this)
                .load(R.drawable.twitter_btn)
                .into(img_twitter);

        layout_new_user_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent si = new Intent(LoginOptionActivity.this, SignUpActivity.class);
                startActivity(si);

            }
        });

        layout_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent li = new Intent(LoginOptionActivity.this,LoginActivity.class);
                startActivity(li);

            }
        });


        img_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loggedIn = AccessToken.getCurrentAccessToken() != null;

                if(loggedIn){
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    Log.d("object", "object = " + object + "=="+response);

                                    if(object!=null){
                                        //facebook get data
                                        try {
                                            String FbEmail = "";
                                            String FbName = "";
                                            if(object.has("email"))
                                                FbEmail = object.getString("email");
                                            if(object.has("name"))
                                                FbName = object.getString("name");

                                            //CheckFacebookUser(Url.facebookLoginUrl,object.getString("id"),"",FbEmail,FbName);
                                            new CheckFacebookUserHttp(Url.facebookLoginUrl,object.getString("id"),"",FbEmail,FbName).execute();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        Toast.makeText(LoginOptionActivity.this,"Something went wrong",Toast.LENGTH_LONG);
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields","id,name,email");
                    request.setParameters(parameters);
                    request.executeAsync();


                }else{

                    callbackManager = CallbackManager.Factory.create();

                    LoginManager.getInstance().logInWithPublishPermissions(LoginOptionActivity.this, Arrays.asList("publish_actions"));

                    LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {

                            Log.d("loginResult", "loginResult = " + loginResult);
                            // App code
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.d("object", "object = " + object + "==" + response);

                                            if (object != null) {
                                                //facebook get data
                                                //object.getString("id")

                                                try {
                                                    String FbEmail = "";
                                                    String FbName = "";
                                                    if(object.has("email"))
                                                        FbEmail = object.getString("email");
                                                    if(object.has("name"))
                                                        FbName = object.getString("name");

                                                    String FacebookSocialUrl = Url.facebookLoginUrl+"?facebook_id="+object.getString("id");
                                                    //CheckFacebookUser(FacebookSocialUrl,object.getString("id"),"",FbEmail,FbName);
                                                    new CheckFacebookUserHttp(Url.facebookLoginUrl,object.getString("id"),"",FbEmail,FbName).execute();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                Toast.makeText(LoginOptionActivity.this, "Something went wrong", Toast.LENGTH_LONG);
                                            }


                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            Log.d("cancel", "cancel = ");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.d("fb error", "fb error = " + exception.getMessage());
                            System.out.println("exception >>" + exception.getMessage());
                        }
                    });
                }

            }
        });

        img_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isNetworkAvailable(LoginOptionActivity.this)){
                    try{

                        socialFlg = 2;
                        twitterLoginBtn = new TwitterLoginButton(LoginOptionActivity.this);

                        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
                            @Override
                            public void success(Result<TwitterSession> result) {
                       Log.d("twitter", "twitter = " + result.toString());
                            }
                            @Override
                            public void failure(TwitterException exception) {
                                // Do something on failure
                                Log.d("twitter", "twitter erro = " + exception.getMessage());
                            }
                        });

                        TwitterAuthClient authClient = new TwitterAuthClient();
                        authClient.authorize(LoginOptionActivity.this, new Callback<TwitterSession>() {
                            @Override
                            public void success(Result<TwitterSession> twitterSessionResult) {
                                TwitterSession user = twitterSessionResult.data;
                                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                                TwitterAuthToken authToken = session.getAuthToken();

                                String token = authToken.token;
                                String secret = authToken.secret;

                                new CheckFacebookUserHttp(Url.twitterLoginUrl,"",String.valueOf(session.getUserId()),"",session.getUserName()).execute();
//                                TwitterApiClient twitterApiClient = Twitter.getApiClient();
//                                StatusesService twapiclient = twitterApiClient.getStatusesService();
//                                twapiclient.userTimeline(user.getUserId(), null, null, null, null, null, null, null, null, new Callback<List<Tweet>>() {
//                                    @Override
//                                    public void success(Result<List<Tweet>> listResult) {
//
//                                        String twitterPrfImageUrl = listResult.data.get(0).user.profileImageUrl;
//                                        twitterPrfImageUrl = twitterPrfImageUrl.replace("_normal","_bigger");
//
//                                        String twitterUsername="";
//                                        if(!listResult.data.get(0).user.name.equals(""))
//                                            twitterUsername = listResult.data.get(0).user.name;
//
//                                        String twitterId = String.valueOf(listResult.data.get(0).user.id);
//
////                                        RequestParams socialParams = new RequestParams();
////                                        socialParams.put("twitter_id", twitterId);
////
////                                        //String twitterUrl = Url.twitterUrl + "?sign=" + ss.sign + "&salt=" + ss.salt + "&twitter_id=" + listResult.data.get(0).user.id + "&username=" +listResult.data.get(0).user.name+"&device_token="+common.device_token;
////                                        Log.d("Twitter Url","Twitter Url = "+Url.twitterLoginUrl+"?"+socialParams);
////                                        CheckFacebookUser(Url.twitterLoginUrl, socialParams, "", twitterId, "", twitterUsername);
//                                        new CheckFacebookUserHttp(Url.twitterLoginUrl,"",twitterId,"",twitterUsername).execute();
//                                    }
//
//                                    @Override
//                                    public void failure(TwitterException e) {
//
//                                    }
//                                });


                            }
                            @Override
                            public void failure(TwitterException e) {
                                System.out.println("Twitter Auth is failure");
                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Common.showInternetInfo(LoginOptionActivity.this,"");
                }
            }
        });

        Log.d("user_InActive","user_InActive = "+Common.user_InActive);
        if(Common.user_InActive == 1){
            showMKPanelError(LoginOptionActivity.this, getResources().getString(R.string.inactive_user),rlMainView,tvTitle,regularRoboto);
            Common.user_InActive = 0;
        }

        layout_option_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlMainView.getVisibility() == View.VISIBLE){
                    if(!isFinishing()){
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
        });
    }

    class CheckFacebookUserHttp extends AsyncTask<String, Integer, String>{

        HttpEntity entity;
        String SocialUrl;
        String facebook_id;
        String twitter_id;
        String FbEmail;
        String FbName;
        private String content =  null;

        public CheckFacebookUserHttp(String SocialUrl,String f_id, String twitter_id, String FbEmail, String FbName){
            this.SocialUrl = SocialUrl;
            facebook_id = f_id;
            this.twitter_id = twitter_id;
            this.FbEmail = FbEmail;
            this.FbName = FbName;

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            if(!facebook_id.equals(""))
                entityBuilder.addTextBody("facebook_id", facebook_id);
            else if(!twitter_id.equals(""))
                entityBuilder.addTextBody("twitter_id", twitter_id);
            entity = entityBuilder.build();
        }

        protected void onPreExecute() {
            ProgressDialog.show();
            cusRotateLoading.start();
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
            cusRotateLoading.stop();
            ProgressDialog.cancel();

            boolean isStatus = Common.ShowHttpErrorMessage(LoginOptionActivity.this,result);
            if(isStatus) {
                try {
                    JSONObject resObj = new JSONObject(result);
                    Log.d("Social Register resObj", "Social Register resObj = " + resObj);
                    if (resObj.getString("status").equals("failed")) {

                        new AlertDialog.Builder(LoginOptionActivity.this)
                                .setMessage(getResources().getString(R.string.facebook_popup_string))
                                .setPositiveButton(getResources().getString(R.string.register), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent ri = new Intent(LoginOptionActivity.this, SignUpActivity.class);
                                        ri.putExtra("facebook_id", facebook_id);
                                        ri.putExtra("twitter_id", twitter_id);
                                        ri.putExtra("facebook_email", FbEmail);
                                        ri.putExtra("facebook_name", FbName);
                                        startActivity(ri);
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();

                    } else if (resObj.getString("status").equals("success")) {

                        JSONArray CabDetAry = new JSONArray(resObj.getString("cabDetails"));
                        Common.CabDetail = CabDetAry;

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

                        //Common.showMkSucess(LoginOptionActivity.this, resObj.getString("message").toString(), "no");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent hi = new Intent(LoginOptionActivity.this, HomeActivity.class);
                                hi.putExtra("PickupLatitude", PickupLatitude);
                                hi.putExtra("PickupLongtude", PickupLongtude);
                                startActivity(hi);
                                finish();
                            }
                        }, 500);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void CheckFacebookUser(String SocialUrl,final String facebook_id, final String twitter_id, final String FbEmail, final String FbName){

        ProgressDialog.show();
        cusRotateLoading.start();
        Log.d("Social Url ", "Social Url = " + SocialUrl + "?facebook_id=" + facebook_id);
        Ion.with(LoginOptionActivity.this)
            .load(SocialUrl)
            .setTimeout(10000)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception error, JsonObject result) {
                    // do stuff with the result or error

                    if (error == null) {

                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                        Log.d("Social Register resObj", "Social Register resObj = " + result.toString());
                        try {
                            JSONObject resObj = new JSONObject(result.toString());
                            Log.d("Social Register resObj", "Social Register resObj = " + resObj);
                            if (resObj.getString("status").equals("failed")) {

                                new AlertDialog.Builder(LoginOptionActivity.this)
                                        .setMessage(getResources().getString(R.string.facebook_popup_string))
                                        .setPositiveButton(getResources().getString(R.string.register), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent ri = new Intent(LoginOptionActivity.this, SignUpActivity.class);
                                                ri.putExtra("facebook_id", facebook_id);
                                                ri.putExtra("twitter_id", twitter_id);
                                                ri.putExtra("facebook_email", FbEmail);
                                                ri.putExtra("facebook_name", FbName);
                                                startActivity(ri);
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .show();

                            } else if (resObj.getString("status").equals("success")) {

                                Common.showMkSucess(LoginOptionActivity.this, resObj.getString("message").toString(), "yes");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent hi = new Intent(LoginOptionActivity.this, HomeActivity.class);
                                        startActivity(hi);
                                        finish();
                                    }
                                }, 2000);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();

                        Common.ShowHttpErrorMessage(LoginOptionActivity.this, error.getMessage());
                    }
                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(socialFlg == 2) {
            Log.d("requestCode","requestCode = "+requestCode);
            twitterLoginBtn.onActivityResult(requestCode, resultCode, data);
            socialFlg = 0;
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

}
