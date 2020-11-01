
package cf.quanganh.musicplayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;
import android.widget.Toast;

import cf.quanganh.musicplayer.Fragments.PlayFragment;
import cf.quanganh.musicplayer.objects.AudioPlayer;

import static cf.quanganh.musicplayer.Fragments.PlayFragment.newInstance;

public class NewAppWidget extends AppWidgetProvider {

    public static String WIDGET_BUTTON_BACK = "cf.quanganh.musicplayer.WIDGET_BUTTON_BACK";
    public static String WIDGET_BUTTON_PLAY = "cf.quanganh.musicplayer.WIDGET_BUTTON_PLAY";
    public static String WIDGET_BUTTON_NEXT = "cf.quanganh.musicplayer.WIDGET_BUTTON_NEXT";
    static RemoteViews views;
    static PlayFragment fm = newInstance("", true, true,false);
    static Context c = null;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        c = context;

        Intent it1 = new Intent(WIDGET_BUTTON_BACK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, it1, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imgbtnwidgetBack, pendingIntent );

        Intent it2 = new Intent(WIDGET_BUTTON_PLAY);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, it2, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imgbtnwidgetPlay, pendingIntent2 );

        Intent it3 = new Intent(WIDGET_BUTTON_NEXT);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0, it3, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imgbtnwidgetNext, pendingIntent3 );

        // Create an Intent to launch MainActivity
        Intent it4 = new Intent(context, MainActivity.class);
        it4.putExtra("isFromWidget", true);
        it4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent4 = PendingIntent.getActivity(context, 0, it4, 0);
        views.setOnClickPendingIntent(R.id.lnlayout1, pendingIntent4);

        //appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), NewAppWidget.class.getName()), views);
        Toast.makeText(context, "widget added", Toast.LENGTH_SHORT).show();
        AudioPlayer.isWidgetAdded = true;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        c = context;

        if (WIDGET_BUTTON_BACK.equals(intent.getAction())) {
            Toast.makeText(context, "Back button", Toast.LENGTH_SHORT).show();
            if(!AudioPlayer.playedList.isEmpty()) {
                fm.playPreviousSong(context,0);
            }
        }

        if (WIDGET_BUTTON_PLAY.equals(intent.getAction())) {
            Toast.makeText(context, "Play button", Toast.LENGTH_SHORT).show();
            fm.widgetSongPlayer(context);
            updateTimer();
        }

        if (WIDGET_BUTTON_NEXT.equals(intent.getAction())) {
            Toast.makeText(context, "Next button", Toast.LENGTH_SHORT).show();
            if(!AudioPlayer.playedList.isEmpty()){
                fm.playNextSong(context,0);
            }
        }
    }

    private static Handler mHandler = new Handler();

    public static void updateTimer() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            String str_currentDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getCurrentPosition());
            String str_totalDuration = Helper.milliSecondsToTimer(AudioPlayer.player.getDuration());

            if(AudioPlayer.isplayingAudio) {
                //System.out.println("is Playing");
                Helper.setTextforAppWidget(c, str_currentDuration, str_totalDuration, R.drawable.wg_pause);
            } else {
                //System.out.println("is not playing");
                Helper.setTextforAppWidget(c, str_currentDuration, str_totalDuration, R.drawable.wg_play);
            }
            mHandler.postDelayed(this, 100);
        }
    };

}