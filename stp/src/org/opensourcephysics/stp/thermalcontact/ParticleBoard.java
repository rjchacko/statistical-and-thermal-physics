package org.opensourcephysics.stp.thermalcontact;

import org.opensourcephysics.display.*;
import java.awt.*;


/**
* A drawable object containing a set of particles inside a box.  Particle positions are specified
* in two dimensions.  Particle centers must lie within the box dimensions.
* @author Kipton Barros
*/

public class ParticleBoard implements Measurable {
   /**
   * The number of A and B particles
   */
   public int Na, Nb;
   
   /**
   * The size of the board
   */ 
   public double Lx, Ly;

   /**
   * The base offset from which to draw this board
   */
   public double Bx = 0, By = 0;
      
   /**
   * The position of particles
   */
   public double[] pos_x, pos_y;
   
   /**
   * The diameter of particles used for display
   */
   public double diam;
   
   /**
   * The opacity of particles.  A number between 0 (transparent) and 255 (opaque)
   */
   public int opacity = 200;
   
   /**
   * Create a new ParticleBoard.
   * @param N  The number of particles to store
   * @param Lx  The x length of the particle box
   * @param Ly  The y length of the particle box
   * @param diam  The diameter of particles
   */
   public ParticleBoard(int _Na, int _Nb, double _Lx, double _Ly, double _diam) {
      Na = _Na;
      Nb = _Nb;      
      Lx = _Lx;
      Ly = _Ly;
      diam = _diam;
      
      pos_x = new double[Na + Nb];
      pos_y = new double[Na + Nb];
   }
   
   /**
   * Specify a particle's position.
   * @param i  Reference the i'th particle
   * @param x  The new x position
   * @param y  The new y position
   */
   public void setParticle(int i, double x, double y) {
      pos_x[i] = x;
      pos_y[i] = y;
   }
   
   // Method for interface (Drawable >> Measurable)
   //
   // This code is very confusing because (yToPix(1) < yToPix(0)), so many signs
   // must be reversed
   //
   public void draw(DrawingPanel panel, Graphics g) {
      int _rx = panel.xToPix(diam/2) - panel.xToPix(0);
      int _ry = - (panel.yToPix(diam/2) - panel.yToPix(0));
      int _Lx = panel.xToPix(Lx) - panel.xToPix(0);
      int _Ly = - (panel.yToPix(Ly) - panel.yToPix(0));
      
      g.setColor(new Color(90,90,255,128));
      g.fillRect(panel.xToPix(Bx), panel.yToPix(By) - _Ly, _Lx, _Ly);
      
      g.setColor(new Color(255, 128, 128, opacity));
      for (int i = 0; i < Na; i++) {
         g.fillOval( panel.xToPix(Bx + pos_x[i] - diam/2),
                     panel.yToPix(By + pos_y[i] + diam/2),
                     2*_rx, 2*_ry);
      }
      g.setColor(new Color(255, 40, 40, 255));
      for (int i = 0; i < Na; i++) {
         g.drawOval( panel.xToPix(Bx + pos_x[i] - diam/2),
                     panel.yToPix(By + pos_y[i] + diam/2),
                     2*_rx, 2*_ry);
      }
      
      g.setColor(new Color(128, 255, 128, opacity));
      for (int i = Na; i < Na+Nb; i++) {
         g.fillOval( panel.xToPix(Bx + pos_x[i] - diam/2),
                     panel.yToPix(By + pos_y[i] + diam/2),
                     2*_rx, 2*_ry);
      }
      g.setColor(new Color(40, 140, 80, 255));
      for (int i = Na; i < Na+Nb; i++) {
         g.drawOval( panel.xToPix(Bx + pos_x[i] - diam/2),
                     panel.yToPix(By + pos_y[i] + diam/2),
                     2*_rx, 2*_ry);
      }
   }
   
   // Methods for interface Measurable
   //
   public double getXMin() {
      return Bx - diam/2;
   }
   
   public double getXMax() {
      return Bx + Lx + diam/2;
   }
   
   public double getYMin() {
      return By - diam/2;
   }
   
   public double getYMax() {
      return By + Ly + diam/2;
   }
   
   public boolean isMeasured() {
      return true;
   }
}
