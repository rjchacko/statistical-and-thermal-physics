package org.opensourcephysics.stp.approach;

	/**
	 * TODO - check results. Original version was not working, converted to AbstractSimulation
	 *
	 *
	 *
	 * Approach to equilibrium. Starts all particles in a left part of simulation cell and
	 * keeps track of how many particles are in the left third, center third and right third of cell.
	 * User can also reverse velocities to see particles return to their original state for short enough
	 * times. Particles interact with Lennard Jones forces. Density is low enough for a gas.
	 * @author Jan Tobochnik
	 * @author Peter Sibley
	 */

	import org.opensourcephysics.controls.*;
	import org.opensourcephysics.frames.DisplayFrame;
	import org.opensourcephysics.frames.PlotFrame;

	public class LJgas2boxApp extends AbstractSimulation
	{
		org.opensourcephysics.stp.approach.LJgas2box gas;

		PlotFrame plotFrame = new PlotFrame("time", "n1, n2",
				"Number of particles in each cell");

		DisplayFrame displayFrame = new DisplayFrame("Particle positions");

		public LJgas2boxApp()
		{
			gas = new LJgas2box();
		}

		public void initialize()
		{
			displayFrame.addDrawable(gas);
			
			plotFrame.setPreferredMinMax(0, 10, 0, 10);
			plotFrame.setAutoscaleX(true);
			plotFrame.setAutoscaleY(true);
			gas.numberOfParticles = control.getInt("N");
			gas.initialize();
			plotFrame.clearData();
			plotFrame.render();
			control.println("Default density: " + gas.rho);
			
			control.println("Plotting Window Legend");

			control.println("black: # particles in the left cell");
			control.println("blue: # particles in the right cell");
			

			displayFrame.setPreferredMinMax(-0.2 * gas.cellLength,
					1.2 * gas.cellLength, -0.2 * gas.cellLength,
					1.2 * gas.cellLength);
			displayFrame.render();
			plotFrame.clearData();
			plotFrame.render();
		}

		public void doStep()
		{
			for (int i = 0; i < 10; i++)
			{
				gas.step(); // advance the solution of the ODE by one step
			}

			double norm = 1.0 / gas.steps;
			plotFrame.append(0, gas.t, norm * gas.n0);	//black, left
			plotFrame.append(1, gas.t, norm * gas.n1);	//blue, right
			gas.zeroAverages();
			plotFrame.render();
			displayFrame.render();
		}

		public void reset()
		{
			//gas.initialize();
			control.setValue("N", 270);
			initialize();
		}

		public void reverse()
		{
			gas.reverse();
			gas.zeroAverages();
		}

		public static void main(String[] args)
		{
			SimulationControl control = SimulationControl.createApp(new LJgas2boxApp(),args);
			control.addButton("reverse", "Reverse");
		}
	}

