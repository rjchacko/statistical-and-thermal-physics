package org.opensourcephysics.stp.randomwalk.saw;

/**
  * Flat histogram method for self avoiding walkers
  * @author Jan Tobochnik 
  * 7/14/06
  */

public class Flat {
  double xAccum, xSquaredAccum; // accumulated data on displacement of walkers, index is time
  double yAccum, ySquaredAccum; // accumulated data on displacement of walkers, index is time
  double norm;
  int numberOfWalkers; // not fixed
  OneWalker[] walker;
  int remove[];
  int maxWalkers;
  
  
  public void initialize() {
    xAccum = 0;
    yAccum = 0;
    xSquaredAccum = 0;
    ySquaredAccum = 0;
    norm = 0;
    maxWalkers = (int)(numberOfWalkers*5); 
    walker = new OneWalker[maxWalkers]; 
    remove = new int[maxWalkers]; 
    for(int i = 0; i < numberOfWalkers; i ++) {
       walker[i] = new OneWalker();
       walker[i].newWalker(this);
    }
  }
  
  public void step() {
    xAccum = 0;
    yAccum = 0;
    xSquaredAccum = 0;
    ySquaredAccum = 0;
    norm = 0;
    int numberToRemove = 0;
    for(int i = 0; i < numberOfWalkers; i ++) {
       walker[i].step(this);
    }
    int currentNumberOfWalkers = numberOfWalkers;
    for(int i = 0; i < currentNumberOfWalkers; i++) {
       double r = currentNumberOfWalkers*walker[i].weight/norm;
       if(r > 1) {
          int numberOfCopies = (int)r;
          if(walker[i].m < numberOfCopies) numberOfCopies = walker[i].m; 
          double newWeight = walker[i].weight/numberOfCopies;
          for(int j = 0; j < numberOfCopies;j++) {
            if(numberOfWalkers < maxWalkers) {
              walker[numberOfWalkers] = new OneWalker();
              walker[numberOfWalkers].copyWalker(walker[i],newWeight);
              numberOfWalkers++;
            }
          }
        }
        else if(Math.random() < 1-r){  // will eliminate walker
           remove[numberToRemove] = i; // make list of walkers to remove
           numberToRemove++;
        }
    }
    for(int k = 0; k <  numberToRemove; k++) {
         numberOfWalkers--;
         walker[remove[k]] = walker[numberOfWalkers];  // move walkers at end of array to removed walker spots
    }   
  }           
        
}
    
