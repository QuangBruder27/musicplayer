package cf.quanganh.musicplayer;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import cf.quanganh.musicplayer.objects.Song;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MusicApp.db";
    public static final String MP_TABLE_NAME = "songs";
    public static final String MP_ID = "id";
    public static final String MP_PATH = "path";
    public static final String MP_NAME = "name";
    public static final String MP_DURATION = "duration";
    public static final String MP_SIZE = "size";
    public static final String MP_COMPOSER = "composer";
    public static final String MP_IMG = "image";
    public static final String MP_ISLIKED = "isLiked";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Try to create table");
        db.execSQL("DROP TABLE IF EXISTS "+MP_TABLE_NAME);
        String s = "CREATE TABLE "+MP_TABLE_NAME +
                "("+MP_ID+" integer primary key, "+MP_PATH+" text, "+MP_NAME+" text, " +
                MP_DURATION + " text, "+MP_SIZE+" text, "+MP_COMPOSER+" text, "+MP_IMG+" text, "+MP_ISLIKED+" text);";
        db.execSQL(s);
        System.out.println("Create DB successfylly");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MP_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertSong(String path, String name, String duration, String size, String composer,String img, String isLiked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MP_PATH, path);
        contentValues.put(MP_NAME, name);
        contentValues.put(MP_DURATION, duration);
        contentValues.put(MP_SIZE, size);
        contentValues.put(MP_COMPOSER, composer);
        contentValues.put(MP_IMG, img);
        contentValues.put(MP_ISLIKED,isLiked);
        db.insert(MP_TABLE_NAME, null, contentValues);
        return true;
    }

    public Song getSongById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MP_TABLE_NAME, new String[] { MP_ID,
                        MP_PATH, MP_NAME, MP_DURATION,MP_SIZE, MP_COMPOSER, MP_IMG, MP_ISLIKED }, MP_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Song s = new Song(cursor.getString(1),cursor.getString(2)
                ,cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6), cursor.getString(7));
        cursor.close();
        db.close();
        return s;
    }

    public Song getSongByName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MP_TABLE_NAME, new String[] { MP_ID,
                        MP_PATH, MP_NAME, MP_DURATION,MP_SIZE, MP_COMPOSER, MP_IMG, MP_ISLIKED }, MP_NAME + "=?",
                new String[] { name }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Song s = new Song(cursor.getString(1),cursor.getString(2)
                ,cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6), cursor.getString(7));
        cursor.close();
        db.close();
        return s;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MP_TABLE_NAME);
        return numRows;
    }

    public Integer deleteSong (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MP_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer getID(String path){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM "+MP_TABLE_NAME+
                " WHERE "+MP_PATH+" ='"+path+"';", null );
        if (res != null) res.moveToFirst();
        return Integer.valueOf(res.getString(0));
    }


    public ArrayList<Song> getSongList() {
        ArrayList<Song> array_list = new ArrayList<Song>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + MP_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Song s = new Song();
                s.setPath(cursor.getString(1));
                s.setName(cursor.getString(2));
                s.setDuration(cursor.getString(3));
                s.setSize(cursor.getString(4));
                s.setComposer(cursor.getString(5));
                s.setImg(cursor.getString(6));
                s.setLiked(Boolean.valueOf(cursor.getString(7)));

                array_list.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return array_list;
    }

    public ArrayList<Song> getFavouriteSongList() {
        ArrayList<Song> array_list = new ArrayList<Song>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + MP_TABLE_NAME+" WHERE "+ MP_ISLIKED+" = 'true'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Song s = new Song();
                s.setPath(cursor.getString(1));
                s.setName(cursor.getString(2));
                s.setDuration(cursor.getString(3));
                s.setSize(cursor.getString(4));
                s.setComposer(cursor.getString(5));
                s.setImg(cursor.getString(6));
                s.setLiked(Boolean.valueOf(cursor.getString(7)));

                array_list.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return array_list;
    }

    public void updateSong(Song song){
        SQLiteDatabase db = this.getWritableDatabase();
        String str = "UPDATE "+ MP_TABLE_NAME+
                " SET "+ MP_ISLIKED+ " = '"+ song.isLiked()+
                "' WHERE "+ MP_NAME+ " = '"+ song.getName()+"';";
        db.execSQL(str);
    }

    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ MP_TABLE_NAME);
    }

}