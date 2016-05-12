package tk.cuiyn.works018;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryMessageActivity extends AppCompatActivity {

    List<Message> allMessage = new ArrayList<>();

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

        ListView historyMessageListView = (ListView) findViewById(R.id.historyMessageListView);

        Intent intent = getIntent();

        setTitle(intent.getStringExtra("subjectName")+"历史通知");

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor chomework = database.query("homework", null, "subjectid="+intent.getIntExtra("subjectID", 0), null, null, null, null);
        if(chomework.getCount() != 0) {
            if (chomework.moveToLast()) {
                do {
                    Message message = new Message();
                    message.setPk(chomework.getInt(chomework.getColumnIndex("id")));
                    Map<String, String> fields = new HashMap<>();
                    fields.put("text", chomework.getString(chomework.getColumnIndex("homework")));
                    fields.put("date", chomework.getString(chomework.getColumnIndex("date")));
                    fields.put("subject", "" + chomework.getInt(chomework.getColumnIndex("subjectid")));
                    message.setFields(fields);
                    message.setSubjectName(intent.getStringExtra("subjectName"));
                    allMessage.add(message);
                } while (chomework.moveToPrevious());
            }
        }
        else {
            Message message = new Message();
            message.setPk(intent.getIntExtra("subjectID", 0));
            Map<String, String> fields = new HashMap<>();
            fields.put("text", "本科目暂无通知");
            fields.put("date", "1995-03-31T08:00:00.0000");
            fields.put("subject", intent.getStringExtra("subjectName"));
            message.setFields(fields);
            message.setSubjectName(intent.getStringExtra("subjectName"));
            allMessage.add(message);
        }

        MessageAdapter adapter = new MessageAdapter(HistoryMessageActivity.this, R.layout.message_item, allMessage);
        historyMessageListView.setAdapter(adapter);

        historyMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        database.close();
    }
}
