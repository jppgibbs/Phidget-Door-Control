package com.example.androidmqttlabsolution;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.androidmqttdemo.R;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import java.text.SimpleDateFormat;
import java.util.Date;
public class MainActivity extends AppCompatActivity {

    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";

    String userid = "16014690";
    String IMEI = "861536030196001"; // Hardcoded IMEI for testing purposes - Change this to see validation works

    //We have to generate a unique Client id.
    String clientId = userid + "-sub";
    String topicname = userid;

    String JSONdataHolder = "";

    private MqttClient mqttClient;

    Button lockPublishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lockPublishButton = (Button) findViewById(R.id.lockPubButton);
        lockPublishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                System.out.println("Attempting to move lock");
                runOnUiThread(new Runnable() {
                    public void run() {
                        publishMotor();
                    }
                });
            }
        });

        // Create MQTT client and start subscribing to message queue
        try {
            mqttClient = new MqttClient(BROKER_URL, clientId, null);
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("DEBUG: Message arrived. Topic: " + topic + "  Message: " + message.toString());
                    // get message data
                    runOnUiThread(new Runnable() {
                        public void run() {
                            System.out.println("Updating UI");
                        }
                    });
                    if ((topicname + "/LWT").equals(topic)) {
                        System.err.println("Sensor gone!");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //no-op
                }

                @Override
                public void connectComplete(boolean b, String s) {
                    //no-op
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }

        startSubscribing();
    }


    private void startSubscribing() {
        try {
            mqttClient.connect();

            //Subscribe to all subtopics of home
            final String topic = topicname;
            mqttClient.subscribe(topic);

            System.out.println("Subscriber is now listening to " + topic);

        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void publishMotor() {
        AsyncTaskRunner sendData = new AsyncTaskRunner();
        String TOPIC_RFID = userid + "/rfid";
        System.out.println(TOPIC_RFID);
        final MqttTopic rfidTopic = mqttClient.getTopic(TOPIC_RFID);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        RFIDdata temp = new RFIDdata(IMEI, "383274", formatter.format(new Date()));
        Gson gson = new Gson();
        String oneSensorJson = gson.toJson(temp);
        sendData.execute(oneSensorJson);
    }

    private class AsyncTaskRunner extends AsyncTask < String, String, String > {

        private String resp;

        @Override
        protected String doInBackground(String...params) {
            try {
                JSONdataHolder = params[0];
                resp = sendToServer.SendToServer(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            System.out.println("ASYNC POSTEXECUTE RESULT = " + result);
            String TOPIC_RFID = userid + "/rfid";
            System.out.println(TOPIC_RFID);
            final MqttTopic rfidTopic = mqttClient.getTopic(TOPIC_RFID);

            if (result.equals("true")) {
                System.out.println("Tag validity is: " + result);

                // Publish the data using the publisher class
                try {
                    rfidTopic.publish(new MqttMessage(JSONdataHolder.getBytes()));
                    System.out.println("Published data. Topic: " + TOPIC_RFID + "  Message: " + JSONdataHolder);
                } catch (MqttException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}