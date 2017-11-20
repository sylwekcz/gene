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

        if (chromosomes.size() != 1) {
            throw new IllegalArgumentException("Should only be one chromosome!");
        } else {
            alterations.value = mutate(chromosomes, 0, p);
        }
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

        double shrinkHelp =  ((double)genes.size() / (this.nodesLength * this.nodesLength)) / 100;

        double shrinkProbability = p / 2.0 + shrinkHelp;
        double growProbability = p ;

        if (genes.size() < this.nodesLength) {
            shrinkProbability = 0.0;
            growProbability = 1.0;
        } else {

        }

        if (rd < shrinkProbability) {
            genes.remove(0);
        } else if (rd < growProbability) {
            genes.add(genes.get(0).newInstance());
        }

        // Change random Gene
        return (int) indexes(random, genes.size(), p)
                .peek(i -> genes.set(i, genes.get(i).newInstance()))
                .count();
    }

}