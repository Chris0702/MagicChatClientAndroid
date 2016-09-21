package magic.com.chat;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


/**
 * Created by DX on 2016/9/18.
 */

public class SocketIO {
    private Socket socket;
    public SocketIO(String nameSpace) {
        try {
            Log.d("debug", Constants.SERVER_URL + nameSpace);
            socket = IO.socket(Constants.SERVER_URL + nameSpace);
            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void disconnect() {
        socket.disconnect();
    }
}


