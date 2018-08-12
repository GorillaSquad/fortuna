package com.example.jason.myapplication.helpers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class SavedInfo {

    class ChatInfo {
        boolean newMessage;
        int startTime;
    }

    public boolean EUConsent;
    public HashMap<String, ChatInfo> chatInfos;

    private static SavedInfo instance = null;
    public static SavedInfo getInstance()
    {
        if (instance == null)
            instance = new SavedInfo();
        return instance;
    }

    public static void save(Context c) {
        String filename = "cacheInfo";
        FileOutputStream outputStream;
        Gson gson = new Gson();
        String fileContents = gson.toJson(getInstance());
        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context c){
        String filename = "cacheInfo";
        Gson gson = new Gson();
        String fileData = "";
        try {
            FileInputStream inputStream = c.openFileInput(filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            fileData = total.toString();
        }catch(Exception e){

        }
        instance = gson.fromJson(fileData, SavedInfo.class);
    }
}
