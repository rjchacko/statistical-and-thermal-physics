package org.opensourcephysics.stp.idealgasintegrals.bose;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.numerics.*;

/**
 * ComputeBoseIntegralApp class.
 *
 *  @author Hui Wang
 *  @version 1.0   revised 07/19/06
 */
public class ComputeBoseIntegralApp extends AbstractCalculation implements Function{
	PlotFrame plotFrame = new PlotFrame("T", "\u03bc", "");	//mu

	double b = 1.0;
	double T = 1.0;
	double mu = -1.0;
	double rho = 0.5;
	double x_low = 0;
	double x_high = 1e3;
	int npoints = 0;
	double[] mua, Ta;
	double tolerance = 0.01;
	int nmaxTrials = 1000;
	
	ComputeBoseIntegralApp(){
		mua = new double[nmaxTrials];
		Ta = new double[nmaxTrials];
		plotFrame.setPreferredMinMaxX(0, 10);
		plotFrame.setAutoscaleX(true);
	}
   public void reset() {
	   control.setValue("\u03c1*", 1.0);	//rho
	   control.setValue("T", 1.0);   
	   control.setValue("\u03bc", -1.0);	//mu
   }

   public double evaluate(double x){
	   return Math.sqrt(x)/(Math.exp(b*(x - mu)) - 1);
   }
   public void calculate() {

      mu = control.getDouble("\u03bc");      
      T = control.getDouble("T");
      b = 1.0/T;
      rho = Integral.simpson(this, x_low, x_high, 2, tolerance);

      control.println();
      control.println("\u03bc = " + mu);	//mu
      control.println("Calculated \u03c1* = " + rho);	//rho
      
     plot();
   }

   public void accept(){
	   mua[npoints] = mu;
	   Ta[npoints] = T;
	   npoints++;
	   if(npoints > nmaxTrials){
		   control.print("Too many points");
		   System.exit(-1);
	   }
	   plot();
   }
   public void plot(){
	   for(int i = 0; i < npoints; i++){
		   plotFrame.append(2,Ta[i],mua[i]);
	   }
	   plotFrame.render();
   }
   public static void main(String[] args) {
	   CalculationControl control = CalculationControl.createApp(new ComputeBoseIntegralApp(),args);
	   control.addButton("accept", "Accept parameters");
   }
}
