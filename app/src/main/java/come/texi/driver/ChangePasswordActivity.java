package come.texi.driver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import come.texi.driver.utils.Common;
import come.texi.driver.utils.Url;
import cz.msebera.android.httpclient.Header;

import static come.texi.driver.utils.Common.showMKPanelError;

public class ChangePasswordActivity extends AppCompatActivity {

    TextView txt_change_password,txt_change_password_logo,txt_change_pass;
    EditText edit_current_pass;
    EditText edit_new_pass;
    EditText edit_con_pass;
    RelativeLayout layout_change_password_button;
    RelativeLayout layout_menu;

    Typeface OpenSans_Regular,OpenSans_Bold,regularRoboto,Roboto_Bold;
    SlidingMenu slidingMenu;

    SharedPreferences userPref;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;

    //Error Alert
    RelativeLayout rlMainView;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //Error Alert
        rlMainView=(RelativeLayout)findViewById(R.id.rlMainView);
        tvTitle=(TextView)findViewById(R.id.tvTitle);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) getResources().getDimension(R.dimen.height_50), 0, 0);
        rlMainView.setLayoutParams(params);

        txt_change_password = (TextView)findViewById(R.id.txt_change_password);
        edit_current_pass = (EditText)findViewById(R.id.edit_current_pass);
        edit_new_pass = (EditText)findViewById(R.id.edit_new_pass);
        edit_con_pass = (EditText)findViewById(R.id.edit_con_pass);
        layout_change_password_button = (RelativeLayout) findViewById(R.id.layout_change_password_button);
        layout_menu = (RelativeLayout)findViewById(R.id.layout_menu);
        txt_change_password_logo = (TextView)findViewById(R.id.txt_change_password_logo);
        txt_change_pass = (TextView)findViewById(R.id.txt_change_pass);

        ProgressDialog = new Dialog(ChangePasswordActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);

        userPref = PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this);

        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");
        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        regularRoboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        txt_change_password.setTypeface(OpenSans_Bold);
        edit_new_pass.setTypeface(OpenSans_Regular);
        edit_con_pass.setTypeface(OpenSans_Regular);
        edit_current_pass.setTypeface(OpenSans_Regular);
        txt_change_password_logo.setTypeface(Roboto_Bold);
        txt_change_pass.setTypeface(Roboto_Bold);

        layout_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("password","Login password = "+userPref.getString("password",""));
                if(edit_current_pass.getText().toString().trim().length() == 0){
                    showMKPanelError(ChangePasswordActivity.this, getResources().getString(R.string.please_enter_current_password),rlMainView,tvTitle,regularRoboto);
                    edit_current_pass.requestFocus();
                    return;
                }else if(!edit_current_pass.getText().toString().trim().equals(userPref.getString("password",""))){
                    showMKPanelError(ChangePasswordActivity.this, getResources().getString(R.string.please_current_password),rlMainView,tvTitle,regularRoboto);
                    edit_current_pass.requestFocus();
                    return;
                }else if(edit_new_pass.getText().toString().trim().length() == 0){
                    showMKPanelError(ChangePasswordActivity.this, getResources().getString(R.string.please_enter_new_password),rlMainView,tvTitle,regularRoboto);
                    edit_new_pass.requestFocus();
                    return;
                }else if (edit_new_pass.getText().toString().trim().length() < 6 || edit_new_pass.getText().toString().trim().length() > 32) {
                    showMKPanelError(ChangePasswordActivity.this, getResources().getString(R.string.password_new_length),rlMainView,tvTitle,regularRoboto);
                    edit_new_pass.requestFocus();
                    return;
                }else if(edit_con_pass.getText().toString().trim().length() == 0){
                    showMKPanelError(ChangePasswordActivity.this, getResources().getString(R.string.please_enter_confirm_password),rlMainView,tvTitle,regularRoboto);
                    edit_con_pass.requestFocus();
                    return;
                }else if(!edit_new_pass.getText().toString().equals(edit_con_pass.getText().toString())){
                    showMKPanelError(ChangePasswordActivity.this, getResources().getString(R.string.password_new_confirm),rlMainView,tvTitle,regularRoboto);
                    edit_con_pass.requestFocus();
                    return;
                }

                if (Common.isNetworkAvailable(ChangePasswordActivity.this)) {

                    ProgressDialog.show();
                    cusRotateLoading.start();

                    Ion.with(ChangePasswordActivity.this)
                            .load(Url.changePasswordUrl)
                            .setTimeout(6000)
                            //.setJsonObjectBody(json)
                            .setMultipartParameter("password", edit_new_pass.getText().toString().trim())
                            .setMultipartParameter("uid", userPref.getString("id", ""))
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception error, JsonObject result) {
                                    // do stuff with the result or error
                                    Log.d("Login result", "Login result = " + result + "==" + error);
                                    if (error == null) {

                                        ProgressDialog.cancel();
                                        cusRotateLoading.stop();

                                        try {
                                            JSONObject resObj = new JSONObject(result.toString());
                                            if(resObj.getString("status").equals("success")){
                                                Common.showMkSucess(ChangePasswordActivity.this, getResources().getString(R.string.password_change_sucess),"yes");

                                                SharedPreferences.Editor newPass = userPref.edit();
                                                newPass.putString("password", edit_new_pass.getText().toString().trim());
                                                newPass.commit();

                                            }else if(resObj.getString("status").equals("false")){
                                                Common.user_InActive = 1;
                                                Common.InActive_msg = resObj.getString("message");

                                                SharedPreferences.Editor editor = userPref.edit();
                                                editor.clear();
                                                editor.commit();

                                                Intent logInt = new Intent(ChangePasswordActivity.this, LoginOptionActivity.class);
                                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(logInt);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        ProgressDialog.cancel();
                                        cusRotateLoading.stop();

                                        Common.ShowHttpErrorMessage(ChangePasswordActivity.this, error.getMessage());
                                    }
                                }
                            });
                }
            }
        });

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width);
        slidingMenu.setFadeDegree(0.20f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.left_menu);

        Common common = new Common();
        common.SlideMenuDesign(slidingMenu, ChangePasswordActivity.this, "change password");

        layout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

        Common.ValidationGone(ChangePasswordActivity.this,rlMainView,edit_current_pass);
        Common.ValidationGone(ChangePasswordActivity.this,rlMainView,edit_new_pass);
        Common.ValidationGone(ChangePasswordActivity.this,rlMainView,edit_con_pass);



    }

    @Override
    public void onBackPressed() {
        if(slidingMenu.isMenuShowing()){
            slidingMenu.toggle();
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            ChangePasswordActivity.super.onBackPressed();
                        }
                    }).create().show();
        }
    }
}
