package com.self.encodeapp;

import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;

import okhttp3.Request;
import okio.ByteString;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.OkHttpClient;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {
  OkHttpClient client;
  Button button;
  int ackId = 50;
  int vid = 4352;
  String TAG = MainActivity.class.getCanonicalName();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    client = new OkHttpClient();
    final Request request = new Request.Builder().url("ws://192.168.43.185:9000/wss").build();
    Log.d(TAG, "request " + request);
    button = findViewById(R.id.ens);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        client.newWebSocket(request, new WSListener());
      }
    });
  }

  public static byte[] intToByteArray(int val) {
    byte[] ret = new byte[4];
    ret[0] = (byte) val;
    ret[1] = (byte) (val >>> 8);
    ret[2] = (byte) (val >>> 16);
    ret[3] = (byte) (val >>> 24);
    return ret;
  }

  private final class WSListener extends WebSocketListener{
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
      super.onOpen(webSocket, response);
      Log.d(TAG, "onOpen");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
      super.onMessage(webSocket, text);
      Log.d(TAG, "onMessage");
      try {
        JSONObject json = new JSONObject(text);
        Log.d(TAG, "onMessage json " + json);
        String action = json.optString("a");
        if (action == null) return;
        else if(action.equals("ready")){
          ByteArrayOutputStream bOut = new ByteArrayOutputStream();
          DataOutputStream dos = new DataOutputStream(bOut);
          try {
            dos.write(intToByteArray(ackId));
            dos.write(intToByteArray(vid));
            dos.flush();
            webSocket.send(ByteString.of(bOut.toByteArray()));
            bOut.close();
          } catch (Exception e) {
            Log.e(TAG, "error while creating packet", e);
          }
        }
      } catch (JSONException e) {
        Log.e(TAG, "OkHttpWS onMessage for json: " + text, e);
      }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
      super.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
      super.onClosed(webSocket, code, reason);
      Log.d(TAG, "onClosed");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
      super.onFailure(webSocket, t, response);
      Log.d(TAG, "onFailure " + response + "throwable " + t);
    }
  }
}