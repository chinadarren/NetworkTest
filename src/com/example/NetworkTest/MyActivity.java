package com.example.NetworkTest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyActivity extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    public static final int SHOW_RESPONSE = 0;
    public static final boolean sbc = true;
    private Button sendRequest;
    private TextView reponseText;
    private Handler handler = new Handler() {
        //HandleMessage������Message���д������հѽ�����õ�TextView��
        public void handleMessage(Message msg) {
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
        if (v.getId() == R.id.send_request) {
            //       sendRequestWithHttpURLConnection();
            sendRequestWithHttpClient();
        }
    }

    private void sendRequestWithHttpClient() {
        //  Toast.makeText(getApplicationContext(),"ssssss",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HttpClient httpClient = new DefaultHttpClient();

                    //ָ�����ʵķ�������ַ�ǵ��Ա���
                    HttpGet httpGet = new HttpGet("http://172.19.0.24/get_data.xml");

                    //   HttpGet httpGet = new HttpGet("http://www.baidu.com");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
//httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED

                //    if (sbc) {

                        //�������Ӧ���ɹ���
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        parseXMLWithPull(response);
//                        Message message = new Message();
//                        message.what = SHOW_RESPONSE;
                        //�����������صĽ����ŵ�Message��
//                        message.obj = response.toString();
//                        handler.sendMessage(message);
                 //   }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
            private void parseXMLWithPull(String xmlData) {

                Log.d(MyActivity.ACTIVITY_SERVICE, "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");

                try{
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xmlPullParser = factory.newPullParser();
                    //setInput���������������ص�XML�������ý�ȥ�Ϳ��Կ�ʼ������
                    xmlPullParser.setInput(new StringReader(xmlData));
                    //�õ���ǰ�������¼�
                    int eventType = xmlPullParser.getEventType();
                    String id = "";
                    String name = "";
                    String version = "";
                    //whileѭ�����ϵĽ���
                    //�����ǰ�Ľ����¼������� XmlPullParser.END_DOCUMENT��
                    // ˵���� ��������û���
                    while(eventType != XmlPullParser.END_DOCUMENT){
                        //ͨ�� getName()�����õ���ǰ������
                        String nodeName = xmlPullParser.getName();
                        //���� next()��������Ի�ȡ��һ�������¼�
                        switch (eventType){
                            //��ʼ����ĳ���ڵ�
                            //��������� id��name �� version
                            //���� nextText()��������ȡ����ھ��������
                            case XmlPullParser.START_TAG:{
                                if("id".equals(nodeName)){
                                    id = xmlPullParser.nextText();
                                }else if("name".equals(nodeName)){
                                    name = xmlPullParser.nextText();
                                }else if ("version".equals(nodeName)){
                                    version = xmlPullParser.nextText();
                                }
                                break;
                            }
                            //��ɽ���ĳ���ڵ�
                            case XmlPullParser.END_TAG:{
                                //��������һ�� app ����ͽ���ȡ�������ݴ�ӡ����
                                if("app".equals(nodeName)){
                                    Log.d("MainActivity","id is " + id);
                                    Log.d("MainActivity","name is " + name);
                                    Log.d("MainActivity","version is" + version);
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        eventType = xmlPullParser.next();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }



    private void sendRequestWithHttpURLConnection() {
        //�����߳���������������
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    //����һ��HTTP����Ŀ���ַ�ǰٶȵ���ҳ
                    URL rul = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection) rul.openConnection();
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
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
                    //�����������صĽ����ŵ�Message��
                    message.obj = response.toString();
                    //Handler�������ͳ�ȥ
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
