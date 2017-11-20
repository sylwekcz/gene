package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.jenetics.Genotype;
import org.jenetics.IntegerGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;

import java.util.HashSet;
import java.util.Set;

public class RouteFinder {

    private Set<Edge> edges = new HashSet<>();
    private Set<Integer> nodes = new HashSet<>();

    private int populationSize = 8;
    private long limit = 1_000;
    private double probabilityOfMutation = 0.25;

    public RouteFinder(FileContent fileContent) {
        this.edges = fileContent.getEdges();
        this.nodes = fileContent.getNodes();
    }

    public RouteFinder(FileContent fileContent, int populationSize, long limit, double probabilityOfMutation) {
        this(fileContent);
        this.populationSize = populationSize;
        this.limit = limit;
        this.probabilityOfMutation = probabilityOfMutation;
    }

    private double fitness(final Route route) {
        if (route.isValid()) {
            return route.getWeight();
        }
        return Double.MAX_VALUE;
    }

    public Route findBest() {
        final Engine<IntegerGene, Double> engine = Engine
                .builder(this::fitness, Route.codec(this.nodes, this.edges))
                .minimizing()
                .alterers(new RouteMutator<>(this.probabilityOfMutation))
                .populationSize(this.populationSize)
                .build();

        final EvolutionResult<IntegerGene, Double> result = engine.stream().parallel()
                .limit(this.limit)
                .collect(EvolutionResult.toBestEvolutionResult());

        Genotype<IntegerGene> genotype = result.getBestPhenotype().getGenotype();
        return Route.codec(this.nodes, this.edges).decoder().apply(genotype);
    }

}