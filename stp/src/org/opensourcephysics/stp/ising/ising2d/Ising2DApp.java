package org.opensourcephysics.stp.ising.ising2d;

import java.text.NumberFormat;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.Histogram;
import org.opensourcephysics.frames.*;

public class Ising2DApp extends AbstractSimulation
{
	Ising2D ising;
	DisplayFrame displayFrame = new DisplayFrame("spin configurations");
	PlotFrame plotFrame = new PlotFrame("time", "E and M", "Thermodynamic Quantities");
	HistogramFrame histogramFrame = new HistogramFrame("E", "H(E)","H(E) versus E");
	Histogram energyHistogram = new Histogram();
	NumberFormat nf;
	double bondProbability;
	boolean metropolis=true;
	public Ising2DApp()
	{
		ising = new Ising2D();
		plotFrame.setPreferredMinMaxX(0,10);
		plotFrame.setAutoscaleX(true);
		plotFrame.setAutoscaleY(true);
		displayFrame.addDrawable(ising);
		histogramFrame.setPreferredMinMaxY(0, 10);
		histogramFrame.setAutoscaleX(true);
		histogramFrame.setAutoscaleY(true);
		histogramFrame.addDrawable(energyHistogram);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	public void initialize()
	{
		ising.initialize(control.getInt("length"), control
				.getDouble("temperature"), control.getDouble("external field"));
		this.bondProbability=bondProbability(ising.J, ising.T);
		if(control.getString("dynamics")=="Metropolis") metropolis=true;
		else metropolis=false;
		
		displayFrame.setPreferredMinMax(-5, ising.L + 5, -5, ising.L + 5);
		control.clearMessages();
		plotFrame.clearData();
		plotFrame.repaint();
		displayFrame.repaint();
		energyHistogram.clear();
		histogramFrame.clearDataAndRepaint();
	}
	
	public double bondProbability(double J, double T) {
		return 1 - Math.exp(-2*J/T);
	}
	
	public void doStep()
	{
		ising.setTemperature(control.getDouble("temperature"));
		ising.setExternalField(control.getDouble("external field"));
		
		if(metropolis)ising.doOneMCStep();
		else ising.doOneWolffStep(bondProbability);
		
		plotFrame.append(0, ising.mcs, (double) ising.M / ising.N);
		plotFrame.append(1, ising.mcs, (double) ising.E / ising.N);
		energyHistogram.append(ising.E);
		plotFrame.repaint();
		displayFrame.repaint();
		histogramFrame.render();
		double norm = 1.0 / (ising.mcs * ising.N);
		control.println("mcs = " + ising.mcs);
		control.println("<E> = " + nf.format(ising.E_acc * norm));
		control.println("Specific heat = " + nf.format(ising.specificHeat()));
		control.println("<M> = " + nf.format(ising.M_acc * norm));
		control.println("<|M|>=" + nf.format(Math.abs(ising.absM_acc*norm)));

		control
				.println("Susceptibility = "
						+ nf.format(ising.susceptibility()));
		if(metropolis)control.println("Acceptance ratio = "
				+ nf.format(ising.acceptedMoves * norm));
	}

	public void reset()
	{
		control.setAdjustableValue("length", 32);
		control.setAdjustableValue("temperature", nf.format(Ising2D.criticalTemperature));
		control.setAdjustableValue("external field", 0);
		OSPCombo combo = new OSPCombo(new String[] {"Metropolis",  "Wolff"},0);  // second argument is default
	    control.setValue("dynamics", combo);
	}

	public void cleardata()
	{
		control.clearMessages();
		ising.resetData();
		plotFrame.clearData();
		plotFrame.repaint();
	}

	public static void main(String[] args)
	{
		SimulationControl control = SimulationControl.createApp(new Ising2DApp(),args);
		control.addButton("cleardata", "Zero averages");
	}
}
