package com.example.jason.myapplication.helpers;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class WebHelper  extends AsyncTask<String, Void, String> {


    public WebHelper(){
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }

    public static void POST(String page, HashMap<String,String> values){

    }

    private static String GET(String page){
        try {
            URL url = new URL("http://www.gorillasquad.ca/chatrandom/"+page);


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        }catch (Exception e){
            Log.d("WebHelper", e.toString());
        }
        return "";
    }

    @Override
    protected String doInBackground(String... strings) {
        if(strings[0].equalsIgnoreCase("POST")){

        }else if(strings[0].equalsIgnoreCase("GET")){
            return GET(strings[1]);
        }
        return null;
    }
}
