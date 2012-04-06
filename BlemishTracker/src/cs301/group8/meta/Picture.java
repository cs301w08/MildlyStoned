package cs301.group8.meta;

import android.os.Parcel;
import android.os.Parcelable;

/** Picture class contains methods and constructors regarding the actual picture objects that 
 * are taken from the camera and stored into the database.
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 1.0
 */
public class Picture implements Parcelable{

	private long time;
	private String path;
	private String group;
	private String note;
	private long id;

	/** Picture constructor creates a picture object with values for
	 * time of creation, group, and path.
	 *
	 * @param group			Group associated with the picture
	 * @param time				Time that the picture was taken (via system call)
	 * @param path				Path to the saved image in memory
	 *
	 */

	public Picture (String group, long time){
		this(-1,group, time);
	}

	public Picture (long id, String group, long time){
		this.id=id;
		this.time=time;
		this.group = group;
		this.path = group + "/" + time + ".jpg";
	}

	/** Picture constructor creates a picture object from a parcel
	 * this parcel is created by the write to parcel method
	 * 
	 * @param in				Parceled Picture object
	 */
	public Picture (Parcel in){
		this.id = in.readLong();
		this.time = in.readLong();
		this.path = in.readString();
		this.group = in.readString();
		this.note = in.readString();

	}

	/** Returns the note currently set for this picture
	 * 
	 * @return 					The picture current note
	 */

	public String getNote(){
		return this.note;
	}

	/**
	 * Sets the pictures note.
	 * @param note				The new note to set
	 */

	public void setNote(String note){
		this.note=note;
	}

	/**
	 * Returns the time associated with the picture
	 *
	 * @return time			Time of picture creation
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Returns the path associated with the picture
	 *
	 * @return path			Path to the picture on the SD external memory
	 */

	public String getPath() {
		return path;
	}

	/**
	 * Returns the group associated with the picture
	 *
	 * @return group			Group that the picture is associated with
	 */

	public String getGroup() {
		return group;
	}

	/**
	 * returns the ID associated with the picture
	 * 
	 * @return id		long value of the id.
	 */
	public long getId(){
		return id;
	}
	/**
	 * Implemented method
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Fills a Parcel dest with the object's data
	 * 
	 * @param dest			the parcel to fill
	 * @param flags			not needed for this object
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeLong(this.time);
		dest.writeString(this.path);
		dest.writeString(this.group);
		dest.writeString(this.note);
	}

	/**
	 * Used to created the parceled objects by java
	 */
	public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
		public Picture createFromParcel(Parcel in) {
			return new Picture(in);
		}

		public Picture[] newArray(int size) {
			return new Picture[size];
		}
	};


}
