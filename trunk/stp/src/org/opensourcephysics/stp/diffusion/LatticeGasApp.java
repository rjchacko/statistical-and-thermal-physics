package org.opensourcephysics.stp.diffusion;

///import org.opensourcephysics.display.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

/**
 * simulation of particle diffusion in a lattice gas
 *
 * @author Peter Sibley
 * @author Joshua Gould
 * @created December 26, 2002
 */
public class LatticeGasApp extends AbstractSimulation
{
	int L;// linear dimension of lattice

	double L_2;// 0.5 * L

	int N;// number of particles

	final int OCCUPIED = 1;

	final int EMPTY = 0;

	double t;

	LatticeGas latticegas;

	PlotFrame plotFrame = new PlotFrame("time", "<R2>", "Mean squared displacement");

	DisplayFrame displayFrame = new DisplayFrame("Lattice Display");

	// Thread animationThread;
	// DrawingPanel drawingPanel;
	// PlottingPanel plottingPanel;
	// DrawingFrame drawingFrame;
	// DrawingFrame drawingFrame2;// we have the lattice displayed in one frame
	// and a plot of time v R2Bar in the other
	// Control control;
	// Dataset dataset;

	public LatticeGasApp()
	{
		latticegas = new LatticeGas();
		// dataset = new Dataset();
		// plottingPanel = new PlottingPanel("time", "R2Bar", "Lattice Gas");
		// drawingPanel = new DrawingPanel();
		// drawingFrame = new DrawingFrame("Lattice Display", drawingPanel);
		// drawingFrame2 = new DrawingFrame("Time v R2Bar", plottingPanel);
		// drawingPanel.setSquareAspect(true);
		// drawingPanel.addDrawable(latticegas);
		// plottingPanel.addDrawable(dataset);
		displayFrame.setSquareAspect(true);
		displayFrame.addDrawable(latticegas);
	}

	public void initialize()
	{
		L = control.getInt("L");
		N = control.getInt("N");
		latticegas.initialize(L, N);
		if (N > L * L)
		{
			control.println("N must be less than L*L");
			return;
		}
		t = 0;
		latticegas.fillLattice();
		displayFrame.setPreferredMinMax(-1, L + 1, L + 1, -1);// notice that
	}

	public void reset()
	{
		control.setValue("L", 40);
		control.setValue("N", 500);
		plotFrame.render();
		displayFrame.render();
		displayFrame.clearData();
	}

	protected void doStep()
	{
		double R2bar;
		latticegas.move();
		R2bar = latticegas.computeR2Bar();
		t++;
		plotFrame.append(0, t, R2bar);
		plotFrame.render();
		displayFrame.render();
	}

	public static void main(String[] args)
	{
		SimulationControl.createApp(new LatticeGasApp(),args);
	}
}
