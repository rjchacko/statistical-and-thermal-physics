package org.opensourcephysics.stp.ising.ising1d;

import java.awt.Color;
import java.text.NumberFormat;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

public class Ising1DApp extends AbstractSimulation
{
	DisplayFrame displayFrame = new DisplayFrame("Spin Configuration");
	PlotFrame eFrame = new PlotFrame("time", "E",
			"Energy");
	PlotFrame mFrame = new PlotFrame("time", "M",
	"Magnetization");

	Ising1D ising;
	NumberFormat nf;

	public Ising1DApp()
	{
		ising = new Ising1D();
		eFrame.setAutoscaleX(true);
		eFrame.setAutoscaleY(true);
		eFrame.setMarkerColor(0, Color.black);
		
		mFrame.setAutoscaleX(true);
		mFrame.setAutoscaleY(true);
		mFrame.setMarkerColor(0, Color.black);
		
		displayFrame.setSize(400, 100);
		displayFrame.addDrawable(ising);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
	}

	public void initialize()
	{
		ising.initialize(control.getInt("N"), control
				.getDouble("temperature"), control.getDouble("external field"));
		displayFrame.setPreferredMinMax(-2, ising.N + 2, -2, +3);
		control.clearMessages();
		eFrame.clearData();
		eFrame.repaint();
		mFrame.clearData();
		mFrame.repaint();
		
		displayFrame.repaint();
	}

	public void doStep()
	{
		ising.setTemperature(control.getDouble("temperature"));
		ising.setExternalField(control.getDouble("external field"));
		ising.doOneMCStep();
		mFrame.append(1, ising.mcs, (double) ising.M / ising.N);
		eFrame.append(1, ising.mcs, (double) ising.E / ising.N);
		mFrame.repaint();
		eFrame.repaint();

		double norm = 1.0 / (ising.mcs * ising.N);
		control.println("mcs = " + ising.mcs);
		control.println("<E> = " + nf.format(ising.E_acc * norm));
		control.println("Specific heat = " + nf.format(ising.specificHeat()));
		control.println("<M> = " + nf.format(ising.M_acc * norm));
		control.println("Susceptibility = "
						+ nf.format(ising.susceptibility()));
		control.println("Acceptance ratio = "
				+ nf.format(ising.acceptedMoves * norm));
	}

	public void reset()
	{
		control.setValue("N", 64);
		control.setAdjustableValue("temperature", 1);
		control.setAdjustableValue("external field", 0);
	}

	public void clearData()
	{
		control.clearMessages();
		ising.resetData();
		eFrame.clearData();
		mFrame.clearData();
		displayFrame.repaint();
	}

	public static void main(String[] args)
	{
		SimulationControl control = SimulationControl.createApp(new Ising1DApp(),args);
		control.addButton("clearData", "Zero averages");
	}
}
