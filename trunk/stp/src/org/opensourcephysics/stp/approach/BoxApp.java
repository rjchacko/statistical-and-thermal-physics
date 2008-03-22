package org.opensourcephysics.stp.approach;

import java.text.NumberFormat;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.PlotFrame;
/**
 *  simulation of the particles in a box problem
 *
 * @author     jgould
 * @created    December 26, 2002
 * @authors    Joshua Gould , Peter Sibley
 * modified    Natali Gulbahce, Oct 2002, Jan 28 2003
 */
public class BoxApp extends AbstractSimulation {
	PlotFrame plotFrame = new PlotFrame("time", "n", "");

	/**
	 * These are the variables used by the simulation.
	 */
	int N=64;// total number of particles
	int nleft;// number on left
	int time;
	int zeroedTime;
	int nleftAccumulator;
	int nleftSquaredAccumulator;
	NumberFormat numberFormatTwoDigits = NumberFormat.getInstance();

	public BoxApp()
	{
		numberFormatTwoDigits.setMaximumFractionDigits(2);
		numberFormatTwoDigits.setMinimumFractionDigits(2);
	}

	public void reset()
	{
		control.clearMessages();
		control.setValue("N", 64);
		plotFrame.setPreferredMinMaxX(0,10);
		plotFrame.setAutoscaleX(true);
		plotFrame.repaint();
		zeroAverages();
	}

	public void initialize()
	{
		N = control.getInt("N");
		plotFrame.setPreferredMinMaxY(0,N); //
		time = 0;
		zeroedTime = 0;
		nleft = N;// all particles initially on left side
		nleftAccumulator = 0;
		nleftSquaredAccumulator = 0;
	}

	// move particle through hole
	public void moveParticle()
	{
		// generate random number and move particle
		double r = Math.random();
		double ratio = (double) nleft / N;
		if(r <= ratio) {
			nleft--;
		} else {
			nleft++;
		}
		time++;
		zeroedTime++;
	}


	public void zeroAverages() {
		nleftAccumulator = 0;
		nleftSquaredAccumulator = 0;
		zeroedTime = 0;
		control.println("<n> = 0");
		control.println("<n\u00b2> = 0");
		control.println("\u03c3\u00b2 = 0");
	}

	public void output()
	{
		control.println("<n> = " + numberFormatTwoDigits.format((double) nleftAccumulator / zeroedTime));
		control.println("<n\u00b2> = " + numberFormatTwoDigits.format((double) nleftSquaredAccumulator / zeroedTime));
		control.println("\u03c3\u00b2 = " + numberFormatTwoDigits.format((double) nleftSquaredAccumulator / zeroedTime - ((double) nleftAccumulator / zeroedTime * (double) nleftAccumulator / zeroedTime)));
	}//????

	public static void main(String[] args)
	{
		SimulationControl control = SimulationControl.createApp(new BoxApp(),args);
		control.addButton("zeroAverages", "Zero averages");
	}

	public void doStep() {
		// TODO Auto-generated method stub
		plotFrame.append(2, time, nleft);
		plotFrame.render();
		moveParticle();
		nleftAccumulator += nleft;
		nleftSquaredAccumulator += nleft * nleft;
		control.clearMessages();
		output();
	}
}


