package com.example.baizhi_xu.signin;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baizhi_xu.signin.Adapter.RecyclerViewAdapter;
import com.example.baizhi_xu.signin.Listener.EndlessRecyclerOnScrollListener;
import com.example.baizhi_xu.signin.model.DetailedRecord;
import com.example.baizhi_xu.signin.model.People;
import com.example.baizhi_xu.signin.unit.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RecordFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter loadMoreAdapter;
    private List<DetailedRecord> dataList = new ArrayList<>();
    private RequestQueue requestQueue;
    private People people;
    private List<DetailedRecord> records = new ArrayList<>();
    // 上拉加载一次显示的数量
    private final int NUMBER_ONCE = 5;
    // 当前显示的数量
    private int Number_Current=0;
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
                case 0:
                    loadMoreAdapter.setLoadState(loadMoreAdapter.LOADING_COMPLETE);
                    loadMoreAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        people = (People) getActivity().getIntent().getSerializableExtra("People");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#4DB6AC"));
        // 模拟获取数据
        loadMoreAdapter = new RecyclerViewAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(loadMoreAdapter);
        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新数据
                getData("getRecord");
                loadMoreAdapter.notifyDataSetChanged();
                // 延时0.5s关闭下拉刷新
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 500);
            }
        });

        // 设置上滑加载
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreAdapter.setLoadState(loadMoreAdapter.LOADING);
                loadMoreAdapter.notifyDataSetChanged();
                if (dataList.size() < records.size()) {
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
        return view;
    }

    private void getData(String tag) {
        dataList.clear();
        records.clear();
        Number_Current = 0;
        String Account = "&account="+people.getAccount();
        String Url = Constant.urlGetRecord+Account;
        Log.d("record",Url);
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.cancelAll(tag);
        StringRequest request = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.toString().equals("[null]")){

                    } else {
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            DetailedRecord record = new Gson().fromJson(jsonObject.toString(), DetailedRecord.class);
                            records.add(record);
                            if (i < NUMBER_ONCE && i < jsonArray.length()){
                                Number_Current++;
                                dataList.add(record);
                            }
                        }
                    }
                    Message message = new Message();
                    message.what=0;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "网络异常！", Toast.LENGTH_SHORT).show();
                Log.e("TAG", error.getMessage(), error);
            }
        });
        request.setTag(tag);
        requestQueue.add(request);
    }

    private void loadMore(){
//        for (;Number_Current%NUMBER_ONCE < NUMBER_ONCE; Number_Current++){
//            if (Number_Current<records.size()) {
//                dataList.add(records.get(Number_Current));
//                Log.d("2",records.get(Number_Current).getBtime());
//            }
//        }
        while (Number_Current%NUMBER_ONCE < NUMBER_ONCE && Number_Current<records.size()){
            dataList.add(records.get(Number_Current));
            Number_Current++;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
