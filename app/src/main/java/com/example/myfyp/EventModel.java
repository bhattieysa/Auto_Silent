package com.example.myfyp;
// model class for the event management
public class EventModel {

    String name,start,end;

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

    public EventModel(String name, String start, String end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }
}
