package cf.quanganh.musicplayer.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cf.quanganh.musicplayer.objects.Song;

public class ExpandableListDataPump {

    public static HashMap<String, ArrayList<Song>> getHashMap(ArrayList<Song> songList){
        List<String> artistList = new ArrayList<String>();
        String composer;
        for(Song s: songList) {
            composer = s.getComposer();
            if (artistList.isEmpty()) {
                artistList.add(composer);
            } else if(isAddable(composer,artistList)){
                artistList.add(composer);
            }
        }
        Collections.sort(artistList);

        HashMap<String, ArrayList<Song>> expandableListDetail = new HashMap<String, ArrayList<Song>>();

        for (String artist: artistList){
            ArrayList<Song> songListbyArtist = new ArrayList<Song>();
            for(Song song: songList){
                if (artist.equals(song.getComposer())){
                    songListbyArtist.add(song);
                }
            }
            //System.out.println("Artist:"+artist+"with songlist:"+songListbyArtist);
            expandableListDetail.put(artist,songListbyArtist);
        }

       return expandableListDetail;
    }

    public static boolean isAddable(String composer, List<String> arList){
        for (String str : arList) {
            if (str.equals(composer)) {
                return false;
            }
        }
        return true;
    }

}
