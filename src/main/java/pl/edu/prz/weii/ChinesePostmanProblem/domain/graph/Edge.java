package pl.edu.prz.weii.ChinesePostmanProblem.domain.graph;


import org.jenetics.util.Copyable;

import java.io.Serializable;
import java.util.Objects;

public class Edge {

    private int nodeA;
    private int nodeB;
    private double weightFromAToB;
    private double weightFromBToA;
    private boolean AtoB = true;

    public Edge(String nodeA, String nodeB, String weightFromAToB, String weightFromBToA) {
        this.nodeA = Integer.parseInt(nodeA);
        this.nodeB = Integer.parseInt(nodeB);
        this.weightFromAToB = Double.parseDouble(weightFromAToB);
        this.weightFromBToA = Double.parseDouble(weightFromBToA);
    }

    public Edge(int nodeA, int nodeB, double weightFromAToB, double weightFromBToA) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.weightFromAToB = weightFromAToB;
        this.weightFromBToA = weightFromBToA;
    }

    public Edge(int nodeA, int nodeB, double weightFromAToB, double weightFromBToA, boolean AtoB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.weightFromAToB = weightFromAToB;
        this.weightFromBToA = weightFromBToA;
        this.AtoB = AtoB;
    }

    public int getNodeA() {
        return nodeA;
    }

    public int getNodeB() {
        return nodeB;
    }

    public double getWeightFromAToB() {
        return weightFromAToB;
    }

    public double getWeightFromBToA() {
        return weightFromBToA;
    }

    public boolean isAtoB() {
        return AtoB;
    }

    public double getWeight() {
        if (AtoB) {
            return weightFromAToB;
        } else {
            return weightFromBToA;
        }
    }

    public Edge ifEqualsReturnCopyWithProperDirection(int nodeA, int nodeB) {
        if (this.nodeA == nodeA && this.nodeB == nodeB) {
            return new Edge(this.nodeA, this.nodeB, weightFromAToB, weightFromBToA, true);
        } else if (this.nodeB == nodeA && this.nodeA == nodeB) {
             return new Edge(this.nodeA, this.nodeB, weightFromAToB, weightFromBToA, false);
        }
        return null;
    }

    public void printLikeFileLine() {
        System.out.println(nodeA + " " + nodeB + " " + weightFromAToB + " " + weightFromBToA);
    }

    @Override
    public boolean equals(Object o) {
        Edge edge = (Edge) o;
        return nodeA == edge.nodeA &&
                nodeB == edge.nodeB;
    }

    @Override
    public int hashCode() {

        return Objects.hash(nodeA, nodeB);
    }

    @Override
    public String toString() {
        String arrow = "%d --(%.1f)-> %d";
        return AtoB ? String.format(arrow,  nodeA, weightFromAToB, nodeB) : String.format(arrow, nodeB, weightFromBToA, nodeA);
    }
}
