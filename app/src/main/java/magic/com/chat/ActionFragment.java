package magic.com.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by DX on 2016/9/18.
 */
public class ActionFragment extends Fragment {
    private Factory factory;
    private Fragment chatTextFragment;
    private Button boyFindGirl;
    private Button girlFindBoy;
    private Button boyFindBoy;
    private Model model;
    private float upX ,upY, downX ,downY;
    MainActivity.MyTouchListener myTouchListener;
    LinearLayout action_background ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.action_fragment, container, false);
        init(returnView);
        setListener();
        return returnView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Toast.makeText(getActivity(), "onDestroyView()", Toast.LENGTH_SHORT).show();
        ((MainActivity)this.getActivity()).unRegisterMyTouchListener(myTouchListener);
    }

    private void init(View view) {
        objectInit(view);
    }

    private void objectInit(View view)
    {
        factory = new Factory();
        chatTextFragment = factory.createChatTextFragment();
        boyFindGirl = (Button) view.findViewById(R.id.boyFindGirl);
        girlFindBoy = (Button) view.findViewById(R.id.girlFindBoy);
        boyFindBoy = (Button) view.findViewById(R.id.boyFindBoy);
        action_background =(LinearLayout)view.findViewById(R.id.action_background);

        action_background.setBackgroundResource(R.drawable.female_body);
        model = factory.createModel();
    }

    private void setListener()
    {
        /* Fragment中，注册
            * 接收MainActivity的Touch回调的对象
            * 重写其中的onTouchEvent函数，并进行该Fragment的逻辑处理
            */
        myTouchListener = new MainActivity.MyTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                // 处理手势事件
                //继承了Activity的onTouchEvent方法，直接监听点击事件
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    downX = event.getX();
                    downY = event.getY();
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    //当手指离开的时候
                    upX = event.getX();
                    upY = event.getY();
                    if(downY - upY > Constants.TOUCH_MOVE_DIFF_Y) {
                        Toast.makeText(getActivity(), "向上滑", Toast.LENGTH_SHORT).show();
                    } else if(upY - downY > Constants.TOUCH_MOVE_DIFF_Y) {
                        Toast.makeText(getActivity(), "向下滑", Toast.LENGTH_SHORT).show();
                    } else if(downX - upX > Constants.TOUCH_MOVE_DIFF_X) {
                        Toast.makeText(getActivity(), "向左滑", Toast.LENGTH_SHORT).show();
                    } else if(upX - downX > Constants.TOUCH_MOVE_DIFF_X) {
                        Toast.makeText(getActivity(), "向右滑", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };
        // 将myTouchListener注册到分发列表
        ((MainActivity)this.getActivity()).registerMyTouchListener(myTouchListener);

        boyFindGirl.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "boyFindGirl click" , Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("findType", "boyFindGirl");
                chatTextFragment.setArguments(bundle);
                model.changeFragment(getFragmentManager(), R.id.content_main, chatTextFragment);
            }
        });
        girlFindBoy.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "girlFindBoy click", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("findType", "girlFindBoy");
                chatTextFragment.setArguments(bundle);
                model.changeFragment(getFragmentManager(), R.id.content_main, chatTextFragment);
            }
        });
        boyFindBoy.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "boyFindBoy click", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("findType", "boyFindBoy");
                chatTextFragment.setArguments(bundle);
                model.changeFragment(getFragmentManager(), R.id.content_main, chatTextFragment);
            }
        });
    }
}
