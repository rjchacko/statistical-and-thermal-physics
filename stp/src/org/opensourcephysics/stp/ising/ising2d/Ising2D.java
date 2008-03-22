package org.opensourcephysics.stp.ising.ising2d;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import org.opensourcephysics.display.Drawable;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.CellLattice;

public class Ising2D implements Drawable {
   public static final double criticalTemperature = 2.0/Math.log(1.0 + Math.sqrt(2.0));
   
   public int [][] spin;
   public int L;
   public int N;                 // Number of sites
   public double J = 1.0;		// Interaction strength
   public double T;              // Temperature
   public double H;              // External magnetic field
   public double E;              // System energy
   public double E_acc;          // E accumulator
   public double E2_acc;         // E^2 accumulator
   public int M;                 // System magnetization
   public double M_acc;          // M accumulator
   public double absM_acc;   // |M| acumulator
   public double M2_acc;         // M^2 accumulator
   
   public int mcs;               // Number of MC moves per spin
   public int acceptedMoves;     // Used to determine acceptance ratio
   
   private CellLattice lattice;  // Used only for drawing 


   public void initialize(int _L, double _T, double _H) {
      L=_L;
      N = L*L;
      T = _T;
      H = _H;
         
      lattice = new CellLattice(L,L);         // only used for drawing 
      lattice.setIndexedColor(0, Color.red);   
      lattice.setIndexedColor(2, Color.green);
      
      // all spins up
      spin = new int[L][L];
      for (int i = 0; i < L; ++i)
         for (int j = 0; j < L; ++j)
            spin[i][j] = 1;

      M = N;
      E = -2*J*N - H*M;
      resetData();
   }
   
   public void setTemperature(double _T) {
      T = _T;
   }
   
   public void setExternalField(double _H) {
      E += H*M - _H*M;
      H = _H;
   }
   
   public double specificHeat() {
      double E2_avg = E2_acc / mcs;
      double E_avg = E_acc / mcs;
      return (E2_avg - E_avg*E_avg) / (T*T*N);
   }

   public double susceptibility() {
      double M2_avg = M2_acc/mcs;
      double M_avg = absM_acc / mcs;
      return (M2_avg - M_avg*M_avg) / (T*N);
   }

   public void resetData() {
      mcs = 0;
      E_acc = 0;
      E2_acc = 0;
      absM_acc=0;
      M_acc = 0;
      M2_acc = 0;
      acceptedMoves = 0;
   }

   public void doOneMCStep() {
      for (int k = 0; k < N; ++k) {
         int i = (int)(Math.random()*L);
         int j = (int)(Math.random()*L);
         double dE = 2*J*spin[i][j]*(H + spin[(i+1)%L][j] + spin[(i-1+L)%L][j] + spin[i][(j+1)%L] + spin[i][(j-1+L)%L]);
         if (dE <= 0 || Math.random() < Math.exp(-dE/T)) {
            spin[i][j] = -spin[i][j];
            acceptedMoves++;
            E += dE;
            M += 2*spin[i][j];
         }
      }
      accumulate_EM();
      mcs++;
   }
   public void doOneWolffStep(double bondProbability){
	   int wolffCluster[]=growWolffCluster(bondProbability);
	   int size=wolffCluster.length;
		for(int i=0;i<size;i++){
			int x=wolffCluster[i]%L;
			int y=wolffCluster[i]/L;				
			
			int spinDirection=spin[x][y];
			spin[x][y]*=-1;;
							
			double dE=2*spinDirection*(this.J*(this.spin[(x-1+L)%L][y]+this.spin[(x+1+L)%L][y]+this.spin[x][(y-1+L)%L]+this.spin[x][(y+1+L)%L]));		
			
			this.M += -2*spinDirection;
			this.E+=dE;
		}
		accumulate_EM();
		mcs++;
   }
   
   public int[] growWolffCluster(double bondProbability){
		Random r=new Random();
//		HashMap<Integer, Integer> wolffSpins=new HashMap<Integer, Integer>(100);
		int wolffSpins[]=new int[this.N];
		boolean inStack[]=new boolean[this.N];
		boolean visit[]=new boolean[this.N];	
		int stackSize=0;
		int stackPointer=0;
		int firstSpinX=r.nextInt(this.L);
		int firstSpinY=r.nextInt(this.L);
		int firstSpin=firstSpinX+this.L*firstSpinY;
		int direction=spin[firstSpinX][firstSpinY];	
		wolffSpins[stackPointer]=firstSpin;
		stackSize++;
		visit[firstSpin]=true;
		while(stackPointer<stackSize){		
			int currentSpin=wolffSpins[stackPointer];
			int neighborList[]=new int[4];
			int currentX=currentSpin%L;
			int currentY=currentSpin/L;
			neighborList[0]=currentX+((currentY-1+L)%L)*L;
			neighborList[1]=currentX+((currentY+1+L)%L)*L;
			neighborList[2]=(currentX-1+L)%L+currentY*L;
			neighborList[3]=(currentX+1+L)%L+currentY*L;
			visit[currentSpin]=true;
			int numAdded=0;
			for(int i=0;i<neighborList.length;i++){
				int nextSpin=neighborList[i];
				boolean visited=visit[nextSpin];
				boolean inSameDir=(spin[nextSpin%this.L][nextSpin/this.L]==direction);
				boolean occupyBond=(r.nextDouble()<bondProbability);
				if(!visited){
					if(inSameDir&&occupyBond){
							if(!inStack[nextSpin] ) {
								wolffSpins[stackSize]=nextSpin;
								inStack[nextSpin]=true;
								stackSize++;				
							}
							numAdded++;
					}
				}
			}
			stackPointer++;
		}

		return wolffSpins;
	}
   
   public void accumulate_EM(){
	   E_acc += E;
	   E2_acc += E*E;
	   M_acc +=M;
	   absM_acc += Math.abs(M);
	   M2_acc += M*M;
   }
   
   public void draw (DrawingPanel panel, Graphics g) {
      if(lattice==null) return;
      for(int i = 0; i < L; i++)
         for(int j = 0; j < L; j++)
            lattice.setValue(i,j,(byte)(spin[i][j]+1));
      lattice.draw(panel,g);
   }
}
