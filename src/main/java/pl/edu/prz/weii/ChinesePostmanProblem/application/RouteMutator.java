package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.jenetics.*;
import org.jenetics.internal.util.IntRef;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        // mutate random population
        indexes(RandomRegistry.getRandom(), population.size(), _probability).forEach(i -> {
            final Phenotype<G, C> pt = population.get(i);
            final Genotype<G> gt = pt.getGenotype();
            final Genotype<G> mgt = mutate(gt, alterations);

            final Phenotype<G, C> mpt = pt.newInstance(mgt, generation);
            population.set(i, mpt);
        });
        return alterations.value;
    }

    // mutate genotype
    private Genotype<G> mutate(
            final Genotype<G> genotype,
            final IntRef alterations
    ) {
        final List<Chromosome<G>> chromosomes =
                new ArrayList<>(genotype.toSeq().asList());

        if (chromosomes.size() != 1) {
            throw new IllegalArgumentException("Should only have one chromosome!");
        } else {

            final List<G> genes = new ArrayList<>(chromosomes.get(0).toSeq().asList());
            alterations.value = mutate(genes);
            chromosomes.set(0, chromosomes.get(0).newInstance(ISeq.of(genes)));
        }
        return Genotype.of(chromosomes);
    }

    // mutate chromosome genes
    private int mutate(final List<G> genes) {
        final Random random = RandomRegistry.getRandom();

        // Add/remove Gene from chromosome.
        final double rd = random.nextDouble();

        double shrinkHelp =  ((double)genes.size() / (this.nodesLength * this.nodesLength)) / 100;

        double shrinkProbability = _probability / 2.0 + shrinkHelp;
        double growProbability = _probability ;

        if (genes.size() < this.nodesLength) {
            shrinkProbability = 0.0;
            growProbability = 1.0;
        }
        if (rd < shrinkProbability) {
            genes.remove(0);
        } else if (rd < growProbability) {
            genes.add(genes.get(0).newInstance());
        }
        return (int) indexes(random, genes.size(), _probability)
                .peek(i -> genes.set(i, genes.get(i).newInstance()))
                .count();
    }

}