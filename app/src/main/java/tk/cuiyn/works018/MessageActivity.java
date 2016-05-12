package tk.cuiyn.works018;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.text.Html;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();

        TextView subjectName = (TextView) findViewById(R.id.message_subject);
        TextView subjectDate = (TextView) findViewById(R.id.message_date);
        TextView subjectText = (TextView) findViewById(R.id.message_text);

        String name = "<b>科目：</b>" + intent.getStringExtra("subject");
        String date = "<b>发布日期：</b>" + intent.getStringExtra("date");
        String text = "<b>内容：</b>" + intent.getStringExtra("text");

        subjectName.setText(Html.fromHtml(name));
        subjectDate.setText(Html.fromHtml(date));
        subjectText.setText(Html.fromHtml(text));

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
