package com.example.baizhi_xu.signin;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baizhi_xu.signin.model.People;
import com.example.baizhi_xu.signin.model.Work;
import com.example.baizhi_xu.signin.unit.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class MineFragment extends Fragment {

    private People people;
    private TextView pname,pclass;
    private Button tuichu,deleteuuidpoint;
    private RequestQueue requestQueue;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
                case 1:
                    Toast.makeText(getActivity(), "注销成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public MineFragment() {
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
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        pname = view.findViewById(R.id.name);
        pclass = view.findViewById(R.id.cname);
        tuichu = view.findViewById(R.id.tuichu);
        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new  AlertDialog.Builder(getContext())
                        .setTitle("Confirm" )
                        .setMessage("是否退出登录？" )
                        .setPositiveButton("是" ,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("否" , null)
                        .show();
            }
        });
        deleteuuidpoint = view.findViewById(R.id.deleteuuidpoint);
        deleteuuidpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new  AlertDialog.Builder(getContext())
                        .setTitle("Confirm" )
                        .setMessage("是否注销uuid？(注销次数有限)" )
                        .setPositiveButton("是" ,  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteUuid("deleteUuid");
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("否" , null)
                        .show();

            }
        });
        setPeople();
        return view;
    }

    private void setPeople(){
        pname.setText(people.getName());
        pclass.setText(people.getCname());
    }

    protected void deleteUuid(String tag){
        String Account = "?account="+people.getAccount();
        String Url = Constant.urlDeleteUuid+Account;
        Log.d("Url:",Url);
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.cancelAll(tag);
        StringRequest request = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "网络异常！", Toast.LENGTH_SHORT).show();
                Log.e("TAG", error.getMessage(), error);
            }
        });
        request.setTag(tag);
        requestQueue.add(request);
    }
        protected void query(String tag) {
            String Url = "http://134.175.28.101/workplane/WorkServlet?action=seletework";
            Log.d("set1", Url);
            requestQueue = Volley.newRequestQueue(getContext());
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
                                Work work = new Gson().fromJson(jsonObject.toString(), Work.class);
                                Log.d("1",work.getPname());
                            }
                        }
                        //                    Log.d("listener", "onClick: "+task.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "网络异常！", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", error.getMessage(), error);
                }
            });
            request.setTag(tag);
            requestQueue.add(request);
        }

}

