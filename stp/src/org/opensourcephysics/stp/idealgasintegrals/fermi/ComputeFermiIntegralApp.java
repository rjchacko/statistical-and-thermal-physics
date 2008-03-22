package org.opensourcephysics.stp.idealgasintegrals.fermi;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.numerics.*;

/**
 * ComputeFermiIntegralApp class.
 *
 *  @author Hui Wang
 *  @version 1.0   revised 07/19/06
 */
public class ComputeFermiIntegralApp extends AbstractCalculation implements Function{
	PlotFrame plotFrame = new PlotFrame("T*", "\u03bc*", "");	//��
	PlotFrame energyFrame = new PlotFrame("T*", "e", "");	//��

	double b = 1.0;
	double T = 1.0;
	double mu = 1.0;
	double rho = 0.5;
	double x_low = 0;
	double x_high = 1e2;
	int npoints = 0;
	double[] mua, Ta,Ea;
	double tolerance = 0.01;
	int nmaxTrials = 1000;
	ComputeFermiIntegralApp(){
		mua = new double[nmaxTrials];
		Ta = new double[nmaxTrials];
		Ea = new double[nmaxTrials];
	}
   public void reset() {
	   control.setValue("T*", 1.0);   
	   control.setValue("\u03bc*", 1.0);	//mu
   }

   public double evaluate(double x){
	   return 1.5*Math.sqrt(x)/(Math.exp(b*(x - mu)) + 1);
   }
   public void calculate() {

      mu = control.getDouble("\u03bc*");      
      T = control.getDouble("T*");
      b = 1.0/T;
      rho = Integral.simpson(this, x_low, x_high, 2, tolerance);      
      
      control.println();
      control.println("\u03bc* = " + mu);	//mu
      //control.println("Calculated \u03c1 = " + rho);	//rho
      control.println("Calculated Integral = " + rho);	//rho
      
     plot();
   }

   public double calculateE(double m, double t){
	   FermiEnergyIntegral f = new FermiEnergyIntegral();
	   f.b = 1/t;
	   f.mu = m;
	   double e = Integral.simpson(f, x_low, x_high, 2, tolerance);
	   return e;
   }
   public void accept(){
	   mua[npoints] = mu;
	   Ta[npoints] = T;
	   Ea[npoints] = calculateE(mu,T);
	   npoints++;
	   if(npoints >= nmaxTrials){
		   control.print("Too many points");
		   System.exit(-1);
	   }
	   plot();
   }
   public void plot(){
	   for(int i = 0; i < npoints; i++){
		   plotFrame.append(2,Ta[i],mua[i]);
		   energyFrame.append(2,Ta[i],Ea[i]);
	   }
	   plotFrame.render();
	   energyFrame.render();
   }
   public static void main(String[] args) {
	   CalculationControl control = CalculationControl.createApp(new ComputeFermiIntegralApp());
	   control.addButton("accept", "Accept parameters");
   }
}

class FermiEnergyIntegral implements Function{
	double b,mu;
	public double evaluate(double x){
		return 1.5*Math.sqrt(x*x*x)/(Math.exp(b*(x - mu)) + 1);
	}
	
}