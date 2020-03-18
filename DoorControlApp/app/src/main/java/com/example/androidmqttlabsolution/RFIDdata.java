package com.example.androidmqttlabsolution;

public class RFIDdata {

    String tagId;
    String readerId;
    String doorId;
    String roomId;
    String date;


    public RFIDdata(String tagId, String readerId, String date) {
        super();
        this.tagId = tagId;
        this.readerId = readerId;
        this.date = date;
    }

    public RFIDdata(String doorId, String roomId) {
        super();
        this.doorId = doorId;
        this.roomId = roomId;
    }


    public String gettagId() {
        return tagId;
    }

    public void settagId(String tagId) {
        this.tagId = tagId;
    }

    public String getreaderId() {
        return readerId;
    }

    public void setreaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getdoorId() {
        return doorId;
    }

    public void setdoorId(String doorId) {
        this.doorId = doorId;
    }

    public String getroomId() {
        return roomId;
    }

    public void setroomId(String roomId) {
        this.roomId = roomId;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "SensorData [tagId=" + tagId + ", readerId=" + readerId + ", date=" + date + "]";
    }

}