package cf.quanganh.musicplayer;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.network.request.event.RequestListener;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;
import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineActivity extends AppCompatActivity {

    private List<Track> mTracksList = new ArrayList<Track>();
    public static TrackPlayer mTrackPlayer;
    public static DeezerConnect deezerConnect;

    TextView txtView;
    ImageButton btnBack;
    CircleImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); //<< this

        setContentView(R.layout.activity_online);

        avatar = findViewById(R.id.avatar);
        btnBack = findViewById(R.id.backbtn);
        txtView = findViewById(R.id.greeting);
        listView = (ListView) findViewById(R.id.listViewOnline);

        // Button Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // replace with your own Application ID
        String applicationID = "371724";
        deezerConnect = new DeezerConnect(this, applicationID);

        // The set of Deezer Permissions needed by the app
        String[] permissions = new String[] {
                Permissions.BASIC_ACCESS,
                Permissions.MANAGE_LIBRARY,
                Permissions.LISTENING_HISTORY };

        // The listener for authentication events
        DialogListener listener = new DialogListener() {
            public void onComplete(Bundle values) {
                Toast.makeText(OnlineActivity.this, "Login succes", Toast.LENGTH_SHORT).show();
                SessionStore sessionStore = new SessionStore();
                sessionStore.save( deezerConnect, OnlineActivity.this);
                txtView.setText("Hi, "+deezerConnect.getCurrentUser().getName());
                Picasso.get().load(deezerConnect.getCurrentUser().getMediumImageUrl()).into(avatar);
                requestTracks();
            }
            public void onCancel() {
                Toast.makeText(OnlineActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
            public void onException(Exception e) {}
        };
        // restore any saved session
        SessionStore sessionStore = new SessionStore();
        if (sessionStore.restore(deezerConnect, OnlineActivity.this)) {
                txtView.setText("Hi, "+deezerConnect.getCurrentUser().getName());
            Picasso.get().load(deezerConnect.getCurrentUser().getMediumImageUrl()).into(avatar);
            requestTracks();
        } else {
            // Launches the authentication process
            deezerConnect.authorize(this, permissions, listener);
        }
    }

    /*
    void albumPlay() throws DeezerError, TooManyPlayersExceptions {
        // create the player
        AlbumPlayer albumPlayer = new AlbumPlayer(getApplication(),deezerConnect,
                NetworkStateCheckerFactory.wifiAndMobile()); // 1v
        // start playing music
        long albumId = 89142;
        albumPlayer.playAlbum(albumId);
    }


    void requestAlbum(){
        // the request listener
        RequestListener listener = new JsonRequestListener() {
            public void onResult(Object result, Object requestId) {
                List<Album> albums = (List<Album>) result; // 1v
                // do something with the albums
                for (Album album: albums){
                    System.out.println(album.toString());
                    System.out.println(album.getTitle());
                }
            }
            public void onUnparsedResult(String requestResponse, Object requestId) {} // 2v
            public void onException(Exception e, Object requestId) {} // 3v
        };
        long artistId = 11472;
        DeezerRequest request = DeezerRequestFactory.requestArtistAlbums(artistId);

        deezerConnect.requestAsync(request, listener);
    }
*/

    private void requestTracks() {
        DeezerRequest request = DeezerRequestFactory.requestChartTracks();
        RequestListener listener = new JsonRequestListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(final Object result, final Object requestId) {

                        mTracksList.clear();
                        mTracksList.addAll((List<Track>) result);

                        System.out.println("Track List");

                        if (mTracksList.isEmpty()) {
                            System.out.println("mTracklist is empty");
                        } else {
                            setCustomListViewOnline(mTracksList);
                        }
                    }

                    @Override
                    public void onUnparsedResult(final String response, final Object requestId) {
                        System.out.println("unparsedResult");
                    }

                    @Override
                    public void onException(final Exception exception,
                                            final Object requestId) {
                        System.out.println("Exception");
                    }

                };
        deezerConnect.requestAsync(request, listener);

    }

    public static void trackPlayer(Track track, Application app) throws DeezerError, TooManyPlayersExceptions {
        if(mTrackPlayer != null) {
            mTrackPlayer.stop();
            mTrackPlayer.release();
        }
        mTrackPlayer = new TrackPlayer(app, deezerConnect,
                new WifiAndMobileNetworkStateChecker());
        Toast.makeText(app, track.getTitle()+"is playing", Toast.LENGTH_SHORT).show();
        mTrackPlayer.playTrack(track.getId());
    }


    static CustomAdapterOnline adapter;
    ListView listView;
    void setCustomListViewOnline(List<Track> songList){
        if (songList == null) return;

        adapter= new CustomAdapterOnline(this,songList,getApplication());

        if (adapter.isEmpty()) {
            System.out.println("Adapter is empty");
            return;
        }
        listView.setAdapter(adapter);
    }








}
