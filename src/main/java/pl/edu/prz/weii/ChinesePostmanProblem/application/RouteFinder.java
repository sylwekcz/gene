package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.jenetics.Genotype;
import org.jenetics.IntegerGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Route;

import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;

import static org.jenetics.engine.limit.bySteadyFitness;

public class RouteFinder {

    private Set<Edge> edges = new HashSet<>();
    private Set<Integer> nodes = new HashSet<>();
    private double maxWeight = Double.MAX_VALUE;

    private int populationSize = 10;
    private long limitIterations = 5_000_000;
    private int limitSteady = 50_000;
    private double probabilityOfMutation = 0.5;

    public RouteFinder(FileContent fileContent) {
        this.edges = fileContent.getEdges();
        this.nodes = fileContent.getNodes();

        OptionalDouble maxWeightAtoB = this.edges.stream().mapToDouble(Edge::getWeightFromAToB).max();
        OptionalDouble maxWeightBtoA = this.edges.stream().mapToDouble(Edge::getWeightFromBToA).max();
        this.maxWeight = Math.max(
                maxWeightAtoB.isPresent() ? maxWeightAtoB.getAsDouble() : Double.MAX_VALUE,
                maxWeightBtoA.isPresent() ? maxWeightBtoA.getAsDouble() : Double.MAX_VALUE
        );
    }

    public RouteFinder(FileContent fileContent, int populationSize, long limitIterations, int limitSteady, double probabilityOfMutation) {
        this(fileContent);
        this.populationSize = populationSize;
        this.limitIterations = limitIterations;
        this.limitSteady = limitSteady;
        this.probabilityOfMutation = probabilityOfMutation;
    }

    public double fitness(final Route route) {
        double score = route.getWeight();
        double penalty = maxWeight * 10;
        if (!route.isValid()) {
            score += (route.getNotVisitedEdges() * penalty);
            score += (route.getIncorrectEdges() * penalty);
            if (!route.isStartingAndEndingOnSameNode()) {
                score += penalty;
            }
        } else {
            System.out.println(route);
        }
        return score;
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