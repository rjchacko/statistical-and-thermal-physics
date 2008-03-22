package org.opensourcephysics.stp.oscillatornh;
import java.awt.Color;
import java.awt.Graphics;
import org.opensourcephysics.display.Drawable;
import org.opensourcephysics.display.DrawingPanel;


public class Osci2 implements Drawable {
	double x,vx,accel;
	double K = 1.0;
	double psi;
	double T = 2.5;
	double Q = 0.05;
	double s = 0;
	
	double t = 0.0;
	double Et;
	
	public Osci2(){
		vx = accel = 0;
		x = 1.0;
		psi = 0;
	}
	
	public void step(double dt){
		double vo,vn,v2,psin,psio;
		double err = 1.0,err1 = 1.0;
		double dpsi = 0, bx;
		
		t += dt;
		
		//psi = 0;
		x += vx*dt + (accel - psi*vx)*0.5*dt*dt;
		vx += (accel - psi*vx)*0.5*dt;
		
		s += psi*dt + (vx*vx - T)*0.5*dt*dt/Q;
		psi += (vx*vx - T)*0.5*dt/Q;
		getAccel();
		
		vx += accel*0.5*dt;

		//Force is velocity dependent
		//iterate
		vn = vx;
		psin = psi;
		
		
		Et = 0.5*K*x*x + 0.5*vx*vx;// + psi*psi*Q/2;
		//if(true)return;
		
		int counter = 0;
		while(err > 1e-10 || err1 > 1e-10){
			counter++;
			dpsi = 0;
			
			vo = vn;
			psio = psin;
			
			bx = -0.5*dt*(accel - vo) - (vx - vo);
			dpsi += bx*vo*dt/Q;
			
			
			v2 = vn*vn;
			psin = psi + (v2 - T)*0.5*dt/Q;
			vn = vx - psin*vn*0.5*dt;
			
			err = Math.abs(vn - vo)/Math.abs(vo);
			err1 = Math.abs((psin - psio)/psio);
		}
		
		vx = vn;
		psi = psin;
		Et = 0.5*K*x*x + 0.5*vx*vx + psi*psi*Q/2;
		
	}
	
	public double getAccel(){
		accel = -K*x;
		return accel;
	}
	
	public void draw(DrawingPanel myWorld, Graphics g)
	{
		double radius = 0.5/2.0;
		int pxRadius = Math.abs(myWorld.xToPix(radius) - myWorld.xToPix(0));
		int pyRadius = Math.abs(myWorld.yToPix(radius) - myWorld.yToPix(0));
		
		//Make sure circle size is not 0
		if(pxRadius < 1)pxRadius = 1;
		if(pyRadius < 1)pxRadius = 1;
		
		g.setColor(Color.red);
		
		int xpix = myWorld.xToPix(x) - pxRadius;

		g.fillOval(xpix, 10, 2 * pxRadius, 2 * pyRadius);

	}

}

