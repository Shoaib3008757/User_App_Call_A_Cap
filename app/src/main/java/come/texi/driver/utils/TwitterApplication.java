package come.texi.driver.utils;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by techintegrity on 08/07/16.
 */
public class TwitterApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        TwitterAuthConfig authConfig =  new TwitterAuthConfig("qARyLKqVjnbJ69aDKuCsiOBfo", "bJGtMyN2v0DXqBJPt5AHQkdSUglsRLwBhxhv2Nto4aiP5hY1VD");
        Fabric.with(this, new Twitter(authConfig));
    }
}
