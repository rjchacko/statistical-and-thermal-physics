package org.opensourcephysics.stp.ising.ising1d;
import org.opensourcephysics.display.*;
import org.opensourcephysics.display2d.*;
import java.awt.*;

public class Ising1D implements Drawable {
   public int [] spin;
   public int N;                 // Number of sites
   public double T;              // Temperature
   public double H;              // External magnetic field
   public double E;              // System energy
   public double E_acc;          // E accumulator
   public double E2_acc;         // E^2 accumulator
   public int M;                 // System magnetization
   public double M_acc;          // M accumulator
   public double M2_acc;         // M^2 accumulator
   
   public int mcs;               // Number of MC moves per spin
   public int acceptedMoves;     // Used to determine acceptance ratio
   
   private CellLattice lattice;  // Used only for drawing 
   
   
   public void initialize(int _N, double _T, double _H) {
      N = _N;
      T = _T;
      H = _H;
      
      lattice = new CellLattice(N, 1);
      lattice.setIndexedColor(0, Color.red);
      lattice.setIndexedColor(2, Color.green);
      
      // all spins up
      spin = new int[N];
      for (int i = 0; i < N; ++i)
         spin[i] = 1;
      
      M = N;
      E = - N - H*M;
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
      return (E2_avg - E_avg*E_avg) / (T*T*N*N);
   }

   public double susceptibility() {
      double M2_avg = M2_acc/mcs;
      double M_avg = M_acc / mcs;
      return (M2_avg - M_avg*M_avg) / (T*N*N);
   }
   
   public void resetData() {
      mcs = 0;
      E_acc = 0;
      E2_acc = 0;
      M_acc = 0;
      M2_acc = 0;
      acceptedMoves = 0;
   }

   public void doOneMCStep() {
      for (int k = 0; k < N; ++k) {
         int i = (int)(Math.random()*N);
         double dE = 2*spin[i]*(H + spin[(i+1)%N] + spin[(i-1+N)%N]);
         if (dE <= 0 || Math.random() < Math.exp(-dE/T)) {
            spin[i] = -spin[i];
            acceptedMoves++;
            E += dE;
            M += 2*spin[i];
         }
      }
      E_acc  += E;
      E2_acc += E*E;
      M_acc  += M;
      M2_acc += M*M;
      mcs++;
   }

   public void draw (DrawingPanel panel, Graphics g) {
      if(lattice==null) return;
      for(int i = 0; i < N; i++)
         lattice.setValue(i,0,(byte)(spin[i]+1));
      lattice.draw(panel,g);
   }
}
