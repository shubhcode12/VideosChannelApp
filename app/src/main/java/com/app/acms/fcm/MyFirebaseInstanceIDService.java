package com.app.acms.fcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.acms.utils.Constant;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    String fcm_url = Constant.TOKEN_URL;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        storeRegistrationIdToPreference(refreshedToken);

        sendRegistrationToServer(refreshedToken);

        Intent intent = new Intent(Constant.REGISTRATION_COMPLETE);
        intent.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendRegistrationToServer(final String token) {
        fcm_url += "?token=" + token;
        Log.e("fcm", "" + fcm_url);
        Log.e(TAG, "sendRegistrationToServer: " + fcm_url);
        try {
            URL url = new URL(fcm_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            final int statusCode = httpURLConnection.getResponseCode();
            if (statusCode != 200) {
                Log.e(TAG, "Error " + statusCode + " for URL " + url.toExternalForm());
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuffer stringBuffer = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(inputLine);
            }
            bufferedReader.close();
            Log.w(TAG, "Registration result: " + stringBuffer.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void storeRegistrationIdToPreference(String token) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SHARED_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}

