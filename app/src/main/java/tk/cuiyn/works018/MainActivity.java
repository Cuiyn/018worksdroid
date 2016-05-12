package tk.cuiyn.works018;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Map<Integer, String> allSubject = new HashMap<>();
    List<Message> allMessage = new ArrayList<>();
    public static final int UPDATE_TEXT = 1;

    ListView messageListView = null;

    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "正在刷新……", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    flush();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        messageListView = (ListView) findViewById(R.id.messageListView);

        Snackbar.make(messageListView, "正在刷新……", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        flush();

        MessageAdapter adapter = new MessageAdapter(MainActivity.this, R.layout.message_item, allMessage);

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
        messageListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int index;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_TOUCH_SCROLL:
                        index = view.getLastVisiblePosition();
                        break;
                    case SCROLL_STATE_IDLE:
                        int scrolled = view.getLastVisiblePosition();
                        if (scrolled > index) {
                            fab.hide();
                        } else {
                            fab.show();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_flush) {
            Snackbar.make(messageListView, "正在刷新……", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            flush();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_web) {
            Uri uri = Uri.parse("http://119.29.38.129:8000");
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(MainActivity.this, HistorySetSubjectActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clean) {
            allSubject.clear();
            allMessage.clear();
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            database.execSQL("delete from homework");
            database.execSQL("delete from subject");
            database.close();
            Toast.makeText(MainActivity.this, "清除成功～", Toast.LENGTH_SHORT).show();
            flush();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void sendRequestWithHttpClient(String method) {
        final String m = method;
        Thread thread = new Thread(new Runnable() {
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

                    if (m.equals("subject")) {
                        subjectparseJSONWithGSON(str);
                    } else {
                        messageparseJSONWithGSON(str);
                    }
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
            database.close();
        }
    }

    private void messageparseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Message> messagesList = gson.fromJson(jsonData, new TypeToken<List<Message>>() {
        }.getType());
        for (Message message : messagesList) {
            message.setSubjectName(allSubject.get(message.getSubject()));
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
                    MessageAdapter adapter = new MessageAdapter(MainActivity.this, R.layout.message_item, allMessage);
                    final ListView messageListView = (ListView) findViewById(R.id.messageListView);
                    messageListView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    private void flush() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        allSubject.clear();
        allMessage.clear();


        boolean networkState = NetworkDetector.detect(MainActivity.this);
        if (networkState) {
            database.execSQL("delete from homework");
            //database.execSQL("delete from subject");
            sendRequestWithHttpClient("subject");
            sendRequestWithHttpClient("all");
        } else {
            Cursor csubject = database.query("subject", null, null, null, null, null, null);
            if (csubject.getCount() == 0)
                Toast.makeText(MainActivity.this, "无网络连接，请打开Wifi或数据流量", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(MainActivity.this, "无网络连接，仅显示本地数据", Toast.LENGTH_SHORT).show();
                if (csubject.moveToFirst()) {
                    do {
                        allSubject.put(csubject.getInt(csubject.getColumnIndex("id")), csubject.getString(csubject.getColumnIndex("name")));
                    } while (csubject.moveToNext());
                }
                Cursor chomework = database.query("homework", null, null, null, null, null, null);
                if (chomework.moveToLast()) {
                    do {
                        int subjectid = chomework.getInt(chomework.getColumnIndex("subjectid"));
                        Cursor c = database.query("subject", null, "id=" + subjectid, null, null, null, null);
                        if (c.getCount() != 0) {
                            if (c.moveToFirst()) {
                                do {
                                    int show = c.getInt(c.getColumnIndex("isView"));
                                    if (show == 1) {
                                        Message message = new Message();
                                        message.setPk(chomework.getInt(chomework.getColumnIndex("id")));
                                        Map<String, String> fields = new HashMap<>();
                                        fields.put("text", chomework.getString(chomework.getColumnIndex("homework")));
                                        fields.put("date", chomework.getString(chomework.getColumnIndex("date")));
                                        fields.put("subject", "" + chomework.getInt(chomework.getColumnIndex("subjectid")));
                                        message.setFields(fields);
                                        message.setSubjectName(allSubject.get(message.getSubject()));
                                        allMessage.add(message);
                                    }
                                } while (c.moveToNext());
                            }
                        }
                    } while (chomework.moveToPrevious());
                }
                android.os.Message m = new android.os.Message();
                m.what = UPDATE_TEXT;
                handler.sendMessage(m);
                MessageAdapter adapter = (MessageAdapter) messageListView.getAdapter();
                try {
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        database.close();
    }
}
