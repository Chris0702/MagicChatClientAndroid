package magic.com.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    //    private TextView chatContent;
    private LinearLayout chatContent;
    private ScrollView chatContentScroll;
    private SocketIO socketIO;
    private Socket socket;
    private String chatContentText;
    private String gender;
//    private View sendMessageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.chat_text_fragment, container, false);
        init(returnView);
        socketRun();
        buttonClick();
        return returnView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getActivity(), "onDestroyView()", Toast.LENGTH_SHORT).show();
        socket.disconnect();
    }

    private void buttonClick() {
        goAction.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "goAction click", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(getActivity(), "已傳送", Toast.LENGTH_SHORT).show();
                    input.setText("");
                    LayoutInflater inflater = getActivity().getLayoutInflater(); //調用Activity的getLayoutInflater()
                    View sendMessageView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_send_message_view, null);
                    TextView sendMessageViewText = (TextView) sendMessageView.findViewById(R.id.sendMessageText);
                    ImageView sendMessageViewIcon = (ImageView) sendMessageView.findViewById(R.id.sendMessageIcon);
                    if (gender.equals("girl")) {
//                        Toast.makeText(getActivity(), "333", Toast.LENGTH_SHORT).show();
                        sendMessageViewIcon.setImageResource(R.drawable.female_icon);
                        sendMessageViewText.setTextColor(Color.parseColor(Constants.FEMALE_TEXT_COLOR));
                        sendMessageViewText.setBackgroundColor(Color.parseColor(Constants.FEMALE_BACKGROUND_COLOR));
                    } else {
//                        Toast.makeText(getActivity(), "44444", Toast.LENGTH_SHORT).show();
                        sendMessageViewIcon.setImageResource(R.drawable.male_icon);
                        sendMessageViewText.setTextColor(Color.parseColor(Constants.MALE_TEXT_COLOR));
                        sendMessageViewText.setBackgroundColor(Color.parseColor(Constants.MALE_BACKGROUND_COLOR));
                    }
                    sendMessageViewText.setText(message);
                    chatContent.addView(sendMessageView);
//                    View view = inflater.inflate(R.layout.chat_send_message_view, (ViewGroup)findViewById(R.id.test));
                }
            }
        });
    }

    private void init(View view) {
        objectInit(view);
    }

    private void socketRun() {
        String findType = getArguments().getString("findType");
        Toast.makeText(getActivity(), findType, Toast.LENGTH_LONG).show();
        socket.emit(Constants.FIND_EVENT, findType);
        socket.on(Constants.CHAT_EVENT, onChatMessage);
    }

    private void objectInit(View view) {
        String findType = getArguments().getString("findType");
        factory = new Factory();
        actionFragment = factory.createActionFragment();
        goAction = (Button) view.findViewById(R.id.goAction);
        send = (Button) view.findViewById(R.id.send);
        input = (EditText) view.findViewById(R.id.input);
//        chatContent = (TextView) view.findViewById(R.id.chat_content);
        chatContent = (LinearLayout) view.findViewById(R.id.chatMessageContentLayout);
        chatContentScroll = (ScrollView) view.findViewById(R.id.chatMessageScrollLayout);
        model = factory.createModel();
        socketIO = factory.createSocketIO(Constants.CHAT_NAMESPACE);
        socket = socketIO.getSocket();
        chatContentText = "";
        Toast.makeText(getActivity(), "findType   " + findType, Toast.LENGTH_SHORT).show();
        if (findType.equals("girlFindBoy")) {
//            Toast.makeText(getActivity(), "111111", Toast.LENGTH_SHORT).show();
            gender = "girl";
        } else {
//            Toast.makeText(getActivity(), "22222", Toast.LENGTH_SHORT).show();
            gender = "boy";
        }
    }

//    private View getChatSendMessageView(String type)
//    {
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.chat_send_message_view, (ViewGroup)findViewById(R.id.test));
//    }

    private Emitter.Listener onChatMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatContentText = "" + args[0];
                    TextView receiveMessage = new TextView(getActivity());
                    receiveMessage.setText(chatContentText);
                    receiveMessage.setTextSize(50);
//                    chatContent.addView(receiveMessage);
                    chatContentScroll.fullScroll(ScrollView.FOCUS_DOWN);
                    //chatContentText = chatContentText + args[0] + "\n";
                    //chatContent.setText(chatContentText);
                }
            });
        }
    };
}
