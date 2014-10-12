package imagesslideshow.learning.cleancoder.com.imagesslideshow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by lsemenov on 09.10.2014.
 */
public class ImageMeta implements Parcelable {

    public static final Creator<ImageMeta> CREATOR = new Creator<ImageMeta>() {
        @Override
        public ImageMeta createFromParcel(Parcel parcel) {
            return new ImageMeta(parcel);
        }

        @Override
        public ImageMeta[] newArray(int size) {
            return new ImageMeta[size];
        }
    };

    private String path;

    public static ImageMeta fromPath(String path) {
        ImageMeta imageMeta = new ImageMeta();
        imageMeta.path = path;
        return imageMeta;
    }

    private ImageMeta() {
        path = null;
    }

    public ImageMeta(Parcel in) {
        path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeFile(path);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        int indexOfLastSeparator = path.lastIndexOf(File.separator);
        if (indexOfLastSeparator == -1) {
            return path;
        }
        return path.substring(indexOfLastSeparator + 1);
    }

}
