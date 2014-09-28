package com.app.plan2see.web.areas;

import com.app.plan2see.db.controller.EventsController;
import com.app.plan2see.model.db.Client;
import com.app.plan2see.model.db.ClusteringHistory;
import com.app.plan2see.model.db.Event;
import com.app.plan2see.model.db.TTestTerm;
import com.app.plan2see.web.controllers.AppDBControllers;
import com.app.plan2see.web.servlet.P2SWindow;
import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

public class ClustersVisualizer {

    Logger log = Logger.getLogger(ClustersVisualizer.class);
    AppDBControllers dbControllers;
    P2SWindow UI;
    HorizontalLayout layout = new HorizontalLayout();
    VerticalLayout vlayout = new VerticalLayout();
    List<ClusteringHistory> historyList = new ArrayList<ClusteringHistory>();
    List<Integer> clusters = new ArrayList<Integer>();
    List<Client> clients = new ArrayList<Client>();
    List<Integer> uniqueClusters;
    Label labelIteration;
    int dynamicalIteration;
    String sIteration;

    public ClustersVisualizer(int iteration, P2SWindow UI) {
        this.UI = UI;
        this.dynamicalIteration = iteration;
        sIteration = "<center><h3>Iteration <span style='color:blue;'>"
                + dynamicalIteration + "</span></h3></center>";
    }

    public HorizontalLayout getContainer() {
        this.dbControllers = UI.getControllers();
        layout.setStyleName("blue");
        layout.setWidth("100%");
        layout.setHeight("100%");
        return layout;
    }

    public void addVisualizer() {
        vlayout.setSpacing(true);
        this.historyList = getModifiedClusters();
        HorizontalLayout hl2 = getTextAreas();
        HorizontalLayout hl1 = getControlArea();
        vlayout.addComponent(hl1);
        vlayout.addComponent(hl2);
        vlayout.setStyleName("blue");
        vlayout.setMargin(new MarginInfo(true, true, true, true));
        layout.addComponent(vlayout);
        layout.setComponentAlignment(vlayout, Alignment.TOP_CENTER);
    }

    public HorizontalLayout getControlArea() {
        //CLUSTERING ITERATION AND CLUSTERING ACTION (GROUP or DIVIDE)
        HorizontalLayout hl1 = new HorizontalLayout();
        hl1.setWidth("100%");
        hl1.setHeight("100px");
        String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
        FileResource resourcePrevious = new FileResource(new File(basepath + "/resources/images/previous.png"));
        Button previousButton = new Button();
        previousButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                if (!dbControllers.getClustering().existsEntity(dynamicalIteration - 1)) {
                    Notification.show("Does not exists previous iteration",
                            Notification.Type.ERROR_MESSAGE);
                    return;
                }
                dynamicalIteration -= 1;
                UI.changeClustersVisualizer(dynamicalIteration);
            }
        });
        previousButton.setIcon(resourcePrevious);
        hl1.addComponent(previousButton);
        hl1.setComponentAlignment(previousButton, Alignment.MIDDLE_LEFT);

        labelIteration = new Label(sIteration, ContentMode.HTML);
        hl1.addComponent(labelIteration);
        hl1.setComponentAlignment(labelIteration, Alignment.MIDDLE_LEFT);

        FileResource resourceNext = new FileResource(new File(basepath + "/resources/images/next.png"));
        Button nextButton = new Button();
        nextButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                if (!dbControllers.getClustering().existsEntity(dynamicalIteration + 1)) {
                    Notification.show("Does not exists next iteration",
                            Notification.Type.ERROR_MESSAGE);
                    return;
                }
                dynamicalIteration += 1;
                UI.changeClustersVisualizer(dynamicalIteration);
            }
        });
        nextButton.setIcon(resourceNext);
        hl1.addComponent(nextButton);
        hl1.setComponentAlignment(nextButton, Alignment.MIDDLE_LEFT);

        String action = historyList.get(0).getAction();
        Label lAction = new Label("<center><h3>Iteration Action "
                + "<span style='color:blue;'>" + action.toUpperCase() + "</span>"
                + "</h3></center>", ContentMode.HTML);
        hl1.addComponent(lAction);
        hl1.setComponentAlignment(lAction, Alignment.MIDDLE_RIGHT);
        return hl1;
    }

    public HorizontalLayout getTextAreas() {
        //CLUSTERING EVENTS
        for (int i = 0; i < dbControllers.getClients().getEntitiesCount(); i++) {
            clients.add(dbControllers.getClients().getEntityById(i + 1));
        }
        List<TTestTerm> termsListOne = getRelevantTermsOne();
        List<TTestTerm> termsListTwo = getRelevantTermsTwo();
        HorizontalLayout hl2 = new HorizontalLayout();
        hl2.setWidth("100%");
        hl2.setSpacing(true);
        hl2.setMargin(new MarginInfo(true, true, true, true));
        VerticalLayout vl1 = new VerticalLayout();
        PagedTable eventsArea = new PagedTable("HANDLED EVENTS");
        eventsArea.addContainerProperty("Event ID", Integer.class, null);
        eventsArea.addContainerProperty("Event Title", String.class, null);
        eventsArea.addContainerProperty("Event Description", String.class, null);
        //log.trace(historyList.size());
        uniqueClusters = new ArrayList<Integer>();
        int idx = 0;
        EventsController events = dbControllers.getEvents();
        for (ClusteringHistory history : historyList) {
            int idEvent = history.getIdevent();
            Event e = events.getEntityById(idEvent);
            Item evtItem = eventsArea.addItem(idx++);
            evtItem.getItemProperty("Event ID").setValue(
                    e.getEventID());
            evtItem.getItemProperty("Event Title").setValue(
                    e.getEventTitle());
            evtItem.getItemProperty("Event Description").setValue(
                    e.getEventDescription());

            if (!uniqueClusters.contains(history.getCluster())) {
                uniqueClusters.add(history.getCluster());
            }
            clusters.add(history.getCluster());
        }
        eventsArea.setColumnWidth("Event ID", 30);
        eventsArea.setColumnWidth("Event Title", 250);
        eventsArea.setColumnWidth("Event Description", 300);
        vl1.setWidth("60%");
        eventsArea.setCellStyleGenerator(new Table.CellStyleGenerator() {
            public String getStyle(Table table, Object itemId, Object propertyId) {
                int row = ((Integer) itemId).intValue();
                if (row == 0) {
                    return "";
                }
                for (Client c : clients) {
                    int event = Integer.parseInt(table.getItem(itemId)
                            .getItemProperty("Event ID").getValue().toString());
                    if (c.hasChoosedEvent(event)) {
                        return "green";
                    }
                }
                return "";
            }
        });

        Collections.sort(uniqueClusters, Collections.reverseOrder());
        vl1.addComponent(eventsArea);
        vl1.addComponent(eventsArea.createControls());
        hl2.addComponent(vl1);

        //CLUSTERING TERMS
        VerticalLayout vl2 = new VerticalLayout();
        Table termsArea = new Table("T-TEST TERMS");
        termsArea.addContainerProperty("Term", String.class, null);
        termsArea.addContainerProperty("Mean One", Double.class, null);
        termsArea.addContainerProperty("Mean Two", Double.class, null);

        idx = 0;
        for (TTestTerm termOne : termsListOne) {
            TTestTerm termTwo = null;
            for (TTestTerm t : termsListTwo) {
                if (t.getRelevant_term().equals(termOne.getRelevant_term())) {
                    termTwo = t;
                    break;
                }
            }
            termsArea.addItem(new Object[]{
                termOne.getRelevant_term(),
                termOne.getMean(),
                termTwo.getMean()
            },
                    idx);
            idx++;
        }

        termsArea.setColumnWidth("Term", 80);
        termsArea.setColumnWidth("Mean One", 35);
        termsArea.setColumnWidth("Mean Two", 35);
        termsArea.setSortEnabled(true);
        termsArea.setSortContainerPropertyId("Term");
        termsArea.setSortAscending(false);
        vl2.setWidth("20%");

        vl2.addComponent(termsArea);
        hl2.addComponent(vl2);

        //MODIFIED CLUSTERS
        VerticalLayout vl3 = new VerticalLayout();
        Table clustersArea = new Table("MODIFIED CLUSTERS");
        clustersArea.setPageLength(25);
        clustersArea.addContainerProperty("Cluster ID", Integer.class, null);
        clustersArea.addContainerProperty("Cluster Size", Integer.class, null);
        clustersArea.addContainerProperty("Relevant Terms", String.class, null);

        HashMap<Integer, Integer> addedClusters = new HashMap<Integer, Integer>();
        for (ClusteringHistory history : historyList) {
            int c = history.getCluster();
            if (!addedClusters.containsKey(c)) {
                addedClusters.put(c, 1);
            } else {
                int s = addedClusters.get(c);
                addedClusters.remove(c);
                addedClusters.put(c, ++s);
            }
        }

        Iterator<Integer> it = addedClusters.keySet().iterator();
        while (it.hasNext()) {
            int cluster = it.next();

            String relevantTerms = "";
            for (ClusteringHistory h : historyList) {
                if (h.getCluster() == cluster) {
                    relevantTerms = h.getRelevant_terms();
                }
            }

            clustersArea.addItem(new Object[]{
                cluster,
                addedClusters.get(cluster),
                relevantTerms
            },
                    cluster);
        }

        clustersArea.setColumnWidth(
                "Cluster ID", 30);
        clustersArea.setColumnWidth(
                "Cluster Size", 80);
        clustersArea.setColumnWidth(
                "Relevant Terms", 160);

        clustersArea.setSelectable(
                true);
        clustersArea.setImmediate(
                true);
        clustersArea.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            public void itemClick(ItemClickEvent event) {
                int id = Integer.parseInt(event.getItem().
                        getItemProperty("Cluster ID").getValue().toString());
                Window subWindow = new Window("Cluster " + id);
                Table clusterEvents = new Table();
                clusterEvents.addContainerProperty("Event ID", Integer.class, null);
                clusterEvents.addContainerProperty("Event Title", String.class, null);
                clusterEvents.addContainerProperty("Event Description", String.class, null);
                int idx = 0;
                EventsController events = dbControllers.getEvents();
                for (ClusteringHistory history : historyList) {
                    int idEvent = history.getIdevent();
                    Event e = events.getEntityById(idEvent);
                    if (history.getCluster() == id) {
                        Item evtItem = clusterEvents.addItem(idx++);
                        evtItem.getItemProperty("Event ID").setValue(
                                e.getEventID());
                        evtItem.getItemProperty("Event Title").setValue(
                                e.getEventTitle());
                        evtItem.getItemProperty("Event Description").setValue(
                                e.getEventDescription());
                    }
                }
                clusterEvents.setColumnWidth("Event ID", 50);
                clusterEvents.setColumnWidth("Event Title", 250);
                clusterEvents.setColumnWidth("Event Description", 300);
                clusterEvents.setCellStyleGenerator(new Table.CellStyleGenerator() {
                    public String getStyle(Table table, Object itemId, Object propertyId) {
                        int row = ((Integer) itemId).intValue();
                        if (row == 0) {
                            return "";
                        }
                        for (Client c : clients) {
                            int event = Integer.parseInt(table.getItem(itemId)
                                    .getItemProperty("Event ID").getValue().toString());
                            if (c.hasChoosedEvent(event)) {
                                return "green";
                            }
                        }
                        return "";
                    }
                });
                subWindow.setContent(clusterEvents);
                UI.addWindow(subWindow);
            }
        }
        );

        vl3.setWidth("20%");
        vl3.addComponent(clustersArea);
        hl2.addComponent(vl3);
        hl2.setExpandRatio(vl1, 2.5f);
        hl2.setExpandRatio(vl2, 1f);
        hl2.setExpandRatio(vl3, 1f);
        return hl2;
    }

    public List<ClusteringHistory> getModifiedClusters() {
        return dbControllers.getClustering().getEntitiesById(dynamicalIteration);
    }

    public List<TTestTerm> getRelevantTermsOne() {
        return dbControllers.getTtestController().getEntitiesById(dynamicalIteration, 1);
    }

    public List<TTestTerm> getRelevantTermsTwo() {
        return dbControllers.getTtestController().getEntitiesById(dynamicalIteration, 2);
    }
}
