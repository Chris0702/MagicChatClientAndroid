package magic.com.chat;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by DX on 2016/9/18.
 */
public class Factory {
    public ActionFragment createActionFragment() {
        return new ActionFragment();
    }

    public ChatTextFragment createChatTextFragment() {
        return new ChatTextFragment();
    }

    public SocketIO createSocketIO(String nameSpace) {
        return new SocketIO(nameSpace);
    }

    public Model createModel() {
        return new Model();
    }

//    public View getChatSendMessageView(String type)
//    {
//        LayoutInflater inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);;
//        View returnView = inflater.inflate(R.layout.action_fragment, container, false);
//        if(type.equals("boy"))
//        {
//
//        }
//        else if(type.equals("girl"))
//        {
//
//        }
//    }

}
