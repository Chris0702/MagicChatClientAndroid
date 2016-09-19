package magic.com.chat;

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

    public Model createModel() {
        return new Model();
    }

}
