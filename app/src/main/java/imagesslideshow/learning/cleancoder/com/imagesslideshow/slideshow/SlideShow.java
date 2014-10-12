package imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lsemenov on 11.10.2014.
 */
public class SlideShow implements Parcelable {

    public static final Creator<SlideShow> CREATOR = new Creator<SlideShow>() {
        @Override
        public SlideShow createFromParcel(Parcel parcel) {
            return new SlideShow(parcel);
        }

        @Override
        public SlideShow[] newArray(int size) {
            return new SlideShow[size];
        }
    };

    private static final String PERIOD_UNIT = "ms";

    private static final long MIN_PERIOD = 100;
    private static final long MIN_ANIMATION_DURATION = 100;

    int currentImage;

    // required parameters
    private List<String> imagePaths;
    private Long period;

    // optional parameters
    private Long animationDuration;


    public SlideShow() {
        currentImage = 0;
        imagePaths = null;
        period = null;
        animationDuration = null;
    }

    public SlideShow(Parcel in) {
        currentImage = in.readInt();
        imagePaths = (List<String>) in.readSerializable();
        period = (Long) in.readSerializable();
        animationDuration = (Long) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(currentImage);
        out.writeSerializable((Serializable) imagePaths);
        out.writeSerializable(period);
        out.writeSerializable(animationDuration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public SlideShow imagePaths(List<String> imagePaths) {
        if (imagePaths.isEmpty()) {
            throw new IllegalArgumentException("You couldn't set empty list of image paths.");
        }
        this.imagePaths = imagePaths;
        return this;
    }

    List<String> getImagePaths() {
        return new ArrayList<String>(imagePaths);
    }

    public SlideShow period(long period) {
        if (period < MIN_PERIOD) {
            throw new IllegalArgumentException(
                    "You couldn't set period lower than " + MIN_PERIOD + " " + PERIOD_UNIT);
        }
        this.period = period;
        return this;
    }

    long getPeriod() {
        return period;
    }

    public SlideShow animationDuration(long animationDuration) {
        if (animationDuration < MIN_ANIMATION_DURATION) {
            throw new IllegalArgumentException(
                    "You couldn't set minimum animation duration lower than " + MIN_ANIMATION_DURATION + " ms");
        }
        this.animationDuration = animationDuration;
        return this;
    }

    Long getAnimationDuration() {
        return animationDuration;
    }

    public SlideShowController start(Context context, ImageView imageView1, ImageView imageView2) {
        checkAllRequiredParametersAreSet();
        SlideShowController controller = new SlideShowController(this, context, imageView1, imageView2);
        controller.start();
        return controller;
    }

    private void checkAllRequiredParametersAreSet() {
        for (Object requiredParameter : getRequiredParameters()) {
            if (requiredParameter == null) {
                throw new IllegalArgumentException(
                        "You should set all required parameters to start slide show"
                );
            }
        }
    }

    private Iterable<? extends Object> getRequiredParameters() {
        return Arrays.asList(imagePaths, period);
    }

}
