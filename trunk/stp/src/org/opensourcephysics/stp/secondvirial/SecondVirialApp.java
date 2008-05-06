package org.opensourcephysics.stp.secondvirial;

import org.opensourcephysics.controls.AbstractCalculation;
import org.opensourcephysics.controls.CalculationControl;
import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.numerics.Function;
import org.opensourcephysics.numerics.Integral;

public class SecondVirialApp extends AbstractCalculation implements Function{
	PlotFrame pf=new PlotFrame("T","B_2","Second Virial Coefficient");
	double Tmin,Tmax,T;
	int points;
	public static void main(String[] args) {
		CalculationControl control = CalculationControl.createApp(new SecondVirialApp(),args);
	}
    
	public void reset() {
	    control.setValue("Tmin", 0.0);	
	    control.setValue("Tmax", 100.0);   
	    control.setValue("Number of Points", 100);	
    }
	
	public void initialize(){
		points=control.getInt("Number of Points");
		Tmin=control.getDouble("Tmin");
		Tmax=control.getDouble("Tmax");
		pf.clearData();
	}
	
	public void calculate() {
		double x_low = 0.0001, x_high = 10, tolerance = 0.00001;
		initialize();
		System.out.println("beginning calculation");
		for(int i=0;i<points;i++){
			T=Tmin+(Tmax-Tmin)*i/points;
			double B2=Integral.simpson(this, x_low, x_high, 20, tolerance);
			//double B2=Integral.ode(this, x_low, x_high, tolerance);
			//double B2=Integral.romberg(this, x_low, x_high, 10, tolerance);
			pf.append(0, T, B2);
		}
	}
	
	public double evaluate(double x) {
		double r2=x*x;
		double oneOverR2 = 1.0/r2;
        double oneOverR6 = oneOverR2*oneOverR2*oneOverR2;              
		return 2*Math.PI*(1-Math.exp(-(1/T)*4.0*(oneOverR6*oneOverR6-oneOverR6)));
	}

}
