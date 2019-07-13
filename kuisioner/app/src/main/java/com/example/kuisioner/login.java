package com.example.kuisioner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kuisioner.server.AppController;
import com.example.kuisioner.server.Config_URL;
import com.example.kuisioner.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class login extends AppCompatActivity {
    @BindView(R.id.username)
    EditText edUser;
    @BindView(R.id.password)
    EditText edPw;
    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    SharedPreferences prefs;
    private SessionManager session;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);

        session     = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()){
            Intent a = new Intent(login.this, isi.class);
            startActivity(a);
            finish();
        }

        getSupportActionBar().hide();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

    }
    @OnClick(R.id.btnLogin)
    void btnLogin(){
        String strUser, strPw;
        strUser = edUser.getText().toString();
        strPw = edPw.getText().toString();

        if (!strUser.isEmpty()&&!strPw.isEmpty()) {
           checkLogin(strUser,strPw);
        } else {
            Toast.makeText(getApplicationContext(),"Username/Password yang anda masukkan salah", Toast.LENGTH_LONG).show();

        }
    }
    @OnClick(R.id.signup)
    void btnsignup(){
        Intent a = new Intent(login.this, register.class);
        startActivity(a);
        finish();
    }
    private void checkLogin(final String email, final String password){

        String credentials = email + ":" + password;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        String tag_string_req = "req_login";

        pDialog.setMessage("Loading.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("msg", "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("success");

                    if (status == true){
                        String msg = jObj.getString("message");
                        JSONArray jsonArray = new JSONArray(msg);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String email        = jsonObject.getString("email");
                        String username  = jsonObject.getString("username");
                        String namalengkap     = jsonObject.getString("namalengkap");
                        storeRegIdinSharedPref(getApplicationContext(), username, email, namalengkap);
                        session.setLogin(true);
                        Intent a = new Intent(login.this, isi.class);
                        startActivity(a);
                        finish();
                    }else {
                        String msg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("msg", "Login Error : " + error.networkResponse.statusCode);
                error.printStackTrace();
                hideDialog();
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", basic);
                return params;
            }
        };
        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void storeRegIdinSharedPref(Context context, String username, String email, String namalengkap) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("namalengkap", namalengkap);
        editor.commit();
    }
}
