package pl.edu.prz.weii.ChinesePostmanProblem.graphgenerator;

import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class GraphGenerator {

    private int vertexCount;

    public static List<Edge> genGraph(int vertexCount, int saturation, boolean euler, Supplier<Double> weightSupplier) {
        double result[][] = new double[vertexCount][vertexCount];
        Random rand = new Random();

        for (int i = 0; i < vertexCount; i++)
            for (int j = 0; j < vertexCount; j++)
                result[i][j] = 0;

        for (int i = 1; i < vertexCount; i++)
            for (int j = 0; j < i; j++)
                if (rand.nextInt(100) < saturation) {
                    result[i][j] = weightSupplier.get();
                    result[j][i] = weightSupplier.get();
                }

        if (euler) {
            for (int i = 0; i < vertexCount - 1; i++) {
                int deg = 0;
                for (int j = 0; j < vertexCount; j++)
                    if (result[i][j] > 0) {
                        deg++;
                    }
                if (deg % 2 != 0) {
                    int x = rand.nextInt(vertexCount - i - 1) + i + 1;
                    if (result[i][x] > 0) {
                        result[i][x] = 0;
                        result[x][i] = 0;
                    } else {
                        result[i][x] = weightSupplier.get();
                        result[x][i] = weightSupplier.get();
                    }
                }
            }
        }
        System.out.println("Graph adjacency matrix:");
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            System.out.println();
            for (int j = 0; j < vertexCount; j++) {

                if (j < vertexCount - 1) {
                    System.out.print(result[i][j] + ", ");
                } else {
                    System.out.print(result[i][j] + " ");
                }

                if (i < j && result[i][j] != 0) {
                    edges.add(new Edge(i, j, result[i][j], result[j][i]));
                }
            }
        }
        System.out.println();
        return edges;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        int vertexCount = 50;
        List<Edge> edges = genGraph(vertexCount, 100, false,  () -> 1.0);
        System.out.println("Edges:");
        System.out.println(edges.size());
        edges.forEach(Edge::printLikeFileLine);

    }

    public static double generateDoubleBetween1and10(){
        return (double) new Random().nextInt(10) + 1.0;
    }

}