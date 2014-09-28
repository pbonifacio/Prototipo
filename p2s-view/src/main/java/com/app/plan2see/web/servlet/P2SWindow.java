package com.app.plan2see.web.servlet;

import com.app.plan2see.configuration.ConfigurationProperties;
import com.app.plan2see.model.db.Client;
import com.app.plan2see.web.areas.ClustersVisualizer;
import com.app.plan2see.web.areas.HeaderLogo;
import com.app.plan2see.web.areas.MainContainer;
import com.app.plan2see.web.areas.UserInfo;
import com.app.plan2see.web.controllers.AppDBControllers;
import com.app.plan2see.web.table.EventTableList;
import com.app.plan2see.web.table.TableController;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import org.apache.log4j.Logger;

@Title("..:: Plan2See ::..")
@Theme("reindeer")
@PreserveOnRefresh
public class P2SWindow extends UI {

    private static final long serialVersionUID = -3900183510661981677L;
    Logger log = Logger.getLogger(P2SWindow.class);
    VerticalLayout layout = new VerticalLayout();
    Panel logoPanel = new HeaderLogo().getContainer();
    UserInfo userInfo = new UserInfo(this);
    MainContainer mainContainer = new MainContainer(this);
    ClustersVisualizer clustersVisualizer = new ClustersVisualizer(1, this);
    Panel userPanel;
    Panel mainPanel;
    HorizontalLayout analysis;
    Client client = null;
    ConfigurationProperties confProperties = new ConfigurationProperties();
    EventTableList tableList;
    AppDBControllers controllers;
    //user must wait 3 seconds between events choosing
    long selectionDate = Calendar.getInstance().getTimeInMillis() + 3000;
    long seconds = 1500;
    TableController table = null;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(
            productionMode = false,
    ui = com.app.plan2see.web.servlet.P2SWindow.class)
    public static class P2SServlet extends VaadinServlet
            implements SessionInitListener, SessionDestroyListener {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().addSessionInitListener(this);
            getService().addSessionDestroyListener(this);
        }

        @Override
        public void sessionInit(SessionInitEvent event) throws ServiceException {
        }

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
        }
    }

    @Override
    public void init(VaadinRequest request) {
        String greenCell = ".v-table-cell-content-green { color: white; background: green; }";
        getCurrent().getPage().getStyles().add(greenCell);
        layout.setSizeFull();
        userPanel = userInfo.getContainer();
        mainPanel = mainContainer.getContainer();
        layout.addComponent(logoPanel);
        layout.addComponent(userPanel);
        VerticalLayout l = new VerticalLayout();
        layout.addComponent(l);
        layout.setExpandRatio(l, 8f);
        layout.setStyleName("blue");
        controllers = new AppDBControllers();
        this.setContent(layout);
    }
    
    public TableController getTable() {
        if( table == null )
            table = new TableController(controllers.getEvents(), this);
        return table;
    }

    public boolean checkValidLogin(String username, String password) {
        client = controllers.getClients().getClientLogin(username, password);
        if (client != null) {
            if (!client.isIsAdmin()) {
                layout.removeComponent(layout.getComponent(2));
                layout.addComponent(mainPanel);
                layout.setExpandRatio(mainPanel, 8f);
                tableList = getTable().getEventTableItems();
                mainContainer.addEventsTable();
                return true;
            } else {
                layout.removeComponent(layout.getComponent(2));
                analysis = clustersVisualizer.getContainer();
                layout.addComponent(analysis);
                layout.setExpandRatio(analysis, 8f);
                clustersVisualizer.addVisualizer();
                return true;
            }
        }
        return false;
    }

    public void showSelectedEvents(Button selected) {
        if (selected.getData().equals("false")) {
            mainContainer.filterBySelection();
            selected.setData("true");
        } else {
            mainContainer.unFilterEventsTable();
            selected.setData("false");
        }
    }

    public void logout() {
        layout.removeComponent(userPanel);
        if (!client.isIsAdmin()) {
            layout.removeComponent(mainPanel);
        } else {
            layout.removeComponent(analysis);
        }
        userInfo = new UserInfo(this);
        mainContainer = new MainContainer(this);
        clustersVisualizer = new ClustersVisualizer(1, this);
        userPanel = userInfo.getContainer();
        mainPanel = mainContainer.getContainer();
        analysis = clustersVisualizer.getContainer();

        client = null;
        layout.setSizeFull();
        layout.addComponent(userPanel);
        VerticalLayout l = new VerticalLayout();
        layout.addComponent(l);
        layout.setExpandRatio(l, 8f);
        layout.setStyleName("blue");

        this.setContent(layout);
    }

    public Client getClient() {
        return client;
    }

    public EventTableList getTableList() {
        return tableList;
    }

    public AppDBControllers getControllers() {
        return controllers;
    }

    public long getTiming() {
        return (Calendar.getInstance().getTimeInMillis()
                + seconds);
    }

    public void setSelectionDate(long selectionDate) {
        this.selectionDate = selectionDate + seconds * 1;
    }

    public boolean hasTimingError() {
        if (getTiming() > selectionDate) {
            return false;
        } else {
            return true;
        }
    }
    
    public void changeClustersVisualizer(int history){
        layout.removeComponent(analysis);
        clustersVisualizer = new ClustersVisualizer(history, this);
        analysis = clustersVisualizer.getContainer();
        clustersVisualizer.addVisualizer();
        layout.addComponent(analysis);
        layout.setExpandRatio(analysis, 8f);
    }
}
