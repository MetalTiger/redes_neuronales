package com.ch6.tsp;

import neural.exception.NeuralNetworkError;
import neural.genetic.Chromosome;

/**
 * Chapter 6: Training using a Genetic Algorithm
 *
 * TSPChromosome: A chromosome that is used to attempt to solve the
 * traveling salesman problem.  A chromosome is a list of cities.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class TSPChromosome extends Chromosome<Integer, TSPGeneticAlgorithm> {

    protected City[] cities;

    TSPChromosome(final TSPGeneticAlgorithm owner, final City[] cities) {
        this.setGeneticAlgorithm(owner);
        this.cities = cities;

        final Integer[] genes = new Integer[this.cities.length];
        final boolean[] taken = new boolean[cities.length];

        for (int i = 0; i < genes.length; i++) {
            taken[i] = false;
        }
        for (int i = 0; i < genes.length - 1; i++) {
            int icandidate;
            do {
                icandidate = (int) (Math.random() * genes.length);
            } while (taken[icandidate]);
            genes[i] = icandidate;
            taken[icandidate] = true;
            if (i == genes.length - 2) {
                icandidate = 0;
                while (taken[icandidate]) {
                    icandidate++;
                }
                genes[i + 1] = icandidate;
            }
        }
        setGenes(genes);
        calculateCost();

    }

    @Override
    public void calculateCost() throws NeuralNetworkError {

        double cost = 0.0;

        for (int i = 0; i < this.cities.length - 1; i++) {

            final double dist = this.cities[getGene(i)].proximity(this.cities[getGene(i + 1)]);

            cost += dist;

        }

        setCost(cost);

    }

    @Override
    public void mutate() {
        final int length = this.getGenes().length;
        final int iswap1 = (int) (Math.random() * length);
        final int iswap2 = (int) (Math.random() * length);
        final Integer temp = getGene(iswap1);
        setGene(iswap1, getGene(iswap2));
        setGene(iswap2, temp);
    }

}
