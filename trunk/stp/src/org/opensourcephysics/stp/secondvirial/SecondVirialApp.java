package org.opensourcephysics.stp.secondvirial;

import org.opensourcephysics.controls.AbstractCalculation;
import org.opensourcephysics.controls.CalculationControl;
import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.numerics.Function;
import org.opensourcephysics.numerics.Integral;

public class SecondVirialApp extends AbstractCalculation implements Function{
	PlotFrame pf=new PlotFrame("T","B_2","Second Virial Coefficient");
	double Tmin,Tmax,T;
	public static void main(String[] args) {
		CalculationControl control = CalculationControl.createApp(new SecondVirialApp(),args);
	}
    
	public void reset() {
	    control.setValue("Tmin", 0.0);	
	    control.setValue("Tmax", 100.0);   
	    pf.clearData();
    }
	
	public void initialize(){
		Tmin=control.getDouble("Tmin");
		Tmax=control.getDouble("Tmax");
		pf.clearData();
	}
	
	public void calculate() {
		double x_low = 0.00001, x_high = 10, tolerance = 0.0001;
		initialize();
		for(int i=0;i<Tmax;i++){
			T=i;
			double B2=Integral.simpson(this, x_low, x_high, 20, tolerance);
			pf.append(0, T, B2);
		}
	}
	
	public double evaluate(double x) {
		double r2=x*x;
		double oneOverR2 = 1.0/r2;
        double oneOverR6 = oneOverR2*oneOverR2*oneOverR2;  
        double z=Math.exp(-(1/T)*4.0*(oneOverR6*oneOverR6-oneOverR6));
		return 2*Math.PI*(1-z);
	}

}
