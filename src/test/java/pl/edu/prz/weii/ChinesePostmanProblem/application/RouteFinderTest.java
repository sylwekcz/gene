package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.junit.Ignore;
import org.junit.Test;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public class RouteFinderTest {


    private FileContent fileContent = new FileContent(Arrays.asList(
            "6", "1 2 5 8", "1 3 6 9", "1 5 5 8", "2 3 3 4", "3 4 2 3", "4 5 4 5"
    ));

    @Test
    @Ignore
    public void test() {
        RouteFinder routeFinder = new RouteFinder(fileContent, 100,
                10, 22, 100, 0.1, 0.1, false);
        Route best = routeFinder.findBest();
        System.out.println(best);
        System.out.println(routeFinder.fitness(best));
        assertEquals(37.0, best.getWeight(), 0.0);
    }


    @Test
    @Ignore
    public void decompose() throws IOException {
        List<String> lines = Files.lines(Paths.get("nodes-20-edges-44")).collect(Collectors.toList());
        FileContent fileContent = new FileContent(lines);
        Set<Edge> edges = fileContent.getEdges();
        Set<Integer> nodes = fileContent.getNodes();
        int max = nodes.stream().mapToInt(Integer::intValue).max().getAsInt();
        for (int i = 0; i <= max; i++) {
            for (int j = 0; j <= max; j++) {
                System.out.print(findEdgeWeight(edges, i, j) + " ");
            }
            System.out.println();
        }
    }

    private double findEdgeWeight(Set<Edge> edges, int nodeA, int nodeB) {
        for (Edge edge : edges) {
            Edge foundEdge = edge.ifEqualsReturnCopyWithProperDirection(nodeA, nodeB);
            if (foundEdge != null) {
                return foundEdge.getWeight();
            }
        }
        return 0.0;
    }


    private FileContent getContent() throws IOException {
        //nodes-10-edges-22
        //nodes-10-edges-44
        //small-euler
        List<String> lines = Files.lines(Paths.get("nodes-10-edges-22")).collect(Collectors.toList());
        return new FileContent(lines);
    }


    private void testSingle(int populationSize, int penaltyMultiplier, double routeMutatorProbability, double singlePointCrossoverProbability, boolean useRoulette) throws IOException {
        int startChromosomeCunt = 8;
        int startChromosomeLength = 100;

        for (int i = 0; i < 5; i++) {
            try {
                RouteFinder routeFinder = new RouteFinder(getContent(), populationSize,
                        startChromosomeCunt, startChromosomeLength, penaltyMultiplier, routeMutatorProbability, singlePointCrossoverProbability, useRoulette);
                routeFinder.findBest();
            } catch (RuntimeException ex) {
                System.out.println("OK");
            }
        }
    }


    @Test
    @Ignore
    public void test1() throws IOException {
        testSingle(100,100, 0.1, 0.1, false);
        testSingle(100,100, 0.01, 0.1, false);
        testSingle(100,100, 0.01, 0.01, false);
        testSingle(1000,1000, 0.1, 0.1, false);
        testSingle(1000,100, 0.01, 0.1, false);
        testSingle(100,1000, 0.01, 0.01, false);

    }

}