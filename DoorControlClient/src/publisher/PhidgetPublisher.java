package publisher;

import org.eclipse.paho.client.mqttv3.*;

public class PhidgetPublisher {

    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    public static final String userid = "16014690";
    public static final String TOPIC_GENERIC = userid;
    public static final String TOPIC_RFID = userid + "/rfid";

    private MqttClient client;

    public PhidgetPublisher() {
        try {
            client = new MqttClient(BROKER_URL, userid);
            // Create new MQTT Session
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setWill(client.getTopic(userid + "/LWT"), "I'm gone :(".getBytes(), 0, false);
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Publish to RFID Topic
    public void publishRfid(String rfidTag) throws MqttException {
        final MqttTopic rfidTopic = client.getTopic(TOPIC_RFID);
        final String rfid = rfidTag + "";
        rfidTopic.publish(new MqttMessage(rfid.getBytes()));
        System.out.println("Published data. Topic: " + rfidTopic.getName() + "   Message: " + rfid);
    }

    // Polymorphic publishing methods - seperate methods depending on the datatype of sensorValue
    public void publishRfid(int rfidTag) throws MqttException {
        // Convert int to string
        publishRfid(String.valueOf(rfidTag));
    }
    public void publishSensor(float rfidTag) throws MqttException {
        // Convert float to String
        publishRfid(String.valueOf(rfidTag));
    }
}