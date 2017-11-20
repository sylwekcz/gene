package pl.edu.prz.weii.ChinesePostmanProblem.domain.file;

import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

    public class FileContent {

    private int numberOfEdges;
    private Set<Edge> edges = new HashSet<>();

    public FileContent(List<String> lines) {
        numberOfEdges = Integer.parseInt(lines.get(0));
        lines.stream().skip(1).map(String::trim).map(l -> l.split(" ")).forEach(
                (String[] s) -> {
                    edges.add(new Edge(s[0], s[1], s[2], s[3]));
                }
        );

        if(numberOfEdges != edges.size()){
            throw new IllegalArgumentException("File number of edges does not match with edges size");
        }
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Set<Integer> getNodes() {
        Set<Integer> nodes = new HashSet<>();
        nodes.addAll(edges.stream().map(Edge::getNodeA).collect(Collectors.toSet()));
        nodes.addAll(edges.stream().map(Edge::getNodeB).collect(Collectors.toSet()));
        return nodes;
    }

    @Override
    public String toString() {
        return "FileContent{" +
                "numberOfEdges=" + numberOfEdges +
                ", edges=" + edges +
                '}';
    }
}
