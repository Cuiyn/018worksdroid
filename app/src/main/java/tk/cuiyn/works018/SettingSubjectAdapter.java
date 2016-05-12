package tk.cuiyn.works018;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by cuiyn on 16-5-11.
 */
public class SettingSubjectAdapter extends ArrayAdapter<Subject> {
    private int resourceId;

    public SettingSubjectAdapter(Context context, int textViewResourceId, List<Subject> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final Subject subject = getItem(position);
        final View view;
        if (convertView == null)
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        else {
            view = convertView;
        }

        TextView subjectName = (TextView) view.findViewById(R.id.settings_subject_name);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.settings_subject_checkbox);

        subjectName.setText(subject.getName());

        DatabaseHelper dbHelper = new DatabaseHelper(super.getContext());
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        int show = 0;
        Cursor c = database.query("subject", null, "id="+subject.getPk(), null, null, null, null);
        if(c.getCount() != 0) {
            if (c.moveToFirst()) {
                do {
                    show = c.getInt(c.getColumnIndex("isView"));
                } while (c.moveToNext());
            }
        }
        if (show == 1)
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentValues contentValues = new ContentValues();
                if (isChecked)
                {
                    contentValues.put("isView", 1);
                }
                else {
                    contentValues.put("isView", 0);
                }

                database.update("subject", contentValues, "id="+subject.getPk(), null);

            }
        });

        return view;
    }
}
