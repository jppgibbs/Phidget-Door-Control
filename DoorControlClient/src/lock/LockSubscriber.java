package lock;

import util.Utils;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class LockSubscriber {

    // Connect to MQTT broker
    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    // Set userid to be used by the topic string later to ensure the topic is unique
    public static final String userid = "16014690";
    // Get the Mac Address of the client
    String clientId = Utils.getMacAddress() + "-sub";

    private MqttClient mqttClient;

    public LockSubscriber() {
        try {
            // Create a new MQTT Client using the broker url combined with our mac address
            mqttClient = new MqttClient(BROKER_URL, clientId);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        try {
            mqttClient.setCallback(new LockSubscriberCallback());
            mqttClient.connect();
            // Set MQTT topic as our userid + rfid (for scalability)
            final String topic = userid + "/rfid";
            mqttClient.subscribe(topic);
            System.out.println("Subscriber listening to: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String...args) {
        final LockSubscriber subscriber = new LockSubscriber();
        subscriber.start();
    }
}