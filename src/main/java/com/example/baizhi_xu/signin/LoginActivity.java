package com.example.baizhi_xu.signin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
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
import com.example.baizhi_xu.signin.unit.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private EditText et_account;
    private EditText et_password;
    private Button bt_sign;
    private String account;
    private String password;
    private String uuid;
    private RequestQueue requestQueue;
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
                case 0:
                    showToast("账号密码错误！");
                    break;
                case 1:
                    showToast("登录成功！");
                    break;
                case 2:
                    showToast("请使用初始手机！");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password);
        bt_sign = findViewById(R.id.bt_sign);
        bt_sign.setOnClickListener(new Bt_signClick());
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);// 获取当前手机管理器
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            uuid = "";
        } else {
            String imei = telephonyManager.getDeviceId();
            uuid = imei;
        }
    }

    class  Bt_signClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            account = et_account.getText().toString().trim();
            password = et_password.getText().toString().trim();
            login("login");
        }
    }

    protected void login(String tag){
        String Account = "?account="+account;
        String Password = "&psw="+password;
        String Uuid = "&uuid="+uuid;
        String Url = Constant.urlLogin+Account+Password+Uuid;
        Log.d("set1",Url);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.cancelAll(tag);
        StringRequest request = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Integer result = jsonArray.getInt(0);
                    Message message = new Message();
                    if (result==0){
                        message.what = 0;
                    } else if (result==1){
                        JSONObject jsonObject = jsonArray.getJSONObject(1);
                        People people = new Gson().fromJson(jsonObject.toString(), People.class);
                        loginSuccess(people);
                        message.what = 1;
                    } else if (result==2){
                        message.what = 2;
                    }
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("网络异常！");
                Log.e("TAG", error.getMessage(), error);
            }
        });
        request.setTag(tag);
        requestQueue.add(request);
    }

    public static void openActivity(Context context) {
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    public void loginSuccess(People people) {
        Intent intent;
        if (people.getRole()==0){
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, TeacherActivity.class);
        }
        intent.putExtra("People", people);
        startActivity(intent);
    }

    void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
