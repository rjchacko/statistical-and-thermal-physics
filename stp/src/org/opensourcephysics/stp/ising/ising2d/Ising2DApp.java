package org.opensourcephysics.stp.ising.ising2d;

import java.text.NumberFormat;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

public class Ising2DApp extends AbstractSimulation
{
	Ising2D ising;
	DisplayFrame displayFrame = new DisplayFrame("Spin Configuration");
	PlotFrame plotFrame = new PlotFrame("time", "E and M", "Thermodynamic Quantities");
	NumberFormat nf;
	double bondProbability;
	boolean metropolis=true;
	public Ising2DApp()
	{
		ising = new Ising2D();
		plotFrame.setAutoscaleX(true);
		plotFrame.setAutoscaleY(true);
		displayFrame.addDrawable(ising);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	public void initialize()
	{
		ising.initialize(control.getInt("Length"), control
				.getDouble("Temperature"), control.getDouble("External field"));
		this.bondProbability=bondProbability(ising.J, ising.T);
		if(control.getString("Dynamics")=="Metropolis") metropolis=true;
		else metropolis=false;
		
		displayFrame.setPreferredMinMax(-5, ising.L + 5, -5, ising.L + 5);
		control.clearMessages();
		plotFrame.clearData();
		plotFrame.repaint();
		displayFrame.repaint();
	}
	
	public double bondProbability(double J, double T) {
		return 1 - Math.exp(-2*J/T);
	}
	
	public void doStep()
	{
		ising.setTemperature(control.getDouble("Temperature"));
		ising.setExternalField(control.getDouble("External field"));
		
		if(metropolis)ising.doOneMCStep();
		else ising.doOneWolffStep(bondProbability);
		
		plotFrame.append(0, ising.mcs, (double) ising.M / ising.N);
		plotFrame.append(1, ising.mcs, (double) ising.E / ising.N);
		plotFrame.repaint();
		displayFrame.repaint();

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
		control.setAdjustableValue("Length", 32);
		control.setAdjustableValue("Temperature", nf.format(Ising2D.criticalTemperature));
		control.setAdjustableValue("External field", 0);
		OSPCombo combo = new OSPCombo(new String[] {"Metropolis",  "Wolff"},0);  // second argument is default
	    control.setValue("Dynamics", combo);
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
