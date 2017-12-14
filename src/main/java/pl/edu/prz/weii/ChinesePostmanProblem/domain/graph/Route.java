package pl.edu.prz.weii.ChinesePostmanProblem.domain.graph;

import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.NumericGene;
import org.jenetics.engine.Codec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Route {

    private List<Integer> visitedNodes;
    private List<Edge> correctlyVisitedEdges;
    private int incorrectEdges;
    private int notVisitedEdges;
    private boolean startingAndEndingOnSameNode;
    private boolean valid;
    private double weight;

    private Route(Genotype<IntegerGene> gt, Set<Edge> edges) {
        visitedNodes = gt.getChromosome().stream().map(NumericGene::intValue).collect(Collectors.toList());
        correctlyVisitedEdges = new ArrayList<>();
        incorrectEdges = 0;
        for (int i = 1; i < visitedNodes.size(); ++i) {
            Edge edge = findEdge(edges, visitedNodes.get(i - 1), visitedNodes.get(i));
            if (edge != null) {
                correctlyVisitedEdges.add(edge);
            } else {
                ++incorrectEdges;
            }
        }
        if (correctlyVisitedEdges.size() > 1) {
            Edge firstEdge = correctlyVisitedEdges.get(0);
            Edge lastEdge = correctlyVisitedEdges.get(correctlyVisitedEdges.size() - 1);
            int firstNode = firstEdge.isAtoB() ? firstEdge.getNodeA() : firstEdge.getNodeB();
            int lastNode = lastEdge.isAtoB() ? lastEdge.getNodeB() : lastEdge.getNodeA();
            startingAndEndingOnSameNode = firstNode == lastNode;
        }
        notVisitedEdges = edges.size() - new HashSet<>(correctlyVisitedEdges).size();
        valid = startingAndEndingOnSameNode && (notVisitedEdges == 0) && (incorrectEdges == 0);
        weight = correctlyVisitedEdges.stream().mapToDouble(Edge::getWeight).sum();
    }

    private Edge findEdge(Set<Edge> edges, int nodeA, int nodeB) {
        for (Edge edge : edges) {
            if (edge.equals(nodeA, nodeB)) {
                return edge.copy();
            }
        }
        return null;
    }

    public static Codec<Route, IntegerGene> code(Set<Integer> nodes, Set<Edge> edges) {
        int min = nodes.stream().mapToInt(Integer::intValue).min().getAsInt();
        return Codec.of(
                Genotype.of(IntegerChromosome.of(min, nodes.size() + min - 1, nodes.size())),
                gt -> new Route(gt, edges)
        );
    }

    public List<Integer> getVisitedNodes() {
        return visitedNodes;
    }

    public List<Edge> getCorrectlyVisitedEdges() {
        return correctlyVisitedEdges;
    }

    public int getIncorrectEdges() {
        return incorrectEdges;
    }

    public int getNotVisitedEdges() {
        return notVisitedEdges;
    }

    public boolean isStartingAndEndingOnSameNode() {
        return startingAndEndingOnSameNode;
    }

    public boolean isValid() {
        return valid;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Route{" +
                "visitedNodes=" + visitedNodes +
                ", correctlyVisitedEdges=" + correctlyVisitedEdges +
                ", incorrectEdges=" + incorrectEdges +
                ", notVisitedEdges=" + notVisitedEdges +
                ", startingAndEndingOnSameNode=" + startingAndEndingOnSameNode +
                ", valid=" + valid +
                ", weight=" + weight +
                '}';
    }
}

