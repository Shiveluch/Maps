package com.example.maps.classes;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Division {
    public String div_name;
    public String chevron;
    public ArrayList <String> teams=new ArrayList<String>();
    public Division(String div_name)
    {
        this.div_name=div_name;
    }

    @NonNull
    @Override
    public String toString() {
        return div_name;
    }
}
