package imagesslideshow.learning.cleancoder.com.imagesslideshow;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            placeStartFragmentOnLayout();
        }
    }

    private void placeStartFragmentOnLayout() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, ImagesChooserFragment.newInstance(), ImagesChooserFragment.class.getName())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }
}
