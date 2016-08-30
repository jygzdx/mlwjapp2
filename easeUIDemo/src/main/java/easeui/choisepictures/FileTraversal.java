package easeui.choisepictures;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

//文件的类
@SuppressLint("ParcelCreator")
public class FileTraversal implements Parcelable {
	public String filename;//所属图片的文件名称
	public List<String> filecontent=new ArrayList<String>();
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(filename);
		dest.writeList(filecontent);
	}
	
	public static final Creator<easeui.choisepictures.FileTraversal> CREATOR=new Creator<easeui.choisepictures.FileTraversal>() {
		
		@Override
		public easeui.choisepictures.FileTraversal[] newArray(int size) {
			return null;
		}
		
		@Override
		public easeui.choisepictures.FileTraversal createFromParcel(Parcel source) {
			easeui.choisepictures.FileTraversal ft=new easeui.choisepictures.FileTraversal();
			ft.filename= source.readString();
			ft.filecontent= source.readArrayList(easeui.choisepictures.FileTraversal.class.getClassLoader());
			
			return ft;
		}
		
		
	};
}
