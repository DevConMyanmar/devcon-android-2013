package org.devcon.android.objects;

import java.io.Serializable;

public class Speaker implements Serializable{
	public int _id;
	public String name;
	public String title;
	public String bio;
	public String photo;
	public int schedule_id;

	public Speaker() {
	}

	public Speaker(int id, String name, String title, String bio, String photo,
			int schedule_id) {
		this._id = id;
		this.name = name;
		this.title = title;
		this.bio = bio;
		this.photo = photo;
		this.schedule_id = schedule_id;
	}

	public int getID() {
		return this._id;
	}

	public void setID(int id) {
		this._id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBio() {
		return this.bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getPhoto() {
		return this.photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public int getScheduleID() {
		return this.schedule_id;
	}

	public void setScheduleID(int schedule_id) {
		this.schedule_id = schedule_id;
	}

}
