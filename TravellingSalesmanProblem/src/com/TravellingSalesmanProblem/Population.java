package com.TravellingSalesmanProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Population {

  public int genesCount, populationCount;
  public Random random = new Random();
  public Gene[] genes;
  ArrayList<Individual> individuals;

  public Population(int genesCount) {
    this.genesCount = genesCount;
    this.populationCount = this.genesCount * 2;
    this.generateGenes();
    this.generateInitialPopulation();
  }

  public Individual fittest() {
    int fittestIndex = 0;
    for (int i = 0; i < this.populationCount; i++) {
      if (this.individuals.get(i).fitness < this.individuals.get(fittestIndex).fitness) fittestIndex = i;
    }

    return this.individuals.get(fittestIndex);
  }

  public Individual mostUnfit() {
    int fittestIndex = 0;
    for (int i = 0; i < this.populationCount; i++) {
      if (this.individuals.get(i).fitness > this.individuals.get(fittestIndex).fitness) fittestIndex = i;
    }

    return this.individuals.get(fittestIndex);
  }

  private void generateGenes() {
    this.genes = new Gene[this.genesCount];

    for(int i = 0; i < this.genesCount; i++) {
      int randomX = this.generateRandomNumber(100);
      int randomY = this.generateRandomNumber(100);
      this.genes[i] = new Gene(randomX, randomY);
    }
  }

  private int generateRandomNumber(int max) {
    return this.random.nextInt(max);
  }

  public void generateInitialPopulation() {
    this.individuals = new ArrayList<>();

    for(int i = 0; i < this.populationCount; i++) {
      Individual randomIndividual = this.generateIndividual();

      while(this.individuals.contains(randomIndividual)) {
        randomIndividual = this.generateIndividual();
      }

      this.individuals.add(randomIndividual);
    }
  }

  public Individual generateIndividual() {
    Gene[] chromosome = new Gene[this.genesCount];

    for(int i = 0; i < this.genesCount; i++) {
      int randomIndex = this.generateRandomNumber(this.genesCount);
      while(chromosome[randomIndex] != null) {
        randomIndex = this.generateRandomNumber(this.genesCount);
      }

      chromosome[randomIndex] = this.genes[i];
    }

    return new Individual(chromosome);
  }

  public Individual[] selectRandomIndividuals(int count) {
    Individual[] randomIndividuals = new Individual[count];

    for(int i = 0; i < count; i++) {
      int randomIndex = this.generateRandomNumber(this.populationCount);
      while (Arrays.asList(randomIndividuals).contains(this.individuals.get(randomIndex))) {
        randomIndex = this.generateRandomNumber(this.populationCount);
      }

      randomIndividuals[i] = this.individuals.get(randomIndex);
    }

    return randomIndividuals;
  }

  public void print() {
    System.out.println("Population:");
    for(int i = 0; i < this.populationCount; i++) {
      this.individuals.get(i).print();
    }

    System.out.println();
  }
}
