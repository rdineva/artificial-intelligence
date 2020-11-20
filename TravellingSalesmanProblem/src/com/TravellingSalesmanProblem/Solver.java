package com.TravellingSalesmanProblem;

import java.util.Arrays;
import java.util.Random;

public class Solver {
  public int genesCount, k;
  public Random random = new Random();
  Population population;

  public Solver(int n) {
    this.genesCount = n;
    this.k = n/3;
    if (this.k % 2 != 0) this.k--;
    this.population = new Population(n);
  }

  public void start() {
    long iter = 1;

    while(iter <= this.genesCount * 100) {
      System.out.printf("%d generation fittest with fitness: %n", iter);
      Individual fittest = this.population.fittest();
      fittest.print();

      search();
      iter++;
    }

    System.out.printf("%d generation fittest with fitness: %n", iter);
    this.population.fittest().print();
  }

  public void search() {
    Individual[] parents = this.getFittestParents();
    Individual[] children = new Individual[this.k];

    for(int i = 0; i < this.k - 1; i+=2) {
      Individual[] crossoverChildren = this.crossover(parents[i], parents[i + 1]);

      crossoverChildren[0] = this.mutate(crossoverChildren[0]);
      crossoverChildren[1] = this.mutate(crossoverChildren[1]);

      crossoverChildren[0].setFitness();
      crossoverChildren[1].setFitness();

      children[i] = crossoverChildren[0];
      children[i + 1] = crossoverChildren[1];
    }

    this.survivorSelection(children);
  }

  public Individual[] getFittestParents() {
    Individual[] parents = new Individual[this.k];

    for(int i = 0; i < this.k; i++) {
      Individual mostFitParent = this.findFittestIndividual();

      while(Arrays.asList(parents).contains(mostFitParent)) {
        mostFitParent = this.findFittestIndividual();
      }

      parents[i] = mostFitParent;
    }

    return parents;
  }

  public Individual[] getUnfitParents() {
    Individual[] parents = new Individual[this.k];

    for(int i = 0; i < this.k; i++) {
      Individual unfitParent = this.findUnfitIndividual();

      while(Arrays.asList(parents).contains(unfitParent)) {
        unfitParent = this.findUnfitIndividual();
      }

      parents[i] = unfitParent;
    }

    return parents;
  }

  public void survivorSelection(Individual[] children) {
    Individual[] unfitParents = this.getUnfitParents();

    for(int i = 0; i < this.k; i++) {
      int parentIndex = this.population.individuals.indexOf(unfitParents[i]);
      this.population.individuals.set(parentIndex, children[i]);
    }
  }

  public Individual mutate(Individual individual) {
    int firstGeneIndex = this.generateRandomNumber(this.genesCount);
    int secondGeneIndex = this.generateRandomNumber(this.genesCount);

    while(secondGeneIndex == firstGeneIndex) {
      secondGeneIndex = this.generateRandomNumber(this.genesCount);
    }

    individual.swapGenes(firstGeneIndex, secondGeneIndex);
    return individual;
  }

  public Individual[] crossover(Individual firstParent, Individual secondParent) {
    Individual firstChild = new Individual(this.genesCount);
    Individual secondChild = new Individual(this.genesCount);

    int firstRandomPoint = this.generateRandomNumber(this.genesCount);
    while(firstRandomPoint == 0 || firstRandomPoint == this.genesCount - 1) {
      firstRandomPoint = this.generateRandomNumber(this.genesCount);
    }

    int secondRandomPoint = this.generateRandomNumber(this.genesCount);
    while(secondRandomPoint == 0 || secondRandomPoint == this.genesCount - 1 || secondRandomPoint == firstRandomPoint) {
      secondRandomPoint = this.generateRandomNumber(this.genesCount);
    }

    int firstCrossoverPoint = Math.min(firstRandomPoint, secondRandomPoint);
    int secondCrossoverPoint = Math.max(firstRandomPoint, secondRandomPoint);

    for(int i = firstCrossoverPoint; i <= secondCrossoverPoint; i++) {
      firstChild.chromosome[i] = secondParent.chromosome[i];
      secondChild.chromosome[i] = firstParent.chromosome[i];
    }

    firstChild = this.setChild(firstChild, secondParent, secondCrossoverPoint);
    secondChild = this.setChild(secondChild, firstParent, secondCrossoverPoint);

    return new Individual[] { firstChild, secondChild };
  }

  public Individual setChild(Individual child, Individual parent, int secondCrossoverPoint) {
    int parentIndex = secondCrossoverPoint + 1;
    int childIndex = secondCrossoverPoint + 1;

    while(parentIndex != secondCrossoverPoint) {
      if (childIndex == this.genesCount) childIndex = 0;
      if (parentIndex == this.genesCount) parentIndex = 0;

      Gene gene = parent.chromosome[parentIndex];
      if (Arrays.asList(child.chromosome).contains(gene)) {
        parentIndex = this.updateIndex(parentIndex);
        continue;
      }

      child.chromosome[childIndex] = parent.chromosome[parentIndex];
      parentIndex = this.updateIndex(parentIndex);
      childIndex = this.updateIndex(childIndex);
    }

    return child;
  }

  public int updateIndex(int index) {
    index++;
    if (index >= this.genesCount) {
      index = 0;
    }

    return index;
  }

  public Individual findUnfitIndividual() {
    Individual[] randomIndividuals = this.population.selectRandomIndividuals(this.k);
    Individual mostUnfit = randomIndividuals[0];

    for(int i = 1; i < this.k; i++) {
      Individual currentIndividual = randomIndividuals[i];

      if (currentIndividual.fitness > mostUnfit.fitness) {
        mostUnfit = currentIndividual;
      }
    }

    return mostUnfit;
  }

  public Individual findFittestIndividual() {
    Individual[] randomIndividuals = this.population.selectRandomIndividuals(this.k);
    Individual fittest = randomIndividuals[0];

    for(int i = 1; i < this.k; i++) {
      Individual currentIndividual = randomIndividuals[i];

      if (currentIndividual.fitness < fittest.fitness) {
        fittest = currentIndividual;
      }
    }

    return fittest;
  }

  private int generateRandomNumber(int max) {
    return this.random.nextInt(max);
  }
}
