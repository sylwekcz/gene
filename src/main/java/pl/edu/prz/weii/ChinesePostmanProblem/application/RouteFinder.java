package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.jenetics.Genotype;
import org.jenetics.IntegerGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Route;

import java.util.HashSet;
import java.util.Set;

import static org.jenetics.engine.limit.bySteadyFitness;

public class RouteFinder {

    private Set<Edge> edges = new HashSet<>();
    private Set<Integer> nodes = new HashSet<>();

    private int populationSize = 150;
    private long limitIterations = 100_000_000;
    private int limitSteady = 10_000;
    private double probabilityOfMutation = 0.3;

    public RouteFinder(FileContent fileContent) {
        this.edges = fileContent.getEdges();
        this.nodes = fileContent.getNodes();
    }

    public RouteFinder(FileContent fileContent, int populationSize, long limitIterations, int limitSteady,  double probabilityOfMutation) {
        this(fileContent);
        this.populationSize = populationSize;
        this.limitIterations = limitIterations;
        this.limitSteady = limitSteady;
        this.probabilityOfMutation = probabilityOfMutation;
    }

    private double fitness(final Route route) {
        if (route.isValid()) {
            System.out.println(route);
            return route.getWeight();
        }
        return Double.MAX_VALUE;
    }

    public Route findBest() {
        final Engine<IntegerGene, Double> engine = Engine
                .builder(this::fitness, Route.code(this.nodes, this.edges))
                .minimizing()
                .alterers(new RouteMutator<>(this.probabilityOfMutation, this.nodes.size()))
                .populationSize(this.populationSize)
                .build();

        final EvolutionResult<IntegerGene, Double> result = engine.stream()
                .limit(bySteadyFitness(this.limitSteady))
                .limit(this.limitIterations)
                .collect(EvolutionResult.toBestEvolutionResult());
        Genotype<IntegerGene> genotype = result.getBestPhenotype().getGenotype();
        return Route.code(this.nodes, this.edges).decoder().apply(genotype);
    }

}