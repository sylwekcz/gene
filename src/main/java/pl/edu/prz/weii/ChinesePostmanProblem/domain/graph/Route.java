package pl.edu.prz.weii.ChinesePostmanProblem.domain.graph;

import org.jenetics.*;
import org.jenetics.engine.Codec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Route {

    private boolean valid;
    private double weight;
    private int incorrectEdges;
    private int notVisitedEdges;
    private boolean startingAndEndingOnSameNode;
    private List<Integer> visitedNodes;
    private List<Edge> correctlyVisitedEdges;


    private Route(Genotype<IntegerGene> gt, Set<Edge> edges) {
        visitedNodes = new ArrayList<>();
        correctlyVisitedEdges = new ArrayList<>();
        incorrectEdges = 0;

//        int genes = gt.getNumberOfGenes();
//        int chromosomes = gt.length();
//        System.out.println(chromosomes + " " + genes);
        gt.iterator().forEachRemaining(chromosome -> {
                    chromosome.iterator().forEachRemaining(
                            gene -> {
                                visitedNodes.add(gene.intValue());
                            }
                    );
                }
        );
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
        HashSet<Edge> edges1 = new HashSet<>(correctlyVisitedEdges);
        notVisitedEdges = edges.size() - new HashSet<>(correctlyVisitedEdges).size();
        valid = startingAndEndingOnSameNode && (notVisitedEdges == 0) && (incorrectEdges == 0);
        weight = correctlyVisitedEdges.stream().mapToDouble(Edge::getWeight).sum();
    }

    private Edge findEdge(Set<Edge> edges, int nodeA, int nodeB) {
        for (Edge edge : edges) {
            Edge foundEdge = edge.ifEqualsReturnCopyWithProperDirection(nodeA, nodeB);
            if (foundEdge != null) {
                return foundEdge;
            }
        }
        return null;
    }

    public static Codec<Route, IntegerGene> code(Set<Integer> nodes, Set<Edge> edges, int chromosomesCount, int chromosomeLength) {
        int min = nodes.stream().mapToInt(Integer::intValue).min().getAsInt();
        IntegerChromosome randomChromosome = IntegerChromosome.of(min, nodes.size() + min - 1, chromosomeLength);
        List<IntegerChromosome> chromosomes = new ArrayList<>();
        chromosomes.add(randomChromosome);
        for (int i = 1; i < chromosomesCount; i++) {
            chromosomes.add(randomChromosome);
        }
        return Codec.of(
                Genotype.of(chromosomes),
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
                "valid=" + valid +
                ", weight=" + weight +
                ", incorrectEdges=" + incorrectEdges +
                ", notVisitedEdges=" + notVisitedEdges +
                ", startingAndEndingOnSameNode=" + startingAndEndingOnSameNode +
                ", visitedNodes=" + visitedNodes +
                ", correctlyVisitedEdges=" + correctlyVisitedEdges +
                '}';
    }
}

