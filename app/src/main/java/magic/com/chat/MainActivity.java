package magic.com.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Fragment actionFragment;
    private Factory factory;
    private Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);
        setDefaultFragment();
    }

    private void init() {
        factory = new Factory();
        actionFragment = factory.createActionFragment();
        model = factory.createModel();
    }

    private void setDefaultFragment() {
        model.changeFragment(getFragmentManager(), R.id.content_main, actionFragment);
    }
}
