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
        //HandleMessage方法对Message进行处理，最终把结果设置到TextView上
        public void handleMessage(Message msg) {
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

                    //指定访问的服务器地址是电脑本机
                    HttpGet httpGet = new HttpGet("http://172.19.0.24/get_data.xml");

                    //   HttpGet httpGet = new HttpGet("http://www.baidu.com");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
//httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED

                //    if (sbc) {

                        //请求和响应都成功了
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        parseXMLWithPull(response);
//                        Message message = new Message();
//                        message.what = SHOW_RESPONSE;
                        //将服务器返回的结果存放到Message中
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
                    //setInput方法将服务器返回的XML数据设置进去就可以开始解析了
                    xmlPullParser.setInput(new StringReader(xmlData));
                    //得到当前解析的事件
                    int eventType = xmlPullParser.getEventType();
                    String id = "";
                    String name = "";
                    String version = "";
                    //while循环不断的解析
                    //如果当前的解析事件不等于 XmlPullParser.END_DOCUMENT，
                    // 说明解 析工作还没完成
                    while(eventType != XmlPullParser.END_DOCUMENT){
                        //通过 getName()方法得到当前结点的名
                        String nodeName = xmlPullParser.getName();
                        //调用 next()方法后可以获取下一个解析事件
                        switch (eventType){
                            //开始解析某个节点
                            //结点名等于 id、name 或 version
                            //调用 nextText()方法来获取结点内具体的内容
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
                            //完成解析某个节点
                            case XmlPullParser.END_TAG:{
                                //当解析完一个 app 结点后就将获取到的内容打印出来
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
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    //发送一条HTTP请求，目标地址是百度的首页
                    URL rul = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection) rul.openConnection();
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
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
                    //将服务器返回的结果存放到Message中
                    message.obj = response.toString();
                    //Handler将它发送出去
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
