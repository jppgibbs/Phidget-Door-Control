package com.example.androidmqttlabsolution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class sendToServer {

    public static String sensorServerURL = "http://10.0.2.2:8080/DoorControlServer/LockerServer";

    public static String SendToServer(String oneSensorJson) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;

        // Use URLEncoder to remove any invalid characters from the JSON string before using it
        try {
            oneSensorJson = URLEncoder.encode(oneSensorJson, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // Append the url with ?RFIDdata and the JSON string
        String fullURL = sensorServerURL + "?RFIDdata=" + oneSensorJson;
        System.out.println("Sending data to: " + fullURL);

        String line;
        String result = "";

        // Open connection to the server using the URL we constructed
        try {
            url = new URL(fullURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = rd.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
