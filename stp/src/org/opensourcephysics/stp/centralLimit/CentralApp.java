package org.opensourcephysics.stp.centralLimit;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.ControlUtils;
import org.opensourcephysics.controls.OSPCombo;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.display.Histogram;
import org.opensourcephysics.display.PlottingPanel;

public class CentralApp  extends AbstractSimulation {
   int n;

   int trials;

   double x_accum, x2_accum;
   double y_accum, y2_accum;
   double variancex;
   double lambda = 0.5;
   
   DrawingFrame frame;
   PlottingPanel panel;
   Histogram dist;

   String xDistribution;

   public double sample_x_uniform() {
      // Generate a uniform distribution in [0, 1]
      return Math.random();

   }
   public double sample_x_exponential() {
      // Generate distribution f(x) = 1/lambda* e^(-x/lambda), for x > 0
      return -lambda*Math.log(1 - Math.random());
   }

   public double sample_x_lorentz() {
      // Generate Lorentzian distribution with mean 0.5
      return Math.tan(Math.PI * sample_x_uniform());

   }

   public double sample_y() {
      double y = 0;
      double x;
      
      for (int i = 0; i < n; i++) {
         if (xDistribution.equals("uniform")){
        	 	variancex = 1.0/12;
        	 	x = sample_x_uniform();
        	 	x_accum += x;
        	 	x2_accum += x*x;
        	 	y += x;
         }else if (xDistribution.equals("exponential")){
        	 	variancex = lambda*lambda;
        	 	x = sample_x_exponential();
        	 	x_accum += x;
        	 	x2_accum += x*x;
        	 	y += x;
         }else if (xDistribution.equals("Lorentz")){
        	 	x = sample_x_lorentz();
        	 	x_accum += x;
        	 	x2_accum += x*x;
            y += x;
         }
      }
      return y / n;
   }

   public void doStep() {
      for (int t = 0; t < 1000; t++) {
         double y = sample_y();
         y_accum  += y;
         y2_accum += y*y;
         trials++;
         dist.append(y);
      }
      panel.render();
   }


   public void initialize() {
      dist.clear();
      frame.repaint();
      
      x_accum = 0;
      x2_accum = 0;
      y_accum = 0;
      y2_accum = 0;

      trials = 0;

      n = control.getInt("N");
      dist.setBinWidth(0.03/Math.sqrt((double)n));	//variable bin width
      
      xDistribution = control.getString("distribution");
      control.clearMessages();

   }

   public void reset() {

      control.setValue("N", 12);

      //control.setValue("BinWidth", 0.01);
      OSPCombo combo = new OSPCombo(new String[] {"uniform",  "exponential", "Lorentz"},0);  // second argument is default
	  control.setValue("distribution", combo);
      //control.setValue("(U)niform/(E)xponential/(L)orentz", "U");

      initialize();

   }


   public void stop() {
	  double y_avg  = y_accum / trials;
      double y2_avg = y2_accum / trials;

      double variancey = y2_avg - y_avg*y_avg;
      variancey = variancey*trials/(trials - 1);
      
      control.println();
      if(xDistribution.equals("l")){
    	  control.println("<x> = " + 0.0);
      } else {
    	  control.println("<x> = " + 0.5 + "; variance of x = " + ControlUtils.f4(variancex));  
      }
      control.println("<y> = " + ControlUtils.f2(y_avg));
      control.println("sample variance s\u00b2= " + ControlUtils.f4(variancey));
   }
 

   public CentralApp() {
      panel = new PlottingPanel("y", "P(y)", "");
      panel.setPreferredMinMax(-1, 1, 0, 1);
      frame = new DrawingFrame("P(y)", panel);

      frame.setVisible(true);
      dist = new Histogram();
      dist.setNormalizedToOne(true);
      panel.addDrawable(dist);
      panel.setAutoscaleX(true);
      panel.setAutoscaleY(true);

   }


   public static void main (String[] args) {
		SimulationControl.createApp(new CentralApp(),args);
   }

}

