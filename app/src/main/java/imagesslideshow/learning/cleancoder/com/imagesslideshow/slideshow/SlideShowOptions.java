package imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.widget.ImageView;

/**
 * Created by lsemenov on 12.10.2014.
 */
public class SlideShowOptions {

    public static final SlideShowOption<String,Bitmap,ImageView> IMAGES_FROM_PATHS
            = new SlideShowOption<String, Bitmap, ImageView>() {

        @Override
        public Bitmap transform(String what) {
            return BitmapFactory.decodeFile(what);
        }

        @Override
        public void render(Bitmap bitmap, ImageView view) {
            BitmapDrawable drawable = new BitmapDrawable(view.getResources(), bitmap);
            if (Build.VERSION.SDK_INT >= 16) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }

        @Override
        public void releaseResources(String item, Bitmap bitmap) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    };

}
