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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidapp.databinding.BulletinBoardBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulletinBoard extends Fragment {

    //Fragment binding for accessing views.
    private BulletinBoardBinding binding;
    //List to contain list of image responses from API
    private List<ImageResponse> _images;
    //BaseURL for API
    private String url = "http://192.168.0.90:5155/api/Image";
    private String _error;
    //Relative layout for images to be added to.
    RelativeLayout layout;
    private int xDelta;
    private int yDelta;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = BulletinBoardBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View realView, Bundle savedInstanceState) {
        super.onViewCreated(realView, savedInstanceState);
        //On click listener to clear relative layout of images on click.
        binding.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.removeAllViews();
            }
        });


        /**
         * OnClickListener to listen for click on GetALl button to call API and get all saved images and display them
         * in relative layout.
         */
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout = realView.findViewById(R.id.layout1);
                _images = new ArrayList<>();
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        addImagesToList(response);
                        addImageViews();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _error = error.toString();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    /**
     * Creates image views in relative layout based on base64 image strings in _images private list.
     */
    public void addImageViews(){
        for (int i=0;i<_images.size();i++){
            ImageView imageView = new ImageView(BulletinBoard.this.getContext());
            imageView.setImageBitmap(GetBitmapImageFromBase64(_images.get(i).getImageBase64()));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(600, 600);
            imageView.setLayoutParams(params);
            imageView.setOnTouchListener(onTouchListener());
            layout.addView(imageView);
        }
    }

    /**
     * Decodes Base64 string into bytes and then decodes byteArray into bitmap.
     * @param base64String base64 string of image
     * @return bitmap created from parsed base64 string.
     */
    public Bitmap GetBitmapImageFromBase64(String base64String){
        byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    /**
     * Takes string response and creates Json object based on it, to extract the Images array from the Json.
     * Loops through the image array to gather all base64 strings for each image, and saves it to list.
     * @param response rest response string.
     */
    public void addImagesToList(String response){
        try {
            JSONObject object=new JSONObject(response);
            JSONArray array=object.getJSONArray("images");
            for(int i=0;i<array.length();i++) {
                JSONObject object1=array.getJSONObject(i);
                String name =object1.getString("imageBase64");
                _images.add(new ImageResponse(name));
            }
        }catch (JSONException e){

        };
    }

    /***
     * OnTouchlistener for used for imageviews to make them dragable in the relative layout.
     * @return
     */
    private View.OnTouchListener onTouchListener(){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int x = (int) motionEvent.getRawX();
                final int y = (int) motionEvent.getRawY();
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                            view.getLayoutParams();
                        xDelta = x - params.leftMargin;
                        yDelta = y - params.topMargin;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);

                }
                return true;
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}