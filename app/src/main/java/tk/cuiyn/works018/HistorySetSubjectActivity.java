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

public class HistorySetSubjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_set_subject);

        setTitle("选择科目");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final List<Subject> allSubject = new ArrayList<>();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor c = database.query("subject", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Subject subject = new Subject();
                subject.setPk(c.getInt(c.getColumnIndex("id")));
                Map<String, String> map = new HashMap<>();
                map.put("name", c.getString(c.getColumnIndex("name")));
                subject.setFields(map);
                allSubject.add(subject);
            } while (c.moveToNext());
        }

        ListView subjectListView = (ListView) findViewById(R.id.historySubjectListView);

        SubjectAdapter subjectAdapter = new SubjectAdapter(HistorySetSubjectActivity.this, R.layout.subject_item, allSubject);
        subjectListView.setAdapter(subjectAdapter);

        subjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = allSubject.get(position);
                Intent intent = new Intent(HistorySetSubjectActivity.this, HistoryMessageActivity.class);
                intent.putExtra("subjectID", subject.getPk());
                intent.putExtra("subjectName", subject.getName());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

}
