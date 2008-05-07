package org.opensourcephysics.stp.lj;
import org.opensourcephysics.display.*;
import java.awt.*;
/**
 * Simulates Lennard Jones interactions .
 * 
 * @author Jan Tobochnik
 * @author Joshua Gould
 * @author Peter Sibley
 * 
 */
public class LJfluid implements Drawable
{
	public double x[], y[], vx[], vy[], ax[], ay[];
	public int N; // number of particles
	public double Lx, Ly;
	public double rho = N / (Lx * Ly);
	public double initialKineticEnergy;
	public int steps = 0;
	public double dt = 0.01;
	private double rCutoff2 = 3.0 * 3.0;
	public double t;
	public double totalKineticEnergy, totalPotentialEnergy;
	public double totalPotentialEnergyAccumulator;
	public double totalKineticEnergyAccumulator,
			totalKineticEnergySquaredAccumulator;
	public double virial, virialAccumulator;
	public String initialConfiguration;
	Histogram velocityHistogram = new Histogram();
	public double radius = 0.5; // radius of particles on screen
	boolean potential=true;
	
	
	public LJfluid()
	{
	
	}

	public void setArrays()
	{
		x = new double[N];
		y = new double[N];
		vx = new double[N];
		vy = new double[N];
		ax = new double[N];
		ay = new double[N];
	}

	public void initialize()
	{
		reset();
		setArrays();
		if (initialConfiguration.equals("crystal"))
		{
			setCrystalPositions();
		}
		else
		{
			setRandomPositions();
		}
		setVelocities();
		accel();
	}

	public void setRandomPositions()
	{
		// particles placed at random, but not closer than rMinimumSquared
		double rMinimumSquared = Math.pow(2.0, 1.0 / 3.0);
		boolean overlap;
		for (int i = 0; i < N; ++i)
		{
			do
			{
				overlap = false;
				x[i] = Lx * Math.random();
				y[i] = Ly * Math.random();
				int j = 0;
				while (j < i && !overlap)
				{
					double dx = x[i] - x[j];
					double dy = y[i] - y[j];
					if (dx * dx + dy * dy < rMinimumSquared)
					{
						overlap = true;
					}
					j++;
				}
			}
			while (overlap);
		}
	}

	public void setCrystalPositions()
	{
		// place particles on triangular lattice
		double dnx = Math.sqrt(N);
		int ns = (int) dnx;
		if (dnx - ns > 0.001)
		{
			ns++;
		}
		double ax = Lx / ns;
		double ay = Ly / ns;
		int i = 0;
		int iy = 0;
		while (i < N)
		{
			for (int ix = 0; ix < ns; ++ix)
			{
				if (i < N)
				{
					y[i] = ay * (iy + 0.5);
					if (iy % 2 == 0)
						x[i] = ax * (ix + 0.25);
					else
						x[i] = ax * (ix + 0.75);
					i++;
				}
			}
			iy++;
		}
	}

	public void setVelocities()
	{
		double twoPi = 2.0 * Math.PI;
		for (int i = 0; i < N; ++i)
		{
			double r = Math.random(); // use to generate exponential
										// distribution
			double a = -Math.log(r);
			double theta = twoPi * Math.random();
			// assign velocities according to Maxwell-Boltzmann distribution
			// using Box-Muller method
			vx[i] = Math.sqrt(2.0 * a * initialKineticEnergy) * Math.cos(theta);
			vy[i] = Math.sqrt(2.0 * a * initialKineticEnergy) * Math.sin(theta);
		}
		// zero center of mass momentum
		double vxSum = 0.0;
		double vySum = 0.0;
		for (int i = 0; i < N; ++i)
		{
			vxSum += vx[i];
			vySum += vy[i];
		}
		double vxcm = vxSum / N; // center of mass momentum (velocity)
		double vycm = vySum / N;
		for (int i = 0; i < N; ++i)
		{
			vx[i] -= vxcm;
			vy[i] -= vycm;
			appendVelocityPoint(i);
		}
	}

	public void reset()
	{
		t = 0;
		Ly = Lx * Math.sqrt(0.75);
		rho = N / (Lx * Ly);
		resetAverages();
		velocityHistogram.setBinWidth(2 * initialKineticEnergy / N); // assuming
																		// vmax=2*initalTemp
																		// and
																		// bin
																		// width
																		// =
																		// Vmax/N
		velocityHistogram.setBinOffset(initialKineticEnergy / N);
	}

	public void resetAverages()
	{
		steps = 0;
		virialAccumulator = 0;
		totalPotentialEnergyAccumulator = 0;
		totalKineticEnergyAccumulator = 0;
		totalKineticEnergySquaredAccumulator = 0;
		velocityHistogram.clear();
	}

	public double getMeanTemperature()
	{
		return totalKineticEnergyAccumulator / (N * steps);
	}

	public double getInstantaneousTemperature()
	{
		return totalKineticEnergy / N;
	}

	public double getInstantaneousTotalEnergy()
	{
		return totalKineticEnergy + totalPotentialEnergy;
	}

	public double getInstantanousKineticEnergy()
	{
		totalKineticEnergy = 0;
		for (int i = 0; i < N; i++)
		{
			totalKineticEnergy += (vx[i] * vx[i] + vy[i] * vy[i]);
		}
		totalKineticEnergy = 0.5 * totalKineticEnergy;
		return totalKineticEnergy;
	}

	public double getMeanEnergy()
	{
		return totalKineticEnergyAccumulator / steps
				+ totalPotentialEnergyAccumulator / steps;
	}

	public double getMeanPressure()
	{
		double meanVirial;
		meanVirial = virialAccumulator / steps;
		return 1.0 + 0.5 * meanVirial / (N * getMeanTemperature()); // quantity
																	// PV/NkT
	}

	public double getInstantanousPressure()
	{
		return 1.0 + 0.5 * virial / totalKineticEnergy; // quantity PV/NkT
	}

	public double getHeatCapacity()
	{
		double meanTemperature = getMeanTemperature();
		double meanTemperatureSquared = totalKineticEnergySquaredAccumulator
				/ steps;
		double sigma2 = meanTemperatureSquared - meanTemperature
				* meanTemperature;
		// heat capacity related to fluctuations of temperature
		double denom = sigma2 / (N * meanTemperature * meanTemperature) - 1.0;
		return N / denom;
	}

	public void accel()
	{
		double cutoff;
		if(potential) cutoff=rCutoff2;
		else cutoff=Math.pow(2, 1./6.);
		virial = 0;
		double dx, dy, fx, fy, r2, fOverR, oneOverR2, oneOverR6;
		totalPotentialEnergy = 0;
		for (int i = 0; i < N; i++)
		{
			ax[i] = 0;
			ay[i] = 0;
		}
		for (int i = 0; i < N - 1; i++)
		{
			for (int j = i + 1; j < N; j++)
			{
				dx = pbc(x[i] - x[j], Lx);
				dy = pbc(y[i] - y[j], Ly);
				r2 = dx * dx + dy * dy;
				if (r2 < cutoff)
				{
					oneOverR2 = 1.0 / r2;
					oneOverR6 = oneOverR2 * oneOverR2 * oneOverR2;
					fOverR = 48.0 * oneOverR6 * (oneOverR6 - 0.5) * oneOverR2;
					fx = fOverR * dx;
					fy = fOverR * dy;
					ax[i] += fx;
					ay[i] += fy;
					ax[j] -= fx;
					ay[j] -= fy;
					if(potential)totalPotentialEnergy += 4.0 * (oneOverR6 * oneOverR6 - oneOverR6);
					else totalPotentialEnergy += 4.0 * (oneOverR6 * oneOverR6 - oneOverR6)+1;
					virial += dx * fx + dy * fy;
				}
			}
		}
	}

	private double pbc(double ds, double L)
	{
		if (ds > 0.5 * L)
		{
			ds -= L;
		}
		else if (ds < -0.5 * L)
		{
			ds += L;
		}
		return ds;
	}

	private double image(double s, double L)
	{
		if (s > L)
		{
			s -= L;
		}
		else if (s < 0)
		{
			s += L;
		}
		return s;
	}

	public void step()
	{ // velocity Verlet algorithm
		double dt2half = 0.5 * dt * dt;
		double oneHalfDt = 0.5 * dt;
		totalKineticEnergy = 0;
		for (int i = 0; i < N; i++)
		{
			x[i] += vx[i] * dt + ax[i] * dt2half;
			y[i] += vy[i] * dt + ay[i] * dt2half;
			x[i] = image(x[i], Lx);
			y[i] = image(y[i], Ly);
			vx[i] += ax[i] * oneHalfDt;
			vy[i] += ay[i] * oneHalfDt;
		}
		accel();
		for (int i = 0; i < N; i++)
		{
			vx[i] += ax[i] * oneHalfDt;
			vy[i] += ay[i] * oneHalfDt;
			totalKineticEnergy += (vx[i] * vx[i] + vy[i] * vy[i]);
			appendVelocityPoint(i);
		}
		totalKineticEnergy = 0.5 * totalKineticEnergy;
		steps++;
		totalPotentialEnergyAccumulator += totalPotentialEnergy;
		totalKineticEnergyAccumulator += totalKineticEnergy;
		totalKineticEnergySquaredAccumulator += totalKineticEnergy
				* totalKineticEnergy;
		virialAccumulator += virial;
		t += dt;
	}

	public void quench(double quenchRate)
	{
		for (int i = 0; i < N; i++)
		{
			vx[i] *= quenchRate;
			vy[i] *= quenchRate;
		}
	}

	public void appendVelocityPoint(int i)
	{
		velocityHistogram.append(vx[i]);
		// velocityHistogram.append(vy[i]);
	}

	public Histogram getVelocityHistogram()
	{
		return velocityHistogram;
	}

	public void draw(DrawingPanel myWorld, Graphics g)
	{
		if (x == null)
		{
			return;
		}
		int pxRadius = Math.abs(myWorld.xToPix(radius) - myWorld.xToPix(0));
		int pyRadius = Math.abs(myWorld.yToPix(radius) - myWorld.yToPix(0));
		g.setColor(Color.blue);
		int xpix = myWorld.xToPix(x[0]) - pxRadius;
		int ypix = myWorld.yToPix(y[0]) - pyRadius;
		g.fillOval(xpix, ypix, 2 * pxRadius, 2 * pyRadius);
		g.setColor(Color.red);
		for (int i = 1; i < N; i++)
		{
			xpix = myWorld.xToPix(x[i]) - pxRadius;
			ypix = myWorld.yToPix(y[i]) - pyRadius;
			g.fillOval(xpix, ypix, 2 * pxRadius, 2 * pyRadius);
		}
		g.setColor(Color.black);
		xpix = myWorld.xToPix(0);
		ypix = myWorld.yToPix(0);
		int lx = myWorld.xToPix(Lx) - myWorld.xToPix(0);
		int ly = myWorld.yToPix(Ly) - myWorld.yToPix(0);
		g.drawRect(xpix, ypix, lx, ly);
	}
}
