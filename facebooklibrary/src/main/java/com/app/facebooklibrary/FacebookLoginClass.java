package com.app.facebooklibrary;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacebookLoginClass {

    public static List<String> READ_PERMISSIONS = Arrays.asList(FbConstants.PUBLIC_PROFILE, FbConstants.USER_FRIENDS, FbConstants.EMAIL);
    private final LoginManager mLoginManager;
    private final FB_Callback listnerCallBack;
    /*private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {

            if (listnerCallBack != null)
                listnerCallBack.onPostFailure("on cancelled");
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            if (listnerCallBack != null)
                listnerCallBack.onPostFailure(error.getMessage().toString());
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));

        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                if (listnerCallBack != null)
                    listnerCallBack.onPostSuccess(result.getPostId(), "Success Message");
                Log.d("HelloFacebook", "success");
            } else {
                if (listnerCallBack != null)
                    listnerCallBack.onPostFailure("Failure");
            }
            Log.d("HelloFacebook", "failure");
        }
    };*/
    private final CallbackManager callBackManager;
    private final Context mContext;
    public List<String> WRITE_PERMISSIONS = Arrays.asList(FbConstants.PUBLISH_PAGES);
    /**
     * share callback which will get called on sharing anything on facebook
     */
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            listnerCallBack.onPostFailure("on cancelled");
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            listnerCallBack.onPostFailure(error.getMessage().toString());
            if (mContext != null)
                Toast.makeText(mContext, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));

        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
//            if (result.getPostId() != null) {
//            listnerCallBack.onPostSuccess(result.getPostId(), "Success Message");
//            Log.d("HelloFacebook", "success");
//            } else {
//                listnerCallBack.onPostFailure("Failure");


            if (result.getPostId() != null) {
                listnerCallBack.onPostSuccess(result.getPostId(), "Success Message");
                Log.d("HelloFacebook", "success");
            } else {
                listnerCallBack.onPostFailure("Failure");
            }
        }
//            Log.d("HelloFacebook", "failure");
//        }
    };
    private ProgressDialog mProgressDialog;
    private FB_TYPE mFbType;
    private ShareDialog shareDialog;
    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
    private ShareContent content;
    private FacebookCallback<LoginResult> loginCback = new FacebookCallback<LoginResult>() {


        @Override
        public void onSuccess(LoginResult loginResult) {
            switch (mFbType) {
                case LOGIN:
                    fetchUserData(loginResult.getAccessToken());
                    break;
                case LOGOUT:
                    break;
                case SHARE_CONTACT:
                case SHARE_PHOTO:
                case SHARE_VIDEO:
                    shareOnFacebook();
                    break;


                default:
            }
        }

        @Override
        public void onCancel() {
            if (listnerCallBack != null)
                listnerCallBack.onLoginFailure("failure");

        }

        @Override
        public void onError(FacebookException e) {
            if (listnerCallBack != null)
                listnerCallBack.onLoginFailure(e.getMessage());
        }
    };

    public FacebookLoginClass(Context context, CallbackManager callbackManager, Object class2) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());

        try {
            listnerCallBack = (FB_Callback) class2;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement Facebook Callback.");
        }
        this.mContext = context;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading ...");
        mProgressDialog.setCancelable(false);
        this.callBackManager = callbackManager;
        shareDialog = new ShareDialog((android.app.Activity) mContext);
        shareDialog.registerCallback(callbackManager, shareCallback);
        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(
                SharePhotoContent.class);
        mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(callbackManager, loginCback);
    }

    public FacebookLoginClass(Context context, CallbackManager callbackManager) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());

        try {
            listnerCallBack = (FB_Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement Facebook Callback.");
        }
        this.mContext = context;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading ...");
        mProgressDialog.setCancelable(false);
        this.callBackManager = callbackManager;
        shareDialog = new ShareDialog((android.app.Activity) mContext);
        shareDialog.registerCallback(callbackManager, shareCallback);
        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(
                SharePhotoContent.class);
        mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(callbackManager, loginCback);
    }

    public void postVideo(String videoURI, String videoThumbnailUrl, String title, String description, String contentURL) {
        this.mFbType = FB_TYPE.SHARE_VIDEO;
        Uri videoFileUri = Uri.parse(videoURI);

        SharePhoto videoThumbnail = new SharePhoto.Builder().setImageUrl(Uri.parse(videoThumbnailUrl)).build();

        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(videoFileUri)
                .build();

        if (contentURL != null && !contentURL.isEmpty())
            content = new ShareVideoContent.Builder()
                    .setVideo(video)
//                    .setPreviewPhoto(videoThumbnail)
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .setContentUrl(Uri.parse(contentURL))
                    .build();
        else
            content = new ShareVideoContent.Builder()
                    .setVideo(video)
//                    .setPreviewPhoto(videoThumbnail)
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .build();

        shareOnFacebook();
    }

    public void postPhoto(Bitmap image) {
        this.mFbType = FB_TYPE.SHARE_PHOTO;
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).build();
        ArrayList<SharePhoto> photos = new ArrayList<>();
        photos.add(sharePhoto);
        content =
                new SharePhotoContent.Builder().setPhotos(photos).build();

        shareOnFacebook();
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains(FbConstants.PUBLISH_PAGES);
    }

    public void postStatusUpdate(String title, String description, String imageVideoURL, String contentLink) {
        this.mFbType = FB_TYPE.SHARE_CONTACT;
        if (imageVideoURL.isEmpty()) {
            if (contentLink != null && !contentLink.isEmpty()) {
                content = new ShareLinkContent.Builder().setContentTitle(title)
                        .setContentDescription(description)
                        .setContentUrl(Uri.parse(contentLink))
                        .build();
            } else {
                content = new ShareLinkContent.Builder().setContentTitle(title)
                        .setContentDescription(description)
                        //.setContentUrl(Uri.parse("https://www.google.com/"))
                        .build();
            }
        } else {
            if (contentLink != null && !contentLink.isEmpty()) {
                content = new ShareLinkContent.Builder().setContentTitle(title)
                        .setContentDescription(description)
                        .setImageUrl(Uri.parse(imageVideoURL))
                        .setContentUrl(Uri.parse(contentLink))
                        .build();
            } else {
                content = new ShareLinkContent.Builder().setContentTitle(title)
                        .setContentDescription(description)
                        .setImageUrl(Uri.parse(imageVideoURL))
                        //.setContentUrl(Uri.parse("https://www.google.com/"))
                        .build();
            }
        }
        shareOnFacebook();

    }

    /*public void postStatusUpdateWithImage(String title, String description, String contentURL, String imgUrl) {
        this.mFbType = FB_TYPE.SHARE_CONTACT;
        if (contentURL.isEmpty())
            content = new ShareLinkContent.Builder().setContentTitle(title)
                    .setContentDescription(description)
                    .setImageUrl(Uri.parse(imgUrl))
                    .setContentUrl(Uri.parse("https://www.google.com/"))
                    .build();
        else
            content = new ShareLinkContent.Builder().setContentTitle(title)
                    .setContentDescription(description)
                    .setImageUrl(Uri.parse(imgUrl))
                    .setContentUrl(Uri.parse(contentURL))
                    .build();
        shareOnFacebook();

    }*/

//    public void postStatusUpdate1(String title, String description/*, String contentURL*/) {
//        this.mFbType = FB_TYPE.SHARE_CONTACT;
//        content = new ShareLinkContent.Builder().setContentTitle(title)
//                .setContentDescription(description)
//                /*.setContentUrl(Uri.parse(contentURL))*/
//                .build();
//        shareOnFacebook();
//
//    }

    private void shareOnFacebook() {
        Profile profile = Profile.getCurrentProfile();
        if (canPresentShareDialog) {
            shareDialog.registerCallback(callBackManager, shareCallback);
            shareDialog.show(content);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(content, shareCallback);
        } else {
            mLoginManager.logInWithPublishPermissions(
                    (android.app.Activity) mContext,
                    WRITE_PERMISSIONS);
        }
    }

    public void facebookLogin() {
        this.mFbType = FB_TYPE.LOGIN;
        mLoginManager.logInWithReadPermissions((android.app.Activity) mContext, READ_PERMISSIONS);
    }

    public void facebokLogout() {
        mLoginManager.logOut();
    }

    private void fetchUserData(final AccessToken accessToken) {
        mProgressDialog.show();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        mProgressDialog.dismiss();
                        FBBean bean = new FBBean();
                        JSONObject json = response.getJSONObject();
                        bean.setUserID(json.optString("id"));
                        bean.setUserImage("http://graph.facebook.com/" + bean.getUserID() + "/picture?type=large");
                        bean.setFirstName(json.optString("first_name"));
                        bean.setLastName(json.optString("last_name"));
                        bean.setMailId(json.optString("email"));
                        bean.setAccessToken(accessToken.getToken());
                        bean.setUserName(json.optString("name"));
                        String genderVal = json.optString("gender");
                        if (genderVal.length() > 0 && genderVal.toLowerCase().startsWith("F"))
                            bean.setGender("F");
                        else
                            bean.setGender("M");
                        bean.setProfileLink(json.optString("link"));
                        // Application code
                        Log.e("graph user data", response.getJSONObject().toString() + object.toString());
                        if (listnerCallBack != null)
                            listnerCallBack.onLoginSuccess(bean);
                    }
                });
        request.executeAsync();
    }


    public enum FB_TYPE {
        LOGIN,
        SHARE_CONTACT,
        SHARE_PHOTO,
        SHARE_VIDEO,
        LOGOUT
    }
}
