package cf.quanganh.musicplayer.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cf.quanganh.musicplayer.CustomAdapter;
import cf.quanganh.musicplayer.DBHelper;
import cf.quanganh.musicplayer.Helper;
import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.OnlineActivity;
import cf.quanganh.musicplayer.R;
import cf.quanganh.musicplayer.Suggestion.SuggestionSimpleCursorAdapter;
import cf.quanganh.musicplayer.Suggestion.SuggestionsDatabase;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;

import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static cf.quanganh.musicplayer.Helper.makeoriginalList;
import static cf.quanganh.musicplayer.MainActivity.btnPlay;
import static cf.quanganh.musicplayer.MainActivity.linearLayout;
import static cf.quanganh.musicplayer.MainActivity.relativeLayout;
import static cf.quanganh.musicplayer.MainActivity.tvArtistName;
import static cf.quanganh.musicplayer.MainActivity.tvSongName;

public class ListViewFragment extends Fragment {

    SuggestionsDatabase SGdatabase;
    ArrayList<String> suggestionList = new ArrayList<String>();
    public static ArrayList<Song> songList;
    public static ListView listView;
    static CustomAdapter adapter;
    ProgressBar progressBar;
    static DBHelper mydb;
    public static Song selectedSong;
    HashMap<Character, Boolean> firstLetterArray;
    HashMap<String, Boolean> headerArray;
    static Button btn_yes, btn_no;
    static Dialog dialog;
    Switch sw;
    boolean isFirstTime = false;


    public static PlayFragment f = PlayFragment.newInstance("", true, true,false);

    public static SearchView searchView;

    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        ListViewFragment fragment = new ListViewFragment();
        return fragment;
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission granted");
                // permission granted and now can proceed
                    System.out.println("Load 1");
                    load();

                return;
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                System.out.println("Permission denied");
            }
        }
        // add other cases for more permissions
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        System.out.println("onCreate ListViewFragment");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = (SearchManager)  getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity

        if(searchManager.getSearchableInfo(getActivity().getComponentName()) == null){
            System.out.println("this is null");
        } else {
            System.out.println("this is not null");
        }
        if (searchView == null){
            System.out.println("searchView is null");
        } else {
            System.out.println("searchView is not null");
        }

        searchView.setQueryHint(getResources().getString(R.string.search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        //------------ Search View Listener --------------------------------------------------
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("OnClick");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                System.out.println("on Query Text Submit");
                if(s.isEmpty()) return false;

                Cursor cursor = SGdatabase.getSuggestions(s);
                if(cursor.getCount() != 0){
                    gotoSearchResultFragment(suggestionList);
                } else {
                    Toast.makeText(getContext(), R.string.notfound, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s){
                System.out.println("on Query Text Change");
                if(s.isEmpty()) return false;
                Cursor cursor = SGdatabase.getSuggestions(s);

                suggestionList = new ArrayList<String>();
                if(cursor.getCount() != 0){
                    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        suggestionList.add(cursor.getString(1));
                    }
                    String[] columns = new String[] {SuggestionsDatabase.FIELD_SUGGESTION };
                    int[] columnTextId = new int[] { android.R.id.text1};
                    SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getContext(),
                            android.R.layout.simple_list_item_1, cursor,
                            columns , columnTextId, 0);
                    searchView.setSuggestionsAdapter(simple);
                    return true;
                } else {
                    return false;
                }
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i){
                SQLiteCursor cursor = (SQLiteCursor) searchView.getSuggestionsAdapter().getItem(i);
                int indexColumnSuggestion = cursor.getColumnIndex( SuggestionsDatabase.FIELD_SUGGESTION);
                searchView.clearFocus();
                String str = cursor.getString(indexColumnSuggestion);
                gotoPlayFragmentWithNewSong(str, getActivity());
                return true;
            }
        });

        int autoCompleteTextViewID = getResources().getIdentifier("search_src_text", "id", getActivity().getPackageName());
        SearchView.SearchAutoComplete mSearchAutoCompleteTextView = searchView.findViewById(autoCompleteTextViewID);
        mSearchAutoCompleteTextView.setDropDownBackgroundResource(R.color.light_gray); // Setting background color of the suggestion drop down list

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQuery("", false);
                //searchView.clearFocus();
                hideSoftKeyboard();
                System.out.println("Close button click");
            }
        });
    }


    public void hideSoftKeyboard(){
        if(getActivity().getCurrentFocus()!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(MainActivity.drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        System.out.println("OnViewCreated");
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if(isFirstTimeLaunch()) {
            System.out.println("First time launch true");
            progressBar.setVisibility(View.VISIBLE);
            isFirstTime = true;
        } else {
            System.out.println("First time launch false");
        }

        listView =(ListView) view.findViewById(R.id.listView);
        mydb = new DBHelper(getActivity());

        if (AudioPlayer.originalList.isEmpty()){
          makeoriginalList(mydb);
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoPlayFragmentWithOldSong();
            }
        });

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1){
            if (!checkIfAlreadyhavePermission()){
                System.out.println("Call request");
                requestForSpecificPermission();
            } else {
                    System.out.println("Load 2");
                    load();
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


    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");


        SGdatabase = new SuggestionsDatabase(getContext());

        if (!AudioPlayer.songisEmpty()){
          setTextAndButton();
        }
        if(AudioPlayer.isplayingAudio || AudioPlayer.ispausingAudio){
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            relativeLayout.setVisibility(View.GONE);
        }
        // Navigation Drawer
        MainActivity.drawerToggle = new ActionBarDrawerToggle(getActivity(), MainActivity.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActivity().invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        MainActivity.drawerLayout.addDrawerListener(MainActivity.drawerToggle);
        MainActivity.drawerToggle.syncState();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MainActivity.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // set item as selected to persist highlight
                MainActivity.drawerLayout.closeDrawers();
                //backgroundUI.start();
                switch (item.getItemId()) {

                    case R.id.nav_online:
                        if (Helper.isInternetAvailable()) {
                            Intent myIntent = new Intent(getActivity(), OnlineActivity.class);
                            startActivity(myIntent);
                        } else {
                            Toast.makeText(mContext, "No internet", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.nav_playlist:
                        AudioPlayer.playFavouriteSong = true;
                        gotoMyPlaylistFragment();
                        break;

                    case R.id.nav_playlist_singer:
                        gotoSingerFragment();
                        break;

                    case R.id.nav_history:
                        gotoHistoryFragment();
                        break;

                    case R.id.nav_setting:
                        gotoSettingFragment();
                        break;

                    case R.id.nav_scan:
                        System.out.println("CLICK nav_scan");
                        //MainActivity.drawerLayout.closeDrawers();
                            progressBar= ((MainActivity)getActivity()).findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.VISIBLE);

                        showScanDialog(getContext());
                        /*
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstTime", Context.MODE_PRIVATE);
                        //boolean isFirstLauncher = sharedPreferences.getBoolean("FirstTime", true);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("FirstTime", true).commit();
                         */
                        //doRestart(getContext());

                        // Recreate Main Activity

                        /*
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                        getActivity().overridePendingTransition(0, 0);
                         */

                        //triggerRebirth(getContext());
                        /*
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                         */
                        break;

                }

                return true;
            }
        });

        /*
        //get switch view
        sw =(Switch) MenuItemCompat.getActionView(MainActivity.navigationView.getMenu().findItem(R.id.nav_switch))
                .findViewById(R.id.switcher);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String lang;
                Locale current = getResources().getConfiguration().locale;
                if(current.toString().charAt(0) == 'e'){
                    lang = "vi";
                } else {
                    lang = "en";
                }
                changeLanguage(getContext(), lang);
                setLanguageinSP(lang);
                // Recreate Main Activity
                getActivity().finish();
                startActivity(getActivity().getIntent());
                getActivity().overridePendingTransition(0, 0);
            }
        });
        */
    }


    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }



    void setCustomListView(){
        System.out.println("Function setCustom");
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        if(progressBar.getVisibility() == View.VISIBLE){
            System.out.println("progress bar is visible");
        } else {
            System.out.println("progress bar is invisible");
        }

        setHashmapforHeader();
        if(songList.isEmpty()) System.out.println("songList in setCustomListView is empty");
        adapter= new CustomAdapter(songList, headerArray,getActivity(), true, false);

        if (adapter.isEmpty()){
            System.out.println("Adapter is empty");
            return;
        } else {
            System.out.println("Adapter is NOT empty");
        }

        listView.setAdapter(adapter);

        /*
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedSong = songList.get(position);
                showMenu(view);
                return true;
            }
        });
        */

        if (AudioPlayer.playList.isEmpty()){
            AudioPlayer.playList = Helper.getDatafromDB(new DBHelper(getContext()));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment currentFragment = getFragmentManager().findFragmentById( R.id.activity_main);
                if (currentFragment instanceof ListViewFragment){
                    if(AudioPlayer.originalList.isEmpty()){
                        makeoriginalList(mydb);
                    }
                    AudioPlayer.playList = AudioPlayer.originalList;
                    System.out.println(" I 'm ListViewFragment");
                }
                if (currentFragment instanceof HistoryFragment){
                    AudioPlayer.playList = AudioPlayer.playedSongList;
                    System.out.println(" I 'm HistoryFragment");
                }


                if (currentFragment instanceof MyPlaylistFragment){
                    System.out.println(" I 'm MyPlaylistFragment with i= "+i);
                    AudioPlayer.playList = AudioPlayer.myPlayList;
                    selectedSong = AudioPlayer.playList.get(i);
                    MyPlaylistFragment.gotoPlayFragmentWithFavouriteSong(selectedSong.getName(),getContext());
                    return;
                }
                //System.out.println(AudioPlayer.playList.toString());
                selectedSong = AudioPlayer.playList.get(i);
                gotoPlayFragmentWithNewSong(selectedSong.getName(), getActivity());
            }
        });
    }

    char[] alphabet = "abcdđefghijklmnopqrstuvwxyz 0123456789*".toCharArray();

    void setHashmapforHeader(){
        System.out.println("Set Hashmap for Header");
        firstLetterArray = new HashMap<Character, Boolean>();
        for(char c: alphabet){
            firstLetterArray.put(c,false);
        }

        headerArray = new HashMap<String, Boolean>();
        for(int i=0;i< songList.size();i++) {
            String nameofSong = this.songList.get(i).getName();

            char ch = nameofSong.toLowerCase().charAt(0);
            if (!isAllowed(ch)){
                ch = '*';
            }
            if(firstLetterArray.get(ch)){
                headerArray.put(nameofSong, false);
            } else {
                firstLetterArray.put(ch,true);
                headerArray.put(nameofSong, true);
            }
        }
    }

    boolean isAllowed(char character){
        for(char c: alphabet){
            if(c == character) return  true;
        }
        return false;
    }


    public static void gotoPlayFragmentWithNewSong(String songName, Activity activity){
        Fragment fragment = PlayFragment.newInstance(songName, true, true, false);
        ((PlayFragment) fragment).havetoPlayNewSong = true;
        FragmentManager fragmentManager = ((MainActivity)activity).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

    Fragment fragment;
    public void gotoPlayFragmentWithOldSong(){
        if (!AudioPlayer.playFavouriteSong){
            if (AudioPlayer.ispausingAudio) {
                fragment = PlayFragment.newInstance(AudioPlayer.song.getName(), false, false,false);
            } else {
                fragment = PlayFragment.newInstance(AudioPlayer.song.getName(), false, true, false);
            }} else {
                if (AudioPlayer.ispausingAudio) {
                    fragment = PlayFragment.newInstance(AudioPlayer.song.getName(), false, false,true);
                } else {
                    fragment = PlayFragment.newInstance(AudioPlayer.song.getName(), false, true, true);
                }
            }
        if((MainActivity)mContext == null) {
            System.out.println("getActivity is null");
        }
        FragmentManager fragmentManager = ((MainActivity)mContext).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

    Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    boolean isFirstTimeLaunch(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstTime", Context.MODE_PRIVATE);
        boolean isFirstLauncher = sharedPreferences.getBoolean("FirstTime", true);
        if (isFirstLauncher){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("FirstTime", false).commit();
            return  true;
        } else {
            return false;
        }
    }


    void load(){
        System.out.println("Function load");
        if(isFirstTime){
            isFirstTime = false;
            System.out.println("isFirstTime true");
                songList = Helper.scan(getContext());
                Helper.createAndInserttoDB(songList, mydb);
                makeoriginalList(mydb);
            // Create suggestions database
            SGdatabase = new SuggestionsDatabase(getContext());
            SGdatabase.helper.onCreate(SGdatabase.db);
            SGdatabase.insertAllSuggestion(AudioPlayer.originalList);
        } else {
            System.out.println("isFirstTime false");
            songList = Helper.getDatafromDB(mydb);
        }
        setCustomListView();
    }

    void loadbyScanRequest(){
        //MainActivity.drawerLayout.closeDrawers();

        if (!checkIfAlreadyhavePermission()){
            System.out.println("Call Scan Request");
            requestForSpecificPermission();
        }

        if(checkIfAlreadyhavePermission()) {
            System.out.println("have Permission");
            // Delete old table in DB
            mydb.dropTable();

/*
            for(int i = 0; i < threads.length; i++) {
                System.out.println("THREAD i="+i);
                //threads[i] = new Thread();
                threads[i].start();
            }
                // Wait for all of the threads to finish.
            for (Thread thread : threads)
                thread.join();
 */
/*
            threads[0] = new Thread(new Runnable() {
                public void run() {
                    System.out.println("Thread 0");
                    System.out.println("Hello i 'm UI");
                    MainActivity.drawerLayout.closeDrawers();
                }
            });

            threads[1] = new Thread(new Runnable() {
                public void run() {
                    System.out.println("Thread 1");
                    System.out.println("Hello i 'm Scanner");
                    songList = Helper.scan(getContext());
                }
            });

 */
            songList = Helper.scan(getContext());

            Helper.createAndInserttoDB(songList, mydb);
            makeoriginalList(mydb);
            for(Song s: AudioPlayer.originalList){
                System.out.println(s.getName().toString());
            }
            // Create suggestions database
            SGdatabase = new SuggestionsDatabase(getContext());
            SGdatabase.helper.onCreate(SGdatabase.db);
            SGdatabase.insertAllSuggestion(AudioPlayer.originalList);
            setCustomListView();
        } else {
            System.out.println("donot have permission");
        }

    }

    public static void showMenu (View view, final Context context) {
        System.out.println("Function showMenu");
        PopupMenu menu = new PopupMenu(context, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            menu.setGravity(Gravity.CENTER);
        }
        menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener (){
            @Override
            public boolean onMenuItemClick (MenuItem item)
            {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu1:
                        gotoInfoFragment(context);
                        System.out.println("Menu 1"); break;
                    case R.id.menu2:
                        showDialog(item, context);
                        System.out.println("Menu 2"); break;
                }
                return true;
            }
        });
        menu.inflate (R.menu.popup);
        menu.show();
    }

    public static void showDialog(MenuItem item, Context context) {
        System.out.println("Function showDialog");
        dialog = new Dialog(context);
        dialog.setTitle("Dialog");
        dialog.setContentView(R.layout.dialog);
        dialog.show();

        btn_yes = (Button)dialog.findViewById(R.id.dialog_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("button yes "+ selectedSong.toString());
                mydb.deleteSong(mydb.getID(selectedSong.getPath()));
                adapter.remove(selectedSong);
                dialog.dismiss();
            }
        });
        btn_no = (Button) dialog.findViewById(R.id.dialog_no);
        btn_no.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public  void showScanDialog(Context context) {
        System.out.println("Function show Scan Dialog");
        dialog = new Dialog(context);
        dialog.setTitle("Dialog");

        dialog.setContentView(R.layout.dialog);
        TextView tv = (TextView)dialog.findViewById(R.id.dialog_text);
        tv.setText(getResources().getString(R.string.scanall));

        listView.setVisibility(View.INVISIBLE);
        dialog.show();

        btn_yes = (Button)dialog.findViewById(R.id.dialog_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                loadbyScanRequest();
            }
        });

        btn_no = (Button) dialog.findViewById(R.id.dialog_no);
        btn_no.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void gotoSearchResultFragment(ArrayList<String> list){
        Fragment fragment = SearchResultFragment.newInstance(list);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

    void gotoHistoryFragment(){
        Fragment fragment = HistoryFragment.newInstance(AudioPlayer.playedList);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

    void gotoMyPlaylistFragment(){
        AudioPlayer.myPlayList = new DBHelper(getContext()).getFavouriteSongList();
        Fragment fragment = MyPlaylistFragment.newInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

    static void gotoInfoFragment(Context context){
        Fragment fragment = InfoFragment.newInstance(selectedSong.getName());
        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

    void gotoSettingFragment(){
        Fragment fragment = SettingFragment.newInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }


    void gotoSingerFragment(){
        Fragment fragment = SingerFragment.newInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }

    public static void doMySearch(String query){
        System.out.println("Query: "+query);
        searchView.setSelected(true);
        searchView.setQuery(query,true);
        //this.onQueryTextChange(query);
    }

    public static void setFavoriteSong(Song s){
        mydb.updateSong(s);
    }


}
