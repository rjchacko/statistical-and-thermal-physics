package org.opensourcephysics.stp.binomial;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.stp.util.MyMath;
import org.opensourcephysics.display.DatasetManager;

/**
 * put your documentation comment here
 *
 * @author Hui Wang
 * @created Oct 18, 2006
 */
public class BinomialApp extends AbstractCalculation
{
	int N;
	double p;
	final static int MAXIMUM_NUMERATOR = 20;
	final static double tol = 1e-100;
	int counter = 1;	//color is not black
	double[] nbar = new double[100];
	
	PlotFrame plotFrame = new PlotFrame("n", "P", "P(n) versus n");
	PlotFrame plotFrameNorm = new PlotFrame("n/<n>", "P", "P(n) versus n/<n>");
	DatasetManager data = new DatasetManager();
	
	public BinomialApp()
	{
		plotFrame.setAutoscaleY(true);
		plotFrame.setAutoscaleX(true);
		plotFrameNorm.setAutoscaleY(true);
		plotFrameNorm.setAutoscaleX(true);
		
	}

	public void addPoints()
	{
		double log_N = 0;
		double log_n1 = 0, log_n2 = 0;
		boolean stirling = false;
		
		stirling = N > MAXIMUM_NUMERATOR;
		
		for (int i = 0; i <= N; i++)
		{
			
			if (stirling)
			{
				log_N = MyMath.stirling(N);
				log_n1 = MyMath.stirling(i);
				log_n2 = MyMath.stirling(N - i);
			}
			else
			{	//use double to avoid integer overflow
				log_N = Math.log(MyMath.factorial((double)N));
				log_n1 = Math.log(MyMath.factorial((double)i));
				log_n2 = Math.log(MyMath.factorial((double)(N - i)));	
			}
			
			double w = log_N - log_n1 - log_n2 + i*Math.log(p) + (N - i)*Math.log(1 - p);
			double prob = Math.exp(w);
			if(prob > tol)data.append(counter, i, prob);
		}
	}

	public void resetCalculation()
	{
		plotFrame.clearData();
		plotFrame.repaint();
		plotFrameNorm.clearData();
		plotFrameNorm.repaint();
		
		//clear all datasets
		counter=1;
		data.clear();
		N = 60;
		p = 0.5;

		control.setValue("N", N);
		control.setValue("p", p);

	}
	  
	public void calculate()
	{
		N = control.getInt("N");
		p = control.getDouble("p");
		addPoints();
		nbar[counter] = N*p;
		counter++;
		plotFrame.clearData();
		plotFrameNorm.clearData();
		
	//draw all the datasets
		for(int i = 0; i < counter; i++){
			double[] x = data.getXPoints(i);
			double[] y = data.getYPoints(i);

			for(int j = 0; j < x.length; j++){
				plotFrame.append(i, x[j], y[j]);
				plotFrameNorm.append(i, x[j]/nbar[i], y[j]);
			}
		}
		plotFrame.render();
	}

	public static void main(String[] args)
	{
		CalculationControl.createApp(new BinomialApp(),args);
	}
}
