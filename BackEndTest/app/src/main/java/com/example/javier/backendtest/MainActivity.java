package com.example.javier.backendtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import oracle.cloud.mobile.authorization.AuthType;
import oracle.cloud.mobile.authorization.AuthorizationAgent;
import oracle.cloud.mobile.authorization.AuthorizationCallback;
import oracle.cloud.mobile.customcode.CustomHttpResponse;
import oracle.cloud.mobile.customcode.GenericCustomCodeClientCallBack;
import oracle.cloud.mobile.exception.ServiceProxyException;
import oracle.cloud.mobile.mobilebackend.MobileBackend;
import oracle.cloud.mobile.mobilebackend.MobileBackendManager;
import oracle.cloud.mobile.rest.RestClient;

public class MainActivity extends AppCompatActivity {
    private AuthorizationAgent mAuthorization;
    private Context mContext = this;

    private MobileBackend mobileBackend;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.algo);
        final TextView tv1 = (TextView)findViewById(R.id.text);
        Button mButton = (Button)findViewById(R.id.button2);
        final EditText mEdit   = (EditText)findViewById(R.id.number);
        try {

            mobileBackend=   MobileBackendManager.getManager().getMobileBackend(mContext,"ora_test_back");
        } catch (ServiceProxyException e) {
            e.printStackTrace();
        }

        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        final GenericCustomCodeClientCallBack genericCustomCodeClientCallBack = new GenericCustomCodeClientCallBack() {
                            @Override
                            public void requestCompleted(CustomHttpResponse response, JSONObject data, Exception e) {
                                boolean getResponse = (response.getHttpStatus() >=200 && response.getHttpStatus() <300);
                                // write any logic based on above response

                                Log.e("FUNC", response.toString());
                                // Log.e("FUNC", String.valueOf(response.getHeaders()));

                                try {
                                    Log.e("FUNC", String.valueOf(data.get("result")));
                                    tv1.setText( String.valueOf(data.get("result")));

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        };
                        JSONObject json = new JSONObject();

                        JSONObject manJson = new JSONObject();
                        try {
                            manJson.put("id", "1234");
                            json.put("qs",manJson);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("FUNC", String.valueOf(json));

                        Log.e("EditText", mEdit.getText().toString());

                        mAuthorization.invokeCustomCodeJSONRequest(genericCustomCodeClientCallBack, null, "ConnectorProcess/getService?id="+mEdit.getText().toString(), RestClient.HttpMethod.GET);

                    }
                });

      //  AuthorizationAgent authorizationAgent = MobileBackendManager.getManager().getDefaultMobileBackend(this).getAuthorization();

        mAuthorization = mobileBackend.getAuthorization(AuthType.BASIC_AUTH);
        String userName = "cloud.admin";
        String passWord = "tOlEraNt@8Pass";
        AuthorizationCallback mLoginCallback = new AuthorizationCallback() {
            @Override
            public void onCompletion(ServiceProxyException exception) {
                Log.d("ALLL", "OnCompletion Auth Callback");
                if (exception != null) {
                    Log.e("ALLL", "Exception while receiving the Access Token", exception);
                } else {
                    Log.e("ALLL", "Authorization successful");

                }
            }
        };
        mAuthorization.authenticate(mContext, userName, passWord, mLoginCallback);




// after the user successfully authenticates, make a call to the custom API endpoint
    }
}
    