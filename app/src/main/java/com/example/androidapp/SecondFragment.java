package com.example.androidapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidapp.databinding.FragmentSecondBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private List<ImageResponse> _images;
    private String url = "http://192.168.0.90:5155/api/Image";
    private String _error;
    LinearLayout layout;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View realView, Bundle savedInstanceState) {
        super.onViewCreated(realView, savedInstanceState);
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tview = realView.findViewById(R.id.textView2);
                layout = realView.findViewById(R.id.layout1);
                _images = new ArrayList<>();
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            JSONArray array=object.getJSONArray("images");
                            for(int i=0;i<array.length();i++) {
                                JSONObject object1=array.getJSONObject(i);
                                String name =object1.getString("imageBase64");
                                _images.add(new ImageResponse(name));
                            }
                            for (int i=0;i<_images.size();i++){
                                ImageView imageView = new ImageView(SecondFragment.this.getContext());
                                byte[] imageBytes = Base64.decode(_images.get(i).getImageBase64(), Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                imageView.setImageBitmap(decodedImage);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600,600);
                                params.setMargins(0,10,0,10);
                                imageView.setLayoutParams(params);
                                layout.addView(imageView);
                            }

                        } catch (JSONException e) {
                            tview.setText(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _error = error.toString();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // below line we are creating a map for
                        // storing our values in key and value pair.
                        Map<String, String> params = new HashMap<String, String>();

                        // on below line we are passing our key
                        // and value pair to our parameters.
                        params.put("Content-Type", "application/json");

                        // at last we are
                        // returning our params.
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}