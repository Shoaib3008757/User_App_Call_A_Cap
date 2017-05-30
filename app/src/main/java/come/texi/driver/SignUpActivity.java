package come.texi.driver;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import come.texi.driver.gpsLocation.GPSTracker;
import come.texi.driver.utils.CircleTransform;
import come.texi.driver.utils.Common;
import come.texi.driver.utils.Url;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.util.EntityUtils;

import static come.texi.driver.utils.Common.showMKPanelError;

public class SignUpActivity extends AppCompatActivity {

    EditText edit_username;
    EditText edit_name;
    EditText edit_mobile;
    EditText edit_email;
    EditText edit_password;
    EditText edit_com_password;
    RelativeLayout layout_signup;
    ImageView img_add_image;
    EditText edit_date_of_birth;
    Spinner spinner_gender;
    RelativeLayout layout_show_hide;
    TextView txt_hide_show;
    RelativeLayout layout_info_panel;
    TextView subtitle,txt_sign_up_logo,txt_signup;
    ScrollView profile_scrollview;

    Typeface OpenSans_Regular,regularRoboto,Roboto_Bold;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;

    String facebook_id = "";
    String twitter_id = "";
    String facebook_email = "";
    String facebook_name = "";
    String genderString = "gender";

    Dialog OpenCameraDialog;

    private Uri mCapturedImageURI;

    public static int REQUEST_CAMERA = 1;
    public static int REQUEST_GALLERY = 2;
    File userImage;
    SharedPreferences userPref;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    double PickupLongtude;
    double PickupLatitude;

    boolean passwordShowHide = false;
    Pattern letter;

    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

    boolean newFocuValidation = false;

    //Error Alert
    RelativeLayout rlMainView;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().setBackgroundDrawableResource(R.drawable.background);

        Log.d("Locale","Locale Language = "+Locale.getDefault().getLanguage());
        if(Locale.getDefault().getLanguage().equals("en")){
            letter = Pattern.compile("[a-zA-z]");
            Log.d("Locale","Locale Language one= "+Locale.getDefault().getLanguage());
        }else{
            letter = Pattern.compile("[^x00-x7F]");
        }

        profile_scrollview = (ScrollView)findViewById(R.id.profile_scrollview);
        edit_username = (EditText)findViewById(R.id.edit_username);
        edit_name = (EditText)findViewById(R.id.edit_name);
        edit_mobile = (EditText)findViewById(R.id.edit_mobile);
        edit_email = (EditText)findViewById(R.id.edit_email);
        edit_password = (EditText)findViewById(R.id.edit_password);
        edit_com_password = (EditText)findViewById(R.id.edit_com_password);
        layout_signup = (RelativeLayout) findViewById(R.id.layout_signup);
        img_add_image = (ImageView)findViewById(R.id.img_add_image);
        edit_date_of_birth = (EditText)findViewById(R.id.edit_date_of_birth);
        spinner_gender = (Spinner)findViewById(R.id.spinner_gender);
        layout_show_hide = (RelativeLayout)findViewById(R.id.layout_show_hide);
        txt_hide_show = (TextView)findViewById(R.id.txt_hide_show);
        layout_info_panel = (RelativeLayout)findViewById(R.id.layout_info_panel);
        subtitle = (TextView)findViewById(R.id.subtitle);
        txt_sign_up_logo = (TextView)findViewById(R.id.txt_sign_up_logo);
        txt_signup = (TextView)findViewById(R.id.txt_signup);

        //Error Alert
        rlMainView=(RelativeLayout)findViewById(R.id.rlMainView);
        tvTitle=(TextView)findViewById(R.id.tvTitle);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        GPSTracker gpsTracker = new GPSTracker(SignUpActivity.this);
        PickupLatitude = gpsTracker.getLatitude();
        PickupLongtude = gpsTracker.getLongitude();

        userPref = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);

        ProgressDialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);

        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        regularRoboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        edit_name.setTypeface(OpenSans_Regular);
        edit_username.setTypeface(OpenSans_Regular);
        edit_mobile.setTypeface(OpenSans_Regular);
        edit_email.setTypeface(OpenSans_Regular);
        edit_password.setTypeface(OpenSans_Regular);
        edit_com_password.setTypeface(OpenSans_Regular);
        edit_date_of_birth.setTypeface(OpenSans_Regular);
        subtitle.setTypeface(OpenSans_Regular);
        txt_sign_up_logo.setTypeface(Roboto_Bold);
        txt_signup.setTypeface(Roboto_Bold);

        facebook_id = getIntent().getStringExtra("facebook_id");
        facebook_email = getIntent().getStringExtra("facebook_email");
        facebook_name = getIntent().getStringExtra("facebook_name");
        twitter_id = getIntent().getStringExtra("twitter_id");

        edit_name.setText(facebook_name);
        edit_email.setText(facebook_email);

        //showMkHitMessage(edit_name, getResources().getString(R.string.allow_minimum_four_characters));

        //getResources().getString(R.string.please_enter_name)
//        edit_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//            Log.d("hasFocus", "hasFocus password= " + hasFocus+"=="+edit_name.getText().toString().length());
//                if (!hasFocus) {
//                    boolean EditValidation = false;
//
//                    if(!EditValidation){
//                        newFocuValidation = false;
//                    }
//                }
//            }
//        });

        edit_name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EdittextActonListner(edit_name,"name");

        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    edit_name.removeTextChangedListener(this);
                    // change the text

                    String text = s.toString();
                    String upperString = "";
                    if(s.length() != 0)
                        upperString = text.substring(0, 1).toUpperCase()+ text.substring(1,text.length());

                    Log.d("upperString","upperString = "+upperString.toString());
                    edit_name.setText(upperString);
                    // enable it again
                    edit_name.addTextChangedListener(this);
                    edit_name.setSelection(edit_name.getText().length());
                }
            }
        });

        EdittextActonListner(edit_username, "username");

        EdittextActonListner(edit_mobile,"mobile");

        EdittextActonListner(edit_email,"email");


        edit_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("hasFocus", "hasFocus password= " + hasFocus);
                if (hasFocus) {

//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.showSoftInput(edit_password, InputMethodManager.SHOW_IMPLICIT);
//                        }
//                    }, 500);

                    subtitle.setText("");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LayoutEnimation(R.color.dialog_hit_color, getResources().getString(R.string.password_valid));
                        }
                    }, 100);

                }
            }
        });
        EdittextActonListner(edit_password,"password");

        EdittextActonListner(edit_com_password,"confirm_password");

        layout_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isvalid = ValidationRegister();
                if (isvalid) {
                    if (Common.isNetworkAvailable(SignUpActivity.this)) {
                        new SighUpUserHttp().execute();
                    } else {
                        Common.showInternetInfo(SignUpActivity.this, "");
                    }
                }

            }
        });
        Log.d("facebook_id","facebook_id = "+facebook_id);
        if(facebook_id != null && !facebook_id.equals("")) {
            String facebookImage = Url.FacebookImgUrl + facebook_id + "/picture?type=large";
            Log.d("facebookImage","facebookImage = "+facebookImage);
            Picasso.with(SignUpActivity.this)
                    .load(facebookImage)
                    .placeholder(R.drawable.avatar_placeholder)
                    .resize(200, 200)
                    .transform(new  CircleTransform())
                    .into(img_add_image);
        }

        img_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCameraDialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                OpenCameraDialog.setContentView(R.layout.camera_dialog_layout);

                RelativeLayout layout_open_camera = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_camera);
                layout_open_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenCameraDialog.cancel();
                        Intent ci = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mCapturedImageURI = getImageUri();
                        ci.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                        startActivityForResult(ci, REQUEST_CAMERA);
                    }
                });

                RelativeLayout layout_open_gallery = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_gallery);
                layout_open_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenCameraDialog.cancel();
                        Intent gi = new Intent(Intent.ACTION_PICK);
                        gi.setType("image/*");
                        startActivityForResult(gi, REQUEST_GALLERY);
                    }
                });

                RelativeLayout layout_open_cancel = (RelativeLayout) OpenCameraDialog.findViewById(R.id.layout_open_cancel);
                layout_open_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenCameraDialog.cancel();
                    }
                });

                OpenCameraDialog.show();
            }
        });

        myCalendar = Calendar.getInstance();


        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                edit_date_of_birth.setText(sdf.format(myCalendar.getTime()));

                spinner_gender.performClick();
            }

        };

        edit_date_of_birth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                    Calendar minCal = Calendar.getInstance();
                    minCal.add(Calendar.YEAR, -100);
                    long hundredYearsAgo = minCal.getTimeInMillis();
                    dpd.getDatePicker().setMinDate(hundredYearsAgo);
                    dpd.show();
                }
            }
        });




        edit_date_of_birth.setInputType(InputType.TYPE_NULL);
        edit_date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        List<String> list = new ArrayList<String>();
        list.add("Gender");
        list.add("Male");
        list.add("Female");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.gender_spinner_layout);
        spinner_gender.setAdapter(dataAdapter);

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                ((TextView) parent.getChildAt(0)).setTypeface(OpenSans_Regular);
                genderString = parent.getItemAtPosition(position).toString();
                Log.d("genderString", "genderString = " + genderString);
//                if (!genderString.equals("Gender"))
//                    edit_password.requestFocus();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        layout_show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordShowHide) {
                    txt_hide_show.setText(getResources().getString(R.string.show));
                    edit_password.setTransformationMethod(new PasswordTransformationMethod());
                    passwordShowHide = false;
                } else {
                    edit_password.setTransformationMethod(new HideReturnsTransformationMethod());
                    txt_hide_show.setText(getResources().getString(R.string.hide));
                    passwordShowHide = true;
                }
                edit_password.setSelection(edit_password.getText().length());

            }
        });

        profile_scrollview.setOnClickListener(new View.OnClickListener() {
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

        Common.ValidationGone(SignUpActivity.this,rlMainView,edit_name);
        Common.ValidationGone(SignUpActivity.this,rlMainView,edit_username);
        Common.ValidationGone(SignUpActivity.this,rlMainView,edit_password);
        Common.ValidationGone(SignUpActivity.this,rlMainView,edit_com_password);
        Common.ValidationGone(SignUpActivity.this,rlMainView,edit_mobile);
        Common.ValidationGone(SignUpActivity.this,rlMainView,edit_email);
        Common.ValidationGone(SignUpActivity.this,rlMainView,edit_date_of_birth);

    }

    public Uri getImageUri(){
        File file1 = new File(Environment.getExternalStorageDirectory() + "/Naqil");
        if (!file1.exists())
        {
            file1.mkdirs();
        }
        File file2 = new File(Environment.getExternalStorageDirectory() + "/Naqil/UserImage");
        if (!file2.exists())
        {
            file2.mkdirs();
        }

        File file = new File(Environment.getExternalStorageDirectory() + "/Naqil/UserImage/"+System.currentTimeMillis()+".jpg");

        Uri imgUri = Uri.fromFile(file);

        return imgUri;
    }

    public void EdittextActonListner(final EditText editText, final String EdtTxtName){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    boolean isvalid = ValidationRegister();
                    if (isvalid) {
                        if (Common.isNetworkAvailable(SignUpActivity.this)) {
                            new SighUpUserHttp().execute();
                        } else {
                            Common.showInternetInfo(SignUpActivity.this, "");
                        }
                    }
                    return true;
                }else if(actionId == EditorInfo.IME_ACTION_NEXT){
                    Log.d("Done", "hasFocus Done = " + editText.getText().length() + "==" + EdtTxtName);
                    layout_info_panel.setVisibility(View.GONE);
                    boolean isValidNext = ValidationNextRegister(EdtTxtName);

                    if(!isValidNext){
                        return true;
                    }else{
                        return false;
                    }
                }
                return false;
            }
        });
    }


    public class SighUpUserHttp extends AsyncTask<String, Integer, String>{

        private String content =  null;
        HttpEntity entity;

        protected void onPreExecute() {
            Log.d("Start","start");
            ProgressDialog.show();
            cusRotateLoading.start();

        }

        public SighUpUserHttp(){

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            entityBuilder.addTextBody("name", edit_name.getText().toString());
            entityBuilder.addTextBody("email", edit_email.getText().toString());
            entityBuilder.addTextBody("username", edit_username.getText().toString());
            entityBuilder.addTextBody("mobile", edit_mobile.getText().toString());
            entityBuilder.addTextBody("password", edit_password.getText().toString());
            entityBuilder.addTextBody("isdevice", "1");
            if(facebook_id != null && !facebook_id.equals(""))
                entityBuilder.addTextBody("facebook_id", facebook_id);
            else
                entityBuilder.addTextBody("facebook_id", "");
            if(twitter_id != null && !twitter_id.equals(""))
                entityBuilder.addTextBody("twitter_id", twitter_id);
            else
                entityBuilder.addTextBody("twitter_id", "");
            entityBuilder.addTextBody("dob", edit_date_of_birth.getText().toString());
            entityBuilder.addTextBody("gender", genderString);
            if(userImage != null){
                File userFile = new File(userImage.getPath());
                entityBuilder.addPart("image", new FileBody(userFile));
            }else{
                entityBuilder.addTextBody("image", "");
            }
            entity = entityBuilder.build();
        }

        @Override
        protected String doInBackground(String... params) {

            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpParams HttpParams = client.getParams();
                HttpConnectionParams.setConnectionTimeout(HttpParams, 60 * 60 * 1000);
                HttpConnectionParams.setSoTimeout(HttpParams, 60 * 60 * 1000);

                HttpPost post = new HttpPost(Url.signupUrl);
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
            Log.d("signupUrl", "signupUrl result= " + result);
            boolean isStatus = Common.ShowHttpErrorMessage(SignUpActivity.this,result);
            if(isStatus) {
                try {
                    JSONObject resObj = new JSONObject(new String(result));
                    Log.d("signupUrl", "signupUrl two= " + resObj);
                    if (resObj.getString("status").equals("success")) {

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
                        JSONArray userDetilArray = new JSONArray(resObj.getString("user_Detail"));
                        JSONObject userDetilObj = userDetilArray.getJSONObject(0);

                        SharedPreferences.Editor id = userPref.edit();
                        id.putString("id", userDetilObj.getString("id").toString());
                        id.commit();

                        SharedPreferences.Editor name = userPref.edit();
                        name.putString("name", userDetilObj.getString("name").toString());
                        name.commit();

                        SharedPreferences.Editor passwordPre = userPref.edit();
                        passwordPre.putString("password", edit_password.getText().toString());
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
                        gender.putString("gender", genderString);
                        gender.commit();

                        if(!userDetilObj.getString("facebook_id").toString().equals("")) {
                            SharedPreferences.Editor facebook_id = userPref.edit();
                            facebook_id.putString("facebook_id", userDetilObj.getString("facebook_id").toString());
                            facebook_id.commit();
                        }

                        if(!userDetilObj.getString("twitter_id").toString().equals("")) {
                            SharedPreferences.Editor twitter_id = userPref.edit();
                            twitter_id.putString("twitter_id", userDetilObj.getString("twitter_id").toString());
                            twitter_id.commit();
                        }

                        ProgressDialog.cancel();
                        cusRotateLoading.stop();

                        //Common.showMkSucess(SignUpActivity.this, resObj.getString("message"),"no");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent hi = new Intent(SignUpActivity.this, HomeActivity.class);
                                hi.putExtra("PickupLatitude",PickupLatitude);
                                hi.putExtra("PickupLongtude",PickupLongtude);
                                startActivity(hi);
                                finish();
                            }
                        }, 1000);

//                        RequestParams loginParams = new RequestParams();
//                        loginParams.put("password", edit_password.getText().toString());
//                        loginParams.put("email", edit_email.getText().toString());
//
//                        Common.LoginCall(SignUpActivity.this, loginParams, ProgressDialog, cusRotateLoading, edit_password.getText().toString());

                    } else if (resObj.getString("status").equals("failed")) {
                        Log.d("signupUrl", "signupUrl status = " + resObj.getString("status"));
                        ProgressDialog.cancel();
                        cusRotateLoading.stop();
                        Common.showLoginRegisterMkError(SignUpActivity.this, resObj.getString("message"));
                    }
                } catch (JSONException e) {
                    Log.d("signupUrl", "signupUrl error = " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean ValidationNextRegister(String EditTextString){


        Log.d("Passwors", "Password = " + PasswordValidaton(edit_password.getText().toString()));
        if(EditTextString.equals("name")){
            if(edit_name.getText().toString().trim().length() == 0){
                //layout_info_panel.setVisibility(View.GONE);
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_name),rlMainView,tvTitle,regularRoboto);
                edit_name.requestFocus();
                return false;
            }else if(edit_name.getText().toString().trim().length() < 4){
                //layout_info_panel.setVisibility(View.GONE);
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_min_name),rlMainView,tvTitle,regularRoboto);
                edit_name.requestFocus();
                return false;
            }
        }else if(EditTextString.equals("username")){
            if(edit_username.getText().toString().trim().length() == 0){
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_username),rlMainView,tvTitle,regularRoboto);
                edit_username.requestFocus();
                return false;
            }else if(edit_username.getText().toString().trim().length() < 4){
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_min_isername),rlMainView,tvTitle,regularRoboto);
                edit_username.requestFocus();
                return false;
            }
        }else if(EditTextString.equals("password")){
            if(!PasswordValidaton(edit_password.getText().toString())){
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.password_valid),rlMainView,tvTitle,regularRoboto);
                edit_password.requestFocus();
                return false;
            }else if(edit_password.getText().toString().trim().length() == 0){
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_password),rlMainView,tvTitle,regularRoboto);
                edit_password.requestFocus();
                return false;
            }else if (edit_password.getText().toString().trim().length() < 6 || edit_password.getText().toString().trim().length() > 32) {
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.password_length),rlMainView,tvTitle,regularRoboto);
                edit_password.requestFocus();
                return false;
            }
        }else if(EditTextString.equals("confirm_password")) {
            if (edit_com_password.getText().toString().trim().length() == 0) {
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_confirm_password),rlMainView,tvTitle,regularRoboto);
                edit_com_password.requestFocus();
                return false;
            } else if (!edit_password.getText().toString().equals(edit_com_password.getText().toString())) {
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.password_confirm),rlMainView,tvTitle,regularRoboto);
                edit_com_password.requestFocus();
                return false;
            }
        }else if (EditTextString.equals("mobile")) {
            if (edit_mobile.getText().toString().trim().length() == 0) {
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_mobile),rlMainView,tvTitle,regularRoboto);
                edit_mobile.requestFocus();
                return false;
            }
        }else if (EditTextString.equals("email")) {
            if(edit_email.getText().toString().trim().length() == 0){
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_email),rlMainView,tvTitle,regularRoboto);
                edit_email.requestFocus();
                return false;
            }else if(edit_email.getText().toString().trim().length() != 0 && !isValidEmail(edit_email.getText().toString().trim())){
                showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_valid_email),rlMainView,tvTitle,regularRoboto);
                edit_email.requestFocus();
                return false;
            }
        }
        return true;
    }

    public boolean ValidationRegister(){


        Log.d("Passwors", "Password = " + PasswordValidaton(edit_password.getText().toString()));
        if(edit_name.getText().toString().trim().length() == 0){
            //layout_info_panel.setVisibility(View.GONE);
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_name),rlMainView,tvTitle,regularRoboto);
            edit_name.requestFocus();
            return false;
        }else if(edit_name.getText().toString().trim().length() < 4){
            //layout_info_panel.setVisibility(View.GONE);
            LayoutEnimation(R.color.dialog_error_color, getResources().getString(R.string.please_min_name));
            edit_name.requestFocus();
            return false;
        }else if(edit_username.getText().toString().trim().length() == 0){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_username),rlMainView,tvTitle,regularRoboto);
            edit_username.requestFocus();
            return false;
        }else if(edit_username.getText().toString().trim().length() < 4){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_min_isername),rlMainView,tvTitle,regularRoboto);
            edit_username.requestFocus();
            return false;
        }else if(edit_password.getText().toString().trim().length() == 0){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_password),rlMainView,tvTitle,regularRoboto);
            edit_password.requestFocus();
            return false;
        }else if(!PasswordValidaton(edit_password.getText().toString())){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.password_valid),rlMainView,tvTitle,regularRoboto);
            edit_password.requestFocus();
            return false;
        }else if (edit_password.getText().toString().trim().length() < 6 || edit_password.getText().toString().trim().length() > 32) {
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.password_length),rlMainView,tvTitle,regularRoboto);
            edit_password.requestFocus();
            return false;
        }else if(edit_com_password.getText().toString().trim().length() == 0){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_confirm_password),rlMainView,tvTitle,regularRoboto);
            edit_com_password.requestFocus();
            return false;
        }else if(!edit_password.getText().toString().equals(edit_com_password.getText().toString())){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.password_confirm),rlMainView,tvTitle,regularRoboto);
            edit_com_password.requestFocus();
            return false;
        }else if(edit_mobile.getText().toString().trim().length() == 0){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_mobile),rlMainView,tvTitle,regularRoboto);
            edit_mobile.requestFocus();
            return false;
        }else if(edit_email.getText().toString().trim().length() == 0){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_email),rlMainView,tvTitle,regularRoboto);
            edit_email.requestFocus();
            return false;
        }else if(edit_email.getText().toString().trim().length() != 0 && !isValidEmail(edit_email.getText().toString().trim())){
            showMKPanelError(SignUpActivity.this, getResources().getString(R.string.please_enter_valid_email),rlMainView,tvTitle,regularRoboto);
            edit_email.requestFocus();
            return false;
        }

        return true;
    }

    public final boolean PasswordValidaton(String password) {
        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        //Matcher hasSpecial = special.matcher(password);

        return hasLetter.find() && hasDigit.find();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("requestCode = ","requestCode = "+requestCode+"=="+resultCode+"=="+data);
        if(requestCode == REQUEST_CAMERA){
                CreateUserImage(mCapturedImageURI);
        }else if(requestCode == REQUEST_GALLERY){
            if(data != null){
                String selImagePath = getPath(data.getData());
                mCapturedImageURI=Uri.parse(selImagePath);
                CreateUserImage(mCapturedImageURI);
            }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void CreateUserImage(Uri imagePath)
    {
        try{
            Log.d("imagePath","imagePath = "+imagePath);
            //Bitmap bitmap = BitmapFactory.decodeStream(is,null,o2);
            File file = new File(imagePath.getPath());
            ExifInterface exif = new ExifInterface(file.getPath());
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Bitmap bitmap = resizeBitMapImage1(imagePath.getPath(), 200, 200);

            Bitmap RotateBitmap = RotateBitmap(bitmap,rotationAngle);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            String myFinalImagePath = saveToInternalSorage(RotateBitmap, currentDateandTime);
            Log.d("imagePath1","imagePath1 = "+myFinalImagePath);

            userImage = new File(myFinalImagePath);

            Picasso.with(SignUpActivity.this).load(userImage).transform(new CircleTransform()).into(img_add_image);

        }
        catch(Exception es)
        {	es.printStackTrace();
            System.out.println("==== exceptin in setimage : "+es);
        }
    }

    public static Bitmap resizeBitMapImage1(String filePath, int targetWidth, int targetHeight) {
        Bitmap bitMapImage = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            double sampleSize = 0;
            Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth
                    - targetWidth);
            if (options.outHeight * options.outWidth * 2 >= 1638) {
                sampleSize = scaleByHeight ? options.outHeight / targetHeight : options.outWidth / targetWidth;
                sampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize) / Math.log(2d)));
            }
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[128];
            while (true) {
                try {
                    options.inSampleSize = (int) sampleSize;
                    bitMapImage = BitmapFactory.decodeFile(filePath, options);
                    break;
                } catch (Exception ex) {
                    try {
                        sampleSize = sampleSize * 2;
                    } catch (Exception ex1) {

                    }
                }
            }
        } catch (Exception ex) {

        }
        return bitMapImage;
    }

    private String saveToInternalSorage(Bitmap bitmapImage,String imageName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Log.d("imagePath2","imagePath2 = "+directory);
        // Create imageDir
        File mypath=new File(directory,imageName+".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath()+"/"+imageName+".jpg";
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void LayoutEnimation(int color,String message){

        layout_info_panel.setBackgroundResource(color);
        if ((layout_info_panel.getVisibility() == View.GONE)) {
            layout_info_panel.setVisibility(View.VISIBLE);
        }
        subtitle.setText(message);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_map);
        layout_info_panel.startAnimation(slideUpAnimation);

        slideUpAnimation.setFillAfter(true);
        slideUpAnimation.setDuration(2000);

        slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layout_info_panel.setAlpha(1);
                if ((layout_info_panel.getVisibility() == View.GONE)) {
                    layout_info_panel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.cancel();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout_info_panel.setAlpha(0);
                    }
                }, 1000);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        edit_username = null;
        edit_name = null;
        edit_mobile = null;
        edit_email = null;
        edit_password = null;
        edit_com_password = null;
        layout_signup = null;
        img_add_image = null;
        edit_date_of_birth = null;
        spinner_gender = null;
    }
}
