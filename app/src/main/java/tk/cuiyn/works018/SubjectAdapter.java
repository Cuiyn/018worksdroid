package tk.cuiyn.works018;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by cuiyn on 16-5-11.
 */
public class SubjectAdapter extends ArrayAdapter<Subject> {
    private int resourceId;

    public SubjectAdapter(Context context, int textViewResourceId, List<Subject> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Subject subject = getItem(position);
        View view;
        if (convertView == null)
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        else {
            view = convertView;
        }

        TextView subjectName = (TextView) view.findViewById(R.id.subject_name);
        subjectName.setText(subject.getName());

        return view;
    }
}
