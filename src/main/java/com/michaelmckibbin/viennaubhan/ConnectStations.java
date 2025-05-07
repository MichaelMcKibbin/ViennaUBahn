package com.michaelmckibbin.viennaubhan;

public class ConnectStations {
    public com.michaelmckibbin.viennaubhan.GraphNode<?> destNode; // represents the node that the edge is directed towards
    public int cost;

    public ConnectStations(com.michaelmckibbin.viennaubhan.GraphNode<?> destNode, int cost) {
        this.destNode =destNode;
        this.cost=cost;
    }
}
