package magic.com.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by DX on 2016/9/18.
 */
public class ChatTextFragment extends Fragment {
    private Factory factory;
    private Fragment actionFragment;
    private Button sendMessageButton;
    private Button sendImageButton;
    private EditText input;
    private Model model;
    private LinearLayout chatContent;
    private ScrollView chatContentScroll;
    private SocketIO socketIO;
    private Socket socket;
    private String myGender;
    private String talkGender;
    private TextView systemMessage;

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
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button
//                    Toast.makeText(getActivity(), "這是返回鍵", Toast.LENGTH_SHORT).show();
                    model.changeFragment(getFragmentManager(), R.id.content_main, actionFragment);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Toast.makeText(getActivity(), "onDestroyView()", Toast.LENGTH_SHORT).show();
        socket.disconnect();
    }

    private void buttonClick() {
        sendMessageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String message = input.getText().toString();
                if (!message.equals("")) {
                    socket.emit(Constants.CHAT_MESSAGE_EVENT, message);
                    input.setText("");
                    LayoutInflater inflater = getActivity().getLayoutInflater(); //調用Activity的getLayoutInflater()
                    View sendMessageView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_send_message_view, null);
                    LinearLayout sendMessageContent = (LinearLayout) sendMessageView.findViewById(R.id.sendMessageContent);
                    TextView sendMessageViewText = new TextView(sendMessageView.getContext());
                    ImageView sendMessageViewIcon = (ImageView) sendMessageView.findViewById(R.id.sendMessageIcon);
                    Handler handler = new Handler();
                    if (myGender.equals("girl")) {
                        sendMessageViewIcon.setImageResource(R.drawable.female_icon);
                        sendMessageViewText.setTextColor(Color.parseColor(Constants.FEMALE_TEXT_COLOR));
                        sendMessageViewText.setBackgroundColor(Color.parseColor(Constants.FEMALE_BACKGROUND_COLOR));
                    } else {
                        sendMessageViewIcon.setImageResource(R.drawable.male_icon);
                        sendMessageViewText.setTextColor(Color.parseColor(Constants.MALE_TEXT_COLOR));
                        sendMessageViewText.setBackgroundColor(Color.parseColor(Constants.MALE_BACKGROUND_COLOR));
                    }
                    sendMessageViewText.setText(message);
                    sendMessageContent.addView(sendMessageViewText);
                    sendMessageViewText.setTextSize(Constants.MESSAGE_TEXT_SIZE);
                    chatContent.addView(sendMessageView);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chatContentScroll.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
//                    System.out.println("aaaaaaaaaaaaaa: "+chatContentScroll.fullScroll(ScrollView.FOCUS_DOWN));
//                    System.out.println("aaaaaaaaaaaaaa: "+sendMessageViewIcon.getWidth());
                }
            }
        });

        sendImageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                System.out.println("sendmessageImg");
                //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因為點選相片後返回程式呼叫onActivityResult
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Constants.PHOTO);
            }
        });

    }

    private void init(View view) {
        objectInit(view);
    }

    private void socketRun() {
        String findType = getArguments().getString("findType");
        //Toast.makeText(getActivity(), findType, Toast.LENGTH_SHORT).show();
        socket.emit(Constants.FIND_EVENT, findType);
        socket.on(Constants.CHAT_MESSAGE_EVENT, onChatMessage);

        socket.on(Constants.CHAT_IMAGE_EVENT, onChatImage);


        socket.on(Constants.SYSTEM_EVENT, onSystemMessage);
//        socket.on(Constants.SYSTEM_EVENT, onChatMessage);
    }

    private void objectInit(View view) {
        String findType = getArguments().getString("findType");
        factory = new Factory();
        actionFragment = factory.createActionFragment();
        sendMessageButton = (Button) view.findViewById(R.id.sendMessageButton);
        sendImageButton = (Button) view.findViewById(R.id.sendImageButton);
        input = (EditText) view.findViewById(R.id.input);
//        chatContent = (TextView) view.findViewById(R.id.chat_content);
        chatContent = (LinearLayout) view.findViewById(R.id.chatMessageContentLayout);
        chatContentScroll = (ScrollView) view.findViewById(R.id.chatMessageScrollLayout);
        model = factory.createModel();
        socketIO = factory.createSocketIO(Constants.CHAT_NAMESPACE);
        socket = socketIO.getSocket();
        systemMessage = (TextView) view.findViewById(R.id.systemMessage);
//        Toast.makeText(getActivity(), "findType   " + findType, Toast.LENGTH_SHORT).show();
        if (findType.equals("girlFindBoy")) {
//            Toast.makeText(getActivity(), "111111", Toast.LENGTH_SHORT).show();
            myGender = "girl";
        } else {
//            Toast.makeText(getActivity(), "22222", Toast.LENGTH_SHORT).show();
            myGender = "boy";
        }
        if (findType.equals("boyFindGirl")) {
            talkGender = "girl";
        } else {
            talkGender = "boy";
        }
    }

    private Emitter.Listener onChatImage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LayoutInflater inflater = getActivity().getLayoutInflater(); //調用Activity的getLayoutInflater()
                    View receiveMessageView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_receive_message_view, null);
                    LinearLayout receiveMessageContent = (LinearLayout) receiveMessageView.findViewById(R.id.receiveMessageContent);
                    ImageView receiveMessageViewIcon = (ImageView) receiveMessageView.findViewById(R.id.receiveMessageIcon);
                    ImageView receiveMessageViewImage = new ImageView(receiveMessageView.getContext());
                    Handler handler = new Handler();
                    if (talkGender.equals("girl")) {
                        receiveMessageViewIcon.setImageResource(R.drawable.female_icon);
                    } else {
                        receiveMessageViewIcon.setImageResource(R.drawable.male_icon);

                    }
                    receiveMessageContent.setBackgroundColor(Color.GRAY);
                    receiveMessageViewImage.setBackgroundColor(Color.BLUE);
                    byte[] byteArray = (byte[]) args[0];
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                    if (bmp.getHeight() <= Constants.MESSAGE_VIEW_IMAGE_MAX_HEIGHT) {
//                        receiveMessageContent.getLayoutParams().height = bmp.getHeight();
//                    } else {
//                        receiveMessageContent.getLayoutParams().height = Constants.MESSAGE_VIEW_IMAGE_MAX_HEIGHT;
//                    }
                    receiveMessageContent.getLayoutParams().height = receiveMessageContent.getLayoutParams().width * bmp.getHeight() / bmp.getWidth();
                    receiveMessageViewImage.setImageBitmap(bmp);
                    receiveMessageViewImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    receiveMessageContent.addView(receiveMessageViewImage);


                    chatContent.addView(receiveMessageView);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chatContentScroll.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            });
        }
    };

    private Emitter.Listener onChatMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receiveChatText = "" + args[0];
                    LayoutInflater inflater = getActivity().getLayoutInflater(); //調用Activity的getLayoutInflater()
                    View receiveMessageView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_receive_message_view, null);
                    LinearLayout receiveMessageContent = (LinearLayout) receiveMessageView.findViewById(R.id.receiveMessageContent);
                    TextView receiveMessageViewText = new TextView(receiveMessageView.getContext());
                    ImageView receiveMessageViewIcon = (ImageView) receiveMessageView.findViewById(R.id.receiveMessageIcon);
                    Handler handler = new Handler();
                    if (talkGender.equals("girl")) {
                        receiveMessageViewIcon.setImageResource(R.drawable.female_icon);
                        receiveMessageViewText.setTextColor(Color.parseColor(Constants.FEMALE_TEXT_COLOR));
                        receiveMessageViewText.setBackgroundColor(Color.parseColor(Constants.FEMALE_BACKGROUND_COLOR));
                    } else {
                        receiveMessageViewIcon.setImageResource(R.drawable.male_icon);
                        receiveMessageViewText.setTextColor(Color.parseColor(Constants.MALE_TEXT_COLOR));
                        receiveMessageViewText.setBackgroundColor(Color.parseColor(Constants.MALE_BACKGROUND_COLOR));
                    }
                    receiveMessageViewText.setText(receiveChatText);
                    receiveMessageContent.addView(receiveMessageViewText);
                    receiveMessageContent.setBackgroundColor(Color.GRAY);
                    receiveMessageViewText.setTextSize(Constants.MESSAGE_TEXT_SIZE);
                    chatContent.addView(receiveMessageView);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chatContentScroll.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            });
        }
    };

    private Emitter.Listener onSystemMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receiveSystemText = " " + args[0];
//                    Toast.makeText(getActivity(),receiveSystemText, Toast.LENGTH_SHORT).show();
                    systemMessage.setText(receiveSystemText);
                }
            });
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if (requestCode == Constants.PHOTO && data != null) {
            //取得照片路徑uri
            Uri uri = data.getData();
            ContentResolver cr = getActivity().getContentResolver();
            try {
                //讀取照片，型態為Bitmap
                Bitmap bmp = BitmapFactory.decodeStream(cr.openInputStream(uri));
                LayoutInflater inflater = getActivity().getLayoutInflater(); //調用Activity的getLayoutInflater()
                View sendMessageView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_send_message_view, null);
                LinearLayout sendMessageContent = (LinearLayout) sendMessageView.findViewById(R.id.sendMessageContent);
                ImageView sendMessageViewImage = new ImageView(sendMessageView.getContext());
                ImageView sendMessageViewIcon = (ImageView) sendMessageView.findViewById(R.id.sendMessageIcon);
                Handler handler = new Handler();
                if (myGender.equals("girl")) {
                    sendMessageViewIcon.setImageResource(R.drawable.female_icon);

                } else {
                    sendMessageViewIcon.setImageResource(R.drawable.male_icon);

                }
                sendMessageViewImage.setImageBitmap(bmp);
                sendMessageViewImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                sendMessageContent.getLayoutParams().height = sendMessageContent.getLayoutParams().width * bmp.getHeight() / bmp.getWidth();
                sendMessageContent.addView(sendMessageViewImage);
                chatContent.addView(sendMessageView);
                socket.emit(Constants.CHAT_IMAGE_EVENT, Bitmap2Bytes(bmp));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        chatContentScroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            } catch (FileNotFoundException e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, Constants.BITMAP_COMPRESS_RATIO, baos);
        return baos.toByteArray();
    }

}
