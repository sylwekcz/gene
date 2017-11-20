package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.engine.Codec;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Route {

    private Set<Edge> edges;
    private Set<Integer> nodes;
    private List<Integer> visitedNodes;
    private List<Edge> visitedEdges;


    Route(Genotype<IntegerGene> gt, Set<Integer> nodes, Set<Edge> edges) {
        this.visitedNodes = gt.getChromosome().stream().map(g -> g.intValue()).collect(Collectors.toList());
        this.nodes = nodes;
        this.edges = edges;
    }

    Route(Set<Edge> edges, List<Integer> visitedNodes) {
        this.edges = edges;
        this.visitedNodes = visitedNodes;
    }

    public List<Edge> getAsEdges() {
        if (this.visitedEdges != null) {
            return this.visitedEdges;
        }
        List<Edge> visitedEdges = new ArrayList();
        for (int i = 1; i < visitedNodes.size(); i++) {
            int nodeA = visitedNodes.get(i - 1);
            int nodeB = visitedNodes.get(i);
            Edge edge = findEdge(nodeA, nodeB);
            if (edge != null) {
                visitedEdges.add(edge.copy());
            } else {
                // invalid route
                return new ArrayList<>();
            }
        }
        return visitedEdges;
    }

    public List<Integer> getVisitedNodes() {
        return visitedNodes;
    }

    private Edge findEdge(int nodeA, int nodeB) {
        for (Edge edge : this.edges) {
            if (edge.isNode(nodeA, nodeB)) {
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

    public double getWeight() {
        return getAsEdges().stream().mapToDouble(Edge::getWeight).sum();
    }

    public static Codec<Route, IntegerGene> codec(Set<Integer> nodes, Set<Edge> edges) {
        int min = nodes.stream().mapToInt(Integer::intValue).min().getAsInt();
        return Codec.of(
                Genotype.of(IntegerChromosome.of(min, nodes.size() + min - 1, nodes.size())),
                gt -> new Route(gt, nodes, edges)
        );
    }

}

