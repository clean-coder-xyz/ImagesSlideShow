package imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Transformation;

/**
 * Created by lsemenov on 11.10.2014.
 */
public class SlideShowAnimation extends AlphaAnimation {


    public static enum Action {
        APPEAR(0.0f, 1.0f), DISAPPEAR(1.0f, 0.0f);

        private final float fromAlpha;
        private final float toAlpha;

        private Action(float fromAlpha, float toAlpha) {
            this.fromAlpha = fromAlpha;
            this.toAlpha = toAlpha;
        }
    }

    public static SlideShowAnimation newInstance(Action action, long duration) {
        SlideShowAnimation animation = new SlideShowAnimation(action.fromAlpha, action.toAlpha);
        animation.setDuration(duration);
        return animation;
    }

    private AlphaAnimation animationHide;
    private View viewToShow;
    private View viewToHide;


    public SlideShowAnimation(float fromAlpha, float toAlpha) {
        super(fromAlpha, toAlpha);
    }

    SlideShowAnimation(View viewToShow, View viewToHide, long duration) {
        super(0.0f, 1.0f);
        setDuration(duration);
        this.viewToShow = viewToShow;
        this.animationHide = new AlphaAnimation(1.0f, 0.0f);
        this.animationHide.setDuration(duration);
        this.viewToHide = viewToHide;
    }

    public void startSlideShow() {
        viewToShow.startAnimation(this);
        viewToHide.startAnimation(animationHide);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

    }

}
