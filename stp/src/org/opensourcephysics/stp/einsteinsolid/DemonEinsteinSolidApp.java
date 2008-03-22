package org.opensourcephysics.stp.einsteinsolid;

import org.opensourcephysics.display.*;
import org.opensourcephysics.controls.*;
import java.text.NumberFormat;
import javax.swing.JButton;
import java.util.*;
// The demon algorithm applied to an ideal gas

public class DemonEinsteinSolidApp extends AbstractSimulation
{
	int N, dimensions, acceptedMoves = 0, mcs = 0;
	int[] e;
	int systemEnergy, demonEnergy, systemEnergyAccumulator = 0,
			demonEnergyAccumulator = 0;
	JButton logButton;
	NumberFormat nf;
	DrawingFrame frame;
    PlottingPanel panel;
    Histogram dist;
    Random rnd = new Random();
  
	public void doStep()
	{
		for (int i = 0; i < 10000 / N; i++)
		{
			for (int j = 0; j < N; ++j)
			{
				int particleIndex = (int) (Math.random() * N);
				int de = 2*rnd.nextInt(2) - 1;//get -1 or 1
				if( (de == 1 && demonEnergy > 0) || (de == -1 && e[particleIndex] > 0)){
					acceptedMoves++;
					e[particleIndex] += de;
					systemEnergy += de;
					demonEnergy -= de;
				}

				systemEnergyAccumulator += systemEnergy;
				demonEnergyAccumulator += demonEnergy;
			    dist.append(demonEnergy);
			}
			mcs++;
		}

      	panel.render();

      	//getSlope();
      	
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
		systemEnergy = control.getInt("System energy");
		dist.setBinWidth(1);

      	e = new int[N];
      	for(int i = 0; i < N; i++)e[i] = 0;
      	e[0] = systemEnergy;	//initial energy goes to the first oscillator
      	
      	demonEnergy = 0;
		mcs = 0;
		systemEnergyAccumulator = 0;
		demonEnergyAccumulator = 0;
		acceptedMoves = 0;

		control.clearMessages();
	    dist.clear();
	    panel.render();
	}

	public void reset()
	{
		control.setValue("N", 40);
		control.setValue("System energy", 20);
		control.clearMessages();
      	dist.clear();
      	panel.render();
	}

	public void flipLogScale()
	{
	    dist.logScale = !dist.logScale;
      	dist.setNormalizedToOne(!dist.logScale);
      
      	panel.setYLabel(dist.logScale ? "ln P(Ed) + C" : "P(Ed)");
      	panel.repaint();
      
      	if (logButton != null)
         	//logButton.setText((dist.logScale?"Disable":"Enable") + " log scale");
      		logButton.setText((dist.logScale?"Semilog":"Linear"));
  	}
 
   public void setLinearScale() {
      if (dist.logScale) flipLogScale();
   }   
   public void setLogScale() {
      if (!dist.logScale) flipLogScale();
   }
   
   /*
   public void getSlope(){
	   double[][] h = dist.getData();
	   Polynomial interpolator = new PolynomialLeastSquareFit(h[0], h[1], 1);
	   double[] coef = interpolator.getCoefficients();
	   control.println("c["+1+"]="+coef[1]);

	   //panel.addDrawable(new FunctionDrawer(interpolator));
 	   
   }
   */
	public DemonEinsteinSolidApp()
	{
		dist = new Histogram();
      	dist.setNormalizedToOne(true);
      	dist.adjustForWidth = true;
      	panel = new PlottingPanel("Ed", "P(Ed)", "");
      	panel.setPreferredMinMax(0.0, 5, 0, 1);
      	panel.addDrawable(dist);      
      	panel.setAutoscaleX(true);
      	panel.setAutoscaleY(true);
      	
      	frame = new DrawingFrame("Demon Energy Distribution", panel);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
	}

	public static void main(String[] args)
	{
		DemonEinsteinSolidApp application = new DemonEinsteinSolidApp();
                SimulationControl control=SimulationControl.createApp(application,args);
		application.logButton = control.addButton("flipLogScale","Linear");
	}
}
