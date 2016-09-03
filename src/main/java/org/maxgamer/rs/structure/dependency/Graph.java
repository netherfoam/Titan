package org.maxgamer.rs.structure.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Represents a graph of nodes. Every node is of GraphNode type and it has set a
 * value of the generic type <T>. It basically derives an evaluation order out
 * of its nodes. A node gets the chance to be evaluated when all the incoming
 * nodes were previously evaluated. The evaluating method of the
 * NodeValueListener is used to notify the outside of the fact that a node just
 * got the chance to be evaluated. A value of the node that is of the generic
 * type <T> is passed as argument to the evaluating method.
 *
 * @param <T>
 * @author nicolae caralicea
 */
final public class Graph<T> {
    /**
     * These are basically the nodes of the graph
     */
    private HashMap<T, GraphNode<T>> nodes = new HashMap<T, GraphNode<T>>();

    /**
     * It holds a list of the already evaluated nodes
     */
    private List<GraphNode<T>> evaluatedNodes = new ArrayList<GraphNode<T>>();

    /**
     * The main constructor
     */
    public Graph() {

    }

    /**
     * Allows adding of new dependicies to the graph. "evalFirstValue" needs to
     * be evaluated before "evalAfterValue"
     *
     * @param evalFirstValue The parameter that needs to be evaluated first
     * @param evalAfterValue The parameter that needs to be evaluated after
     */
    public void addDependency(T evalFirstValue, T evalAfterValue) {
        if (evalFirstValue == null) throw new NullPointerException("evalFirstValue can't be null");
        if (evalAfterValue == null) throw new NullPointerException("evalAfterValue can't be null");

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
     * Creates a graph node of the <T> generic type
     *
     * @param value The value that is hosted by the node
     * @return a generic GraphNode object
     */
    private GraphNode<T> createNode(T value) {
        GraphNode<T> node = new GraphNode<T>();
        node.value = value;
        return node;
    }

    /**
     * Takes all the nodes and calculates the dependency order for them.
     */
    public ArrayList<T> generateDependencies() {
        List<GraphNode<T>> orphanNodes = getOrphanNodes();
        List<GraphNode<T>> nextNodesToDisplay = new ArrayList<GraphNode<T>>();

        ArrayList<T> order = new ArrayList<T>();
        if (orphanNodes != null) {
            for (GraphNode<T> node : orphanNodes) {
                order.add(node.value);
                evaluatedNodes.add(node);
                nextNodesToDisplay.addAll(node.getGoingOutNodes());
            }
        }
        generateDependencies(nextNodesToDisplay, order);
        return order;
    }

    /**
     * Generates the dependency order of the nodes passed in as parameter
     *
     * @param nodes The nodes for which the dependency order order is executed
     */
    private void generateDependencies(List<GraphNode<T>> nodes, ArrayList<T> order) {
        List<GraphNode<T>> nextNodesToDisplay = null;
        for (GraphNode<T> node : nodes) {
            if (!isAlreadyEvaluated(node)) {
                List<GraphNode<T>> comingInNodes = node.getComingInNodes();
                if (areAlreadyEvaluated(comingInNodes)) {
                    order.add(node.value);
                    evaluatedNodes.add(node);
                    List<GraphNode<T>> goingOutNodes = node.getGoingOutNodes();
                    if (goingOutNodes != null) {
                        if (nextNodesToDisplay == null)
                            nextNodesToDisplay = new ArrayList<GraphNode<T>>();
                        // add these too, so they get a chance to be displayed
                        // as well
                        nextNodesToDisplay.addAll(goingOutNodes);
                    }
                } else {
                    if (nextNodesToDisplay == null)
                        nextNodesToDisplay = new ArrayList<GraphNode<T>>();
                    // the checked node should be carried
                    nextNodesToDisplay.add(node);
                }
            }
        }
        if (nextNodesToDisplay != null) {
            generateDependencies(nextNodesToDisplay, order);
        }
        // here the recursive call ends
    }

    /**
     * Checks to see if the passed in node was aready evaluated A node defined
     * as already evaluated means that its incoming nodes were already evaluated
     * as well
     *
     * @param node The Node to be checked
     * @return The return value represents the node evaluation status
     */
    private boolean isAlreadyEvaluated(GraphNode<T> node) {
        return evaluatedNodes.contains(node);
    }

    /**
     * Check to see if all the passed nodes were already evaluated. This could
     * be thought as an and logic between every node evaluation status
     *
     * @param nodes The nodes to be checked
     * @return The return value represents the evaluation status for all the
     * nodes
     */
    private boolean areAlreadyEvaluated(List<GraphNode<T>> nodes) {
        return evaluatedNodes.containsAll(nodes);
    }

    /**
     * These nodes represent the starting nodes. They are firstly evaluated.
     * They have no incoming nodes. The order they are evaluated does not
     * matter.
     *
     * @return It returns a list of graph nodes
     */
    private List<GraphNode<T>> getOrphanNodes() {
        List<GraphNode<T>> orphanNodes = null;
        Set<T> keys = nodes.keySet();
        for (T key : keys) {
            GraphNode<T> node = nodes.get(key);
            if (node.getComingInNodes() == null) {
                if (orphanNodes == null)
                    orphanNodes = new ArrayList<GraphNode<T>>();
                orphanNodes.add(node);
            }
        }
        return orphanNodes;
    }
}
