package org.example.letstry.model;

import java.util.List;

public class GraphEventsResponse {
    private List<Event> value;

    public List<Event> getValue() {
        return value;
    }

    public void setValue(List<Event> value) {
        this.value = value;
    }

    public static class Event {
        private String subject;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }
}
