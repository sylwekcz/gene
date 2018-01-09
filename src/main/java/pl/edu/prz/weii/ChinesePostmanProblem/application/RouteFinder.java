package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.jenetics.Genotype;
import org.jenetics.IntegerGene;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Route;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static org.jenetics.engine.limit.bySteadyFitness;

public class RouteFinder {

    private File outFile;
    private Set<Edge> edges;
    private Set<Integer> nodes;
    private double maxWeight;

    private int populationSize = 10000;
    private int startChromosomeCount = 6;
    private int startChromosomeLength = 2;
    private long limitIterations = 1_000_000L;
    private int limitSteady = 10_000;
    private int penaltyMultiplier = 10;

    private double routeMutatorProbability = 0.1;
    private double singlePointCrossoverProbability = 0.05;
    private boolean useRoulette = false;
    private double penalty;
    private String alterers = "";
    private String offspringSelector = "";
    private String survivorsSelector = "";
    private double prevBestScore = Double.MAX_VALUE;
    private long startTime = -1;


    public RouteFinder(FileContent fileContent, int populationSize, int startChromosomeCount, int startChromosomeLength, int penaltyMultiplier, double routeMutatorProbability, double singlePointCrossoverProbability, boolean useRoulette) {
        this.populationSize = populationSize;
        this.startChromosomeCount = startChromosomeCount;
        this.startChromosomeLength = startChromosomeLength;
        this.penaltyMultiplier = penaltyMultiplier;
        this.routeMutatorProbability = routeMutatorProbability;
        this.singlePointCrossoverProbability = singlePointCrossoverProbability;
        this.useRoulette = useRoulette;
        this.edges = fileContent.getEdges();
        this.nodes = fileContent.getNodes();

        try {
            new File("results").mkdirs();
            this.outFile = new File("results/nodes-" + this.nodes.size() + "-edges-" + this.edges.size() + ".csv");
            if (!this.outFile.exists()) {
                this.outFile.createNewFile();
                List<String> lines = Collections.singletonList("nodes;edges;valid;time[s];weight;penalty;populationSize;startChromosomeCount;startChromosomeLength;"
                        + "limitIterations;limitSteady;alterers;offspringSelector;survivorsSelector;routeNodes;routeEdges");
                Files.write(this.outFile.toPath(), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        OptionalDouble maxWeightAtoB = this.edges.stream().mapToDouble(Edge::getWeightFromAToB).max();
        OptionalDouble maxWeightBtoA = this.edges.stream().mapToDouble(Edge::getWeightFromBToA).max();
        this.maxWeight = Math.max(
                maxWeightAtoB.isPresent() ? maxWeightAtoB.getAsDouble() : Double.MAX_VALUE,
                maxWeightBtoA.isPresent() ? maxWeightBtoA.getAsDouble() : Double.MAX_VALUE
        );
        this.penalty = maxWeight * this.penaltyMultiplier;
    }


    public double fitness(final Route route) {
        double score = route.getWeight();
        if (!route.isValid()) {
            score += (route.getNotVisitedEdges() * penalty);
            score += (route.getIncorrectEdges() * penalty);
            if (!route.isStartingAndEndingOnSameNode()) {
                score += penalty * 2;
            }
        }
        if (prevBestScore > score) {
            prevBestScore = score;
            System.out.println(route);
        }
        return score;
    }

    private void toFile(Route route) {
        long time = System.currentTimeMillis() - startTime;
        StringJoiner stringJoiner = new StringJoiner(";");
        stringJoiner
                .add(String.valueOf(this.nodes.size()))
                .add(String.valueOf(this.edges.size()))
                .add(String.valueOf(route.isValid()))
                .add(String.valueOf(time / 1000.0))
                .add(String.valueOf(route.getWeight()))
                .add(String.valueOf(penalty))
                .add(String.valueOf(populationSize))
                .add(String.valueOf(startChromosomeCount))
                .add(String.valueOf(startChromosomeLength))
                .add(String.valueOf(limitIterations))
                .add(String.valueOf(limitSteady))
                .add(String.valueOf(alterers))
                .add(String.valueOf(offspringSelector))
                .add(String.valueOf(survivorsSelector))
                .add(String.valueOf(route.getVisitedNodes()))
                .add(String.valueOf(route.getCorrectlyVisitedEdges()));

        try {
            List<String> lines = Collections.singletonList(stringJoiner.toString());
            Files.write(this.outFile.toPath(), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Route findBest() {
        this.startTime = System.currentTimeMillis();

        Engine.Builder<IntegerGene, Double> engineBuilder = Engine
                .builder(this::fitness, Route.code(this.nodes, this.edges, startChromosomeCount, startChromosomeLength))
                .minimizing()
                .alterers(
                        new RouteMutator<>(this.routeMutatorProbability, this.nodes.size()),
                        new SinglePointCrossover<>(this.singlePointCrossoverProbability)
                )
                .populationSize(this.populationSize).selector(new RouletteWheelSelector<>());

        Engine<IntegerGene, Double> engine = engineBuilder.build();

        this.alterers = engine.getAlterer().toString().replace("\n", "");
        this.offspringSelector = engine.getOffspringSelector().toString();
        this.survivorsSelector = engine.getSurvivorsSelector().toString();

        final EvolutionResult<IntegerGene, Double> result = engine.stream()
                .limit(bySteadyFitness(this.limitSteady))
                .limit(this.limitIterations)
                .collect(EvolutionResult.toBestEvolutionResult());
        Genotype<IntegerGene> genotype = result.getBestPhenotype().getGenotype();

        Route bestRoute = Route.code(this.nodes, this.edges, startChromosomeCount, startChromosomeLength).decoder().apply(genotype);
        toFile(bestRoute);
        return bestRoute;
    }


    @Override
    public String toString() {
        return "RouteFinder{" +
                "startChromosomeCount=" + startChromosomeCount +
                ", startChromosomeLength=" + startChromosomeLength +
                ", populationSize=" + populationSize +
                ", limitIterations=" + limitIterations +
                ", limitSteady=" + limitSteady +
                '}';
    }

}