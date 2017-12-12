package pl.edu.prz.weii.ChinesePostmanProblem.domain.graph;


import org.jenetics.util.Copyable;

import java.io.Serializable;

public class Edge implements Serializable, Copyable<Edge> {

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

    public boolean equals(int nodeA, int nodeB) {
        if (this.nodeA == nodeA && this.nodeB == nodeB) {
            this.AtoB = true;
            return true;
        } else if (this.nodeB == nodeA && this.nodeA == nodeB) {
            this.AtoB = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (nodeA != edge.nodeA) return false;
        if (nodeB != edge.nodeB) return false;
        if (Double.compare(edge.weightFromAToB, weightFromAToB) != 0) return false;
        return Double.compare(edge.weightFromBToA, weightFromBToA) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = nodeA;
        result = 31 * result + nodeB;
        temp = Double.doubleToLongBits(weightFromAToB);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(weightFromBToA);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public Edge copy() {
        return new Edge(nodeA, nodeB, weightFromAToB, weightFromBToA, AtoB);
    }

    @Override
    public String toString() {
        return nodeA + " " + nodeB + " " + weightFromAToB + " " + weightFromBToA;
    }
}
