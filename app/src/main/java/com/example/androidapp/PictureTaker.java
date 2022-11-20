package com.example.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidapp.databinding.PictureTakerBinding;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class PictureTaker extends Fragment {

    //Binding to access views
    private PictureTakerBinding binding;
    //Image view to see picture taken.
    private ImageView imageView;
    //text view for showing API response text.
    private TextView textView;
    //Imagestring to post to the API.
    private String imageStr;
    //BaseURL for API.
    private String url = "http://192.168.0.90:5155/api/Image";
    private static final int CAMERA_REQUEST = 1888;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = PictureTakerBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Image view to show taken image
        imageView = view.findViewById(R.id.imageView1);
        //textview to show API text.
        textView = view.findViewById(R.id.textView);

        //Button listener to activate camera on click.
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
                NavHostFragment.findNavController(PictureTaker.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    /**
     * When camera activity ends (result)
     * @param requestCode to make sure it's camera_request
     * @param resultCode whether or not result is success.
     * @param data data to extract image data from.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Checking if requestcode is camera request and result is ok, then gets image data and sets image on view.
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            //Converts Bitmap to base64 string to prepare for API call to save image.
            ConvertBitmapToBase64(photo);
        }

        //Initializing POST call to API with base64 image string.
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url + "?imageString=" + imageStr, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Sets text to api response text.
                textView.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Converts bitmap to base64 string, and compresses image to JPEG and reduces quality.
     * @param photo bitmap image from camera data.
     */
    public void ConvertBitmapToBase64(Bitmap photo){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageStr = Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}