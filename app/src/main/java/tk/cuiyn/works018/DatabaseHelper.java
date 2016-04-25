package tk.cuiyn.works018;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cuiyn on 16-4-25.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    DatabaseHelper(Context context)
    {
        super(context, "works.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String subject = "create table subject (id integer primary key, name text)";
        String homework = "create table homework (id integer primary key, date text, homework text, subjectid integer,FOREIGN KEY(subjectid) REFERENCES subject(id) )";
        db.execSQL(subject);
        db.execSQL(homework);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 更改数据库版本的操作
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // TODO 每次成功打开数据库后首先被执行
    }
}
