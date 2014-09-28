package com.app.plan2see.web.table;

import com.app.plan2see.web.servlet.P2SWindow;
import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import java.util.Calendar;

public class P2SCheckBox extends CheckBox {

    P2SWindow UI;
    boolean selection = false;
    int id;
    
    public P2SCheckBox(int id, P2SWindow ui) {
        super();
        this.id = id;
        this.UI = ui;
        if (UI.getClient().hasChoosedEvent(id)) {
            selection = true;
            super.setValue(selection);
            
        }
        super.setData(id);
        super.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent e) {
                if( !UI.hasTimingError() ) {
                    UI.setSelectionDate(Calendar.getInstance().getTimeInMillis());
                    selection = !selection;
                    int idEvent = Integer.parseInt(getData().toString());
                    com.app.plan2see.model.db.Event event = UI.getControllers().getEvents().getEntityById(idEvent);
                    if (selection) {
                        UI.getClient().getChoosedEvents().add(event);
                        if (!event.getClients().contains(UI.getClient())) {
                            event.getClients().add(UI.getClient());
                        }
                    } else {
                        UI.getClient().removeEvent(idEvent);
                        if (UI.getClient().getChoosedEvents().isEmpty()) {
                            event.getClients().remove(UI.getClient());
                        }
                    }
                    UI.getControllers().commitChoosedEvents(UI.getClient());
                }
                else{
                    Notification.show("Wait 2 seconds",
                            Notification.Type.WARNING_MESSAGE);
                    setCheckBoxValue();
                }
            }
        });
    }
    
    public int getEventId() {
        return id;
    }
    
    public void setCheckBoxValue(){
        super.setValue(selection);
    }
}
