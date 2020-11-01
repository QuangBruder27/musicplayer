package cf.quanganh.musicplayer.objects;

import android.media.MediaPlayer;
import java.util.ArrayList;

public class AudioPlayer {


    public static Song song;
    public static String songName = "";
    public static MediaPlayer player;
    public static boolean isplayingAudio=false;
    public static boolean ispausingAudio=false;
    public static ArrayList<String> playedList = new ArrayList<String>();
    public static ArrayList<Song> playedSongList = new ArrayList<Song>();
    public static ArrayList<Song> originalList = new ArrayList<Song>();
    public static ArrayList<Song> myPlayList = new ArrayList<Song>();
    public static ArrayList<Song> playList = new ArrayList<Song>();
    public static ArrayList<Song> playListbySinger = new ArrayList<Song>();
    public static boolean isWidgetAdded = false;
    public static boolean playFavouriteSong = false;


    public static void addtoPlayedList(String songName){
        playedList.add(songName);
    }

    public static ArrayList<String> getPlayedList(){
        return playedList;
    }

    public static void playAudio(String path, String name){
        songName = name;
        player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.toString());
            e.printStackTrace();
        }
        if(!player.isPlaying()){
            isplayingAudio=true;
            addtoPlayedList(name);
            player.start();
        }
    }

    public static void stopAudio(){
        isplayingAudio=false;
        player.stop();
    }

    public static void pauseAudio(){
        ispausingAudio = true;
        isplayingAudio=false;
        player.pause();
    }

    public static void resumePlay(){
        ispausingAudio = false;
        isplayingAudio= true;
        player.start();
    }

    public static boolean songisEmpty(){
        if (song == null) return true;
        return false;
    }
}
