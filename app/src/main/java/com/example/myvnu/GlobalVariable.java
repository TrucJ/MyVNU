package com.example.myvnu;

import android.app.Application;

public class GlobalVariable extends Application {
    private double chosenLat = 10.8785166, chosenLng = 106.7959034;

    public double getChosenLat() {
        return chosenLat;
    }

    public double getChosenLng() {
        return chosenLng;
    }

    public void setChosenLat(double chosenLat) {
        this.chosenLat = chosenLat;
    }

    public void setChosenLng(double chosenLng) {
        this.chosenLng = chosenLng;
    }
}
