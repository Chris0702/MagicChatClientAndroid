package magic.com.chat;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.TextView;

/**
 * Created by DX on 2016/9/18.
 */
public class Model {
    public void changeFragment(FragmentManager fragmentManager, int id, Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(id, fragment);
        transaction.commit();
    }
}
