package com.example.ma.hellowoorld;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ma.sslSession.SSLTrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;

public class MainActivity extends ActionBarActivity {
    private Button helloButton;
    private TextView helloText;
    private ThreadHandler webServiceHandler;

    protected final int LOG_IN = 9;

    private static final String SOAP_ACTION1 = "http://utk.com/UmallService"; //Web Services命名空間+函數名稱
    private static final String HelloWorldmethod = "UmallService"; //要呼叫的函數名稱
    private static final String NAMESPACE = "http://utk.com/"; //Web Services命名空間
    private static final String URL = "https://magic.taipei-101.com.tw/WSUmall.asmx"; //Web Services的網址
    private static final String URL2 = "magic.taipei-101.com.tw"; //Web Services的網址
    private static final String SERVER = "magic.taipei-101.com.tw/WSUmall.asmx?op=UmallService"; //Services的網址
    //    private CallSoap cs=new CallSoap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helloButton = (Button)findViewById(R.id.helloButton);
        helloText = (TextView)findViewById(R.id.textView);

        webServiceHandler = new ThreadHandler();

        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                jsonContent();
//                CallHellow();
//                helloText.setText("GGininDer");
            }
        });
        //hello
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void CallHellow()
    {

//        SSLConection.allowAllSSL();
        SSLTrustManager.allowAllSSL();
        try{

            // add paramaters and values
            SoapObject request1 = new SoapObject(NAMESPACE, HelloWorldmethod);
            request1.addProperty("receiveData",jsonContent());

            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request1);

            //Web method call

//            final HttpsTransportSE androidHttpTransport = new HttpsTransportSE(URL2,443,HelloWorldmethod,1000);

            final HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.debug = true;

            new Thread()
            {
                public void run() {
                    try {
                        androidHttpTransport.call(SOAP_ACTION1, envelope);
                        SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
                        System.out.println("result: " + result);
                        Message mg = Message.obtain();
                        mg.what = LOG_IN;
                        mg.obj = result;
                        webServiceHandler.sendMessage(mg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            }.start();


            //get the response

//            SoapPrimitive result= (SoapPrimitive)envelope.getResponse();
//            String results = result.toString();
//            Get_HelloWorld=results;

        }catch (Exception e){
//            Get_HelloWorld=e.getMessage(); //將錯誤訊息傳回
            Log.d("Max","errrrrrror");
        }
    }

    private class ThreadHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOG_IN:
//                    helloText.setText(msg.obj.toString());
                    String resultString = msg.obj.toString();
                    helloText.setText(resultString+"\n"+Base64.decode(resultString.getBytes(),Base64.DEFAULT).toString());
                    break;
            }
        }
    }

    private String jsonContent(){
        String rr = "";

        JSONObject sendValueJson = new JSONObject();
        try {

            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat formatterTime = new SimpleDateFormat("hhmmss");
            String date = formatterDate.format(new java.util.Date());
            String time = formatterTime.format(new java.util.Date());

            TelephonyManager mTelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String imeiString = mTelManager.getDeviceId().substring(mTelManager.getDeviceId().length()-8,mTelManager.getDeviceId().length());

            Log.d("Max",date+" "+time);

            sendValueJson.put("T0100", "0100");
            //        sendValueJson.put("T0202", "2222222200000648");
            sendValueJson.put("T0202", "A126466081");
            sendValueJson.put("T0231", "A126466081");
            sendValueJson.put("T0300", "250172");
            sendValueJson.put("T1200", time);
            sendValueJson.put("T1300", date);
//            sendValueJson.put("T4100", "51488439");
            sendValueJson.put("T4100", imeiString);
            sendValueJson.put("T5509", "A");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encodeString = new String(Base64.encode(sendValueJson.toString().getBytes(),Base64.DEFAULT));
        Log.d("Max",sendValueJson.toString()+" "+encodeString);
        return encodeString;
    }
}
