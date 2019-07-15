package com.example.baizhi_xu.signin;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baizhi_xu.signin.Adapter.RecyclerViewAdapter;
import com.example.baizhi_xu.signin.Adapter.SearchAdapter;
import com.example.baizhi_xu.signin.Listener.EndlessRecyclerOnScrollListener;
import com.example.baizhi_xu.signin.model.People;
import com.example.baizhi_xu.signin.model.RecordSignRate;
import com.example.baizhi_xu.signin.unit.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout0;
    private RecyclerView recyclerView0;
    private SearchAdapter loadMoreAdapter;
    private People people;
    private List<RecordSignRate> RecordSignRates = new ArrayList<>();
    private List<RecordSignRate> dataList = new ArrayList<>();

    // 上拉加载一次显示的数量
    private final int NUMBER_ONCE = 5;
    // 当前显示的数量
    private int Number_Current=0;

    public final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "暂无签到率信息！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    loadMoreAdapter.setLoadState(loadMoreAdapter.LOADING_COMPLETE);
                    loadMoreAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toast.makeText(getApplicationContext(), "上拉刷新", Toast.LENGTH_SHORT).show();
        people = (People) getIntent().getSerializableExtra("People");
        swipeRefreshLayout0 = findViewById(R.id.swipe_refresh_layout0);
        swipeRefreshLayout0.setColorSchemeColors(Color.parseColor("#4DB6AC"));
        recyclerView0 = findViewById(R.id.recycler_view0);
        loadMoreAdapter = new SearchAdapter(RecordSignRates);
        recyclerView0.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView0.setAdapter(loadMoreAdapter);
        // 设置下拉刷新
        swipeRefreshLayout0.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新数据
                teacherSearch("teacherSearch:");
                loadMoreAdapter.notifyDataSetChanged();
                // 延时0.5s关闭下拉刷新
                swipeRefreshLayout0.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout0 != null && swipeRefreshLayout0.isRefreshing()) {
                            swipeRefreshLayout0.setRefreshing(false);
                        }
                    }
                }, 500);
            }
        });

        // 设置上滑加载
        recyclerView0.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreAdapter.setLoadState(loadMoreAdapter.LOADING);
                loadMoreAdapter.notifyDataSetChanged();
                if (dataList.size() < RecordSignRates.size()) {
                    // 模拟获取网络数据，延时1s
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            loadMore();
                            Message message = new Message();
                            message.what=0;
                            handler.sendMessage(message);
                        }
                    }, 500);
                } else {
                    // 显示加载到底的提示
                    loadMoreAdapter.setLoadState(loadMoreAdapter.LOADING_END);
                    loadMoreAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void teacherSearch(String tag) {
        dataList.clear();
        RecordSignRates.clear();
        Number_Current = 0;
        String url = Constant.urlTeacherSearch + "?tname=" + people.getName();
        Log.d(tag, url);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.cancelAll(tag);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Message message = new Message();
                    if (jsonArray.toString().equals("[null]")) {
                        message.what = 0;
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            RecordSignRate record = new Gson().fromJson(jsonObject.toString(), RecordSignRate.class);
                            RecordSignRates.add(record);
                            if (i < NUMBER_ONCE && i < jsonArray.length()){
                                Number_Current++;
                                dataList.add(record);
                            }
                        }
                        message.what = 1;
                    }
                    handler.sendMessage(message);
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

    private void loadMore(){
        while (Number_Current%NUMBER_ONCE < NUMBER_ONCE && Number_Current<RecordSignRates.size()){
            dataList.add(RecordSignRates.get(Number_Current));
            Number_Current++;
        }
    }
}
