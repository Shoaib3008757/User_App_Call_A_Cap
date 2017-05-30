package come.texi.driver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class TestMapActivity extends AppCompatActivity {

    private static final String SERVER_IP = "http://107.170.36.24:4040";
    private Socket mSocket;

//    private com.github.nkzawa.socketio.client.Socket mSocket;
//    {
//        try {
//            mSocket = IO.socket("http://107.170.36.24:3000");
//            mSocket.connect();
//            Log.d("mSocket","mSocket = "+mSocket.connected());
//        } catch (URISyntaxException e) {}
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map);

        try {
            mSocket = IO.socket(SERVER_IP);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        CreateDriver();
        socketConnection();

    }

    public void CreateDriver()
    {


        try {
            JSONObject userobj = new JSONObject();
            userobj.put("driver_id", "3");
            Log.d("connected ", "connected one = " + mSocket.connected() + "==" + userobj);
            mSocket.emit("New User Register", userobj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * chat socket connection methods
     */
    public void socketConnection()
    {
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on("Driver Detail", onSocketConnectionListener);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("connected ","connected one = "+mSocket.connected());
                while(mSocket.connected()==false)
                {
                    //do nothing
                }
                Log.d("connected ", "connected one = " + mSocket.connected());
                //sendConnectData();
            }
        }).start();
    }

    /**
     * Listener to handle messages received from chat server of any type... Listener registered at the time of socket connected
     */
    private Emitter.Listener onSocketConnectionListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // handle the response args
                    Toast.makeText(TestMapActivity.this,"Come",Toast.LENGTH_LONG).show();
                    JSONObject data = (JSONObject) args[0];

                    Toast.makeText(TestMapActivity.this,data+"",Toast.LENGTH_LONG).show();
                    Log.d("data", "connected data = " + data);

                }
            });
        }
    };

    /**
     * Listener for socket connection error.. listener registered at the time of socket connection
     */
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSocket != null)
                        if (mSocket.connected() == false) {
                            Log.d("connected", "connected two= " + mSocket.connected());
                            //socketConnection();
                        }else
                        {
                            Log.d("connected", "connected three= " + mSocket.connected());
                        }
                }
            });
        }
    };

}
