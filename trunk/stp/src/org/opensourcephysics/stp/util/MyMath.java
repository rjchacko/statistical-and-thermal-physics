package org.opensourcephysics.stp.util;

public class MyMath
{

	private MyMath()
	{
	} // don't allow users to instantiate this class

	public static double logBase10(double n)
	{
		return Math.log(n) / Math.log(10);
	}

	public static int factorial(int n)
	{
		if (n < 0)
			throw new IllegalArgumentException(
					"Can't compute the factorial of a number less than 0");
		int total = 1;
		for (int i = n; i > 1; i--)
			total *= i;
		return total;
	}


	public static double factorial(double n)
	{
		if (n < 0)
			throw new IllegalArgumentException(
					"Can't compute the factorial of a number less than 0");
		double total = 1;
		for (double i = n; i > 1; i--)
			total *= i;
		return total;
	}


	public static double stirling(int n)
	{
		if (n == 0)
			return 0;
		double result = n * Math.log(n) - n + 0.5 * Math.log(2.0 * Math.PI * n);
		return result;
	}
}
