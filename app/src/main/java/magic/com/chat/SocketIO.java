package magic.com.chat;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by DX on 2016/9/18.
 */

public class SocketIO {
    private Socket socket;
    private Activity controlActivity;
    private TextView controlTextView;

    public SocketIO(String nameSpace) {
        try {
            Log.d("debug", Constants.SERVER_URL + nameSpace);
            socket = IO.socket(Constants.SERVER_URL + nameSpace);
            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void emit(String event, String message) {
        socket.emit(event, message);
    }

    public void onChatMessage(Activity activity, TextView TextView) {
        controlActivity = activity;
        controlTextView = TextView;
        socket.on(Constants.CHAT_EVENT, onChatMessage);
    }

    public Socket getSocket() {
        return socket;
    }

    public void disconnect() {
        socket.disconnect();
    }

    private Emitter.Listener onChatMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            controlActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = controlTextView.getText().toString();
                    message = message + args[0] + "\n";
                    controlTextView.setText(message);
                }
            });
        }
    };
}


