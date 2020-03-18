package reader;

import com.google.gson.Gson;
import com.phidget22.*;
import reader.sendToServer;
import publisher.PhidgetPublisher;
import reader.RFIDdata;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.paho.client.mqttv3.MqttException;

public class CardReader {
	// Create new card reader object with empty data
    RFIDdata oneSensor = new RFIDdata("unknown", "unknown", "unknown", false);
    Gson gson = new Gson();
    String oneSensorJson = new String();

    public static void main(String[] args) throws PhidgetException {
        new CardReader();
    }

    public CardReader() throws PhidgetException {
        RFID rfid = new RFID();
        rfid.addTagListener(new RFIDTagListener() {
            public void onTag(RFIDTagEvent e) {

                // Get Date/Time
                LocalDateTime now = LocalDateTime.now();
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                // Print the contents of the tag
                System.out.println("Tag Read: " + e.getTag());

                // Store data on the tag in a string
                String tag = e.getTag();

                // Update values in the object before publishing
                oneSensor.settagId(tag);
                // Get the serial number of the RFID reader
                try {
                    oneSensor.setreaderId(String.valueOf(e.getSource().getDeviceSerialNumber()));
                } catch (PhidgetException e2) {
                    e2.printStackTrace();
                }

                oneSensor.setdate(dtf.format(now)); // Get current date/time
                
                // Convert to JSON
                oneSensorJson = gson.toJson(oneSensor);
                // topic String is the result value of serverside check of tag validity
                String topic = sendToServer.SendToServer(oneSensorJson);

                // Check validity before publishing to MQTT
                if (topic.equals("true")) {
                    System.out.println("Tag validity is: " + topic);
                    PhidgetPublisher publisher = new PhidgetPublisher();

                    // Publish the data using the publisher class
                    try {
                        publisher.publishRfid(oneSensorJson);
                    } catch (MqttException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        // What to do when the tag is lost
        rfid.addTagLostListener(new RFIDTagLostListener() {
            public void onTagLost(RFIDTagLostEvent e) {
                System.out.println("Tag lost: " + e.getTag());
            }
        });

        // Establish connection to RFID Reader
        rfid.open(30000);
        System.out.println("\nReader is now recieving data\n");

        // Wait for a tag to be scanned
        util.Utils.waitFor(60);

        // Close RFID Connection
        rfid.close();
        System.out.println("\nReader is no longer receiving data");
    }
}