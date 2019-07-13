package com.example.kuisioner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kuisioner.Adapter.AdapterPertanyaan;
import com.example.kuisioner.data.DataPertanyaan;
import com.example.kuisioner.server.AppController;
import com.example.kuisioner.server.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class isi extends AppCompatActivity {
    ListView list;

    ArrayList<DataPertanyaan> newsList = new ArrayList<DataPertanyaan>();
    AdapterPertanyaan adapter;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        list = (ListView) findViewById(R.id.array_list);
        newsList.clear();

        adapter = new AdapterPertanyaan(isi.this, newsList);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.textNodata));

        getDataBuku();
    }
    // Fungsi get JSON Mahasiswa
    private void getDataBuku() {

        pDialog.setMessage("Loading.....");
        showDialog();

        String tag_json_obj = "json_obj_req";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Config_URL.pertanyaan,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("Response: ", response.toString());
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("success");

                            if(status == true){

                                String getObject = jObj.getString("message");
                                JSONArray jsonArray = new JSONArray(getObject);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    final JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    final DataPertanyaan news = new DataPertanyaan();
                                    news.setKodepertanyaan(jsonObject.getString("kodepertanyaan"));
                                    news.setPertanyaan(jsonObject.getString("pertanyaan"));
                                    //Log.v("Ini nomor", String.valueOf(i));
                                    news.setNomor(String.valueOf(i+1));
                                    newsList.add(news);
                                }
                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(String.valueOf(getApplication()), "Error : " + error.getMessage());
                error.printStackTrace();
                hideDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
