package imagesslideshow.learning.cleancoder.com.imagesslideshow;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

/**
 * Created by lsemenov on 09.10.2014.
 */
public class FragmentHelper extends Fragment {

    public int getContainerId() {
        return ((ViewGroup)getView().getParent()).getId();
    }

    public void replaceItself(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(getContainerId(), fragment, fragment.getClass().getName());
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

}
