package com.example.myfyp;

// model class for the event management


public class AutoSilenceEvent {
    //Unique id for the geofence location.
    private int id;

    public AutoSilenceEvent() {
    }

    //Name and start and ent time
    private String name, start, end;



    //Parameterized Cosntructor for the class
    public AutoSilenceEvent(int id, String name, String start, String end) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
    }


    //getters and setters for the values
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}

