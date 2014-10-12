package imagesslideshow.learning.cleancoder.com.imagesslideshow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lsemenov on 09.10.2014.
 */
public class ImageViewerFragment extends FragmentHelper {

    private static final String KEY_IMAGE = "KEY_IMAGE";

    private Bitmap bitmap;
    private boolean destroyed;
    private View contentView;

    public static ImageViewerFragment newInstance(String imagePath) {
        Bundle args = new Bundle();
        args.putString(KEY_IMAGE, imagePath);
        ImageViewerFragment fragment = new ImageViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        destroyed = false;
        bitmap = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_image_viewer, null);
        loadImage();
        return contentView;
    }

    private void loadImage() {
        AsyncTask<Void,Void,Bitmap> taskImageLoader = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                return BitmapFactory.decodeFile(getImagePath());
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                onImageLoaded(bitmap);
            }
        };
        taskImageLoader.execute();
    }

    protected String getImagePath() {
        return getArguments().getString(KEY_IMAGE);
    }

    private void onImageLoaded(Bitmap bitmap) {
        if (isDestroyed()) {
            releaseBitmapResources(bitmap);
            return;
        }
        if ((bitmap == null) || bitmap.isRecycled()) {
            onLoadedImageIsInvalid();
            return;
        }
        setImage(bitmap);
    }

    private void onLoadedImageIsInvalid() {
        TextView textView = (TextView) contentView.findViewById(R.id.text_view);
        textView.setText(R.string.loaded_image_is_invalid);
        hideProgressBar();
    }

    private void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        ImageView imageView = (ImageView) contentView.findViewById(R.id.image_view);
        imageView.setImageBitmap(bitmap);
        hideProgressBar();
    }

    private void hideProgressBar() {
        View progressBar = contentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private boolean isDestroyed() {
        return destroyed;
    }

    private static void releaseBitmapResources(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
        releaseBitmapResources(bitmap);
        bitmap = null;
    }
}
