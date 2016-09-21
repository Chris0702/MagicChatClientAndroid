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

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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
    private TextView chatContent;
    private SocketIO socketIO;
    private Socket socket;
    private String chatContentText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.chat_text_fragment, container, false);
        init(returnView);
        socket.on(Constants.CHAT_EVENT, onChatMessage);
        buttonClick();
        return returnView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getActivity(), "onDestroyView()", Toast.LENGTH_LONG).show();
        socket.disconnect();
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
                    socket.emit(Constants.CHAT_EVENT, message);
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
        chatContent = (TextView) view.findViewById(R.id.chat_content);
        model = factory.createModel();
        socketIO = factory.createSocketIO(Constants.CHAT_NAMESPACE);
        socket = socketIO.getSocket();
        chatContentText = "";
    }

    private Emitter.Listener onChatMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatContentText = chatContentText + args[0] + "\n";
                    chatContent.setText(chatContentText);
                }
            });
        }
    };
}
