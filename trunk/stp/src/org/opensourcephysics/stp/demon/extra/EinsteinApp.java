package org.opensourcephysics.stp.demon.extra;

import java.text.NumberFormat;
import javax.swing.JButton;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.HistogramFrame;

// The demon algorithm applied to an Einstein solid

/**
 * @author jantobochnik
 *
 */
public class EinsteinApp extends AbstractSimulation
{
	boolean logScale;

	int[] E; // Particle energies

	int N, systemEnergy, demonEnergy, mcs = 0, systemEnergyAccumulator = 0,
			demonEnergyAccumulator = 0, acceptedMoves = 0; // number of MC moves per particle

	HistogramFrame histogramFrame = new HistogramFrame("Ed", "P(Ed)", "Demon Energy Distribution");

	JButton logButton;

	NumberFormat nf;

	public void doStep()
	{
		for (int i = 0; i < 10000 / N; i++)
		{
			for (int j = 0; j < N; ++j)
			{
				int particleIndex = (int) (Math.random() * N); // choose
				// particle at
				// random

				int dE = (Math.random() < 0.5) ? 1 : -1;

				if (demonEnergy - dE >= 0 && E[particleIndex] + dE >= 0)
				{
					demonEnergy -= dE;
					E[particleIndex] += dE;
					systemEnergy += dE;
					acceptedMoves++;
				}
				systemEnergyAccumulator += systemEnergy;
				demonEnergyAccumulator += demonEnergy;
					histogramFrame.append(demonEnergy);
			}
			mcs++;
		}

		double norm = 1.0 / (mcs * N);
		// control.clearMessages();
		control.println("mcs = " + nf.format(mcs));
		control.println("<Ed> = " + nf.format(demonEnergyAccumulator * norm));
		control.println("<E> = " + nf.format(systemEnergyAccumulator * norm));
		control.println("acceptance ratio = "
						+ nf.format(acceptedMoves * norm));
	}

	public void initialize()
	{
		N = control.getInt("N");
		systemEnergy = control.getInt("system energy");

		E = new int[N];
		for (int i = 0; i < N; i++)
		{
			E[i] += systemEnergy / N;
		}
		E[0] += systemEnergy % N; // Make sure we put all system energy in

		demonEnergy = 0;
		mcs = 0;
		systemEnergyAccumulator = 0;
		demonEnergyAccumulator = 0;
		acceptedMoves = 0;

		control.clearMessages();
		histogramFrame.clearData();
		histogramFrame.render();
	}

	public void reset()
	{
		control.setValue("N", 40);
		control.setValue("system energy", 200);
		control.clearMessages();
		histogramFrame.clearData();
		histogramFrame.render();
	}
	public void zeroAverages(){
		histogramFrame.clearData();
		histogramFrame.render();
		control.clearMessages();
	}
	public EinsteinApp()
	{
		histogramFrame.setPreferredMinMax(0, 5, 0, 1);
		histogramFrame.setAutoscaleX(true);
		histogramFrame.setAutoscaleY(true);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	public static void main(String[] args)
	{
		EinsteinApp appplication = new EinsteinApp();
		SimulationControl control = SimulationControl.createApp(appplication);
		control.addButton("zeroAverages", "Zero averages");
	}
}
