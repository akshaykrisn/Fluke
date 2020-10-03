package app.fluky.ml.fluk.frags.utils2;

import android.graphics.Bitmap;

public class user_entry_history_item {
    private Bitmap image;
    private String title;
    private String subtitle;
    public user_entry_history_item(Bitmap image, String title, String subtitle){
        this.image=image;
        this.title =title;
        this.subtitle=subtitle;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public  String getTitle(){return title;}
    public void setTitle(String title){
        this.title=title;
    }
    public String getSubtitle(){return subtitle;}
    public void setSubtitle(String subtitle){
        this.subtitle=subtitle;
    }
}