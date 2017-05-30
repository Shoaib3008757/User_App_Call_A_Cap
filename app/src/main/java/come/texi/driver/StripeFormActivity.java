package come.texi.driver;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.victor.loading.rotate.RotateLoading;
import come.texi.driver.utils.Common;

public class StripeFormActivity extends AppCompatActivity {

    TextView txt_stripe_form;
    EditText expYear;
    EditText number;
    EditText expMonth;
    EditText cvc;
    RelativeLayout save;
    RelativeLayout layout_back_arrow;

    Typeface OpenSans_Regular,OpenSans_Bold;

    Dialog ProgressDialog;
    RotateLoading cusRotateLoading;

    /*Stripe integration variable*/
    public static final String PUBLISHABLE_KEY = "pk_test_6pRNASCoBOKtIshFeQd4XMUh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_form);

        txt_stripe_form = (TextView)findViewById(R.id.txt_stripe_form);
        expYear = (EditText)findViewById(R.id.expYear);
        number = (EditText)findViewById(R.id.number);
        expMonth = (EditText)findViewById(R.id.expMonth);
        cvc = (EditText)findViewById(R.id.cvc);
        save = (RelativeLayout)findViewById(R.id.save);
        layout_back_arrow = (RelativeLayout)findViewById(R.id.layout_back_arrow);

        ProgressDialog = new Dialog(StripeFormActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        ProgressDialog.setContentView(R.layout.custom_progress_dialog);
        ProgressDialog.setCancelable(false);
        cusRotateLoading = (RotateLoading)ProgressDialog.findViewById(R.id.rotateloading_register);

        OpenSans_Bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold_0.ttf");
        OpenSans_Regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular_0.ttf");

        txt_stripe_form.setTypeface(OpenSans_Bold);

        number.setTypeface(OpenSans_Regular);
        expYear.setTypeface(OpenSans_Regular);
        expMonth.setTypeface(OpenSans_Regular);
        cvc.setTypeface(OpenSans_Regular);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(number.getText().toString().length() == 0){
                    Common.showMkError(StripeFormActivity.this, getResources().getString(R.string.please_enter_card_number));
                    return;
                }else if(expYear.getText().toString().length() == 0){
                    Common.showMkError(StripeFormActivity.this, getResources().getString(R.string.please_enter_year));
                    return;
                }else if(expMonth.getText().toString().length() == 0){
                    Common.showMkError(StripeFormActivity.this, getResources().getString(R.string.please_enter_month));
                    return;
                }else if(cvc.getText().toString().length() == 0){
                    Common.showMkError(StripeFormActivity.this, getResources().getString(R.string.please_enter_cvc));
                    return;
                }

                ProgressDialog.show();
                cusRotateLoading.start();

                Log.d("Data", "Data = " + expYear.getText().toString() + "==" + expMonth.getText().toString());
                Card card = new Card(number.getText().toString(),
                        Integer.parseInt(expMonth.getText().toString()),
                        Integer.parseInt(expYear.getText().toString()),
                        cvc.getText().toString());

                //card.setCurrency("USD");

                boolean validation = card.validateCard();

                if (validation) {

                    new Stripe().createToken(
                            card,
                            PUBLISHABLE_KEY,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    //getTokenList().addToList(token);

                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();

                                    Log.d("token", "token = " + token.getId());
                                    Intent ri = new Intent();
                                    ri.putExtra("stripe_id", token.getId());
                                    setResult(2, ri);
                                    finish();
                                }

                                public void onError(Exception error) {
                                    ProgressDialog.cancel();
                                    cusRotateLoading.stop();
                                    Common.showMkError(StripeFormActivity.this, error.getLocalizedMessage());
                                }
                            });
                } else if (!card.validateNumber()) {
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    Common.showMkError(StripeFormActivity.this, "The card number that you entered is invalid");
                } else if (!card.validateExpiryDate()) {
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    Common.showMkError(StripeFormActivity.this, "The expiration date that you entered is invalid");
                } else if (!card.validateCVC()) {
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    Common.showMkError(StripeFormActivity.this, "The CVC code that you entered is invalid");
                } else {
                    ProgressDialog.cancel();
                    cusRotateLoading.stop();
                    Common.showMkError(StripeFormActivity.this, "The card details that you entered are invalid");
                }
            }
        });

        layout_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        expYear = null;
        number = null;
        expMonth = null;
        cvc = null;
        save = null;
        layout_back_arrow = null;

    }


}
