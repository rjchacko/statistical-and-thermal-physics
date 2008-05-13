package org.opensourcephysics.stp.ising.ising2d;

import java.text.NumberFormat;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

public class Ising2DAntiApp extends AbstractSimulation
{
	Ising2DAnti ising;
	DisplayFrame displayFrame = new DisplayFrame("Spin Configuration");
	PlotFrame plotFrame = new PlotFrame("time", "E and M", "Thermodynamic Quantities");
	NumberFormat nf;

	public Ising2DAntiApp()
	{
		ising = new Ising2DAnti();
		plotFrame.setPreferredMinMaxX(0, 10);
		plotFrame.setAutoscaleX(true);
		plotFrame.setAutoscaleY(true);
		displayFrame.addDrawable(ising);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	public void initialize()
	{
		ising.J = -1.0;	//initialize to be anti-ferromagnetic
		
		ising.initialize(control.getInt("length"), control
				.getDouble("temperature"), control.getDouble("external field"));
		displayFrame.setPreferredMinMax(-5, ising.L + 5, -5, ising.L + 5);
		control.clearMessages();
		plotFrame.clearData();
		plotFrame.repaint();
		displayFrame.repaint();
	}

	public void doStep()
	{
		ising.setTemperature(control.getDouble("temperature"));
		ising.setExternalField(control.getDouble("external field"));
		ising.doOneMCStep();
		plotFrame.append(0, ising.mcs, (double) ising.M / ising.N);
		plotFrame.append(1, ising.mcs, (double) ising.E / ising.N);
		plotFrame.repaint();
		displayFrame.repaint();

		double norm = 1.0 / (ising.mcs * ising.N);
		control.println("mcs = " + ising.mcs);
		control.println("<E> = " + nf.format(ising.E_acc * norm));
		control.println("Specific heat = " + nf.format(ising.specificHeat()));
		control.println("<M> = " + nf.format(ising.M_acc * norm));
		control.println("Susceptibility = "
						+ nf.format(ising.susceptibility()));
		control.println("Staggered susceptibility = " + nf.format(ising.Staggeredsusceptibility()));
		control.println("Acceptance ratio = "
				+ nf.format(ising.acceptedMoves * norm));
	}

	public void reset()
	{
		control.setAdjustableValue("length", 32);
		control.setAdjustableValue("temperature", nf.format(Ising2D.criticalTemperature));
		control.setAdjustableValue("external field", 0);
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
		SimulationControl control = SimulationControl.createApp(new Ising2DAntiApp(),args);
		control.addButton("cleardata", "Zero averages");
	}
}
