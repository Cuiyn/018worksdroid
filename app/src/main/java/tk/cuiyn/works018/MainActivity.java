package tk.cuiyn.works018;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    Map<Integer, String> allSubject = new HashMap<>();
    List<Message> allMessage = new ArrayList<>();
    public static final int UPDATE_TEXT = 1;
    final CountDownLatch subjectCount = new CountDownLatch(1);

    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        boolean networkState = NetworkDetector.detect(MainActivity.this);
        if(networkState) {
            sendRequestWithHttpClient("subject");
            try {
                subjectCount.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendRequestWithHttpClient("all");
        }
        else {
            Cursor csubject = database.query("subject", null, null, null, null, null, null);
            if (csubject.getCount() == 0)
                Toast.makeText(MainActivity.this, "无网络连接，请打开Wifi或数据流量", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(MainActivity.this, "无网络连接，仅显示本地数据", Toast.LENGTH_SHORT).show();
                if(csubject.moveToFirst())
                {
                    do {
                        allSubject.put(csubject.getInt(csubject.getColumnIndex("id")), csubject.getString(csubject.getColumnIndex("name")));
                    }while (csubject.moveToNext());
                }
                Cursor chomework = database.query("homework", null, null, null, null, null, null);
                if(chomework.moveToLast())
                {
                    do {
                        Message message = new Message();
                        message.setPk(chomework.getInt(chomework.getColumnIndex("id")));
                        Map<String, String> fields = new HashMap<>();
                        fields.put("text", chomework.getString(chomework.getColumnIndex("homework")));
                        fields.put("date", chomework.getString(chomework.getColumnIndex("date")));
                        fields.put("subject", ""+chomework.getInt(chomework.getColumnIndex("subjectid")));
                        message.setFields(fields);
                        message.setSubjectName(allSubject.get(message.getSubject()));
                        allMessage.add(message);
                    }while (chomework.moveToPrevious());
                }
                android.os.Message m = new android.os.Message();
                m.what = UPDATE_TEXT;
                handler.sendMessage(m);
            }
        }


        MessageAdapter adapter = new MessageAdapter(MainActivity.this, R.layout.message_item, allMessage);
        final ListView messageListView = (ListView) findViewById(R.id.messageListView);
        messageListView.setAdapter(adapter);
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = allMessage.get(position);
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("subject", message.getSubjectName());
                intent.putExtra("date", message.getDate());
                intent.putExtra("text", message.getText());
                startActivity(intent);
            }
        });
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
        if (id == R.id.action_web) {
            Uri uri = Uri.parse("http://119.29.38.129:8000");
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendRequestWithHttpClient(String method) {
        final String m = method;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlstring;
                if (m == "subject") {
                    urlstring = "http://119.29.38.129:8000/json/?m=subject";
                } else {
                    urlstring = "http://119.29.38.129:8000/json/?m=all";
                }
                try {
                    URL url = new URL(urlstring);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("请求url失败");
                    }
                    InputStream in = conn.getInputStream();
                    byte[] data = StreamTool.read(in);
                    String str = new String(data, "UTF-8");
                    conn.disconnect();

                    if (m == "subject") {
                        subjectparseJSONWithGSON(str);
                        subjectCount.countDown();
                    } else {
                        messageparseJSONWithGSON(str);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void subjectparseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Subject> subjectsList = gson.fromJson(jsonData, new TypeToken<List<Subject>>() {
        }.getType());
        for (Subject subject : subjectsList) {
            allSubject.put(subject.getPk(), subject.getName());
            Log.d("MainActivity", "id is " + subject.getPk());
            Log.d("MainActivity", "name is " + subject.getName());

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor c = database.query("subject", null, "id=" + subject.getPk(), null, null, null, null);
            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put("id", subject.getPk());
                values.put("name", subject.getName());
                database.insert("subject", null, values);
            }
        }
    }

    private void messageparseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Message> messagesList = gson.fromJson(jsonData, new TypeToken<List<Message>>() {
        }.getType());
        for (Message message : messagesList) {
            message.setSubjectName(allSubject.get(message.getSubject()));
            allMessage.add(message);
            Log.d("MainActivity", "subject name is " + allSubject.get(message.getSubject()));
            Log.d("MainActivity", "subject id is " + message.getSubject());
            Log.d("MainActivity", "text is " + message.getText());
            Log.d("MainActivity", "date is " + message.getDate());

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor c = database.query("homework", null, "id=" + message.getPk(), null, null, null, null);
            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put("id", message.getPk());
                values.put("subjectid", message.getSubject());
                values.put("homework", message.getText());
                values.put("date", message.getDate());
                database.insert("homework", null, values);
            }
        }
        android.os.Message m = new android.os.Message();
        m.what = UPDATE_TEXT;
        handler.sendMessage(m);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    MessageAdapter adapter = new MessageAdapter(MainActivity.this, R.layout.message_item, allMessage);
                    final ListView messageListView = (ListView) findViewById(R.id.messageListView);
                    messageListView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };
}
