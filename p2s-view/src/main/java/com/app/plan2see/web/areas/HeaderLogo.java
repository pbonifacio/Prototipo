package com.app.plan2see.web.areas;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import java.io.File;

public class HeaderLogo {
    
    public Panel getContainer(){
        Panel panel = new Panel("");
        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setStyleName("blue");
        Label title = new Label("<strong><h1>Profiling Web Users Preferences with Text Mining<h1>"
                + "<h2>MSc Dissertation @ ISCTE-IUL Portugal<h2></strong>",ContentMode.HTML);
        String basepath = VaadinService.getCurrent()
                  .getBaseDirectory().getAbsolutePath();
        FileResource resource = new FileResource(new File(basepath + "/resources/images/logo.png"));
        Image image = new Image("Plan2See prototype", resource);
        hlayout.addComponent(image);
        hlayout.addComponent(title);
        hlayout.setComponentAlignment(image, Alignment.MIDDLE_LEFT);
        hlayout.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);
        hlayout.setWidth("100%");
        hlayout.setHeight("140px");
        hlayout.setMargin(new MarginInfo(true, true, true, true));
        panel.setContent(hlayout);
        return panel;
    }
}
