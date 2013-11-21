package org.devcon.android.objects;

import java.io.Serializable;

public class Talk implements Serializable {
    public int _id;
    public String sch_id;
    public String title;
    public String time;
    public String date;
    public String speaker;
    public int speaker_id;
    public String desc;
    public boolean fav;

    public Talk() {
    }

    public Talk(int id, String time, String date, String title, String speaker, int speaker_id, String desc,
                boolean fav) {
        this._id = id;
        this.time = time;
        this.date = date;
        this.title = title;
        this.speaker = speaker;
        this.speaker_id = speaker_id;
        this.desc = desc;
        this.fav = fav;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpeaker() {
        return this.speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getFav() {
        return this.fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public int getSpeaker_id() {
        return speaker_id;
    }

    public void setSpeaker_id(int speaker_id) {
        this.speaker_id = speaker_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;

    }
}
