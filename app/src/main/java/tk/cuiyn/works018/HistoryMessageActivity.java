package tk.cuiyn.works018;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryMessageActivity extends AppCompatActivity {

    List<Message> allMessage = new ArrayList<>();
    public static final int UPDATE_TEXT = 1;
    public String subjectName;
    private DatabaseHelper dbHelper = new DatabaseHelper(this);
    ListView messageListView = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_message);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        messageListView = (ListView) findViewById(R.id.historyMessageListView);

        Intent intent = getIntent();

        subjectName = intent.getStringExtra("subjectName");

        setTitle(subjectName +"历史通知");


        sendRequestWithHttpClient(subjectName);


        MessageAdapter adapter = new MessageAdapter(HistoryMessageActivity.this, R.layout.message_item, allMessage);

        messageListView.setAdapter(adapter);
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = allMessage.get(position);
                Intent intent = new Intent(HistoryMessageActivity.this, MessageActivity.class);
                intent.putExtra("subject", message.getSubjectName());
                intent.putExtra("date", message.getDate());
                intent.putExtra("text", message.getText());
                startActivity(intent);
            }
        });
    }

    private void sendRequestWithHttpClient(String subject) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String urlstring = "http://119.29.38.129:8000/newjson/?m=h&p=1&s=" + subjectName;
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

                    messageparseJSONWithGSON(str);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void messageparseJSONWithGSON(String jsonData) {
        Log.d("MainActivity", jsonData);
        Gson gson = new Gson();
        List<Message> messagesList = gson.fromJson(jsonData, new TypeToken<List<Message>>() {}.getType());
        for (Message message : messagesList) {
            message.setSubjectName(subjectName);
            Log.d("MainActivity", "subject name is " + subjectName);
            Log.d("MainActivity", "subject id is " + message.getSubject());
            Log.d("MainActivity", "text is " + message.getText());
            Log.d("MainActivity", "date is " + message.getDate());

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor c = database.query("homework", null, "id=" + message.getId(), null, null, null, null);
            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put("id", message.getId());
                values.put("subjectid", message.getSubject());
                values.put("homework", message.getText());
                values.put("date", message.getDate());
                database.insert("homework", null, values);
            }

            c = database.query("subject", null, "id=" + message.getSubject(), null, null, null, null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    do {
                        int show = c.getInt(c.getColumnIndex("isView"));
                        if (show == 1)
                            allMessage.add(message);
                    } while (c.moveToNext());
                }
            }
            database.close();
        }
        android.os.Message m = new android.os.Message();
        m.what = UPDATE_TEXT;
        handler.sendMessage(m);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    MessageAdapter adapter = new MessageAdapter(HistoryMessageActivity.this, R.layout.message_item, allMessage);
                    messageListView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };
}
