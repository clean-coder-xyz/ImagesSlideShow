package imagesslideshow.learning.cleancoder.com.imagesslideshow;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow.SlideShow;

/**
 * Created by lsemenov on 09.10.2014.
 */
public class ImagesChooserFragment extends FragmentHelper {

    private static final String KEY_IMAGES = "KEY_IMAGES";

    private static final int REQUEST_CODE_SELECT_IMAGE = 200;

    private static final int CONTEXT_MENU_ITEM_ID_REMOVE_IMAGE_FROM_LIST = 1;

    private static final int MENU_ITEM_SLIDE_SHOW = 1;


    private ImagesListAdapter adapter;
    private List<String> imagePaths;
    private ListView listViewImages;
    private View contentView;
    private View buttonAddImage;
    private View emptyListView;


    public static ImagesChooserFragment newInstance() {
        return new ImagesChooserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            initState();
        } else {
            restoreState(savedInstanceState);
        }
        setHasOptionsMenu(true);
    }

    private void initState() {
        imagePaths = new ArrayList<String>();
    }

    private void restoreState(Bundle savedInstanceState) {
        imagePaths = (List<String>) savedInstanceState.getSerializable(KEY_IMAGES);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState(outState);
    }

    private void saveState(Bundle outState) {
        outState.putSerializable(KEY_IMAGES, (Serializable) imagePaths);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_images_chooser, null);
        findViews();
        initView();
        return contentView;
    }

    private void findViews() {
        listViewImages = (ListView) contentView.findViewById(R.id.list_view_images);
        emptyListView = contentView.findViewById(R.id.empty_list_view);
        buttonAddImage = contentView.findViewById(R.id.button_add_image);
    }

    private void initView() {
        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddImage();
            }
        });
        adapter = new ImagesListAdapter(getActivity(), imagePaths);
        listViewImages.setAdapter(adapter);
        listViewImages.setEmptyView(emptyListView);
        listViewImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                onImageClicked(position);
            }
        });
        registerForContextMenu(listViewImages);
    }

    private void onImageClicked(int position) {
        String imagePath = imagePaths.get(position);
        replaceItself(ImageViewerFragment.newInstance(imagePath), true);
    }

    private void onAddImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CODE_SELECT_IMAGE) && (resultCode == Activity.RESULT_OK)) {
            onImageSelected(data);
        }
    }

    private void onImageSelected(Intent imageReturnedIntent) {
        imagePaths.add(extractImagePath(imageReturnedIntent));
        adapter.notifyDataSetChanged();
    }

    private String extractImagePath(Intent imageReturnedIntent) {
        Uri uriOfSelectedImage = imageReturnedIntent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver()
                .query(uriOfSelectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String imagePath = imagePaths.get(info.position);
        menu.setHeaderTitle(imagePath);
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_ID_REMOVE_IMAGE_FROM_LIST, Menu.NONE, R.string.menu_item_remove_image_from_list);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONTEXT_MENU_ITEM_ID_REMOVE_IMAGE_FROM_LIST:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                onRemoveImage(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void onRemoveImage(int imagePosition) {
        imagePaths.remove(imagePosition);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, MENU_ITEM_SLIDE_SHOW, Menu.NONE, R.string.slide_show);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemSlideShow = menu.findItem(MENU_ITEM_SLIDE_SHOW);
        boolean slideShowIsAvailable = (getNumberOfImages() > 0);
        menuItemSlideShow.setVisible(slideShowIsAvailable);
    }

    private int getNumberOfImages() {
        return (imagePaths != null) ? imagePaths.size() : 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_SLIDE_SHOW:
                onSlideShow();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSlideShow() {
        Intent intent = new Intent(getActivity(), SlideShowActivity.class);
        SlideShow slideShow = new SlideShow().imagePaths(imagePaths).period(5000);
        SlideShowActivity.insertArguments(intent, slideShow);
        startActivity(intent);
    }

}
