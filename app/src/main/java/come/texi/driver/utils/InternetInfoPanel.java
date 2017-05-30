package come.texi.driver.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import come.texi.driver.R;

/**
 * Created by techintegrity on 04/07/16.
 */
public class InternetInfoPanel extends Dialog {

    protected static final String TAG = InternetInfoPanel.class.getName();
    private RelativeLayout rl;

    private ImageView iv_ok;

    public enum InternetInfoPanelType{
        MKInfoPanelTypeInfo,
        MKInfoPanelTypeError
    };

    public InternetInfoPanel(Context context, InternetInfoPanelType type, String titletext, String subtitletext, int interval) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.internetinfopanel);

        rl = (RelativeLayout) findViewById(R.id.main);

        iv_ok=(ImageView)findViewById(R.id.iv_ok);

        setCancelable(true);



    }

    public ImageView getIv_ok(){
        return iv_ok;
    }
}
