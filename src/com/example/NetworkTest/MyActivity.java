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
    //HandleMessage������Message���д������հѽ�����õ�TextView��
    public void handleMessage(Message msg) {
        Log.d("MyActivity", "ssssssssssssss");
        switch (msg.what) {
            case SHOW_RESPONSE:
                String response = (String) msg.obj;
                //���������UI�������������ʾ��������
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

        //�����߳���������������
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    //����һ��HTTP����Ŀ���ַ�ǰٶȵ���ҳ
                    URL rul = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection)rul.openConnection();
                    //HTTP�������ݷ���GET/�ύ����POST
                    connection.setRequestMethod("GET");
                    //�ύ���ݸ�ʽ
//                    connection.setRequestMethod("POST");
// DataOutputStream out = new DataOutputStream(connection.getOutputStream());
// out.writeBytes("username=admin&password=123456");
                    connection.setReadTimeout(8000);
                    //����BrfeeredReader�Է��������ص������ж�ȡ
                    InputStream in = connection.getInputStream();
                    //����Ի�ȡ�������������ж�ȡ
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        response.append(line);
                    }

                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
                    //�����������صĽ����ŵ�Message��
                    message.obj = response.toString();
                    //Handler�������ͳ�ȥ
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
