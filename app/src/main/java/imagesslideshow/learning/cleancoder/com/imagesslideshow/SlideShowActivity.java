package imagesslideshow.learning.cleancoder.com.imagesslideshow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow.SlideShow;
import imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow.SlideShowController;
import imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow.SlideShowOptions;


public class SlideShowActivity extends ActionBarActivity {

    private static final String KEY_SLIDE_SHOW = "KEY_SLIDE_SHOW";

    private SlideShow<String> slideShow;
    private SlideShowController slideShowController;

    public static void insertArguments(Intent intent, SlideShow slideShow) {
        intent.putExtra(KEY_SLIDE_SHOW, slideShow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        ImageView imageView1 = findImageViewById(R.id.image_view_1);
        ImageView imageView2 = findImageViewById(R.id.image_view_2);
        slideShow = (savedInstanceState == null)
                            ? (SlideShow) getIntent().getParcelableExtra(KEY_SLIDE_SHOW)
                            : (SlideShow) savedInstanceState.getParcelable(KEY_SLIDE_SHOW);
        slideShowController = slideShow.start(this, imageView1, imageView2, SlideShowOptions.IMAGES_FROM_PATHS);
    }

    private ImageView findImageViewById(int id) {
        return (ImageView) findViewById(id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SLIDE_SHOW, slideShow);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        slideShowController.stop();
    }

}
