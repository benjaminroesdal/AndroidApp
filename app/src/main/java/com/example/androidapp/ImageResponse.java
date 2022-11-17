package com.example.androidapp;

public class ImageResponse {
    private String ImageBase64;

    public ImageResponse(String image64){
        this.ImageBase64 = image64;
    }

    public String getImageBase64() {
        return ImageBase64;
    }
}
