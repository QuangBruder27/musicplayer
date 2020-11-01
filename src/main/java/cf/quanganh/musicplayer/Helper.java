package cf.quanganh.musicplayer;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;

import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;

public class Helper {

    public static void makeoriginalList(DBHelper mydb){
        AudioPlayer.originalList = Helper.getDatafromDB(mydb);
        System.out.println("Originallist is empty: "+AudioPlayer.originalList.isEmpty());
    }

    public static String getLanguageFromSP(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Lang", Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString("Lang", "vi");
        return lang;
    }

    public static void changeLanguage(Context context, String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static ArrayList<Song> getDatafromDB(DBHelper mydb){
        System.out.println("Function getDatafromDB");
        ArrayList<Song> list = mydb.getSongList();
        return list;
    }

    public static void createAndInserttoDB(ArrayList<Song> songList, DBHelper mydb){

        // Insert to DB
        Song s;
        for(int i=0;i< songList.size();i++) {
            s = songList.get(i);
            if(mydb.insertSong(s.getPath(),s.getName(),s.getDuration(),s.getSize(),s.getComposer(), s.getImg(),"false")){
                //System.out.println("Added the song"+s.getName());
            } else {
                System.out.println("Add failed");
            }
        }
    }

   public static int getSongIndexArrayList(ArrayList<Song> list, String name){
        int result = list.size()+1;
        for (int i=0; i< list.size();i++){
            if(list.get(i).getName().equals(name)){
             result = i;
            }
        }
        return  result;
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static void setTextforAppWidget(Context c, String str_currentDuration, String str_totalDuration, int image){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
        RemoteViews remoteViews = new RemoteViews(c.getPackageName(), R.layout.new_app_widget);
        ComponentName thisWidget = new ComponentName(c, NewAppWidget.class);
        remoteViews.setTextViewText(R.id.wgsongname, AudioPlayer.song.getName());
        remoteViews.setTextViewText(R.id.wgartist, AudioPlayer.song.getComposer());
        remoteViews.setTextViewText(R.id.wgcurrentduration, str_currentDuration);
        remoteViews.setTextViewText(R.id.wgtotalduration, str_totalDuration);

        if(image != 0) {
            remoteViews.setImageViewResource(R.id.imgbtnwidgetPlay, image);
        }
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    public static void randomSongSelect(ArrayList<Song> playlist){
        int max = playlist.size()-1;
        int min = 0;
        int x = (int)(Math.random() * ((max - min) + 1)) + min;
        System.out.println("X = "+x);
        AudioPlayer.song = playlist.get(x);
    }

    final static String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/";

    public static ArrayList<Song> addFiletoSongList(Context context){
        ArrayList<Song> list = new ArrayList<Song>();
        System.out.println("Function scanDevice");
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Albums.ALBUM_ID
        };

        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri, projection, selection, null, sortOrder);

            if( cursor != null){
                cursor.moveToFirst();
                while( !cursor.isAfterLast() ){
                    String path = cursor.getString(2);
                    if(path != null) {
                        String title = cursor.getString(0);
                        String artist = cursor.getString(1);
                        //String displayName  = cursor.getString(3);
                        String songDuration = Helper.milliSecondsToTimer(cursor.getLong(4));
                        String songSize = cursor.getString(5);
                        String albumID = cursor.getString(6);
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(path);
                        byte[] artBytes =  mmr.getEmbeddedPicture();
                        Bitmap bm;
                        if(artBytes != null){
                            InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                            bm = BitmapFactory.decodeStream(is);
                        } else {
                            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_ava);
                        }
                        list.add(new Song(path,title,songDuration,songSize, artist, Helper.getImageUri(context,bm).toString(),"false"));
                    }
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }finally{
            if( cursor != null){
                cursor.close();
            }
        }
        return list;
    }

    public static ArrayList<Song> scan(Context context){
        System.out.println("Function scan");
        if (MEDIA_PATH != null) {
            File home = new File(MEDIA_PATH);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    //System.out.println(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file, context);
                    } else {
                        scanNewFile(file, context);
                    }
                }
            }
        }
        // return songs list array
        return addFiletoSongList(context);
    }

    public static void scanDirectory(File directory, Context context){
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file, context);
                    } else {
                        scanNewFile(file, context);
                    }
                }
            }
        }
    }

    public static void scanNewFile(File file, Context context){
        if ( file.getPath().endsWith(".mp3") || file.getPath().endsWith(".MP3"))
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

}
