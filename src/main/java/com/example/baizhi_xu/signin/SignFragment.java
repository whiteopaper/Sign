package com.example.baizhi_xu.signin;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baizhi_xu.signin.Listener.GPSHelperListener;
import com.example.baizhi_xu.signin.model.DetailedRecord;
import com.example.baizhi_xu.signin.model.People;
import com.example.baizhi_xu.signin.model.Plan;
import com.example.baizhi_xu.signin.model.Record;
import com.example.baizhi_xu.signin.unit.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class SignFragment extends Fragment {
    private TextView tv_clock;
    private TextView tv_SignCourse;
    private TextView tv_SignTname;
    private TextView tv_SignCname;
    private TextView tv_SignPlace;
    private People people;
    private Plan plan;
    private Record record;
    private RequestQueue requestQueue;
    private Handler mHandler;
    public final Handler sHandler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
                case 0:
                    Toast.makeText(getActivity(), "签到时间已过！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Message message0 = new Message();
                    message0.what=3;
                    mHandler.sendMessage(message0);
                    break;
                case 2:
                    Toast.makeText(getActivity(), "不在上课地点！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private static final String ARG_PARAM1 = "Plan";
    private static final String ARG_PARAM2 = "Record";
    private static final String tag="未签";

    public static SignFragment newInstance(Plan plan,Record record) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1,plan);
        args.putSerializable(ARG_PARAM2,record);
        SignFragment fragment = new SignFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        people = (People) getActivity().getIntent().getSerializableExtra("People");
        if (getArguments() != null) {
            plan = (Plan) getArguments().getSerializable(ARG_PARAM1);
            record = (Record) getArguments().getSerializable(ARG_PARAM2);
        } else {
            plan = null;
            record = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign, container, false);
        tv_clock = view.findViewById(R.id.tv_clock);
        tv_SignCourse = view.findViewById(R.id.tv_SignCourse);
        tv_SignTname = view.findViewById(R.id.tv_SignTname);
        tv_SignCname = view.findViewById(R.id.tv_SignCname);
        tv_SignPlace = view.findViewById(R.id.tv_SignPlace);
        if (plan != null && record != null) {
            load();
            tv_clock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkIn("check");
                }
            });
        } else {
            tv_clock.setText(tag);
            tv_clock.setTextColor(Color.parseColor("#DC143C"));
            tv_clock.setClickable(false);
            tv_clock.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity){
            mHandler =  ((MainActivity)context).handler;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void load() {
        tv_SignCourse.setText(plan.getCoursename());
        tv_SignTname.setText(plan.getTname());
        tv_SignPlace.setText(plan.getGpsplace());
        tv_SignCname.setText(plan.getCname());
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int time = 0;
        if (hour == 8) {
            time = (19 - minute) * 60 * 1000 + (60 - second) * 1000;
        } else if (hour == 10) {
            time = (14 - minute) * 60 * 1000 + (60 - second) * 1000;
        } else if (hour == 13) {
            time = (59 - minute) * 60 * 1000 + (60 - second) * 1000;
        } else if (hour == 15) {
            time = (54 - minute) * 60 * 1000 + (60 - second) * 1000;
        } else if (hour == 18) {
            time = (29 - minute) * 60 * 1000 + (60 - second) * 1000;
        }
        new CountDownTimer(time, 1000) {
            @Override
            public void onFinish() {
                if (tv_clock != null) {
                    tv_clock.setText("未签");
                    tv_clock.setTextColor(Color.parseColor("#DC143C"));
                    tv_clock.setClickable(false);
                    tv_clock.setEnabled(false);
                }

                cancel();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_clock != null) {
                    if (millisUntilFinished > 60*1000) {
                        tv_clock.setText("0" + millisUntilFinished / (1000 * 60) + ":" + (millisUntilFinished / 1000) % 60);
                    } else {
                        tv_clock.setText(String.valueOf((millisUntilFinished / 1000) % 60));
                    }
                    tv_clock.setTextColor(Color.parseColor("#00FF7F"));
                    tv_clock.setClickable(true);
                    tv_clock.setEnabled(true);
                }
            }
        }.start();
    }

    private void checkIn(String tag) {
        GPSHelperListener gpshelper = new GPSHelperListener(getActivity());
        Location l = gpshelper.getLocation();
        if (l != null&&plan != null && record != null) {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            String rid = "?rid="+record.getRid();
            String longitude = "&longitude="+lon;
            String latitude = "&latitude="+lat;
            String Url = Constant.urlCheckin+rid+longitude+latitude;
            Log.d(tag,Url);
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.cancelAll(tag);
            StringRequest request = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Message message = new Message();
                        JSONArray jsonArray = new JSONArray(response);
                        message.what = jsonArray.getInt(0);
                        sHandler.sendMessage(message);
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
}
