package com.example.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidapp.databinding.FragmentFirstBinding;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ImageView imageView;
    private TextView textView;
    private String imageStr;
    private String url = "http://192.168.0.90:5155/api/Image";
    private static final int CAMERA_REQUEST = 1888;
    Button btnCam;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.imageView1);
        textView = view.findViewById(R.id.textView);

        binding.btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageStr = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            imageView.setImageBitmap(photo);
        }

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url + "?imageString=" + imageStr, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
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
                params.put("imageString", imageStr);

                // at last we are
                // returning our params.
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}