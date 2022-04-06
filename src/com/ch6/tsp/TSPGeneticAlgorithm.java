package com.ch6.tsp;

import neural.exception.NeuralNetworkError;
import neural.genetic.GeneticAlgorithm;

/**
 * Chapter 6: Training using a Genetic Algorithm
 *
 * TSPGeneticAlgorithm: An implementation of the genetic algorithm that
 * attempts to solve the traveling salesman problem.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class TSPGeneticAlgorithm extends GeneticAlgorithm<TSPChromosome> {

    public TSPGeneticAlgorithm(final City[] cities, final int populationSize,
                               final double mutationPercent, final double percentToMate,
                               final double matingPopulationPercent, final int cutLength)
            throws NeuralNetworkError {
        this.setMutationPercent(mutationPercent);
        this.setMatingPopulation(matingPopulationPercent);
        this.setPopulationSize(populationSize);
        this.setPercentToMate(percentToMate);
        this.setCutLength(cutLength);
        this.setPreventRepeat(true);

        setChromosomes(new TSPChromosome[getPopulationSize()]);
        for (int i = 0; i < getChromosomes().length; i++) {

            final TSPChromosome c = new TSPChromosome(this, cities);
            setChromosome(i, c);
        }
        sortChromosomes();
    }

}
