package pl.edu.prz.weii.ChinesePostmanProblem.domain.graph;

import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.NumericGene;
import org.jenetics.engine.Codec;

import java.util.*;
import java.util.stream.Collectors;

public class Route {

    private Set<Edge> edges;
    private List<Integer> visitedNodes;
    private List<Edge> correctlyVisitedEdges;
    private Double weight;
    private Boolean startsAndEndsOnSameNode;
    private Boolean allEdgesVisited;
    private Integer omittedEdges;

    private Route(Genotype<IntegerGene> gt, Set<Edge> edges) {
        this.visitedNodes = gt.getChromosome().stream().map(NumericGene::intValue).collect(Collectors.toList());
        this.edges = edges;
    }

    public List<Edge> getCorrectlyVisitedEdges() {
        if (this.correctlyVisitedEdges == null) {
            // check and build route of edges from nodes route
            List<Edge> correctlyVisitedEdges = new ArrayList<>();
            for (int i = 1; i < this.visitedNodes.size(); ++i) {
                Edge edge = findEdge(this.visitedNodes.get(i - 1), this.visitedNodes.get(i));
                if (edge != null) {
                    correctlyVisitedEdges.add(edge.copy());
                } else {
                    break;
                }
            }
            this.correctlyVisitedEdges = correctlyVisitedEdges;
        }
        return this.correctlyVisitedEdges;
    }

    private Edge findEdge(int nodeA, int nodeB) {
        for (Edge edge : this.edges) {
            if (edge.equals(nodeA, nodeB)) {
                return edge;
            }
        }
        return null;
    }

    public boolean startsAndEndsOnSameNode(){
        if (this.startsAndEndsOnSameNode == null) {
            this.startsAndEndsOnSameNode = this.visitedNodes.get(0).equals(this.visitedNodes.get(this.visitedNodes.size() - 1));
        }
        return this.startsAndEndsOnSameNode;
    }

    public boolean allEdgesVisited(){
        if (this.allEdgesVisited == null) {
            // filter duplicates
            Set<Edge> visitedEdges = new HashSet<>(getCorrectlyVisitedEdges());
            this.omittedEdges = this.edges.size() - visitedEdges.size();
            this.allEdgesVisited = this.edges.size() == visitedEdges.size();
        }
        return this.allEdgesVisited;
    }

    public boolean isValid() {
        return allEdgesVisited() && startsAndEndsOnSameNode();
    }

    public Double getWeight() {
        if (this.weight == null) {
            this.weight = getCorrectlyVisitedEdges().stream().mapToDouble(Edge::getWeight).sum();
        }
        return this.weight;
    }

    public Integer getOmittedEdges() {
        if (this.omittedEdges == null) {
            allEdgesVisited();
        }
        return this.omittedEdges;
    }

    public List<Integer> getVisitedNodes() {
        return visitedNodes;
    }

    @Override
    public String toString() {
        return "Route{" +
                "visitedNodes=" + getVisitedNodes() +
                ", weight=" + getWeight() +
                '}';
    }

    public static Codec<Route, IntegerGene> code(Set<Integer> nodes, Set<Edge> edges) {
        int min = nodes.stream().mapToInt(Integer::intValue).min().getAsInt();
        return Codec.of(
                Genotype.of(IntegerChromosome.of(min, nodes.size() + min - 1, nodes.size())),
                gt -> new Route(gt, edges)
        );
    }
}

