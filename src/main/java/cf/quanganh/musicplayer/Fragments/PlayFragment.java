package cf.quanganh.musicplayer.Fragments;

import android.animation.ObjectAnimator;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;

import cf.quanganh.musicplayer.DBHelper;
import cf.quanganh.musicplayer.Helper;
import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.NewAppWidget;
import cf.quanganh.musicplayer.OnSwipeTouchListener;
import cf.quanganh.musicplayer.R;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;

import static cf.quanganh.musicplayer.MainActivity.relativeLayout;

public class PlayFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    static String selectedSongName = "";
    ImageButton btnBack, btnPlay, btnNext, btnShuffle, btnLike, btnDisplayPlaylist;
    ImageView imageView;
    TextView name, duration, currentPosition;
    //TextView composer;
    ObjectAnimator anim;
    ArrayList<Song> shuffledList;
    RelativeLayout layout;
    ArrayList<String> playedSongList = new ArrayList<String>();

    int songIndex = 0;
    DBHelper mydb;
    SeekBar seekBar;
    boolean isShuffleActived = false;
    boolean isRepeatActived = false;
    public static boolean havetoPlayNewSong = true;
    static boolean havetoPlay = true;
    static boolean playFavouriteSong = false;


    public static PlayFragment newInstance(String name, boolean havetoplayNewSong, boolean havetoplay, boolean playfavouriteSong){
        PlayFragment f = new PlayFragment();
        System.out.println("newInstance name: "+name);
        selectedSongName = name;
        havetoPlayNewSong = havetoplayNewSong;
        havetoPlay = havetoplay;
        playFavouriteSong = playfavouriteSong;
        return  f;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("Select smt");
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                gotoListViewFragment();
                //getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        if(MainActivity.drawerToggle != null)
        MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        setHasOptionsMenu(true);

        layout = (RelativeLayout) view.findViewById(R.id.layoutPlay);
        layout.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft(){
                gotoListViewFragment();
                //getActivity().onBackPressed();
            }
        });

        System.out.println("Play Fragment with name:"+selectedSongName);
        mydb = new DBHelper(getActivity());

        if (AudioPlayer.originalList.isEmpty()){
            AudioPlayer.originalList = Helper.getDatafromDB(new DBHelper(this.getActivity()));
        }

        if (selectedSongName.equals("")) selectedSongName = AudioPlayer.originalList.get(0).getName();

        //System.out.println("SelectedsongName = "+ selectedSongName);

        if(playFavouriteSong){
            AudioPlayer.playList = new DBHelper(getContext()).getFavouriteSongList();
            //if (havetoPlayNewSong) {
            //    selectedSongName = AudioPlayer.playList.get(0).getName();}
        } else {
            AudioPlayer.playList = AudioPlayer.originalList;
        }

        shuffledList = Helper.getDatafromDB(mydb);

        songIndex = Helper.getSongIndexArrayList(AudioPlayer.originalList, selectedSongName);

        if (!AudioPlayer.originalList.isEmpty()) {
            AudioPlayer.song = AudioPlayer.originalList.get(songIndex);
        }

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        btnBack = (ImageButton) view.findViewById(R.id.imgbtnBack);
        btnPlay = (ImageButton) view.findViewById(R.id.imgbtnPlay);
        btnNext = (ImageButton) view.findViewById(R.id.imgbtnNext);
        btnShuffle = (ImageButton) view.findViewById(R.id.imgbtnShuffle);
        btnLike = (ImageButton) view.findViewById(R.id.imgbtnLike);
        btnDisplayPlaylist = (ImageButton) view.findViewById(R.id.imgbtnDisplayDialog);


        btnShuffle.setImageResource(R.drawable.ic_shuffle);
        btnShuffle.setTag(R.drawable.ic_shuffle);

        btnBack.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnDisplayPlaylist.setOnClickListener(this);

        imageView = (ImageView) view.findViewById(R.id.imgAvatar);
        name = (TextView) view.findViewById(R.id.textview_songname);
        //composer = (TextView) view.findViewById(R.id.textview_composername);
        duration = (TextView) view.findViewById(R.id.textview_duration);
        currentPosition = (TextView) view.findViewById(R.id.textview_currentPosition);

        //songPlayer();

        System.out.println("havetoPlayNewSong is "+havetoPlayNewSong);

        if(havetoPlayNewSong) {
            play(getActivity(), 2);
        } else {
            resume(2,getActivity());
        }
        if (!havetoPlay){
            pause(2,getActivity());
        }

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        // you can add listener of elements here
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void activeShuffle(){
        isShuffleActived = true;
        Collections.shuffle(shuffledList);
        AudioPlayer.playList = shuffledList;
    }

    void deactiveShuffle(){
        isShuffleActived = false;
        AudioPlayer.playList = AudioPlayer.originalList;
    }

    void activeRepeat(){
        isRepeatActived = true;
    }

    void deactiveRepeat(){
        isRepeatActived = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgbtnShuffle:    // Button Shuffle
                if((Integer)btnShuffle.getTag() == R.drawable.ic_shuffle){
                    Toast.makeText(getContext(), R.string.random, Toast.LENGTH_SHORT).show();
                    deactiveRepeat();
                    activeShuffle();
                    btnShuffle.setImageResource(R.drawable.ic_repeat);
                    btnShuffle.setTag(R.drawable.ic_repeat);
                } else if((Integer)btnShuffle.getTag() == R.drawable.ic_repeat) {
                    Toast.makeText(getContext(), R.string.repeat1, Toast.LENGTH_SHORT).show();
                    deactiveShuffle();
                    activeRepeat();
                    btnShuffle.setImageResource(R.drawable.ic_repeatall);
                    btnShuffle.setTag(R.drawable.ic_repeatall);
                System.out.println("btn Shuffle 1");
                } else {
                    Toast.makeText(getContext(), R.string.norepeat, Toast.LENGTH_SHORT).show();
                    deactiveShuffle();
                    deactiveRepeat();
                    btnShuffle.setImageResource(R.drawable.ic_shuffle);
                    btnShuffle.setTag(R.drawable.ic_shuffle);
                System.out.println("btn Shuffle 2");
                }
                break;

            case R.id.imgbtnLike: // Button Repeat
                if((Integer)btnLike.getTag() == R.drawable.icon_star){
                    AudioPlayer.song.setLiked(true);
                    ListViewFragment.setFavoriteSong(AudioPlayer.song);
                    btnLike.setImageResource(R.drawable.icon_star_selected);
                    btnLike.setTag(R.drawable.icon_star_selected);
                } else {
                    AudioPlayer.song.setLiked(false);
                    ListViewFragment.setFavoriteSong(AudioPlayer.song);
                    btnLike.setImageResource(R.drawable.icon_star);
                    btnLike.setTag(R.drawable.icon_star);
                }
                break;

            case R.id.imgbtnBack:   // Button back
                playPreviousSong(getActivity(), 2);
                break;

            case R.id.imgbtnPlay:   // Button Play/Pause
                // Pause
                if (AudioPlayer.isplayingAudio) {
                    //Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
                    System.out.println("Pause "+ anim.toString());
                    pause(2, getActivity());
                } else {
                    //Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
                    System.out.println("Play "+anim.toString());
                    // Resume Play
                    resume(2, getActivity());
                }
                break;

            case R.id.imgbtnNext:   // Button Next
                playNextSong(getActivity(), 2);
                break;

            case R.id.imgbtnDisplayDialog:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                playlistDialogFragment = PlaylistDialogFragment.newInstance();
                playlistDialogFragment.show(fm, "dialog");
                break;
        }
    }

    public static PlaylistDialogFragment playlistDialogFragment;

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public static boolean isPlayed = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void widgetSongPlayer(Context context){

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        ComponentName thisWidget = new ComponentName(context, NewAppWidget.class);

        if (AudioPlayer.isplayingAudio){
            //Pause
            remoteViews.setImageViewResource(R.id.imgbtnwidgetPlay, R.drawable.wg_play);
            System.out.println("Pause with song: "+ AudioPlayer.song);
            pause(0, context);

        } else {
            remoteViews.setImageViewResource(R.id.imgbtnwidgetPlay, R.drawable.wg_pause);
            if (isPlayed){
                // Play resume
                resume(0, context);
            } else {
                // Play random
                randomPlay(context);
            }
        }
        isPlayed = true;
        remoteViews.setTextViewText(R.id.wgsongname, AudioPlayer.song.getName());
        remoteViews.setTextViewText(R.id.wgartist, AudioPlayer.song.getComposer());

        //NewAppWidget.updateTimer();
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        updateProgress();
    }

    void randomPlay(Context context){
        if (AudioPlayer.originalList.isEmpty()){
            mydb = new DBHelper(context);
            AudioPlayer.originalList = Helper.getDatafromDB(mydb);
        }
        AudioPlayer.playList = AudioPlayer.originalList;
        Helper.randomSongSelect(AudioPlayer.playList);
        play(context, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume(){
        super.onResume();
        //relativeLayout.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.GONE);
        if (ListViewFragment.searchView != null) {
            ListViewFragment.searchView.clearFocus();
        }
        songPlayer();
        if (AudioPlayer.isplayingAudio){
            btnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            anim.pause();
            btnPlay.setImageResource(R.drawable.icon_play);
        }
    }

    public void songPlayer() {
        // Set Text, Image, Seekbarm, Rotation
        System.out.println("Function songPlayer: "+AudioPlayer.song.toString());


        // Create Notification
        //MainActivity.createNotification(getActivity(),AudioPlayer.song);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(AudioPlayer.song.getName());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(AudioPlayer.song.getComposer());

        // Get my play list
        AudioPlayer.myPlayList = new DBHelper(getContext()).getFavouriteSongList();
        int index = Helper.getSongIndexArrayList(AudioPlayer.myPlayList,AudioPlayer.song.getName());
        if(index < AudioPlayer.myPlayList.size()){
            btnLike.setImageResource(R.drawable.icon_star_selected);
            btnLike.setTag(R.drawable.icon_star_selected);
        } else {
            btnLike.setImageResource(R.drawable.icon_star);
            btnLike.setTag(R.drawable.icon_star);
        }

        Picasso.get().load(Uri.parse(AudioPlayer.song.getImg())).into(imageView);
        name.setText(AudioPlayer.song.getName());
        //composer.setText(AudioPlayer.song.getComposer());
        duration.setText(AudioPlayer.song.getDuration());
        seekBar.setProgress(50);
        seekBar.setMax(100);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        updateProgress();
        //play();
        rotation();
        playedSongList.add(AudioPlayer.song.getName());
        //ViewPagerAdapter.list.add(AudioPlayer.song.getName());
    }

    private void rotation() {
        if(null != anim && anim.isStarted())anim.cancel();
        anim = ObjectAnimator.ofFloat(imageView, "rotation", 0, 1440);
        anim.setDuration(60000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resume(int requestcode, Context context){
       /*
        requestcode = 0: Widget
        requestcode = 1: ListView Fragment
        requestcode = 2: Play Fragment
         */
        System.out.println("Func Resume");

        AudioPlayer.resumePlay();
        if (requestcode != 0) {
            System.out.println("Set icon for widget Resume");

            String str_currentDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getCurrentPosition());
            String str_totalDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getDuration());

            Helper.setTextforAppWidget(context,str_currentDuration,str_totalDuration,0);
        }
        if (requestcode == 2){
            btnPlay.setImageResource(R.drawable.ic_pause);
            if (anim == null){
                System.out.println("anim is null");
            }
            if (anim == null) {
                rotation();
            } else {
                anim.resume();
            }
        }
        isPlayed = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pause(int requestcode, Context context){
        /*
        requestcode = 0: Widget
        requestcode = 1: ListView Fragment
        requestcode = 2: Play Fragment
         */
        //System.out.println("Func Pause");
        AudioPlayer.pauseAudio();
        if (requestcode != 0) {
            //Context context = getActivity();
            System.out.println("Func Pause "+context.toString());

            String str_currentDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getCurrentPosition());
            String str_totalDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getDuration());
            Helper.setTextforAppWidget(context,str_currentDuration,str_totalDuration,0);
        }
        if (requestcode == 2){
            btnPlay.setImageResource(R.drawable.icon_play);
            anim.pause();
        }
        isPlayed = true;
    }

    public void play(final Context context, final int requestcode) {
        /*
        requestcode = 0: Widget
        requestcode = 1: ListView Fragment
        requestcode = 2: Play Fragment
         */
        // Play Audio

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Song",AudioPlayer.song.getName());
        editor.apply();

        if (AudioPlayer.isplayingAudio) {
            AudioPlayer.stopAudio();
        }

        AudioPlayer.playAudio(AudioPlayer.song.getPath(), AudioPlayer.song.getName());

        if(requestcode == 2) {
            btnPlay.setImageResource(R.drawable.ic_pause);
        }
        //Context context = getActivity();
        //if(requestcode != 0) {
        String str_currentDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getCurrentPosition());
        String str_totalDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getDuration());

        if (AudioPlayer.isWidgetAdded) {
            NewAppWidget.updateTimer();
        }

        Helper.setTextforAppWidget(context,str_currentDuration,str_totalDuration,0);

        if (getActivity() != null) {
            AudioPlayer.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //Toast.makeText(PlayActivity.this, "Finish", Toast.LENGTH_SHORT).show();
                    anim.cancel();
                    if (isRepeatActived){
                        play(context, requestcode);
                        rotation();
                    } else if(isShuffleActived) {
                        AudioPlayer.song = shuffledList.get(Helper
                                .getSongIndexArrayList(shuffledList, AudioPlayer.song.getName())+1);
                    }
                    else {
                        playNextSong(context, requestcode);
                    }
                }
            });
        }
            ListViewFragment.setTextAndButton();
    }

    public void playPreviousSong(Context context, int requestcode){
        songIndex = Helper.getSongIndexArrayList(AudioPlayer.playList, AudioPlayer.song.getName());
        if (songIndex < 1) {
            songIndex = AudioPlayer.playList.size() - 1;
        } else {
            songIndex -= 1;
        }
        //Toast.makeText(this, "ID =" + songIndex, Toast.LENGTH_LONG).show();
        AudioPlayer.song = AudioPlayer.playList.get(songIndex);
        if (getActivity() != null)  songPlayer();
        play(context, requestcode);
    }

    public void playNextSong(Context context, int requestcode){
        songIndex = Helper.getSongIndexArrayList(AudioPlayer.playList, AudioPlayer.song.getName());
        if (songIndex > AudioPlayer.playList.size() - 2) {
            songIndex = 0;
        } else {
            songIndex += 1;
        }
        //Toast.makeText(this, "ID =" + songIndex, Toast.LENGTH_LONG).show();
        AudioPlayer.song = AudioPlayer.playList.get(songIndex);
        if (getActivity() != null)songPlayer();
        play(context, requestcode);
    }

    private Handler mHandler = new Handler();

    public void updateProgress() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = AudioPlayer.player.getDuration();
            long currentDuration = AudioPlayer.player.getCurrentPosition();
            String str_currentDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getCurrentPosition());
            String str_totalDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getDuration());
            // Displaying time completed playing
            if(seekBar != null) {
                currentPosition.setText("" + str_currentDuration);
                // Updating progress bar
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                seekBar.setProgress(progress);
            }
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;
        // return percentage
        return percentage.intValue();
    }

    void gotoListViewFragment(){
        Fragment fragment = ListViewFragment.newInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

}

