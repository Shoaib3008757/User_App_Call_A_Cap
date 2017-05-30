package come.texi.driver.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

/**
 * Created by techintegrity on 08/07/16.
 */
public class MyTwitterResultReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Twitter is uploaded successfully"+intent.getAction());

        if(TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())){
            Toast.makeText(context, "Your post was shared successfully.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context,"Your post was shared successfully.",Toast.LENGTH_LONG).show();
        }
    }
}
