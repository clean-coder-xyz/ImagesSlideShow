package imagesslideshow.learning.cleancoder.com.imagesslideshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by lsemenov on 09.10.2014.
 */
public class ImagesListAdapter extends ArrayAdapter<String> {

    private static final int LAYOUT_ID = R.layout.item_images_list;

    private final LayoutInflater layoutInflater;

    public ImagesListAdapter(Context context, List<String> imagePaths) {
        super(context, LAYOUT_ID, imagePaths);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = (convertView != null) ? convertView : layoutInflater.inflate(LAYOUT_ID, null);
        setItemView(itemView, getItem(position));
        return itemView;
    }

    private void setItemView(View itemView, String imagePath) {
        TextView textViewImageDescription = (TextView) itemView.findViewById(R.id.text_view_image_description);
        textViewImageDescription.setText(getFileNameFromPath(imagePath));
    }

    private static String getFileNameFromPath(String path) {
        int indexOfLastSeparator = path.lastIndexOf(File.separator);
        if (indexOfLastSeparator == -1) {
            return path;
        }
        return path.substring(indexOfLastSeparator + 1);
    }

}
