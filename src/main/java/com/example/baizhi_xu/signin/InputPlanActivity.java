package com.example.baizhi_xu.signin;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baizhi_xu.signin.model.People;
import com.example.baizhi_xu.signin.model.RecordSignRate;
import com.example.baizhi_xu.signin.unit.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class InputPlanActivity extends AppCompatActivity {

    private EditText et_cname,et_coursename,et_time,et_place;
    private Button bt_input;
    private People people;

    public final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
                case 1:
                    Toast.makeText(getApplicationContext(), "导入成功!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), TeacherActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_plan);
        people = (People) getIntent().getSerializableExtra("People");
        et_cname = findViewById(R.id.et_cname);
        et_coursename = findViewById(R.id.et_coursename);
        et_time = findViewById(R.id.et_time);
        et_place = findViewById(R.id.et_place);
        bt_input = findViewById(R.id.bt_input);
        bt_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherInput("teacherInput:");
            }
        });
    }

    public void teacherInput(String tag) {
        String tname = "?tname=" +people.getName();
        String cname = "&cname="+et_cname.getText().toString().trim();
        String coursename = "&coursename="+et_coursename.getText().toString().trim();
        String cnum = "&cnum="+et_time.getText().toString().trim();
        String place = "&place="+et_place.getText().toString().trim();
        String url = Constant.urlTeacherInput +tname+cname+coursename+cnum+place;
        Log.d(tag, url);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.cancelAll(tag);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                Toast.makeText(getApplicationContext(), "网络异常！", Toast.LENGTH_SHORT).show();
                Log.e("TAG", error.getMessage(), error);
            }
        });
        request.setTag(tag);
        requestQueue.add(request);
    }
}
