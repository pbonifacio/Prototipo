package com.app.plan2see.web.table;

import com.app.plan2see.db.controller.EventsController;
import com.app.plan2see.model.db.Client;
import com.app.plan2see.model.db.Event;
import com.app.plan2see.web.servlet.P2SWindow;
import java.util.List;

public class TableController{
    EventsController events = null;
    Client client;
    P2SWindow UI;
    
    public TableController(EventsController list, P2SWindow UI){
        this.events = list;
        this.client = UI.getClient();
        this.UI = UI;
    }
    
    public EventTableList getEventTableItems(){
        EventTableList eventItems = new EventTableList();
        List<Event> allDBEvents = events.getAllEntities();
       for( Event evt : allDBEvents ){
            EventTableItem event = new EventTableItem((evt.getEventID()), 
                    evt.getCluster(), evt.getEventTitle(), evt.getEventDescription(), 
                    client, UI);
            eventItems.addEventTableItem(event);
        }
        return eventItems;
    }
}
