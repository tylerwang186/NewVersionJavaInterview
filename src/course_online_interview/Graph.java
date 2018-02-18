/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package course_online_interview;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author yihanwang
 */
final public class Graph<T> {
    
    private HashMap<T, GraphNode<T>> nodes = new HashMap<T, GraphNode<T>>();
    private NodeValueListener<T> listener;
    private List<GraphNode<T>> evaluatedNodes = new ArrayList<>();

    /**
     * The main constructor that has one parameter representing the callback
     * mechanism used by this class to notify when a node gets the evaluation.
     * @param listener the callback interface implemented by the user classes
     */
    public Graph(NodeValueListener<T> listener) {
        this.listener = listener;
    }

    /**
     * This method is to allow adding of new dependencies to the graph. "evalFirstValue" needs to
     * be evaluated before "evalAfterValue"
     * @param evalFirstValue the parameter that needs to be evaluated first
     * @param evalAfterValue the parameter that needs to be evaluated after
     */
    public void addDependency(T evalFirstValue, T evalAfterValue) {
        GraphNode<T> firstNode = null;
        GraphNode<T> afterNode = null;
        if (nodes.containsKey(evalFirstValue)) {
            firstNode = nodes.get(evalFirstValue);
        } else {
            firstNode = createNode(evalFirstValue);
            nodes.put(evalFirstValue, firstNode);
        }
        if (nodes.containsKey(evalAfterValue)) {
            afterNode = nodes.get(evalAfterValue);
        } else {
            afterNode = createNode(evalAfterValue);
            nodes.put(evalAfterValue, afterNode);
        }
        firstNode.addGoingOutNode(afterNode);
        afterNode.addComingInNode(firstNode);
    }

    /**
     * This method is to create a graph node of the <T> generic type
     * @param value the value that is hosted by the node
     * @return a generic GraphNode object
     */
    private GraphNode<T> createNode(T value) {
        GraphNode<T> node = new GraphNode<T>();
        node.value = value;
        return node;
    }

    /*
     * Thsi method is to take all the nodes and calculates the dependency order for them.
     */
    public void generateDependencies() {
        List<GraphNode<T>> orphanNodes = getOrderedNodes();
        List<GraphNode<T>> nextNodesToDisplay = new ArrayList<>();
        for (GraphNode<T> node : orphanNodes) {
            listener.evaluating(node.value);
            evaluatedNodes.add(node);
            nextNodesToDisplay.addAll(node.getGoingOutNodes());
        }
        generateDependencies(nextNodesToDisplay);
    }

    /**
     * This method is to generate the dependency order of the nodes passed in as parameter
     * @param node the nodes for which the dependency order order is executed
     */
    private void generateDependencies(List<GraphNode<T>> nodes) {
        List<GraphNode<T>> nextNodesToDisplay = null;
       
        for (GraphNode<T> node : nodes) {
            if (!isAlreadyEvaluated(node)) {
                List<GraphNode<T>> comingInNodes = node.getComingInNodes();
                if (areAlreadyEvaluated(comingInNodes)) {
                    listener.evaluating(node.value);
                    evaluatedNodes.add(node);
                    List<GraphNode<T>> goingOutNodes = node.getGoingOutNodes();
                    if (goingOutNodes != null) {
                        if (nextNodesToDisplay == null)
                            nextNodesToDisplay = new ArrayList<>();
                        // add these too, so they get a chance to be displayed
                        // as well
                        nextNodesToDisplay.addAll(goingOutNodes);
                    }
                } else {
                    if (nextNodesToDisplay == null)
                        nextNodesToDisplay = new ArrayList<>();
                    // the checked node should be carried
                    nextNodesToDisplay.add(node);
                }
            }
        }
        if (nextNodesToDisplay != null) {
            generateDependencies(nextNodesToDisplay);
        }
        // here the recursive call ends
    }

    /**
     * This method is to check to see if the passed in node was already evaluated A node defined
     * as already evaluated means that its incoming nodes were already evaluated
     * as well
     * @param node the Node to be checked
     * @return The return value represents the node evaluation status
     */
    private boolean isAlreadyEvaluated(GraphNode<T> node) {
        return evaluatedNodes.contains(node);
    }

    /**
     * This method is to check to see if all the passed nodes were already evaluated. This could
     * be thought as an and logic between every node evaluation status
     * @param nodes the nodes to be checked
     * @return the return value represents the evaluation status for all the nodes
     */
    private boolean areAlreadyEvaluated(List<GraphNode<T>> nodes) {
        return evaluatedNodes.containsAll(nodes);
    }

    /**
     * These nodes represent the starting nodes. They are firstly evaluated.
     * They have no incoming nodes. The order they are evaluated does not matter
     * @return a list of graph nodes
     */
    private List<GraphNode<T>> getOrphanNodes() {
        List<GraphNode<T>> orphanNodes = null;
        Set<T> keys = nodes.keySet();
        for (T key : keys) {
            GraphNode<T> node = nodes.get(key);
            if (node.getComingInNodes() == null) {
                if (orphanNodes == null)
                    orphanNodes = new ArrayList<>();
                orphanNodes.add(node);
            }
        }
        return orphanNodes;
    }
    
    /**
     * This method is to to generate the dependency order of the nodes
     * @return a list of graph node values
     */
    public CopyOnWriteArrayList getOrderedNodes(){
        List<GraphNode<T>> orphanNodes = null;
        CopyOnWriteArrayList<Integer>baseArray = new CopyOnWriteArrayList<>();
        Set<T> keys = nodes.keySet();
        for (T key : keys) {
            GraphNode<T> node = nodes.get(key);
            if (node.getGoingOutNodes() == null) {
                if (orphanNodes == null)
                    orphanNodes = new ArrayList<>();
                orphanNodes.add(node);
            }
        }
        for (int i = 0; i < orphanNodes.size(); i++){
            baseArray.add((Integer) orphanNodes.get(i).value);
        }
        
        for (int i = 0; i < baseArray.size(); i++){
            for(T key : keys){
                GraphNode<T> node = nodes.get(key);
                CopyOnWriteArrayList<Integer>goingOutArray = new CopyOnWriteArrayList<>();
                    if(node.getGoingOutNodes() != null){
                        for (int c = 0; c < node.getGoingOutNodes().size(); c++){     
                            goingOutArray.add((Integer) node.getGoingOutNodes().get(c).value);
                        }
                        if(baseArray.containsAll(goingOutArray)){
                            baseArray.add((Integer) node.value);
                        }
                    }
            }
        }
        return baseArray;
    } 
    
}
