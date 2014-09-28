package com.app.plan2see.web.areas;

import com.app.plan2see.db.controller.ClustersController;
import com.app.plan2see.model.Centroid;
import com.app.plan2see.model.db.Client;
import com.app.plan2see.model.db.Cluster;
import com.app.plan2see.model.db.Event;
import com.app.plan2see.utils.StatisticsUtil;
import com.app.plan2see.web.servlet.P2SWindow;
import com.app.plan2see.web.table.EventTableItem;
import com.app.plan2see.web.table.EventTableList;
import com.app.plan2see.web.table.P2SCheckBox;
import com.jensjansson.pagedtable.PagedTable;
import com.jensjansson.pagedtable.PagedTableContainer;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

public class MainContainer {

    Logger log = Logger.getLogger(MainContainer.class);
    HorizontalLayout layout = new HorizontalLayout();
    TextField searchText;
    TextField searchText2;
    VerticalLayout tableLayout = new VerticalLayout();
    HorizontalLayout lsearch = null;
    HorizontalLayout lsearch2 = null;
    HorizontalLayout tableControls;
    P2SWindow UI;
    PagedTable pagedTable;

    public MainContainer(P2SWindow UI) {
        this.UI = UI;
    }

    public Panel getContainer() {
        Panel panel = new Panel();
        layout.setStyleName("blue");
        layout.setWidth("100%");
        layout.setHeight("850px");
        panel.setContent(layout);
        return panel;
    }

    public void removeEventsTable() {
        tableLayout.removeAllComponents();
        pagedTable.removeAllItems();
        pagedTable = null;
        layout.removeComponent(tableLayout);
    }

    public void addEventsTable() {
        tableLayout.setStyleName("blue");
        tableLayout.addComponent(createSearchBar());
        pagedTable = createTable(UI.getTableList());
        tableLayout.addComponent(pagedTable);
        tableControls = pagedTable.createControls();
        tableLayout.addComponent(tableControls);
        tableLayout.setExpandRatio(pagedTable, 8f);
        tableLayout.setMargin(new MarginInfo(false, true, false, true));
        layout.addComponent(tableLayout);
    }

    public void unFilterEventsTable() {
        tableControls.removeAllComponents();
        lsearch.removeAllComponents();
        pagedTable.removeAllItems();
        pagedTable.setContainerDataSource(createContainer(UI.getTableList()));
        tableLayout.removeComponent(pagedTable);
        tableLayout.removeComponent(lsearch);
        if (tableLayout.getComponentIndex(lsearch2) >= 0) {
            lsearch2.removeAllComponents();
            tableLayout.removeComponent(lsearch2);
            lsearch2 = null;
        }
        tableLayout.removeComponent(tableControls);
        lsearch = null;
        tableLayout.addComponent(createSearchBar());
        tableLayout.addComponent(pagedTable);
        tableControls = pagedTable.createControls();
        tableLayout.addComponent(tableControls);
        tableLayout.setExpandRatio(pagedTable, 8f);
        tableLayout.setMargin(new MarginInfo(false, true, false, true));
        layout.addComponent(tableLayout);
    }

    public void unFilterAgainEventsTable() {
        tableControls.removeAllComponents();
        lsearch2.removeAllComponents();
        pagedTable.removeAllItems();
        pagedTable.setContainerDataSource(createContainer(
                UI.getTableList().getEventTableItems(searchText.getValue(),
                UI.getClient())));
        tableLayout.removeComponent(pagedTable);
        tableLayout.removeComponent(lsearch);
        tableLayout.removeComponent(lsearch2);
        tableLayout.removeComponent(tableControls);
        lsearch2 = null;
        tableLayout.addComponent(lsearch);
        tableLayout.addComponent(addSecondSearchBar());
        tableLayout.addComponent(pagedTable);
        tableControls = pagedTable.createControls();
        tableLayout.addComponent(tableControls);
        tableLayout.setExpandRatio(pagedTable, 8f);
        tableLayout.setMargin(new MarginInfo(false, true, false, true));
        layout.addComponent(tableLayout);
    }
    
    public void filterBySelection(){
        lsearch.removeAllComponents();
        tableControls.removeAllComponents();
        pagedTable.removeAllItems();
        pagedTable.setContainerDataSource(getChoosenEventTableITems(
                UI.getClient()));
        tableLayout.removeComponent(pagedTable);
        tableLayout.removeComponent(lsearch);
        if (tableLayout.getComponentIndex(lsearch2) >= 0) {
            tableLayout.removeComponent(lsearch2);
        }
        tableLayout.removeComponent(tableControls);
        tableLayout.addComponent(pagedTable);
        tableControls = pagedTable.createControls();
        tableLayout.addComponent(tableControls);
        tableLayout.setExpandRatio(pagedTable, 8f);
        tableLayout.setMargin(new MarginInfo(false, true, false, true));
        layout.addComponent(tableLayout);
    }

    public void filterEventsTable() {
        if (searchText.getValue() == null || searchText.getValue().length() < 3) {
            Notification.show("Search is null or has less than 3 characters",
                    Notification.Type.ERROR_MESSAGE);
            return;
        }
        lsearch.removeAllComponents();
        tableControls.removeAllComponents();
        pagedTable.removeAllItems();
        pagedTable.setContainerDataSource(createContainer(
                UI.getTableList().getEventTableItems(searchText.getValue(),
                UI.getClient())));
        if (pagedTable.getItemIds().isEmpty()) {
            Notification.show("No results found",
                    Notification.Type.ERROR_MESSAGE);
            unFilterEventsTable();
            return;
        }
        tableLayout.removeComponent(pagedTable);
        tableLayout.removeComponent(lsearch);
        lsearch = null;
        tableLayout.removeComponent(tableControls);
        tableLayout.addComponent(removeSearchBar());
        if (lsearch2 == null) {
            tableLayout.addComponent(addSecondSearchBar());
        }
        tableLayout.addComponent(pagedTable);
        tableControls = pagedTable.createControls();
        tableLayout.addComponent(tableControls);
        tableLayout.setExpandRatio(pagedTable, 8f);
        tableLayout.setMargin(new MarginInfo(false, true, false, true));
        layout.addComponent(tableLayout);
    }

    public void filterAgainEventsTable() {
        if (searchText2.getValue() == null || searchText2.getValue().length() < 3) {
            Notification.show("Search is null or has less than 3 characters",
                    Notification.Type.ERROR_MESSAGE);
            return;
        }
        lsearch2.removeAllComponents();
        lsearch2 = null;
        tableControls.removeAllComponents();
        pagedTable.setContainerDataSource(
                getFilteredEventTableITems(searchText2.getValue(), UI.getClient()));
        if (pagedTable.getItemIds().isEmpty()) {
            Notification.show("No results found",
                    Notification.Type.ERROR_MESSAGE);
            unFilterAgainEventsTable();
            return;
        }
        tableLayout.removeComponent(pagedTable);
        tableLayout.removeComponent(lsearch);
        tableLayout.removeComponent(tableControls);
        tableLayout.addComponent(lsearch);
        tableLayout.addComponent(removeSecondSearchBar());
        tableLayout.addComponent(pagedTable);
        tableControls = pagedTable.createControls();
        tableLayout.addComponent(tableControls);
        tableLayout.setExpandRatio(pagedTable, 8f);
        tableLayout.setMargin(new MarginInfo(false, true, false, true));
        layout.addComponent(tableLayout);
    }

    public PagedTable createTable(EventTableList dsList) {
        pagedTable = new PagedTable();
        pagedTable.setContainerDataSource(createContainer(dsList));
        pagedTable.setSizeFull();
        pagedTable.setPageLength(15);
        pagedTable.setColumnExpandRatio("title", 3f);
        pagedTable.setColumnExpandRatio("description", 6.5f);
        pagedTable.setColumnExpandRatio("selection", 0.5f);
        ClustersController clusters = UI.getControllers().getClustersController();
        //pagedTable.setVisibleColumns(pagedTable.getContainerPropertyIds());
        String[] headers = new String[4+clusters.getAllEntities().size()];
        headers[0] = "Title";
        headers[1] = "Description";
        headers[2] = "Cluster";
        headers[3] = "Selected?";
        int i = 4;
        for( Cluster c : clusters.getAllEntities() ){
            headers[i++] = "D #"+c.getId();
        }
        pagedTable.setColumnHeaders(headers);
        
        return pagedTable;
    }

    public HorizontalLayout createSearchBar() {
        lsearch = new HorizontalLayout();
        lsearch.setStyleName("blue");
        lsearch.setWidth("80%");
        lsearch.setSpacing(true);
        searchText = new TextField();
        Label l = new Label("<span style='color: green'><h3>You can use & to address multiple search terms!</h3></span>", ContentMode.HTML);
        lsearch.addComponent(l);
        lsearch.addComponent(searchText);
        String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
        FileResource resource = new FileResource(new File(basepath + "/resources/images/search.png"));
        Button pictureButton = new Button();
        pictureButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                filterEventsTable();
            }
        });
        pictureButton.setIcon(resource);
        lsearch.addComponent(pictureButton);
        lsearch.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        lsearch.setComponentAlignment(searchText, Alignment.MIDDLE_CENTER);
        lsearch.setComponentAlignment(pictureButton, Alignment.MIDDLE_CENTER);
        lsearch.setMargin(new MarginInfo(false, false, false, true));
        return lsearch;
    }

    public HorizontalLayout removeSearchBar() {
        lsearch = new HorizontalLayout();
        lsearch.setStyleName("blue");
        lsearch.setWidth("30%");
        lsearch.setSpacing(true);
        Label l = new Label("<span style='color: red'><h3>(initialize table) Discard search = " + searchText.getValue() + "</h3></span>", ContentMode.HTML);
        lsearch.addComponent(l);
        String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
        FileResource resource = new FileResource(new File(basepath + "/resources/images/error.png"));
        Button pictureButton = new Button();
        pictureButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                unFilterEventsTable();
            }
        });
        pictureButton.setIcon(resource);
        lsearch.addComponent(pictureButton);
        lsearch.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        lsearch.setComponentAlignment(pictureButton, Alignment.MIDDLE_CENTER);
        lsearch.setMargin(new MarginInfo(false, false, false, true));
        return lsearch;
    }

    public HorizontalLayout removeSecondSearchBar() {
        lsearch2 = new HorizontalLayout();
        lsearch2.setStyleName("blue");
        lsearch2.setWidth("30%");
        lsearch2.setSpacing(true);
        Label l = new Label("<span style='color: red'><h3>(discard 2nd search) Discard search = " + searchText2.getValue() + "</h3></span>", ContentMode.HTML);
        lsearch2.addComponent(l);
        String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
        FileResource resource = new FileResource(new File(basepath + "/resources/images/error.png"));
        Button pictureButton = new Button();
        pictureButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                unFilterAgainEventsTable();
            }
        });
        pictureButton.setIcon(resource);
        lsearch2.addComponent(pictureButton);
        lsearch2.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        lsearch2.setComponentAlignment(pictureButton, Alignment.MIDDLE_CENTER);
        lsearch2.setMargin(new MarginInfo(false, false, false, true));
        return lsearch2;
    }

    public HorizontalLayout addSecondSearchBar() {
        lsearch2 = new HorizontalLayout();
        lsearch2.setStyleName("blue");
        lsearch2.setWidth("80%");
        lsearch2.setSpacing(true);
        searchText2 = new TextField();
        Label l = new Label("<span style='color: green'><h3>You can add a second filter in the same way!</h3></span>", ContentMode.HTML);
        lsearch2.addComponent(l);
        lsearch2.addComponent(searchText2);
        String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
        FileResource resource = new FileResource(new File(basepath + "/resources/images/plus.png"));
        Button pictureButton = new Button();
        pictureButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                filterAgainEventsTable();
            }
        });
        pictureButton.setIcon(resource);
        lsearch2.addComponent(pictureButton);
        lsearch2.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
        lsearch2.setComponentAlignment(searchText2, Alignment.MIDDLE_CENTER);
        lsearch2.setComponentAlignment(pictureButton, Alignment.MIDDLE_CENTER);
        lsearch2.setMargin(new MarginInfo(false, false, false, true));
        return lsearch2;
    }

    public IndexedContainer createContainer(EventTableList dsList) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("title", String.class, null);
        container.addContainerProperty("description", String.class, null);
        container.addContainerProperty("cluster", Integer.class, null);
        container.addContainerProperty("selection", P2SCheckBox.class, null);
        StatisticsUtil utils = new StatisticsUtil();
        ClustersController clusters = UI.getControllers().getClustersController();
        for( Cluster c : clusters.getAllEntities() ){
            container.addContainerProperty("D #"+c.getId(), Double.class, null);
        }
        container.addContainerProperty("selection", P2SCheckBox.class, null);
        
        List<Centroid> clustersCentroid = new ArrayList<Centroid>();
        for( Cluster c : clusters.getAllEntities() ){
            Centroid centroid = new Centroid();
            centroid.setClusterId(c.getId());
            centroid.setClusterCentroid(utils.calculateCentroidForTable(c));
            clustersCentroid.add(centroid);
        }
        
        for (int i = 0; i < dsList.countAllEvents(); i++) {
            EventTableItem evtItem = dsList.getEventByIdx(i);
            String id = "" + i;
            Item item = container.addItem(id);
            item.getItemProperty("title").setValue(evtItem.getTitle() == null ? "N/A" : evtItem.getTitle());
            item.getItemProperty("description").setValue(evtItem.getDescription() == null ? "N/A" : evtItem.getDescription());
            item.getItemProperty("cluster").setValue(evtItem.getCluster());
            P2SCheckBox selected = evtItem.getSelected();
            item.getItemProperty("selection").setValue(selected);
            for(Centroid centroid : clustersCentroid){
                Event evt = new Event();
                evt.setEventID(evtItem.getId());
                HashMap<String, Double> eventCentroid = utils.calculateCentroidForTable(evt);
                double distance = utils.getEuclideanDistance(eventCentroid, centroid.getClusterCentroid());
                item.getItemProperty("D #"+centroid.getClusterId()).setValue(distance);
            }
        }
        container.sort(new Object[]{"title","description"}, new boolean[]{true,true});
        return container;
    }

    public IndexedContainer getFilteredEventTableITems(String expression, Client client) {
        PagedTableContainer container = (PagedTableContainer) pagedTable.getContainerDataSource();
        String[] expressions = expression.toUpperCase().trim().split("&");
        IndexedContainer newContainer = new IndexedContainer();
        newContainer.addContainerProperty("title", String.class, null);
        newContainer.addContainerProperty("description", String.class, null);
        container.addContainerProperty("cluster", Integer.class, null);
        newContainer.addContainerProperty("selection", CheckBox.class, null);
        ClustersController clusters = UI.getControllers().getClustersController();
        for( Cluster c : clusters.getAllEntities() ){
            newContainer.addContainerProperty("D #"+c.getId(), Double.class, null);
        }
        StatisticsUtil utils = new StatisticsUtil();
        int idx = 0;
        List<Centroid> clustersCentroid = new ArrayList<Centroid>();
        for( Cluster c : clusters.getAllEntities() ){
            Centroid centroid = new Centroid();
            centroid.setClusterId(c.getId());
            centroid.setClusterCentroid(utils.calculateCentroidForTable(c));
            clustersCentroid.add(centroid);
        }
        for (int i = 0; i < container.getRealSize(); i++) {
            String id = "" + container.getIdByIndex(i);
            Item item = container.getItem(id);
            P2SCheckBox check = (P2SCheckBox) item.getItemProperty("selection").getValue();
            String title = item.getItemProperty("title").getValue().toString();
            String tUpper = title.toUpperCase();
            String description = item.getItemProperty("description").getValue().toString();
            String dUpper = description.toUpperCase();
            int idxExpression = 0;
            boolean hasAny = false;
            while (idxExpression < expressions.length) {
                if (tUpper.contains(expressions[idxExpression])
                        || dUpper.contains(expressions[idxExpression])) {
                    hasAny = true;
                }
                idxExpression++;
            }
            if (hasAny) {
                Item newItem = newContainer.addItem("" + idx++);
                newItem.getItemProperty("title").setValue(title);
                newItem.getItemProperty("description").setValue(description);
                newItem.getItemProperty("cluster").setValue(item.getItemProperty("cluster"));
                newItem.getItemProperty("selection").setValue(check);
                for( Cluster c : clusters.getAllEntities() ){
                    newItem.getItemProperty("D #"+c.getId()).setValue(
                            item.getItemProperty("D #"+c.getId())
                            );
                }
            }
        }
        container.sort(new Object[]{"title","description"}, new boolean[]{true,true});
        return newContainer;
    }
    
    public IndexedContainer getChoosenEventTableITems(Client client) {
        EventTableList dsList = UI.getTable().getEventTableItems();
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("title", String.class, null);
        container.addContainerProperty("description", String.class, null);
        container.addContainerProperty("cluster", Integer.class, null);
        container.addContainerProperty("selection", P2SCheckBox.class, null);
        client = UI.getControllers().getClients().getEntityById(client.getClientID());
        for (int idx = 0; idx < client.getChoosedEventsSize(); idx++) {
            EventTableItem evtItem= dsList.getEventById(
                    client.getChoosedEvents().get(idx).getEventID());
            String id = "" + idx;
            Item item = container.addItem(idx);
            item.getItemProperty("title").setValue(evtItem.getTitle() == null ? "N/A" : evtItem.getTitle());
            item.getItemProperty("description").setValue(evtItem.getDescription() == null ? "N/A" : evtItem.getDescription());
            Event evt = UI.getControllers().getEvents().getEntityById(evtItem.getId());
            item.getItemProperty("cluster").setValue(evt.getCluster());
            P2SCheckBox selected = evtItem.getSelected();
            item.getItemProperty("selection").setValue(selected);
        }
        container.sort(new Object[]{"title","description"}, new boolean[]{true,true});
        return container;
    }
}
