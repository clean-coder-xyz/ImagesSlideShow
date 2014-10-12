package imagesslideshow.learning.cleancoder.com.imagesslideshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lsemenov on 10.10.2014.
 */
public class SlideShow2 {
    private static final int DEFAULT_DELAY_IN_SECONDS = 6;

    private final Object LOCK_START = new Object();
    private final Object LOCK_STOP = new Object();
    private final Object LOCK_SLIDING = new Object();
    private final Object LOCK_RESOURCES_ARE_USING = new Object();

    private final Animation animationShow;
    private final Animation animationHide;
    private final Context context;
    private boolean isSliding;
    private boolean isStarted;
    private boolean isStopped;
    private boolean areResourcesUsing;
    private ImageView imageView1;
    private ImageView imageView2;
    private int currentImage;
    private int delayInSeconds;
    private List<Bitmap> bitmaps;
    private List<ImageMeta> images;
    private ScheduledExecutorService scheduler;

    public SlideShow2(Context context, ImageView imageView1, ImageView imageView2) {
        this.context = context;
        this.animationShow = AnimationUtils.loadAnimation(context, R.anim.slide_show_appear);
        this.animationHide = AnimationUtils.loadAnimation(context, R.anim.slide_show_disappear);
        this.imageView1 = imageView1;
        this.imageView2 = imageView2;
        this.isSliding = false;
        this.isStarted = false;
        this.isStopped = false;
        this.areResourcesUsing = false;
        this.delayInSeconds = DEFAULT_DELAY_IN_SECONDS;
        this.images = Collections.emptyList();
    }

    public void setDelayInSeconds(int delayInSeconds) {
        if (delayInSeconds < 1) {
            throw new IllegalArgumentException("delay couldn't be less than 1 second");
        }
        this.delayInSeconds = delayInSeconds;
    }

    public void setImages(List<ImageMeta> images) {
        if (images == null) {
            throw new NullPointerException("Argument <images> is null");
        }
        this.images = new ArrayList<ImageMeta>(images);
    }

    public void start() {
        synchronized (LOCK_START) {
            if (isStarted) {
                throw new IllegalStateException("Slide show is already started");
            }
            isStarted = true;
            synchronized (LOCK_RESOURCES_ARE_USING) {
                if (areResourcesUsing) {
                    throw new IllegalStateException("You couldn't start slide show if resources are not realised");
                }
                areResourcesUsing = true;
            }
            onStart();
        }
    }

    protected void onStart() {
        if (images.isEmpty()) {
            throw new IllegalStateException("You couldn't start slide show without images. There is no images.");
        }
        bitmaps = newArrayListWithSize(images.size());
        currentImage = -1;
        startScheduler();
    }

    private static <T> ArrayList<T> newArrayListWithSize(int size) {
        ArrayList<T> list = new ArrayList<T>(size);
        for (int i = 0; i < size; ++i) {
            list.add(null);
        }
        return list;
    }

    private void startScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(slider, 0, delayInSeconds, TimeUnit.SECONDS);
    }

    private final Runnable slider = new Runnable() {
        public void run() {
            synchronized (LOCK_STOP) {
                if (isStopped) {
                    return;
                }
                synchronizedSlide();
            }
        }
    };

    private void synchronizedSlide() {
        synchronized (LOCK_SLIDING) {
            if (isSliding) {
                return;
            }
            isSliding = true;
        }

        try {
            slide();
        } catch (Throwable exception) {
            Log.e("Leonid", exception.getMessage(), exception);
        }

        synchronized (LOCK_SLIDING) {
            isSliding = false;
        }
    }

    private void slide() {
        if (currentImage != -1) {
            hideImage(imageView1);
        }
        currentImage = nextImageIndex();
        showImageByIndex(imageView2, currentImage);
        swapImageViews();
    }

    private void hideImage(final ImageView imageView) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                stopAnimationIfStarted(imageView);
                animationHide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // skip
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setBackground(imageView, null);
                        imageView.setVisibility(View.INVISIBLE);
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
    }

    private void setBackground(View view, Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    private void showImageByIndex(final ImageView imageView, int index) {
        final Bitmap bitmap = getBitmap(index);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                stopAnimationIfStarted(imageView);
                setBackground(imageView, bitmap);
                animationShow.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        imageView.setVisibility(View.VISIBLE);
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
            ImageMeta image = images.get(index);
            bitmap = image.getBitmap();
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
        int index = currentImage + 1;
        return (index < images.size()) ? index : 0;
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
            }
            synchronized (LOCK_RESOURCES_ARE_USING) {
                if (!areResourcesUsing) {
                    throw new AssertionError("Resources must be used before realising them");
                }
                areResourcesUsing = false;
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
