package magic.com.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by DX on 2016/9/18.
 */
public class ChatTextFragment extends Fragment {
    private Factory factory;
    private Fragment actionFragment;
    private Button goAction;
    private Button send;
    private EditText input;
    private Model model;
    private TextView chat_content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.chat_text_fragment, container, false);
        init(returnView);
        model.chatOnMessage(getActivity(), chat_content);
        buttonClick();
        return returnView;
    }

    private void buttonClick() {
        goAction.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "goAction click", Toast.LENGTH_LONG).show();
                model.changeFragment(getFragmentManager(), R.id.content_main, actionFragment);
            }
        });
        send.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String message = input.getText().toString();
                if (!message.equals("")) {
                    model.chatSendMessage(message);
                    Toast.makeText(getActivity(), "已傳送", Toast.LENGTH_LONG).show();
                    input.setText("");
                }
            }
        });
    }

    private void init(View view) {
        factory = new Factory();
        actionFragment = factory.createActionFragment();
        goAction = (Button) view.findViewById(R.id.goAction);
        send = (Button) view.findViewById(R.id.send);
        input = (EditText) view.findViewById(R.id.input);
        chat_content = (TextView) view.findViewById(R.id.chat_content);
        model = factory.createModel();
    }
}
