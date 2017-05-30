package com.app.facebooklibrary;

/**
 * callback interface
 *
 * @author ashok
 */
public interface FB_Callback {
    /**
     * method will be called on facebookLogin after authenticating
     *
     * @param beanObject : facebook bean object containing user data from facebook
     */
    void onLoginSuccess(FBBean beanObject);


    /**
     * method will be called on facebookLogin failure
     *
     * @param message failure message
     */
    void onLoginFailure(String message);


    /**
     * method will called on successfull post
     *
     * @param postID  id of the post
     * @param message post message
     */
    void onPostSuccess(String postID, String message);

    /**
     * method will be called on failure in case of posting data
     *
     * @param message - post failure message
     */
    void onPostFailure(String message);

    /**
     * method will be called on FacebookLogout
     */
    void onLogout();

}
