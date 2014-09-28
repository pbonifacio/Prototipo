package com.app.plan2see.web.table;

import com.app.plan2see.model.db.Client;
import java.util.ArrayList;
import java.util.List;

public class EventTableList {

    private List<EventTableItem> listEvents;

    public EventTableList() {
        listEvents = new ArrayList<EventTableItem>();
    }

    public EventTableItem getEventById(int id) {
        for( int i = 0; i < listEvents.size(); i++ ){
            if( listEvents.get(i).getId() == id )
                return listEvents.get(i);
        }
        return null;
    }
    
    public EventTableItem getEventByIdx(int idx) {
        return listEvents.get(idx);
    }
    
    public EventTableItem searchEventById(int id) {
        EventTableItem evt = null;
        for( EventTableItem e : listEvents ){
            if( e.getId() == id)
                evt = e;
        }
        return evt;
    }

    public List<Integer> getEventIds() {
        List<Integer> listIds = new ArrayList<Integer>();

        for (EventTableItem evt : listEvents) {
            listIds.add(evt.getId());
        }

        return listIds;
    }

    public List<EventTableItem> getAllEvents() {
        return listEvents;
    }

    public int countAllEvents() {
        return listEvents.size();
    }

    public List<EventTableItem> getEventsFromRange(int indexStart, int indexEnd) {
        return listEvents.subList(indexStart, indexEnd);
    }

    public void addEventTableItem(EventTableItem item) {
        listEvents.add(item);
    }

    public EventTableList getEventTableItems(String expression, Client client) {
        int idx = 0;
        expression = expression.toUpperCase();
        String[] expressions = expression.trim().split("&");
        EventTableList eventItems = new EventTableList();
        while (idx < countAllEvents()) {
            EventTableItem evtItem = getEventByIdx(idx++);
            String title = evtItem.getTitle() == null ? "" : evtItem.getTitle().toUpperCase();
            String description = evtItem.getDescription() == null ? "" : evtItem.getDescription().toUpperCase();
            int idxExpression = 0;
            boolean hasAny = false;
            while (idxExpression < expressions.length) {
                if (title.contains(expressions[idxExpression])
                        || description.contains(expressions[idxExpression])) {
                    hasAny = true;
                }
                idxExpression++;
            }
            if (!hasAny) {
                continue;
            }
            eventItems.addEventTableItem(evtItem);
        }
        return eventItems;
    }
}
