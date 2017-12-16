package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.internal.util.IntRef;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.jenetics.internal.math.random.indexes;

public final class RouteMutator<
        G extends Gene<?, G>,
        C extends Comparable<? super C>
        >
        extends AbstractAlterer<G, C> {

    private int nodesLength;

    public RouteMutator(double probability, int nodesLength) {
        super(probability);
        this.nodesLength = nodesLength;
    }

    @Override
    public int alter(
            final Population<G, C> population,
            final long generation
    ) {
        final IntRef alterations = new IntRef(0);

        indexes(RandomRegistry.getRandom(), population.size(), _probability).forEach(i -> {
            final Phenotype<G, C> pt = population.get(i);

            final Genotype<G> gt = pt.getGenotype();
            final Genotype<G> mgt = mutate(gt, _probability, alterations);

            final Phenotype<G, C> mpt = pt.newInstance(mgt, generation);
            population.set(i, mpt);
        });

        return alterations.value;
    }

    private Genotype<G> mutate(
            final Genotype<G> genotype,
            final double p,
            final IntRef alterations
    ) {
        final List<Chromosome<G>> chromosomes =
                new ArrayList<>(genotype.toSeq().asList());

        // Add/remove Chromosome to Genotype.
        final Random random = RandomRegistry.getRandom();
        final double rd = random.nextDouble();

        int randomPosition = ThreadLocalRandom.current().nextInt(0, genotype.length());
        if (rd < 1 / 3.0 && chromosomes.size() > 1) {
            chromosomes.remove(randomPosition);
        } else if (rd < 2 / 3.0) {
            chromosomes.add(randomPosition,chromosomes.get(0).newInstance());
        }

        alterations.value +=
                indexes(RandomRegistry.getRandom(), chromosomes.size(), p)
                        .map(i -> mutate(chromosomes, i, p))
                        .sum();

        return Genotype.of(chromosomes);
    }

    private int mutate(final List<Chromosome<G>> c, final int i, final double p) {
        final Chromosome<G> chromosome = c.get(i);
        final List<G> genes = new ArrayList<>(chromosome.toSeq().asList());
        final int mutations = mutate(genes, p);
        if (mutations > 0) {
            c.set(i, chromosome.newInstance(ISeq.of(genes)));
        }
        return mutations;
    }

    private int mutate(final List<G> genes, final double p) {
        final Random random = RandomRegistry.getRandom();

        // Add/remove Gene from chromosome.
        final double rd = random.nextDouble();

        int randomPosition = ThreadLocalRandom.current().nextInt(0, genes.size());
        if (rd < 1 / 3.0) {
            genes.remove(randomPosition);
        } else if (rd < 2 / 3.0) {
            genes.add(randomPosition,genes.get(randomPosition).newInstance());
        }

        return (int) indexes(random, genes.size(), p)
                .peek(i -> genes.set(i, genes.get(i).newInstance()))
                .count();
    }

    @Override
    public String toString() {
        return "RouteMutator[" +
                "p=" + _probability +
                ']';
    }
}