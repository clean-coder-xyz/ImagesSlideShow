package imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lsemenov on 11.10.2014.
 */
public class SlideShow<ItemType> implements Parcelable {

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
    private final Class<ItemType> itemType;

    // required parameters
    private List<ItemType> items;
    private Long period;

    // optional parameters
    private Long animationDuration;


    public SlideShow(Class<ItemType> itemType) {
        this.itemType = itemType;
        this.items = null;
        this.period = null;
        this.animationDuration = null;
        this.currentImage = 0;
    }


    public SlideShow items(List<ItemType> items) {
        if (items == null) {
            throw new NullPointerException("items can't be null");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("You couldn't set empty list of items.");
        }
        this.items = items;
        return this;
    }

    List<ItemType> getItems() {
        return new ArrayList<ItemType>(items);
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

    public <TransformedItemType, ViewType extends View> SlideShowController
                            start(Context context,
                                  ViewType view1,
                                  ViewType view2,
                                  SlideShowOption<ItemType, TransformedItemType, ViewType> slideShowOption) {
        checkAllRequiredParametersAreSet();
        SlideShowController controller = new SlideShowController(this, context, view1, view2, slideShowOption);
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
        return Arrays.asList(items, period);
    }


    public SlideShow(Parcel in) {
        itemType = (Class<ItemType>) in.readSerializable();
        items = new ArrayList<ItemType>();
        in.readList(items, itemType.getClassLoader());
        period = (Long) in.readSerializable();
        animationDuration = (Long) in.readSerializable();
        currentImage = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeSerializable(itemType);
        out.writeList(items);
        out.writeSerializable(period);
        out.writeSerializable(animationDuration);
        out.writeInt(currentImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
