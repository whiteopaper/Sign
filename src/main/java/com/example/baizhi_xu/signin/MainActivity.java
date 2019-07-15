package com.example.baizhi_xu.signin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baizhi_xu.signin.Adapter.ViewPagerAdapter;
import com.example.baizhi_xu.signin.model.People;
import com.example.baizhi_xu.signin.model.Plan;
import com.example.baizhi_xu.signin.model.Record;
import com.example.baizhi_xu.signin.unit.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager ;
    FragmentTransaction fragmentTransaction;
    private BottomNavigationView navigation;
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mVpMain;
    private Fragment recordFragment = new RecordFragment();
    private Fragment signFragment = new SignFragment();
    private Fragment mineFragment = new MineFragment();
    private List<Fragment> fragments = new ArrayList<>();
    private People people;
    private Plan plan;
    private Record record;
    private RequestQueue requestQueue;

    public final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "暂无签到任务！", Toast.LENGTH_SHORT).show();
                    replaceFragment(0);
                    break;
                case 1:
                    replaceFragment(1);
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "不在签到时间段！", Toast.LENGTH_SHORT).show();
                    replaceFragment(0);
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "签到成功！", Toast.LENGTH_SHORT).show();
                    replaceFragment(0);
                    break;
                case 4:

                    break;
                default:
                    break;
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_record:
                    mVpMain.setCurrentItem(0);
                    return true;
                case R.id.navigation_sign:
                    mVpMain.setCurrentItem(1);
                    getSignData("getSign");
                    return true;
                case R.id.navigation_mine:
                    mVpMain.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };
    private ViewPager.OnPageChangeListener mOnPageChangeListener
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            navigation.getMenu().getItem(position).setChecked(true);
            //写滑动页面后做的事，使每一个fragmen与一个page相对应
            if (position==0){
                Toast.makeText(getApplicationContext(), "下拉刷新", Toast.LENGTH_SHORT).show();
            }else if (position==1){
                getSignData("getSign");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        people = (People) getIntent().getSerializableExtra("People");
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragments.add(recordFragment);
        fragments.add(signFragment);
        fragments.add(mineFragment);
        manager = getSupportFragmentManager();
        mViewPagerAdapter = new ViewPagerAdapter(manager, fragments);
        mVpMain = findViewById(R.id.vp_main);
        mVpMain.setAdapter(mViewPagerAdapter);
        mVpMain.setOffscreenPageLimit(4);
        mVpMain.setOnPageChangeListener(mOnPageChangeListener);
    };

    public void replaceFragment(int tag) {
        SignFragment newSignFragment=new SignFragment();
        if (tag==1&&"未签".equals(record.getState())){
            newSignFragment = SignFragment.newInstance(plan,record);
        }
        fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_sign,newSignFragment,null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getSignData(String tag) {
        String Account = "&account="+people.getAccount();
        String Url = Constant.urlGetSign+Account;
        Log.d("sign",Url);
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.cancelAll(tag);
        StringRequest request = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Message message = new Message();
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.toString().equals("[]")) {
                        record = null;
                        plan = null;
                        message.what = 2;
                    } else if (jsonArray.toString().equals("[null,null]")){
                        record = null;
                        plan = null;
                        message.what = 0;
                    } else {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        record = new Gson().fromJson(jsonObject.toString(), Record.class);
                        jsonObject = jsonArray.getJSONObject(1);
                        plan = new Gson().fromJson(jsonObject.toString(), Plan.class);
                        message.what = 1;
                    }handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "网络异常！", Toast.LENGTH_SHORT).show();
                Log.e("TAG", error.getMessage(), error);
            }
        });
        request.setTag(tag);
        requestQueue.add(request);
    }

    public static void openActivity(Context context) {
        Intent intent=new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }
}
