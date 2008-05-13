package org.opensourcephysics.stp.cointoss;

import java.text.NumberFormat;
import java.util.Random;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.HistogramFrame;

/**
 *  <b>Input Parameters</b> <br>
 *  <code>probability</code> probability of heads coming up, which should be
 *  between zero and unity, inclusive. <br>
 *  <code>coins to flip</code> number of coins to flip.
 *
 * @author     jgould
 * @created    January 23, 2002
 * Modified    Natali  Jan 22, 2003
 */

public class MultipleCoinTossApp extends AbstractSimulation
{
	Random random;

	int totalFlips;

	double sumHeads = 0, sum2Heads = 0, probability, coinsToFlip, time, dt;

	HistogramFrame histogramFrame = new HistogramFrame("Heads", "Occurences", "Histogram");

	NumberFormat nf;

	public MultipleCoinTossApp()
	{
		random = new Random();
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	public void initialize()
	{
		probability = control.getDouble("probability");
		coinsToFlip = control.getInt("coins to flip");
		random.setSeed(System.currentTimeMillis());
		histogramFrame.setPreferredMinMaxX(0, coinsToFlip);
		histogramFrame.setPreferredMinMaxY(0, 10);
		histogramFrame.setAutoscaleY(true);
		histogramFrame.clearData();
		histogramFrame.repaint();
		totalFlips = 0;
		sumHeads = 0;
		sum2Heads = 0;
	}

	public void start()
	{
		probability = control.getDouble("probability");
	}

	public void reset()
	{
		control.setValue("probability", 0.5);
		control.setValue("coins to flip", 100);
		control.clearMessages();
		initialize();
	}

	public void output(int nHeads)
	{

		sumHeads += nHeads;
		sum2Heads += nHeads * nHeads;
		double avg = sumHeads / totalFlips;
		double avg2 = sum2Heads / totalFlips;
		control.println("<H> = " + nf.format(avg));
		control.println("<H*H> = " + nf.format(avg2));
		double sigma = Math.sqrt(avg2 - avg * avg);
		control.println("sigma = " + nf.format(sigma));
		control.println("number of trials= " + totalFlips);
	}

	public static void main(String[] args)
	{
		SimulationControl.createApp(new MultipleCoinTossApp(),args);
	}

	protected void doStep()
	{
		probability = control.getDouble("probability");
		int nHeads = 0;
		for (int i = 0; i < coinsToFlip; i++)
		{
			if (random.nextDouble() < probability)
			{
				nHeads++;
			}
		}
		totalFlips++;
		histogramFrame.append(nHeads);
		histogramFrame.render();
		control.clearMessages();
		output(nHeads);
	}
}
