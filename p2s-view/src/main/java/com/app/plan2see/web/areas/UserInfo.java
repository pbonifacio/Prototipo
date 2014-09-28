package com.app.plan2see.web.areas;

import com.app.plan2see.clustering.ClusteringScheduler;
import com.app.plan2see.model.db.Client;
import com.app.plan2see.model.db.Cluster;
import com.app.plan2see.web.servlet.P2SWindow;
import com.vaadin.data.Item;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.log4j.Logger;

public class UserInfo {

    static Logger log = Logger.getLogger(UserInfo.class);
    final Label l = new Label("<center><strong><h4>"
            + "Login and help us build the prototype!</h4></strong></center>", ContentMode.HTML);
    final Label logError = new Label("<center><span style='color:red;'><strong><h4>"
            + "Login Error! Username and password combined do not exist!</h4></strong></span></center>", ContentMode.HTML);
    final HorizontalLayout hl = new HorizontalLayout();
    final HorizontalLayout hl2 = new HorizontalLayout();
    final HorizontalLayout layout = new HorizontalLayout();
    final GridLayout grid = new GridLayout(3, 3);
    Client client = null;
    TextField tu = null;
    PasswordField pu = null;
    Button login = new Button("Login");
    Button logout = new Button("Logout");
    Button showSelected = new Button("Show Selected Events");
    Button showClusters = new Button("Show Actual Clusters");
    Label logSuccess;
    P2SWindow UI;
    boolean showAll = false;
    ClusteringScheduler clustering = new ClusteringScheduler();

    public UserInfo(P2SWindow UI) {
        this.UI = UI;
    }

    public Panel getContainer() {
        Panel panel = new Panel();
        hl.setStyleName("blue");
        layout.setStyleName("blue");
        hl.addComponent(l);
        hl2.setStyleName("blue");
        grid.setStyleName("blue");
        Label ul = new Label("Username");
        tu = new TextField();
        tu.setMaxLength(50);
        Label pl = new Label("Password");
        pu = new PasswordField();
        pu.setMaxLength(50);
        grid.addComponent(new VerticalLayout(), 0, 0);
        grid.addComponent(new VerticalLayout(), 1, 0);
        grid.addComponent(new VerticalLayout(), 2, 0);
        grid.addComponent(ul, 0, 1);
        grid.addComponent(tu, 1, 1);
        grid.addComponent(pl, 0, 2);
        grid.addComponent(pu, 1, 2);
        grid.addComponent(new VerticalLayout(), 2, 1);
        login.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                checkValidLogin();
            }
        });
        hl2.addComponent(login);
        hl2.setComponentAlignment(login, Alignment.MIDDLE_CENTER);
        grid.addComponent(hl2, 2, 2);
        layout.addComponent(hl);
        layout.addComponent(grid);
        layout.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(grid, Alignment.MIDDLE_RIGHT);
        layout.setSizeFull();
        layout.setExpandRatio(grid, 1f * 1f);
        layout.setExpandRatio(hl, 3f * 1f);
        layout.setWidth("100%");
        layout.setHeight("110px");
        layout.setMargin(new MarginInfo(true, true, true, true));
        panel.setContent(layout);
        return panel;
    }

    public void checkValidLogin() {
        if (!UI.checkValidLogin(tu.getValue(), pu.getValue())) {
            hl.removeComponent(l);
            hl.addComponent(logError);
        } else {
            if (hl.getComponentIndex(logError) >= 0) {
                hl.removeComponent(logError);
            }
            hl.removeComponent(l);
            client = UI.getClient();
            String[] firstName = client.getClientName().split(" ");
            logSuccess = new Label("<center><span style='color:green;'><h4>"
                    + "You've logged in successfully " + firstName[0] + "!</h4></span></center>", ContentMode.HTML);
            hl.addComponent(logSuccess);
            if (!client.isIsAdmin()) {
                showSelected.setData("" + showAll);
                showSelected.addClickListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (UI.getClient().getChoosedEvents().isEmpty()) {
                            Notification.show("There are no selections yet",
                                    Notification.Type.ERROR_MESSAGE);
                            return;
                        }
                        UI.showSelectedEvents(showSelected);
                        if (showSelected.getData().equals("true")) {
                            showSelected.setCaption("Show All Events");
                        } else {
                            showSelected.setCaption("Show Selected Events");
                        }
                    }
                });
            } else {
                showClusters.addClickListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Window subWindow = new Window("Clusters");
                        Table clustersTable = new Table();
                        clustersTable.addContainerProperty("Cluster ID", Integer.class, null);
                        //clustersTable.addContainerProperty("Cluster Size", Integer.class, null);
                        clustersTable.addContainerProperty("Cluster Terms", String.class, null);
                        for (Cluster cluster : UI.getControllers().getClustersController()
                                .getAllEntities()) {
                            Item clusterItem = clustersTable.addItem(cluster.getId());
                            clusterItem.getItemProperty("Cluster ID").setValue(cluster.getId());
                            //clusterItem.getItemProperty("Cluster Size").setValue(cluster.getClusterSize());
                            clusterItem.getItemProperty("Cluster Terms").setValue(cluster.getRelevantTerms());
                        }
                        clustersTable.setColumnWidth("Cluster ID", 50);
                        //clustersTable.setColumnWidth("Cluster Size", 50);
                        clustersTable.setColumnWidth("Cluster Terms", 300);
                        subWindow.setContent(clustersTable);
                        UI.addWindow(subWindow);
                    }
                });
            }
            hl.setComponentAlignment(logSuccess, Alignment.MIDDLE_LEFT);
            layout.removeComponent(grid);
            hl2.removeComponent(login);
            if (!client.isIsAdmin()) {
                hl2.addComponent(showSelected);
            } else {
                hl2.addComponent(showClusters);
            }
            hl2.addComponent(logout);
            layout.addComponent(hl2);
            layout.setComponentAlignment(hl2, Alignment.MIDDLE_RIGHT);
            layout.setSizeFull();
            layout.setExpandRatio(hl, 6f);
            layout.setExpandRatio(hl2, 4f);
            layout.setComponentAlignment(hl2, Alignment.MIDDLE_RIGHT);
            logout.addClickListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    UI.logout();
                }
            });
        }
    }
}
