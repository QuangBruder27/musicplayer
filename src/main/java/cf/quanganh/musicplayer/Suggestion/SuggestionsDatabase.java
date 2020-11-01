package cf.quanganh.musicplayer.Suggestion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import cf.quanganh.musicplayer.objects.Song;

public class SuggestionsDatabase {

    public static final String DB_SUGGESTION = "SUGGESTION_DB";
    public final static String TABLE_SUGGESTION = "SUGGESTION_TB";
    public final static String FIELD_ID = "_id";
    public final static String FIELD_SUGGESTION = "suggestion";

    public SQLiteDatabase db;
    public SuggestionsDatabaseHelper helper;

    public SuggestionsDatabase(Context context) {
        helper = new SuggestionsDatabaseHelper(context, DB_SUGGESTION, null, 1);
        db = helper.getWritableDatabase();
    }

    public long insertSuggestion(String text) {
        ContentValues values = new ContentValues();
        values.put(FIELD_SUGGESTION, text);
        return db.insert(TABLE_SUGGESTION, null, values);
    }

    public  void insertAllSuggestion(ArrayList<Song> list){
        for (Song s : list){
            //System.out.println("Add song:"+s.getName()+" to SG List");
            insertSuggestion(s.getName());
        }
    }

    public Cursor getSuggestions(String text) {
        return db.query(TABLE_SUGGESTION, new String[] {FIELD_ID, FIELD_SUGGESTION},
                FIELD_SUGGESTION+" LIKE '"+ text +"%'", null, null, null, null);
    }

    public class SuggestionsDatabaseHelper extends SQLiteOpenHelper {
        public SuggestionsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SUGGESTION);
            db.execSQL("CREATE TABLE "+TABLE_SUGGESTION+" ("+
                    FIELD_ID+" integer primary key autoincrement, "+FIELD_SUGGESTION+" text, UNIQUE ("+FIELD_SUGGESTION +") ON CONFLICT REPLACE)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SUGGESTION);
            onCreate(db);
        }

    }
}
