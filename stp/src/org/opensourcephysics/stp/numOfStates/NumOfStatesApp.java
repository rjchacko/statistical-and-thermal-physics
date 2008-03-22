package org.opensourcephysics.stp.numOfStates;

import org.opensourcephysics.controls.AbstractCalculation;
import org.opensourcephysics.controls.CalculationControl;
import org.opensourcephysics.frames.PlotFrame;

public class NumOfStatesApp extends AbstractCalculation{
	int dim = 2;
	double R = 10;
	PlotFrame plotFrame = new PlotFrame("R", "NumOfStates", "Number Of States");

	/**
	 * put your documentation comment here
	 *
	 * @author Hui Wang
	 * @created Oct 24, 2006
	 */
	
	public void resetCalculation()
	{
		plotFrame.setConnected(1,true);
		plotFrame.setMarkerSize(1,0);
		
		plotFrame.clearData();
		plotFrame.repaint();
		control.setValue("dimension", dim);
		control.setValue("R", R);
	}
	  
	public void calculate()
	{
		dim = control.getInt("dimension");
		R = control.getDouble("R");
		for(int r = 0; r <= R; r++){
			if(dim == 2){
				plotFrame.append(0,r,countNumOfStates2(r));
				plotFrame.append(1,r,1.0/4*Math.PI*r*r);
			} else if(dim == 3){
				plotFrame.append(0,r,countNumOfStates3(r));
				plotFrame.append(1,r,1.0/6*Math.PI*r*r*r);
			} else if(dim == 1){
				plotFrame.append(0,r,countNumOfStates1(r));
				plotFrame.append(1,r,r);
			}
		}
	}

	public int countNumOfStates3(int r){
		int r2 = r*r;
		int counter = 0;
		int n2;
		
		for(int nx = 0; nx <= r; nx++){
			for(int ny = 0; ny <= r; ny++){
				for(int nz = 0; nz <= r; nz++){
					n2 = nx*nx + ny*ny + nz*nz;
					if(n2 < r2)counter++;
				}
			}
		}
		return counter;		
	}
	
	public int countNumOfStates2(int r){
		int r2 = r*r;
		int counter = 0;
		int n2;
		
		for(int nx = 0; nx <= r; nx++){
			for(int ny = 0; ny <= r; ny++){
				n2 = nx*nx + ny*ny;
				if(n2 < r2)counter++;
			}
		}
		return counter;		
	}

	public int countNumOfStates1(int r){
		int r2 = r*r;
		int counter = 0;
		int n2;
		
		for(int nx = 0; nx <= r; nx++){
			n2 = nx*nx;
			if(n2 < r2)counter++;
		}
		return counter;		
	}
	
	
	public static void main(String[] args)
	{
		CalculationControl.createApp(new NumOfStatesApp(),args);
	}

}
