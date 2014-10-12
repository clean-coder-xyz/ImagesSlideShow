package imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import imagesslideshow.learning.cleancoder.com.imagesslideshow.R;

/**
 * Created by lsemenov on 11.10.2014.
 */
public class SlideShowController {

    private static class Visibility {
        static final int SHOW_VIEW = View.VISIBLE;
        static final int HIDE_VIEW = View.GONE;
    }

    private final Object LOCK_START = new Object();
    private final Object LOCK_STOP = new Object();
    private final Object LOCK_SLIDING = new Object();

    private final Handler handler = new Handler();

    private final Context context;
    private final int numberOfImages;
    private final List<String> imagePaths;
    private final List<Bitmap> bitmaps;
    private final long animationDuration;
    private final long period;
    private final SlideShow slideShow;
    private boolean isSliding;
    private boolean isStarted;
    private boolean isStopped;
    private ImageView imageView1;
    private ImageView imageView2;
    private ScheduledExecutorService scheduler;

    SlideShowController(SlideShow slideShow,
                        Context context,
                        ImageView imageView1,
                        ImageView imageView2) {
        this.slideShow = slideShow;
        this.animationDuration = getAnimationDuration(context, slideShow);
        this.imagePaths = slideShow.getImagePaths();
        this.numberOfImages = imagePaths.size();
        this.bitmaps = newArrayListWithSize(imagePaths.size());
        this.period = slideShow.getPeriod();
        this.context = context;
        this.imageView1 = imageView1;
        this.imageView2 = imageView2;
        this.isSliding = false;
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
                    setImage(imageView1, null);
                    setImage(imageView2, null);
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
                synchronized (LOCK_SLIDING) {
                    if (isSliding) {
                        return;
                    }
                    isSliding = true;
                    try {
                        slide();
                    } catch (Throwable exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    };

    private void slide() {
        startAnimationHideImage(imageView1);
        startAnimationShowImage(imageView2, slideShow.currentImage);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setImage(imageView1, null);
                imageView1.setVisibility(Visibility.HIDE_VIEW);
                imageView2.setVisibility(Visibility.SHOW_VIEW);
                swapImageViews();
                slideShow.currentImage = nextImageIndex();
                synchronized (LOCK_SLIDING) {
                    isSliding = false;
                }
                handler.postDelayed(slider, period);
            }
        }, animationDuration);
    }

    private void startAnimationHideImage(final ImageView imageView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                imageView.clearAnimation();
                Animation animationHide = SlideShowAnimation.newInstance(
                        SlideShowAnimation.Action.DISAPPEAR, animationDuration);
                animationHide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // skip
                        Log.d("Leonid", "Hide image animation is started");
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
                imageView.startAnimation(animationHide);
            }
        });
    }

    private static void stopAnimationIfStarted(View view) {
        if (true) {
            return;
        }
        Animation animation = view.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        view.clearAnimation();
    }

    private void setImage(View view, Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    private void startAnimationShowImage(final ImageView imageView, final int index) {
        final Bitmap bitmap = getBitmap(index);
        handler.post(new Runnable() {
            @Override
            public void run() {
                stopAnimationIfStarted(imageView);
                imageView.setVisibility(Visibility.HIDE_VIEW);
                setImage(imageView, bitmap);
                Animation animationShow = SlideShowAnimation.newInstance(
                        SlideShowAnimation.Action.APPEAR, animationDuration);
                animationShow.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setVisibility(Visibility.SHOW_VIEW);
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
                imageView.startAnimation(animationShow);
                imageView.bringToFront();
            }
        });
    }

    private Bitmap getBitmap(int index) {
        Bitmap bitmap = bitmaps.get(index);
        if (bitmap == null) {
            String imagePath = imagePaths.get(index);
            bitmap = BitmapFactory.decodeFile(imagePath);
            bitmaps.set(index, bitmap);
        }
        return bitmap;
    }

    private void swapImageViews() {
        ImageView temp = imageView1;
        imageView1 = imageView2;
        imageView2 = temp;
    }

    private int nextImageIndex() {
        int index = slideShow.currentImage + 1;
        return (index < numberOfImages) ? index : 0;
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
                scheduler.shutdown();
            }
        }
    }

    private void releaseResources() {
        String prefix = getClass().getName() + ".releaseResources(): ";
        for (int i = 0; i < bitmaps.size(); ++i) {
            Log.d("Leonid", prefix + "recycle bitmap #" + i);
            recycleBitmap(bitmaps.get(i));
            bitmaps.set(i, null);
        }
    }

    private static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

}
