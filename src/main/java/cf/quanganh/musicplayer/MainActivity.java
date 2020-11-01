package cf.quanganh.musicplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import cf.quanganh.musicplayer.Fragments.ListViewFragment;
import cf.quanganh.musicplayer.Fragments.PlayFragment;
import cf.quanganh.musicplayer.Suggestion.SuggestionsDatabase;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;

import static cf.quanganh.musicplayer.Fragments.PlayFragment.newInstance;

public class MainActivity extends AppCompatActivity{


    public static DrawerLayout drawerLayout;
    public static ActionBarDrawerToggle drawerToggle;
    public static NavigationView navigationView;

    public static RelativeLayout relativeLayout;
    public static LinearLayout linearLayout;
    public static TextView tvSongName, tvArtistName;
    public static ImageButton btnBack, btnPlay, btnNext;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;
    }

    @Override
    public void onBackPressed(){
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        String lang = Helper.getLanguageFromSP(this);
        System.out.println("App Language: "+lang);
        Helper.changeLanguage(this, lang);
         */

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Locale current = getResources().getConfiguration().locale;
        System.out.println("Language: "+current.toString());

        relativeLayout = (RelativeLayout) findViewById(R.id.lv_layout);
        linearLayout = (LinearLayout) findViewById(R.id.lvlnlayout1);

        tvSongName = (TextView) findViewById(R.id.lvsongname);
        tvArtistName =  (TextView) findViewById(R.id.lvartist);

        btnBack = (ImageButton) findViewById(R.id.imgbtnlistviewBack);
        btnPlay = (ImageButton) findViewById(R.id.imgbtnlistviewPlay);
        btnNext = (ImageButton) findViewById(R.id.imgbtnlistviewNext);

        drawerLayout = findViewById(R.id.main_layout);
        navigationView = findViewById(R.id.nav_view);

        gotoListViewFragment(this);



        //-------------------------------------------------------
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AudioPlayer.playedList.isEmpty()) {
                    ListViewFragment.f.playPreviousSong(getApplicationContext(),1);
                }
                setTextAndButton();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if(AudioPlayer.isplayingAudio){
                    btnPlay.setImageResource(R.drawable.icon_play);
                    ListViewFragment.f.pause(1, getApplicationContext());
                } else {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    ListViewFragment.f.resume(1, getApplicationContext());
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!AudioPlayer.playedList.isEmpty()) {
                    ListViewFragment.f.playNextSong(getApplicationContext(),1);
                }
                setTextAndButton();
            }
        });

        //----------------------------------------------
        Bundle extras = getIntent().getExtras();
        boolean value = false;
        if(extras != null){
            if(extras.get("isFromWidget") != null) {
                value = (Boolean) extras.get("isFromWidget");
            }

            System.out.println("From Widget: "+value);
            if (value) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String str = preferences.getString("Song", "");
                gotoPlayFragment(str);
            }
        }
    }

   public static void setTextAndButton(){
        tvSongName.setText(AudioPlayer.song.getName());
        //tvSongName.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        tvArtistName.setText(AudioPlayer.song.getComposer());
        if (AudioPlayer.isplayingAudio){
            btnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            btnPlay.setImageResource(R.drawable.icon_play);
        }
    }

     public static void gotoListViewFragment(Activity activity){
         System.out.println("Go to ListView Fragment");
        Fragment fragment = ListViewFragment.newInstance();
        FragmentManager fragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

    Fragment fragment;
    void gotoPlayFragment(String name){
        fragment = PlayFragment.newInstance(name, true, true,false);
        if(AudioPlayer.isplayingAudio){
             fragment = PlayFragment.newInstance(name, false, true, false);
        }
        if (AudioPlayer.ispausingAudio){
            fragment = PlayFragment.newInstance(name, false, false,false);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }


    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            ListViewFragment.doMySearch(query);
        }
    }

    public static PendingIntent onButtonNotificationClick(@IdRes int id, Activity activity) {
        Intent intent = new Intent(activity, buttonListener.class);
        intent.putExtra("EXTRA_BUTTON_CLICKED", id);
        return PendingIntent.getBroadcast(activity, id, intent, 0);
    }

    private static final String CHANNEL_ID = "TEST_CHANNEL";
    public static NotificationManager notificationManager;
    public static RemoteViews notificationLayout;
    public static Notification notification;

    public static void createNotification(Activity activity, Song song){

        if (notificationLayout != null || notificationManager != null){
            notificationLayout = null;
            notificationManager = null;
        }

        notificationLayout =
                new RemoteViews(activity.getPackageName(), R.layout.custom_push);

        notificationLayout.setTextViewText(R.id.noti_songName, song.getName());
        notificationLayout.setTextViewText(R.id.noti_artistName, song.getComposer());
        notificationLayout.setImageViewUri(R.id.noti_cover, Uri.parse(song.getImg()));

        notificationLayout.setOnClickPendingIntent(R.id.noti_back,
                onButtonNotificationClick(R.id.noti_back, activity));
        notificationLayout.setOnClickPendingIntent(R.id.noti_play,
                onButtonNotificationClick(R.id.noti_play, activity));
        notificationLayout.setOnClickPendingIntent(R.id.noti_next,
                onButtonNotificationClick(R.id.noti_next, activity));
        notificationLayout.setOnClickPendingIntent(R.id.noti_close,
                onButtonNotificationClick(R.id.noti_close, activity));

        notification = new NotificationCompat.Builder(activity, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomBigContentView(notificationLayout)
                .build();

        notificationManager =
                (android.app.NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


    public static class buttonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("button listener");
            int id = intent.getIntExtra("EXTRA_BUTTON_CLICKED", -1);
            PlayFragment fm = newInstance("", true, true,false);
            switch (id) {
                case R.id.noti_back:
                    System.out.println("Back");
                    Toast.makeText(context, "Back", Toast.LENGTH_SHORT).show();
                    fm.playPreviousSong(context,0);
                    break;
                case R.id.noti_play:
                    System.out.println("Play");
                    if (AudioPlayer.isplayingAudio){
                        notificationLayout.setImageViewResource(R.id.noti_play,R.drawable.icon_play);
                        fm.pause(2,context);
                    } else {
                        fm.resume(2,context);
                        notificationLayout.setImageViewResource(R.id.noti_play,R.drawable.ic_pause);
                    }
                    notificationManager.notify(1, notification);
                    Toast.makeText(context, "Play", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.noti_next:
                    Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show();
                    fm.playNextSong(context,0);
                    break;
                case R.id.noti_close:
                    notificationManager.cancel(1);
                    Toast.makeText(context, "Close", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }





        }
