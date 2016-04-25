package tk.cuiyn.works018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();

        TextView subjectName = (TextView) findViewById(R.id.message_subject);
        TextView subjectDate = (TextView) findViewById(R.id.message_date);
        TextView subjectText = (TextView) findViewById(R.id.message_text);

        SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder("科目：" + intent.getStringExtra("subject"));
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(24);
        spannableStringBuilder1.setSpan(absoluteSizeSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        subjectName.setText(spannableStringBuilder1);

        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder("发布日期：" + intent.getStringExtra("date").substring(0, 10) + " " + intent.getStringExtra("date").substring(11, 16));
        spannableStringBuilder2.setSpan(absoluteSizeSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        subjectDate.setText(spannableStringBuilder2);

        SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder("内容：" + intent.getStringExtra("text"));
        spannableStringBuilder3.setSpan(absoluteSizeSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        subjectText.setText(spannableStringBuilder3);
    }
}
