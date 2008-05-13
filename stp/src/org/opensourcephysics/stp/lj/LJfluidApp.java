package org.opensourcephysics.stp.lj;

import java.text.DecimalFormat;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.stp.util.Rdf;
/**
 * Shows Lennard Jones interactions.
 *
 * @author Jan Tobochnik
 * @author Joshua Gould
 * @author Peter Sibley
 *
 */
public class LJfluidApp extends AbstractSimulation
{
	LJfluid lj;
	double quenchRate;
	
	DisplayFrame displayFrame = new DisplayFrame("LJ Display");
	HistogramFrame histogramFrame = new HistogramFrame("v_x", "H(v_x)",
			"H(v_x) versus v_x");
	PlotFrame temperatureFrame = new PlotFrame("time", "T", "T versus time");
	PlotFrame pressureFrame = new PlotFrame("time", "pressure",
			"Pressure versus time");
	PlotFrame grFrame = new PlotFrame("r", "g(r)", "Radial distribution function");

	DecimalFormat numberFormatTwoDigits = (DecimalFormat) DecimalFormat
			.getInstance();
	DecimalFormat numberFormatFourDigits = (DecimalFormat) DecimalFormat
			.getInstance();

	  Rdf gr = new Rdf();
	  
	public LJfluidApp()
	{
		numberFormatTwoDigits.setMaximumFractionDigits(2);
		numberFormatTwoDigits.setMinimumFractionDigits(2);
		numberFormatTwoDigits.setGroupingSize(100);
		// never show commas so we set grouping size to a big number.
		numberFormatFourDigits.setMaximumFractionDigits(4);
		numberFormatFourDigits.setMinimumFractionDigits(4);
		numberFormatFourDigits.setGroupingSize(100);
		lj = new LJfluid();
		displayFrame.addDrawable(lj);
		displayFrame.setPreferredMinMax(-0.1 * lj.Lx, 1.1 * lj.Lx,
				-0.1 * lj.Lx, 1.1 * lj.Lx);
		temperatureFrame.setPreferredMinMaxX(0, 10);
		temperatureFrame.setAutoscaleX(true);
		temperatureFrame.setPreferredMinMaxY(0, 2);
		
		pressureFrame.setPreferredMinMaxX(0, 10);
		pressureFrame.setAutoscaleX(true);
		pressureFrame.setPreferredMinMaxY(0, 2);
		
		histogramFrame.setAutoscaleX(true);
		histogramFrame.setAutoscaleY(true);
		histogramFrame.addDrawable(lj.getVelocityHistogram());
		
		grFrame.setPreferredMinMaxX(0, 10);
		grFrame.setAutoscaleX(true);
		grFrame.setPreferredMinMaxY(0, 10);
		grFrame.setAutoscaleY(true);
	}

	public void initialize()
	{
		lj.N = control.getInt("N");
		lj.initialKineticEnergy = control
				.getDouble("initial kinetic energy per particle");
		lj.Lx = control.getDouble("Lx");
		//lj.Ly = control.getDouble("Ly");
		lj.initialConfiguration = control.getString("initial configuration");
		
		if(control.getString("initial configuration")=="Lennard-Jones") lj.potential=true;
		else lj.potential=false;
		
		lj.dt = control.getDouble("dt");
		double tmax = 2.0;//control.getDouble("Maximum for temperature axis");
		double pmax = 2.0;//control.getDouble("Maximum for pressure axis");
//		temperatureFrame.setPreferredMinMaxY(0, tmax);
//		pressureFrame.setPreferredMinMaxY(0, pmax);
		lj.initialize();
	    gr.initialize(lj.Lx,lj.Lx,0.1);
	     
		control.println("Initial kinetic energy = "+ numberFormatFourDigits.format(lj.getInstantanousKineticEnergy()));
		control.println("Initial total energy = "+ numberFormatFourDigits.format(lj.getInstantaneousTotalEnergy()));
		temperatureFrame.clearData();
		pressureFrame.clearData();
		renderPanels();
	}

	public void renderPanels()
	{
		double tmax = 2.0;//control.getDouble("Maximum for temperature axis");
		double pmax = 2.0;//control.getDouble("Maximum for pressure axis");
//		temperatureFrame.setPreferredMinMaxY(0, tmax);
//		pressureFrame.setPreferredMinMaxY(0, pmax);
		displayFrame.setPreferredMinMax(-0.1 * lj.Lx, 1.1 * lj.Lx,
				-0.1 * lj.Lx, 1.1 * lj.Lx);
		temperatureFrame.render();
		histogramFrame.render();
	}

	public void doStep()
	{
		quenchRate = control.getDouble("quench rate");
		for (int i = 0; i < 20; i++)
		{
			lj.step();
			lj.quench(quenchRate);
		}
		if (lj.steps % 100 == 0)
		{// sample every 100 steps
			temperatureFrame.append(0, lj.t, lj.getInstantaneousTemperature());
			pressureFrame.append(0, lj.t, lj.getInstantanousPressure());
		}
		
	    gr.append(lj.x, lj.y);
	    gr.normalize();
	    grFrame.clearData();
	    grFrame.append(2, gr.rx, gr.ngr);	   
	    grFrame.render();
	    
		renderPanels();
	}

	public void stop()
	{
		control.println("Density = " + numberFormatFourDigits.format(lj.rho));
		control.println("Number of time steps = " + lj.steps);
		control.println("Time step dt = "
				+ numberFormatFourDigits.format(lj.dt));
		control.println("Temperature = "
				+ numberFormatFourDigits.format(lj
						.getInstantaneousTemperature()));
		control.println("Energy = "
				+ numberFormatFourDigits.format(lj
						.getInstantaneousTotalEnergy()));
		control.println("<T> = "
				+ numberFormatFourDigits.format(lj.getMeanTemperature()));
		control.println("Heat capacity = "
				+ numberFormatFourDigits.format(lj.getHeatCapacity()));
		control.println("<PA/NkT> = "
				+ numberFormatFourDigits.format(lj.getMeanPressure()));
	}

	public void reset()
	{
		control.setValue("N", 64);
		control.setValue("Lx", 18.0);
		//control.setValue("Ly", 18.0);
		
		control.setValue("initial kinetic energy per particle", 1.0);
		control.setAdjustableValue("dt", 0.01);
		OSPCombo combo = new OSPCombo(new String[] {"crystal",  "random"},0);  // second argument is default
	    control.setValue("initial configuration", combo);
	    OSPCombo combo2 = new OSPCombo(new String[] {"Lennard-Jones",  "Weeks-Chandler-Andersen"},0);  // second argument is default
	    control.setValue("potential type", combo2);
		//control.setValue("initial configuration", "crystal");
		//control.setValue("Maximum for temperature axis", 2.0);
		//control.setValue("Maximum for pressure axis", 2.0);
		control.setAdjustableValue("quench rate", 1.0);
		lj.initialConfiguration = "crystal";
		lj.initialize();
		gr.reset();
		
		temperatureFrame.clearData();
		pressureFrame.clearData();
		control.clearMessages();
		renderPanels();
	}

	public static void main(String[] args)
	{
		SimulationControl.createApp(new LJfluidApp(),args);
	}
}
