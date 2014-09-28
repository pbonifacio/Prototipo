package com.app.plan2see.rapidminer;

import com.app.plan2see.db.controller.EventsController;
import com.app.plan2see.model.db.Event;
import com.rapidminer.RapidMiner;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.clustering.HierarchicalClusterModel;
import com.rapidminer.operator.clustering.HierarchicalClusterNode;
import com.rapidminer.operator.io.IOObjectReader;
import com.rapidminer.tools.OperatorService;
import com.vaadin.addon.jpacontainer.EntityItem;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

public class RapidMinerImpl {

    private String homePath;
    private String p2sHomePath;
    Collection<HierarchicalClusterNode> clusters = null;
    Logger log = Logger.getLogger(RapidMinerImpl.class);

    public RapidMinerImpl(String homePath, String p2sHomePath) {
        this.homePath = homePath;
        this.p2sHomePath = p2sHomePath;
        try {
            clusters = readClusters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void writeModel(int size, TermsController controller) throws Exception{
     System.setProperty("rapidminer.home", homePath);
     System.setProperty("rapidminer.init.plugins", "true"); //(true or false)
     System.setProperty("rapidminer.init.plugins.location", homePath + "lib/plugins"); //(directory path)
     System.setProperty("rapidminer.init.weka", "false"); //(true or false)
     System.setProperty("rapidminer.init.jdbc.lib", "false"); //(true or false)
     System.setProperty("rapidminer.init.jdbc.classpath", "false"); //(true or false)
     RapidMiner.init();
     List<Term> terms = controller.getAllEntities();
     ArrayList<String> attributes = new ArrayList<String>();
     CentroidClusterModel model = new CentroidClusterModel(new SimpleExampleSet(new MemoryExampleTable()),
     size, ,
     new EuclideanDistance(), true, true);
     Operator operator = OperatorService.createOperator(ClusterWriter.class);
     ClusterWriter clusterWriter = new ClusterModWriter(operator.getOperatorDescription());
     clusterWriter.setParameter(ClusterModelWriter.PARAMETER_CLUSTER_MODEL_FILE, p2sHomePath + "kMeansModel.it.clm");
     clusterWriter.write(model);
     }*/
    public void buildDBClusters() {
        int idx = 1;
        EventsController eventsController = new EventsController();
        for (HierarchicalClusterNode c : clusters) {
            if (c.getNumberOfSubNodes() == 0) {
                Collection<Object> events = c.getExampleIdsInSubtree();
                for (Object e : events) {
                    int id = Integer.parseInt(e.toString().replace(".0", ""));
                    EntityItem<Event> event = eventsController.getEntityItemById(id);
                    event.getItemProperty("cluster").setValue(idx);
                }
                idx++;
            }
        }
    }

    public Collection<HierarchicalClusterNode> readClusters() throws Exception {
        System.setProperty("rapidminer.home", homePath);
        System.setProperty("rapidminer.init.plugins", "true"); //(true or false)
        System.setProperty("rapidminer.init.plugins.location", homePath + "lib/plugins"); //(directory path)
        System.setProperty("rapidminer.init.weka", "false"); //(true or false)
        System.setProperty("rapidminer.init.jdbc.lib", "false"); //(true or false)
        System.setProperty("rapidminer.init.jdbc.classpath", "false"); //(true or false)
        RapidMiner.init();
        Operator operator = OperatorService.createOperator(IOObjectReader.class);
        IOObjectReader reader = new IOObjectReader(operator.getOperatorDescription());
        reader.setParameter(IOObjectReader.PARAMETER_OBJECT_FILE, p2sHomePath + "hierarchicalModel.it.xml");
        reader.setParameter(IOObjectReader.PARAMETER_IGNORE_TYPE, "false");
        reader.setParameter(IOObjectReader.PARAMETER_IO_OBJECT, "HierarchicalClusterModel");
        HierarchicalClusterModel model = (HierarchicalClusterModel) reader.read();
        HierarchicalClusterNode root = model.getRootNode();
        /*
         * 
         * @TODO ADD cycles for cluster depth  or recursive function for all
         * @TODO create id for each unique cluster (node)
         * 
         */
        Collection<HierarchicalClusterNode> newClusters = getListOfNodes(root);
        int size = 0;
        log.info("NUMBER OF CLUSTERS = " + newClusters.size());
        for (HierarchicalClusterNode c : newClusters) {
            log.info("READING CLUSTER = " + c.getClusterId());
            if (c.getNumberOfSubNodes() == 0) {
                Collection<Object> events = c.getExampleIdsInSubtree();
                size += events.size();
            }
        }
        log.info("NUMBER OF EVENTS = " + size);
        return newClusters;
    }

    //hierarchical cluster model with 3 clusters per tree node
    public Collection<HierarchicalClusterNode> getListOfNodes(HierarchicalClusterNode root) {
        Collection<HierarchicalClusterNode> nodes = new ArrayList<HierarchicalClusterNode>();
        nodes.add(root);
        for (HierarchicalClusterNode oneNode : root.getSubNodes()) {
            //depth 0
            nodes.add(oneNode);
            for (HierarchicalClusterNode twoNode : oneNode.getSubNodes()) {
                //depth 1
                nodes.add(twoNode);
                for (HierarchicalClusterNode threeNode : twoNode.getSubNodes()) {
                    //depth 2
                    nodes.add(threeNode);
                }
            }
        }
        return nodes;
    }

    public Collection<HierarchicalClusterNode> getClusters() {
        return clusters;
    }

    public void setClusters(Collection<HierarchicalClusterNode> clusters) {
        this.clusters = clusters;
    }
}
