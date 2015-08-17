package com.example.NetworkTest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyActivity extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    public static final int SHOW_RESPONSE = 0;
    private Button sendRequest;
    private TextView reponseText;
    private Handler handler = new Handler() {
    //HandleMessage方法对Message进行处理，最终把结果设置到TextView上
    public void handleMessage(Message msg) {
        Log.d("MyActivity", "ssssssssssssss");
        switch (msg.what) {
            case SHOW_RESPONSE:
                String response = (String) msg.obj;
                //在这里进行UI操作，将结果显示到界面上
                reponseText.setText(response);

        }
    }

};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sendRequest = (Button) findViewById(R.id.send_request);
        reponseText = (TextView) findViewById(R.id.response);
        sendRequest.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send_request){
            sendRequestWithHttpURLConnection();

        }
    }

    private void sendRequestWithHttpURLConnection() {

        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    //发送一条HTTP请求，目标地址是百度的首页
                    URL rul = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection)rul.openConnection();
                    //HTTP请求数据方法GET/提交数据POST
                    connection.setRequestMethod("GET");
                    //提交数据格式
//                    connection.setRequestMethod("POST");
// DataOutputStream out = new DataOutputStream(connection.getOutputStream());
// out.writeBytes("username=admin&password=123456");
                    connection.setReadTimeout(8000);
                    //利用BrfeeredReader对服务器返回的流进行读取
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        response.append(line);
                    }

                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
                    //将服务器返回的结果存放到Message中
                    message.obj = response.toString();
                    //Handler将它发送出去
                    handler.sendMessage(message);

                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
