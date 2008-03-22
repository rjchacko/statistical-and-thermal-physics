package org.opensourcephysics.stp.extremeValue;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.Dataset;
import org.opensourcephysics.display.Histogram;
import org.opensourcephysics.display.HistogramDataset;
import org.opensourcephysics.frames.HistogramFrame;
import org.opensourcephysics.frames.PlotFrame;

public class ExtremeValueApp extends AbstractCalculation{

	Random random;
	PlotFrame distributions = new PlotFrame("x", "H(x)", "Histogram");
	Dataset gaussian = new HistogramDataset(0, 1, 0.001);
	Dataset LEV = new HistogramDataset(0, 1, 0.001);
	Dataset GEV = new HistogramDataset(0, 1, 0.001);
	int numberOfSamples;
	int numberOfTrials;
	
	public ExtremeValueApp(){
		this.random = new Random(System.currentTimeMillis());
		LEV.setMarkerColor(Color.red);
		GEV.setMarkerColor(Color.green);
		gaussian.setMarkerColor(Color.blue);
	}
	
	public void calculate() {
		
		this.numberOfSamples=control.getInt("Number of Samples");
		this.numberOfTrials=control.getInt("Number of Trials");
		double sample[]=new double[numberOfSamples];
		double sum=0;
		clearData();
		for(int i=0;i<this.numberOfTrials;i++){
			for(int j=0;j<this.numberOfSamples;j++){
				sample[j]=random.nextDouble();
				sum+=sample[j];
			}
			Arrays.sort(sample);
			LEV.append(sample[0],1);
			GEV.append(sample[numberOfSamples-1],1);
			sum=sum/numberOfSamples;
			gaussian.append(sum,1);
		}
//		distributions.append(0, LEV.getXPoints(), LEV.getYPoints());
//		distributions.append(1, GEV.getXPoints(), GEV.getYPoints());
		distributions.addDrawable(LEV);
		distributions.addDrawable(GEV);
		distributions.addDrawable(gaussian);
	}
	
	public void reset()
	{
		control.setValue("Number of Samples", 10);
		control.setValue("Number of Trials", 1000000);
		control.clearMessages();
		clearData();
	}

	public void clearData()
	{
		LEV.clear();
		GEV.clear();
		gaussian.clear();
		distributions.clearData();
	}

	public static void main(String[] args) {
		
		CalculationControl.createApp(new ExtremeValueApp(),args);
	}

	
	

}
