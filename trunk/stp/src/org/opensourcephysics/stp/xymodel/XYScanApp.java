package org.opensourcephysics.stp.xymodel;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.PlotFrame;

/**
 * plot heat capacity and vorticity versus temperature of the xy model on a
 * square lattice
 *
 * @author Joshua Gould
 * @author Peter Sibley
 * @author Rongfeng Sun
 */

public class XYScanApp extends AbstractSimulation
{
	XYModel xyModel = new XYModel();

	PlotFrame heatFrame = new PlotFrame("temperature", "C", "C(t) v T");

	PlotFrame vortexFrame = new PlotFrame("temperature", "vorticty",
			"Vorticity versus T");

	PlotFrame chiFrame = new PlotFrame("temperature", "Chi", "Chi versus T");

	public XYScanApp()
	{
		xyModel = new XYModel();
	}

	public void initialize()
	{
		int dimensionoflattice = control.getInt("linear dimension of lattice");
		double initialtemperature = control.getDouble("start temperature");
		double deltatemperature = control.getDouble("temperature interval");
		int nequil = control.getInt("MC steps per spin for equilibrium");
		int mcs = control.getInt("MC steps per spin for data");
		double dThetaMax = control.getDouble("dThetaMax");
		heatFrame.setAnimated(true);
		xyModel.setdeltatemperature(deltatemperature);
		xyModel.setLinearDimension(dimensionoflattice);
		xyModel.setKeepVorticity(true);
		xyModel.setMcs(mcs);
		xyModel.setNequil(nequil);
		xyModel.setDThetaMax(dThetaMax);
		xyModel.setTemperature(initialtemperature);
		xyModel.fillSpinsUp();
		xyModel.initialize();
	}

	public void reset()
	{
		control.setValue("linear dimension of lattice", 20);
		control.setValue("start temperature", 0.5);
		control.setAdjustableValue("temperature interval", 0.1);
		control.setValue("MC steps per spin for equilibrium", 100);
		control.setValue("MC steps per spin for data", 10000);
		control.setValue("dThetaMax", Math.PI / 2.0);
		enableStepsPerDisplay(true);
	}

	public static void main(String args[])
	{
		SimulationControl.createApp(new XYScanApp(),args);
	}

	protected void doStep()
	{
		// TODO Auto-generated method stub
		double temperature = xyModel.getTemperature();
		temperature += xyModel.getdeltatemperature();
		xyModel.setTemperature(temperature);
		control.println(String.valueOf(temperature));
		xyModel.initialize();
		xyModel.compute();
		heatFrame.append(0, temperature, (xyModel.getHeatCapacity()));
		vortexFrame.append(0, temperature, xyModel.getVorticity());
		chiFrame.append(0, temperature, xyModel.getSusceptibility());
		control.clearMessages();
		control.println("temperature = " + xyModel.getTemperature());
	}
}
