package com.skyjaj.xkids.study;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.skyjaj.baseAdapter.bean.BaseMessage;
import com.skyjaj.baseAdapter.bean.ItemListViewBean;
import com.skyjaj.baseAdapter.bean.Message;
import com.skyjaj.test.ReFlashListView;

import java.util.List;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/21.
 */
public class MainInterfaceActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, ReFlashListView.IReflashListener{

    private Button btnOk;
    private EditText inputText;
    private  CommonAdapter<ItemListViewBean> adapter;


    private static ReFlashListView mListView;
    private ListView leftListView;
    private static List<Message> mDatas;
    private static Map<BaseMessage.Type,Integer> itemViews;

    private  List<ItemListViewBean> mListView2;
    private List<ItemListViewBean> mDatas2;

//    private MyHandler myHandler = new MyHandler(this);

    public MainInterfaceActivity() {
    }

/*    private static class MyHandler extends Handler {

        private final WeakReference<Context> mContext;

        protected MyHandler(Context context) {
            this.mContext=new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            Message m = (Message) msg.obj;
//            mDatas.add(m);
//            adapter.notifyDataSetChanged();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_main);
        initDatas();
        initListView();



    }


    public void initDatas() {
//        mDatas = new ArrayList<Message>();
        mDatas2 = new ArrayList<ItemListViewBean>();
        ItemListViewBean bean1 = new ItemListViewBean("这是标题1", "这是内容 这是内容 这是内容", "2015-11-25", "119");
        ItemListViewBean bean2 = new ItemListViewBean("这是标题2", "这是内容 这是内容 这是内容", "2015-11-25", "119");
        ItemListViewBean bean3 = new ItemListViewBean("这是标题3", "这是内容 这是内容 这是内容", "2015-11-25", "119");
        ItemListViewBean bean4 = new ItemListViewBean("这是标题4", "这是内容 这是内容 这是内容", "2015-11-25", "119");
        ItemListViewBean bean5 = new ItemListViewBean("这是标题5", "这是内容 这是内容 这是内容", "2015-11-25", "119");
        bean1.setType(BaseMessage.Type.INCOMING);
        bean2.setType(BaseMessage.Type.INCOMING);
        bean3.setType(BaseMessage.Type.INCOMING);
        bean4.setType(BaseMessage.Type.INCOMING);
        bean5.setType(BaseMessage.Type.INCOMING);

        mDatas2.add(bean1);
        mDatas2.add(bean2);
        mDatas2.add(bean3);
        mDatas2.add(bean4);
        mDatas2.add(bean5);
/*        Message msg = new Message("大叔","我喜欢嫖赌。。",new Date(), Message.Type.INCOMING);
        mDatas.add(msg);
        msg=new Message("天杰","大叔，你本来就喜欢嫖赌。。",new Date(), Message.Type.OUTCOMING);
        mDatas.add(msg);
        msg=new Message("天杰","大叔你本来就喜欢嫖赌。。",new Date(), Message.Type.OUTCOMING);
        mDatas.add(msg);
        msg=new Message("天杰","大叔嫖赌。。",new Date(), Message.Type.OUTCOMING);
        mDatas.add(msg);*/
    }

    public void initListView(){
        mListView = (ReFlashListView) findViewById(R.id.lv);
        leftListView = (ListView) findViewById(R.id.left_lv);
        mListView.setInterface(this);

        itemViews = new HashMap<BaseMessage.Type,Integer>();
        itemViews.put(BaseMessage.Type.INCOMING,R.layout.item_listview);
//        itemViews.put(BaseMessage.Type.INCOMING,R.layout.item_msg_from);
//        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.item_msg_to);
////        itemViews.put(BaseMessage.Type.INCOMING,R.layout.item_listview);
//        System.out.println("layout :" + R.layout.item_msg_from + " " + itemViews.get(BaseMessage.Type.INCOMING));
//        System.out.println("layout2 :" + R.layout.item_msg_to + " " + itemViews.get(BaseMessage.Type.OUTCOMING));

/*        adapter = new CommonAdapter<Message>(MainInterfaceActivity.this,mDatas,itemViews) {
            @Override
            public void convert(ViewHolder holder, Message bean) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (bean.getType() == Message.Type.INCOMING) {

                    holder.setText(R.id.id_from_msg, bean.getMsg()).
                            setText(R.id.id_from_msg_time, df.format(bean.getDate()))
                            .setText(R.id.id_from_msg_name,bean.getName());
                } else {
                    holder.setText(R.id.id_to_msg, bean.getMsg()).
                            setText(R.id.id_to_msg_time, df.format(bean.getDate()))
                            .setText(R.id.id_to_msg_name,bean.getName());
                }
            }
        };
        mListView.setAdapter(adapter);
        inputText = (EditText) findViewById(R.id.id_input_msg);
        btnOk = (Button) findViewById(R.id.id_send_msg);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString();
                System.out.println("text " +text);
                if(text == null || text.length() <=0) {
                    System.out.println("text if :"+text);
                    return;
                }else {
                    System.out.println("text else:" + text + " size :" + adapter);
                    Message msg = new Message("天杰",text,new Date(), BaseMessage.Type.OUTCOMING);
                    mDatas.add(msg);
                    System.out.println(" size :" + mDatas.size());
                    adapter.notifyDataSetChanged();
                    inputText.setText("");
//                    android.os.Message m = android.os.Message.obtain();
//                    m.obj = msg;
//                    myHandler.sendMessage(m);
                }

            }
        });*/

        adapter = new CommonAdapter<ItemListViewBean>(MainInterfaceActivity.this,mDatas2,itemViews) {


            @Override
            public void convert(ViewHolder holder, ItemListViewBean bean) {
                holder.setText(R.id.mtitle, bean.getmTitle()).setText(R.id.mtime,bean.getmTime())
                        .setText(R.id.mtext,bean.getmText()).setText(R.id.phone, bean.getmPhone());
            }
        };
        mListView.setAdapter(adapter);
        leftListView.setAdapter(adapter);
        //监听item
        mListView.setOnItemClickListener(this);
        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainInterfaceActivity.this, "左位置："+position, Toast.LENGTH_SHORT).show();
            }
        });

//        mListView.setAdapter(new CommonAdapter<ItemListViewBean>(MainInterfaceActivity.this,mDatas2,itemViews) {
//            @Override
//            public void convert(ViewHolder holder, ItemListViewBean bean) {
//                holder.setText(R.id.mtitle, bean.getmTitle()).setText(R.id.mtime,bean.getmTime())
//                        .setText(R.id.mtext,bean.getmText()).setText(R.id.phone,bean.getmPhone());
//            }
//        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("position :"+position);
        Log.d("xys", "位置"+position);
        Toast.makeText(this, "位置" + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
      public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activity结束的时候结束任务
        //myHandler.removeCallbacksAndMessages(null);
    }



    private void setReflashData() {
        for (int i = 0; i < 2; i++) {
            ItemListViewBean entity = new ItemListViewBean();
            entity.setType(BaseMessage.Type.INCOMING);
            entity.setmPhone("10085");
            entity.setmText("skyjaj");
            entity.setmTitle("哎哟，不错");
            entity.setmTime("2015-12-13");

            mDatas2.add(0,entity);
        }
    }


    //对IReflashListener的实现
    @Override
    public void onReflash() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取最新数据
                setReflashData();
                //通知界面显示
                adapter.notifyDataSetChanged();
                //通知listview 刷新数据完毕；
                mListView.reflashComplete();
            }
        }, 2000);
    }

}
