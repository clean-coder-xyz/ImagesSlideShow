package imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow;

import android.view.animation.AlphaAnimation;

/**
 * Created by lsemenov on 11.10.2014.
 */
public class SlideShowAnimation extends AlphaAnimation {

    public static SlideShowAnimation newAppearance() {
        return new SlideShowAnimation(0.0f, 1.0f);
    }

    public static SlideShowAnimation newDisappearance() {
        return new SlideShowAnimation(1.0f, 0.0f);
    }

    public SlideShowAnimation(float fromAlpha, float toAlpha) {
        super(fromAlpha, toAlpha);
    }

    public SlideShowAnimation duration(long duration) {
        setDuration(duration);
        return this;
    }

}
