package org.opensourcephysics.stp.xymodel;

import java.text.DecimalFormat;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.frames.DisplayFrame;

/**
 * Animation for XY model that displays the lattice
 *
 * @author Joshua Gould
 * @author Peter Sibley
 * @author Rongfeng Sun
 */

public class XYAnimationApp extends AbstractSimulation
{
	XYModel xy = new XYModel();
	DisplayFrame displayFrame = new DisplayFrame("lattice");
	boolean resetFlag = true;
	DecimalFormat numberFormatTwoDigits = (DecimalFormat) DecimalFormat
			.getInstance();
	DecimalFormat numberFormatFourDigits = (DecimalFormat) DecimalFormat
			.getInstance();

	public XYAnimationApp()
	{
		numberFormatTwoDigits.setMaximumFractionDigits(2);
		numberFormatTwoDigits.setMinimumFractionDigits(2);
		numberFormatTwoDigits.setGroupingSize(100);
		// never show commas so we set grouping size to a big number.
		numberFormatFourDigits.setMaximumFractionDigits(4);
		numberFormatFourDigits.setMinimumFractionDigits(4);
		numberFormatFourDigits.setGroupingSize(100);
		xy = new XYModel();
	}

	public void initialize()
	{
		int L = control.getInt("linear dimension of lattice");
		double T = control.getDouble("temperature");
		int nequil = control.getInt("MC steps per spin for equilibrium");
		int mcs = control.getInt("MC steps per spin for data");
		double dThetaMax = control.getDouble("dThetaMax");
		long seed = Long.parseLong(control.getString("seed"));
		xy.initialConfiguration = control.getString("initial configuration");
		if (resetFlag)
		{ // that is we are running a new simulation
			xy.setSeed(seed);
			xy.setLinearDimension(L);
			xy.setNequil(nequil);
			xy.initialize();
			displayFrame.clearData();
			displayFrame.addDrawable(xy);
			displayFrame.repaint();
		}
		if (resetFlag || xy.getTemperature() != T)
		{
			// reset averages change temperature
			xy.clearData();
			xy.setTemperature(T);
			resetFlag = false;
		}
		xy.setDThetaMax(dThetaMax);
		xy.setMcs(mcs);
	}

	public void stop() {
	output();
	}

	public void output()
	{
		control.clearMessages();
		control.println("number of MC steps = " + xy.getTime());
		control.println("temperature = "
				+ numberFormatTwoDigits.format(xy.getTemperature()));
		control.println("energy = "
				+ numberFormatFourDigits.format(xy.getTotalEnergy()));
		control.println("acceptance probability = "
				+ numberFormatTwoDigits.format(xy.getAcceptanceProbability()));
		control.println("<E> = "
				+ numberFormatFourDigits.format(xy.getMeanEnergy()));
		control.println("<E*E> = "
				+ numberFormatFourDigits.format(xy.getMeanEnergySquared()));
		control.println("<M_x> = "
				+ numberFormatFourDigits.format(xy.getMeanMagnetizationX()));
		control.println("<M_y> = "
				+ numberFormatFourDigits.format(xy.getMeanMagnetizationY()));
		control.println("<M*M> = "
				+ numberFormatFourDigits.format(xy
						.getMeanMagnetizationSquared()));
		control.println("# of vortices = " + xy.getTotalNumberOfVortices());
		control.println("vorticity = "
				+ numberFormatTwoDigits.format(xy.getVorticity()));
		control.println("<# of vortices> = "
				+ numberFormatTwoDigits.format(xy.getMeanNumberOfVortices()));
		control.println("heat capacity = "
				+ numberFormatTwoDigits.format(xy.getHeatCapacity()));
		control.println("susceptibility = "
				+ numberFormatTwoDigits.format(xy.getSusceptibility()));
		displayFrame.repaint();
	}

	public void reset()
	{
		control.clearMessages();
		control.setValue("linear dimension of lattice", 20);
		control.setAdjustableValue("temperature", 0.89);
		control.setValue("MC steps per spin for equilibrium", 20);
		control.setValue("MC steps per spin for data", 10000);
		control.setValue("dThetaMax", numberFormatFourDigits
				.format(Math.PI * 2));
		control.setValue("seed", Math.abs((int) System.currentTimeMillis()));
		control.setValue("initial configuration", "random");
		resetFlag = true;
		enableStepsPerDisplay(true);
	}

	public void showVortex()
	{
		xy.showVortex(true);
		displayFrame.repaint();
	}

	public void hideVortex()
	{
		xy.showVortex(false);
		displayFrame.repaint();
	}

	public static void main(String args[])
	{
		SimulationControl control = SimulationControl.createApp(new XYAnimationApp(),args);
		control.addButton("showVortex", "show vortices");
		control.addButton("hideVortex", "hide vortices");
	}

	protected void doStep()
	{
		if (!xy.isCompleted())
		{
			xy.step();
			displayFrame.repaint();
		}
		else
		{
			output();
		}
	}
}
