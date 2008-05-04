package org.opensourcephysics.stp.ljmc;

import java.awt.*;
import java.util.Random;

import org.opensourcephysics.display.*;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.numerics.*;

/**
 * LJParticlesApp evolves a two-dimensional system of interacting particles
 * via the Lennard-Jones potential using a Verlet ODESolver.
 *
 * @author Jan Tobochnik, Wolfgang Christian, Harvey Gould
 * @version 1.0 revised 03/28/05
 */
public class LJMC implements Drawable{
  public double x[];
  public double y[];
  public double pe;
  public int N, nx, ny; // number of particles, number per row, number per column
  public double Lx, Ly;
  public double rho = N/(Lx*Ly);
  public int steps = 0;
  public double t;
  public String initialConfiguration;
  public double radius = 0.5; // radius of particles on screen
  public double T;
  public double beta;
  public double stepSize;
  public int resolution=100;
  public double reS[];
  public double imS[];
  public double S[];
  Random r=new Random();
  
  public void initialize() {
    N = nx*ny;
    t = 0;
    rho = N/(Lx*Ly);
    beta=1/T;
    x = new double[N];
    y = new double[N];
    reS = new double[resolution];
    imS = new double[resolution];
    S = new double[resolution];
    
    if(initialConfiguration.equals("triangular")) {
      setTriangularLattice();
    } else if(initialConfiguration.equals("rectangular")) {
      setRectangularLattice();
    } else {
      setRandomPositions();
    }
    computePE();
  }

  public void setRandomPositions() { // particles placed at random, but not closer than rMinimumSquared
    double rMinimumSquared = Math.pow(2.0, 1.0/3.0);
    boolean overlap;
    for(int i = 0;i<N;++i) {
      do {
        overlap = false;
        x[i] = Lx*Math.random();   // x
        y[i] = Ly*Math.random(); // y
        int j = 0;
        while((j<i)&&!overlap) {
          double dx = x[i]-x[j];
          double dy = y[i]-y[j];
          if(dx*dx+dy*dy<rMinimumSquared) {
            overlap = true;
          }
          j++;
        }
      } while(overlap);
    }
  }

  // end break
  // start break
  // setRectangularLattice
  public void setRectangularLattice() { // place particles on a rectangular lattice
    double dx = Lx/nx; // distance between columns
    double dy = Ly/ny; // distance between rows
    for(int ix = 0;ix<nx;++ix) {   // loop through particles in a row
      for(int iy = 0;iy<ny;++iy) { // loop through rows
        int i = ix+iy*ny;
        x[i] = dx*(ix+0.5);
        y[i] = dy*(iy+0.5);
      }
    }
  }

  // end break
  // start break
  // setTriangularLattice
  public void setTriangularLattice() { // place particles on triangular lattice
    double dx = Lx/nx; // distance between particles on same row
    double dy = Ly/ny; // distance between rows
    for(int ix = 0;ix<nx;++ix) {
      for(int iy = 0;iy<ny;++iy) {
    	  	int i = ix+iy*ny;	
        y[i] = dy*(iy+0.5);
        if(iy%2==0) {
          x[i] = dx*(ix+0.25);
        } else {
          x[i] = dx*(ix+0.75);
        }
      }
    }
  }

 
  // end break
  // start break
  // computePE
  public void computePE() {
    for(int i = 0;i<N-1;i++) {
      for(int j = i+1;j<N;j++) {
        double dx = pbcSeparation(x[i]-x[j], Lx);
        double dy = pbcSeparation(y[i]-y[j], Ly);
        double r2 = dx*dx+dy*dy;
        double oneOverR2 = 1.0/r2;
        double oneOverR6 = oneOverR2*oneOverR2*oneOverR2;       
        pe+=4.0*(oneOverR6*oneOverR6-oneOverR6);
      }
    }
  }
  
  public double computeTrialPE() {
	  double trialPE=0; 
	  for(int i=0;i<N-1;i++){
		  for(int j=i+1;j<N;j++){
			 double dx = pbcSeparation(x[i]-x[j], Lx);
			 double dy = pbcSeparation(y[i]-y[j], Ly);
			 double r2 = dx*dx+dy*dy;
			 double oneOverR2 = 1.0/r2;
			 double oneOverR6 = oneOverR2*oneOverR2*oneOverR2;       
			 trialPE+=4.0*(oneOverR6*oneOverR6-oneOverR6); 
		  }
	  }
	  return trialPE;
  }

  // end break
  // start break
  // pbcSeparation
  private double pbcSeparation(double ds, double L) {
    if(ds>0) {
      while(ds>0.5*L) {
        ds -= L;
      }
    } else {
      while(ds<-0.5*L) {
        ds += L;
      }
    }
    return ds;
  }


  private double pbcPosition(double s, double L) {
    if(s>0) {
      while(s>L) {
        s -= L;
      }
    } else {
      while(s<0) {
        s += L;
      }
    }
    return s;
  }

  public void perturbParticles() {
	  double dx[],dy[];
	  dx = new double[N];
	  dy = new double[N];
	  for(int i=0;i<N;i++){
		  dx[i]=0;
		  dy[i]=0;
	  }
      for (int iter = 0; iter < N/10; iter++) {
         // Find an arbitrary perturbation
         int n = r.nextInt(N);       // Random particle index
         dx[n]=2*stepSize*(Math.random()-0.5);
         dy[n]=2*stepSize*(Math.random()-0.5);
         x[n]=x[n]+dx[n];
	 	 y[n]=y[n]+dy[n];
      }
      double newPE=computeTrialPE();
      double dE=newPE-pe;
      if( dE<0 || Math.exp(-dE/T)>Math.random() ) pe=newPE;
      else{
    	  	for(int i=0;i<N;i++){
    	  		x[i]=x[i]-dx[i];
    	  		y[i]=y[i]-dy[i];
    	  	}
      }
   }
  
  public void calcStructureFunction(){
	  double kmax=2;
	  double kmin=2*Math.PI/Lx;
	  double dx, dy;
	  reS = new double[resolution];
	  imS = new double[resolution];
	  S = new double[resolution];
	  
	  for(int k=0;k<resolution;k++){
		  double K=k*(kmax/resolution)+kmin;
		  for(int i=0; i<N; i++){
			  for(int j=0; j<N; j++){
				  for(double theta=0; theta<1; theta+=2*Math.PI/100.0){
					  double kx1 = K*(x[i]*Math.cos(theta)+y[i]*Math.sin(theta));
					  double kx2 = K*(x[j]*Math.cos(theta)+y[j]*Math.sin(theta));
					  reS[k] += Math.cos(kx1)*Math.cos(kx2)+Math.sin(kx1)*Math.sin(kx2);
					  imS[k] += Math.sin(kx1)*Math.cos(kx2)-Math.sin(kx2)*Math.cos(kx1);
				  }			  
			  }
		  }
	  }
	  
	  for(int k=0; k<resolution; k++){
		 S[k]=Math.sqrt(reS[k]*reS[k]+imS[k]*imS[k]); 
	  }
	  
  }
  
  public void step() {
   
    perturbParticles();
    
    for(int i = 0;i<N;i++) {
      x[i] = pbcPosition(x[i], Lx);
      y[i] = pbcPosition(y[i], Ly);
    }
    
 //   if(steps%10000==0) calcStructureFunction();
    steps++;
    
  }

  // end break
  // start break
  // draw
  public void draw(DrawingPanel panel, Graphics g) {
    if(x==null||y==null) {
      return;
    }
    int pxRadius = Math.abs(panel.xToPix(radius)-panel.xToPix(0));
    int pyRadius = Math.abs(panel.yToPix(radius)-panel.yToPix(0));
    
    g.setColor(Color.red);
    for(int i = 0;i<N;i++) {
      int xpix = panel.xToPix(x[i])-pxRadius;
      int ypix = panel.yToPix(y[i])-pyRadius;
      g.fillOval(xpix, ypix, 2*pxRadius, 2*pyRadius);
    } // draw central cell boundary
    g.setColor(Color.black);
    int xpix = panel.xToPix(0);
    int ypix = panel.yToPix(Ly);
    int lx = panel.xToPix(Lx)-panel.xToPix(0);
    int ly = panel.yToPix(0)-panel.yToPix(Ly);
    g.drawRect(xpix, ypix, lx, ly);
  }
  // end break
}

/* 
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
