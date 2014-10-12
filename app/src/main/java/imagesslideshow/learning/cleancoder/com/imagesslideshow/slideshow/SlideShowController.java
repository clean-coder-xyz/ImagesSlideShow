package imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.List;

import imagesslideshow.learning.cleancoder.com.imagesslideshow.R;

/**
 * Created by lsemenov on 11.10.2014.
 */
public class SlideShowController<ItemType, TransformedItemType, ViewType extends View> {

    private static class Visibility {
        static final int SHOW_VIEW = View.VISIBLE;
        static final int HIDE_VIEW = View.GONE;
    }

    private final Object LOCK_START = new Object();
    private final Object LOCK_STOP = new Object();

    private final Handler handler = new Handler();

    private final Context context;
    private final int numberOfItems;
    private final List<ItemType> items;
    private final List<TransformedItemType> transformedItems;
    private final long animationDuration;
    private final long period;
    private final SlideShow slideShow;
    private final SlideShowOption<ItemType, TransformedItemType, ViewType> slideShowOption;
    private boolean isStarted;
    private boolean isStopped;
    private ViewType view1;
    private ViewType view2;

    SlideShowController(SlideShow slideShow,
                        Context context,
                        ViewType view1,
                        ViewType view2,
                        SlideShowOption<ItemType, TransformedItemType, ViewType> slideShowOption) {
        this.slideShowOption = slideShowOption;
        this.slideShow = slideShow;
        this.animationDuration = getAnimationDuration(context, slideShow);
        this.items = slideShow.getItems();
        this.numberOfItems = items.size();
        this.transformedItems = newArrayListWithSize(items.size());
        this.period = slideShow.getPeriod();
        this.context = context;
        this.view1 = view1;
        this.view2 = view2;
        this.isStarted = false;
        this.isStopped = false;
    }

    private static long getAnimationDuration(Context context, SlideShow slideShow) {
        Long duration = slideShow.getAnimationDuration();
        if (duration == null) {
            duration = (long) context.getResources().getInteger(R.integer.default_slideshow_animation_duration);
        }
        return duration;
    }

    private static <T> ArrayList<T> newArrayListWithSize(int size) {
        ArrayList<T> list = new ArrayList<T>(size);
        for (int i = 0; i < size; ++i) {
            list.add(null);
        }
        return list;
    }

    void start() {
        synchronized (LOCK_START) {
            if (isStarted) {
                throw new IllegalStateException("Slide show is already started");
            }
            isStarted = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    slideShowOption.render(null, view1);
                    slideShowOption.render(null, view2);
                    slider.run();
                }
            });
        }
    }

    private final Runnable slider = new Runnable() {
        public void run() {
            synchronized (LOCK_STOP) {
                if (isStopped) {
                    return;
                }
                try {
                    slide();
                } catch (Throwable exception) {
                    exception.printStackTrace();
                }
            }
        }
    };

    private void slide() {
        startAnimationHideImage(view1);
        startAnimationShowImage(view2, slideShow.currentImage);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                slideShowOption.render(null, view1);
                view1.setVisibility(Visibility.HIDE_VIEW);
                view1.clearAnimation();
                view2.clearAnimation();
                swapImageViews();
                slideShow.currentImage = nextImageIndex();
                handler.postDelayed(slider, period);
            }
        }, animationDuration);
    }

    private void startAnimationHideImage(final ViewType imageView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Animation animationHide = SlideShowAnimation.newDisappearance().duration(animationDuration);
                imageView.startAnimation(animationHide);
            }
        });
    }

    private void startAnimationShowImage(final ViewType view, final int index) {
        final TransformedItemType transformedItem = getTransformedItem(index);
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(Visibility.HIDE_VIEW);
                slideShowOption.render(transformedItem, view);
                Animation animationShow = SlideShowAnimation.newAppearance().duration(animationDuration);
                animationShow.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setVisibility(Visibility.SHOW_VIEW);
                            }
                        });
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // skip
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // skip
                    }
                });
                view.startAnimation(animationShow);
                view.bringToFront();
            }
        });
    }

    private TransformedItemType getTransformedItem(int index) {
        TransformedItemType transformedItem = transformedItems.get(index);
        if (transformedItem == null) {
            ItemType item = items.get(index);
            transformedItem = slideShowOption.transform(item);
            transformedItems.set(index, transformedItem);
        }
        return transformedItem;
    }

    private void swapImageViews() {
        ViewType temp = view1;
        view1 = view2;
        view2 = temp;
    }

    private int nextImageIndex() {
        int index = slideShow.currentImage + 1;
        return (index < numberOfItems) ? index : 0;
    }

    public void stop() {
        synchronized (LOCK_START) {
            if (!isStarted) {
                throw new IllegalStateException("You couldn't stop not started slide show");
            }
            isStarted = false;
            synchronized (LOCK_STOP) {
                if (isStopped) {
                    throw new IllegalStateException("You couldn't stop slide show more than once");
                }
                isStopped = true;
                releaseResources();
            }
        }
    }

    private void releaseResources() {
        String prefix = getClass().getName() + ".releaseResources(): ";
        for (int i = 0; i < transformedItems.size(); ++i) {
            Log.d("Leonid", prefix + "recycle item #" + i);
            slideShowOption.releaseResources(items.get(i), transformedItems.get(i));
            items.set(i, null);
            transformedItems.set(i, null);
        }
    }

}
