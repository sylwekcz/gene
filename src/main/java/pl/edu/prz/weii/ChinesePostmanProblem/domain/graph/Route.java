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
    private List<Edge> visitedEdges;
    private Double weight;

    private Route(Genotype<IntegerGene> gt, Set<Edge> edges) {
        this.visitedNodes = gt.getChromosome().stream().map(NumericGene::intValue).collect(Collectors.toList());
        this.edges = edges;
    }

    public List<Edge> getAsEdges() {
        if (this.visitedEdges != null) {
            // do not calculate again
            return this.visitedEdges;
        }
        if (!this.visitedNodes.get(0).equals(this.visitedNodes.get(this.visitedNodes.size() - 1))) {
            // invalid route
            return Collections.emptyList();
        }
        // check and build route of edges from nodes route
        List<Edge> visitedEdges = new ArrayList<>();
        for (int i = 1; i < this.visitedNodes.size(); ++i) {
            Edge edge = findEdge(this.visitedNodes.get(i - 1), this.visitedNodes.get(i));
            if (edge != null) {
                visitedEdges.add(edge.copy());
            } else {
                // invalid route
                return Collections.emptyList();
            }
        }
        this.visitedEdges = visitedEdges;
        return this.visitedEdges;
    }

    private Edge findEdge(int nodeA, int nodeB) {
        for (Edge edge : this.edges) {
            if (edge.equals(nodeA, nodeB)) {
                return edge;
            }
        }
        return null;
    }

    public boolean isValid() {
        Set<Edge> edges = new HashSet<>(this.edges);
        Set<Edge> visitedEdges = new HashSet<>(getAsEdges());
        return edges.size() == visitedEdges.size();
    }

    public Double getWeight() {
        if (this.weight != null) {
            return this.weight;
        }
        this.weight = getAsEdges().stream().mapToDouble(Edge::getWeight).sum();
        return this.weight;
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

