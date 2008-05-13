package org.opensourcephysics.stp.demon;

import org.opensourcephysics.controls.*;
import java.text.NumberFormat;
import javax.swing.JButton;
import org.opensourcephysics.frames.HistogramFrame;

// The demon algorithm applied to an ideal gas

public class DemonApp extends AbstractSimulation
{
	int N, dimensions, acceptedMoves = 0, mcs = 0;
	double[][] v; // (N*dimensions) particle velocities
	double systemEnergy, demonEnergy, systemEnergyAccumulator = 0,
			demonEnergyAccumulator = 0, delta, exponent;
	JButton logButton;
	NumberFormat nf;
    HistogramFrame histogramFrame = new HistogramFrame("Ed", "H(Ed)","Demon Energy Histogram");;
    HistogramFrame vhistogramFrame = new HistogramFrame("Vx", "Histogram","Histogram of vx");;
    
	double[] offsetVelocity(double[] vel)
	{
		double[] vnew = new double[dimensions];
		for (int d = 0; d < dimensions; d++)
			vnew[d] = vel[d] + (2.0 * Math.random() - 1.0) * delta;
		return vnew;
	}

	double getSpeed(double[] vel)
	{
		double speed = 0;
		for (int d = 0; d < dimensions; d++)
		{
			speed += vel[d] * vel[d];
		}
		return Math.sqrt(speed);
	}

	public void doStep()
	{
		for (int i = 0; i < 10000 / N; i++)
		{
			for (int j = 0; j < N; ++j)
			{
				int particleIndex = (int) (Math.random() * N); // choose
				// particle at
				// random

				double[] v_old = v[particleIndex];
				double[] v_new = offsetVelocity(v_old);

				double dE = (Math.pow(getSpeed(v_new), exponent) - Math
						.pow(getSpeed(v_old), exponent));
				if (dE <= demonEnergy)
				{
					v[particleIndex] = v_new;
					acceptedMoves++;
					systemEnergy += dE;
					demonEnergy -= dE;
				}
				systemEnergyAccumulator += systemEnergy;
				demonEnergyAccumulator += demonEnergy;
			    histogramFrame.append(demonEnergy);
			    vhistogramFrame.append(v[j][0]);
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
		dimensions = Math.min(control.getInt("dimension"), 3);
		control.setValue("dimension", dimensions);
		systemEnergy = control.getDouble("system energy");
		delta = 0.1;//control.getDouble("Delta");
      	exponent = control.getDouble("momentum exponent");

		v = new double[N][dimensions];
		double v0 = Math.pow(systemEnergy / N, 1.0/exponent);
		
		for (int i = 0; i < N; i++)
		{
			v[i][0] = v0;
			vhistogramFrame.append(v0);
		}
		histogramFrame.render();
		demonEnergy = 0;
		mcs = 0;
		systemEnergyAccumulator = 0;
		demonEnergyAccumulator = 0;
		acceptedMoves = 0;

		control.clearMessages();
	}

	public void reset()
	{
		control.setValue("N", 40);
		control.setValue("dimension", 1);
		control.setValue("system energy", 40);
		control.setValue("momentum exponent", "2");
		control.clearMessages();
      	histogramFrame.clearData();
      	vhistogramFrame.clearData();
	}

	public void zeroAverages(){
		histogramFrame.clearData();
		vhistogramFrame.clearData();
		histogramFrame.render();
		control.clearMessages();
	}

	public DemonApp()
	{
		vhistogramFrame.setBinWidth(0.1);
		vhistogramFrame.setPreferredMinMaxX(-5, 5);
		
		histogramFrame.setBinWidth(0.5);
		histogramFrame.setPreferredMinMaxY(0, 10);
		histogramFrame.setAutoscaleY(true);
		histogramFrame.setPreferredMinMaxX(0, 10);
		histogramFrame.setAutoscaleX(true);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	public static void main(String[] args)
	{
        SimulationControl control=SimulationControl.createApp(new DemonApp(),args);
        control.addButton("zeroAverages", "Zero averages");
	}
}
