package lock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.paho.client.mqttv3.*;
import util.Utils;

public class LockSubscriberCallback implements MqttCallback {
	public String readerID = "";
	public static String doorID = "E127";
	//Server location to pull reader / door ID from
    public static String sensorServerURL = "http://localhost:8080/DoorControlServer/DoorChecker";

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("DEBUG: Connection to motor lost");
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
    	if(readerID == "") {
    		readerID = getReader("E127");
    		System.out.println("readerID");
    	}
        System.out.println("Message arrived. Topic: " + topic + "  Message: " + message.toString());
        System.out.println(message.toString());
        // Move motor to 180 degrees
        if(message.toString().contains(readerID)) {
            lock.LockMover.moveServoTo(180.0);
            System.out.println("Waiting until motor at position 180");
            Utils.waitFor(5);

            // Reset motor back to 0 to close door
            lock.LockMover.moveServoTo(0.0);
            Utils.waitFor(2);
        }

    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
    
    public static String getReader(String doorJson) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;

        // Use URLEncoder to remove any invalid characters from the JSON string before using it
        try {
            doorJson = URLEncoder.encode(doorJson, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // Append the url with ?RFIDdata and the JSON string
        String fullURL = sensorServerURL + "?doorID=" + doorJson;
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