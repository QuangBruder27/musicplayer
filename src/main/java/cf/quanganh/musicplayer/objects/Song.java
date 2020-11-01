package cf.quanganh.musicplayer.objects;

import java.io.Serializable;

public class Song implements Serializable {
    private String path;
    private String duration;
    private String size;
    private String composer;
    private String img;
    private boolean isLiked = false;

    public Song() {
    }

    public Song(String path, String name, String duration, String size, String composer, String img, String isLiked) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.composer = composer;
        this.img = img;
        this.path = path;
        this.isLiked = Boolean.valueOf(isLiked);
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }


    @Override
    public String toString() {
        return "Song{" +
                "path='" + path + '\'' +
                ", duration='" + duration + '\'' +
                ", size='" + size + '\'' +
                ", composer='" + composer + '\'' +
                ", img='" + img + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean isEqual(Song so){
        if(this.getName().equals(so.getName()) && this.getComposer().equals(so.getComposer())){
            return true;
        }
        return false;
    }

}
