package come.texi.driver.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import come.texi.driver.R;

/**
 * Created by techintegrity on 04/07/16.
 */
public class MKInfoPanel extends Dialog {

    protected static final String TAG = MKInfoPanel.class.getName();

    //private TextView title;
    private TextView subtitle;
    //private ImageView image;
    private RelativeLayout layout_info_panel;
    private RelativeLayout rl;
    Context context;


    public enum MKInfoPanelType{
        MKInfoPanelTypeInfo,
        MKInfoPanelTypeError
    };

    public MKInfoPanel(Context context, MKInfoPanelType type, String titletext, String subtitletext, int interval) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mkinfopanel);

        rl = (RelativeLayout) findViewById(R.id.main);
        //title = ((TextView)findViewById(R.id.title));
        subtitle = ((TextView)findViewById(R.id.subtitle));
        //image = ((ImageView)findViewById(R.id.image));
        layout_info_panel = ((RelativeLayout)findViewById(R.id.layout_info_panel));

        if(type == MKInfoPanelType.MKInfoPanelTypeError) {
            layout_info_panel.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }else if(type == MKInfoPanelType.MKInfoPanelTypeInfo) {
            layout_info_panel.setBackgroundColor(Color.GREEN);
        }

        //title.setText(titletext);
        subtitle.setText(subtitletext);

        setCancelable(true);

//        Handler handler =  new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                animateHide();
//            }
//        }, interval);

    }

    public void animateHide() {
        Activity activity = (Activity) context;
        int height = Common.getDisplayHeight(activity);
        TranslateAnimation slideUp = new TranslateAnimation(0f, 0F, 0f, height*0.50f);
        slideUp.setStartOffset(500);
        slideUp.setDuration(2000);
        slideUp.setFillAfter(true);
        slideUp.setInterpolator(new BounceInterpolator());
        layout_info_panel.startAnimation(slideUp);
        slideUp.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cancel();
                dismiss();
            }
        });
    }

}
