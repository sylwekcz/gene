package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.apache.commons.cli.*;
import org.jenetics.*;
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

    private int populationSize = 100;
    private int startChromosomeCount = 10;
    private int startChromosomeLength = 10;
    private long limitIterations = 100_000_000_000L;
    private int limitSteady = 100_000_000;
    private double probabilityOfMutation = 0.1;
    private int penaltyMultiplier = 100;
    private double penalty;
    private String alterers = "";
    private String offspringSelector = "";
    private String survivorsSelector = "";


    private double prevBestScore = Double.MAX_VALUE;
    private long startTime = -1;

    public RouteFinder(FileContent fileContent) {
        this.edges = fileContent.getEdges();
        this.nodes = fileContent.getNodes();

        try {
            new File("results").mkdirs();
            this.outFile = new File("results/nodes-" + this.nodes.size() + "-edges-" + this.edges.size() + "-time-" + System.currentTimeMillis() + ".csv");
            this.outFile.createNewFile();
            List<String> lines = Collections.singletonList("nodes;edges;time[s];weight;score;penalty;populationSize;startChromosomeCount;startChromosomeLength;"
                    + "limitIterations;limitSteady;alterers;offspringSelector;survivorsSelector;routeNodes;routeEdges");
            Files.write(this.outFile.toPath(), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
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
        long time = System.currentTimeMillis() - startTime;
        double score = route.getWeight();
        if (!route.isValid()) {
            score += (route.getNotVisitedEdges() * penalty);
            score += (route.getIncorrectEdges() * penalty);
            if (!route.isStartingAndEndingOnSameNode()) {
                score += penalty * 2;
            }
        } else {
            if (prevBestScore > score) {
                prevBestScore = score;
                StringJoiner stringJoiner = new StringJoiner(";");
                stringJoiner
                        .add(String.valueOf(this.nodes.size()))
                        .add(String.valueOf(this.edges.size()))
                        .add(String.valueOf(time/ 1000.0))
                        .add(String.valueOf(route.getWeight()))
                        .add(String.valueOf(score))
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
                    System.out.println(lines);
                    Files.write(this.outFile.toPath(), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        if (prevBestScore > score) {
            prevBestScore = score;
            System.out.println(time + " " + score + " " + route);
        }
        return score;
    }

    public Route findBest() {
        startTime = System.currentTimeMillis();


        final Engine<IntegerGene, Double> engine = Engine
                .builder(this::fitness, Route.code(this.nodes, this.edges, startChromosomeCount, startChromosomeLength))
                .minimizing()
                .alterers(
                        new RouteMutator<>(this.probabilityOfMutation, this.nodes.size())
                        , new SwapMutator<>()
                        , new SinglePointCrossover<>()
                )
                .populationSize(this.populationSize)
                .selector(new RouletteWheelSelector<>())
                .build();

        this.alterers = engine.getAlterer().toString().replace("\n", "");
        this.offspringSelector = engine.getOffspringSelector().toString();
        this.survivorsSelector = engine.getSurvivorsSelector().toString();

        final EvolutionResult<IntegerGene, Double> result = engine.stream()
                .limit(bySteadyFitness(this.limitSteady))
                .limit(this.limitIterations)
                .collect(EvolutionResult.toBestEvolutionResult());
        Genotype<IntegerGene> genotype = result.getBestPhenotype().getGenotype();


        return Route.code(this.nodes, this.edges, startChromosomeCount, startChromosomeLength).decoder().apply(genotype);
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


    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }
        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");
        System.out.println(inputFilePath);
        System.out.println(outputFilePath);
    }
}