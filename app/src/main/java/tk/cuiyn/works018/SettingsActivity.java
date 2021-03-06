package tk.cuiyn.works018;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("设置");

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
                subject.setId(c.getInt(c.getColumnIndex("id")));
                subject.setName(c.getString(c.getColumnIndex("name")));

                allSubject.add(subject);
            } while (c.moveToNext());
        }

        ListView settingsSubjectListView = (ListView) findViewById(R.id.settingSubjectListView);
        SettingSubjectAdapter settingSubjectAdapter = new SettingSubjectAdapter(SettingsActivity.this, R.layout.settings_subject_item, allSubject);
        settingsSubjectListView.setAdapter(settingSubjectAdapter);

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
