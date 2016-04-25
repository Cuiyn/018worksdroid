package tk.cuiyn.works018;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Created by cuiyn on 16-3-12.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    private int resourceId;

    public MessageAdapter(Context context, int textViewResourceId, List<Message> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        Message message = getItem(position);
        View view;

        if (convertView == null)
            view= LayoutInflater.from(getContext()).inflate(resourceId, null);
        else {
            view = convertView;
        }

        TextView subjectName = (TextView) view.findViewById(R.id.message_subject);
        TextView subjectDate = (TextView) view.findViewById(R.id.message_date);
        TextView subjectText = (TextView) view.findViewById(R.id.message_text);

        SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder("科目：" + message.getSubjectName());
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(24);
        spannableStringBuilder1.setSpan(absoluteSizeSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        subjectName.setText(spannableStringBuilder1);

        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder("发布日期：" + message.getDate().substring(0, 10) + " " + message.getDate().substring(11, 16));
        spannableStringBuilder2.setSpan(absoluteSizeSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        subjectDate.setText(spannableStringBuilder2);

        String text = message.getText();;
        if(message.getText().length() >= 36) {
            text = text.substring(0, 36);
            text+="…";
        }
        SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder("内容：" + text);
        spannableStringBuilder3.setSpan(absoluteSizeSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        subjectText.setText(spannableStringBuilder3);

        return view;
    }
}
