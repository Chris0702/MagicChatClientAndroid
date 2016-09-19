package magic.com.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by DX on 2016/9/18.
 */
public class ActionFragment extends Fragment {
    private Factory factory;
    private Fragment chatTextFragment;
    private Button goChat;
    private Model model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.action_fragment, container, false);
        init(returnView);
        goChat.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "goChat click" + R.id.content_main, Toast.LENGTH_LONG).show();
                model.changeFragment(getFragmentManager(), R.id.content_main, chatTextFragment);
            }
        });
        return returnView;
    }

    private void init(View view) {
        factory = new Factory();
        chatTextFragment = factory.createChatTextFragment();
        goChat = (Button) view.findViewById(R.id.goChat);
        model = factory.createModel();
    }
}
