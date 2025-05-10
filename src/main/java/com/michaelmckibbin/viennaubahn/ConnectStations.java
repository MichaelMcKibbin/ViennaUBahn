package com.michaelmckibbin.viennaubahn;

public class ConnectStations {
    public com.michaelmckibbin.viennaubahn.GraphNode<?> destNode; // represents the node that the edge is directed towards
    public int cost;

    public ConnectStations(com.michaelmckibbin.viennaubahn.GraphNode<?> destNode, int cost) {
        this.destNode =destNode;
        this.cost=cost;
    }
}
