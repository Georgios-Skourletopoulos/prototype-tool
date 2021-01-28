package com.example.usecases;

import java.math.BigDecimal;

/**
 * CalculateCocomo is a class which contains the mathematical formulas used in order to make COCOMO
 * calculations.
 * @author Georgios Skourletopoulos
 * @version 2 August 2013
 */
public final class CalculateCocomo {

	public CalculateCocomo() {}    //the constructor

	/**
	 * This method makes COCOMO calculations for an organic software project.
	 * @param kloc is the software product size in source lines of code (expressed in thousands)
	 * @return cocomo the result represented in an array of Doubles
	 */
	public static Double[] organic(BigDecimal kloc) {
		Double [] cocomo = new Double[3];
		cocomo[0] = 2.4 * Math.pow(new Double(kloc.toPlainString()),1.05);
		cocomo[1] = 2.5 * Math.pow(cocomo[0], 0.38);
		cocomo[2] = cocomo[0] / cocomo[1];
		return cocomo;
	}

	/**
	 * This method makes COCOMO calculations for a semi-detached software project.
	 * @param kloc is the software product size in source lines of code (expressed in thousands)
	 * @return cocomo the result represented in an array of Doubles
	 */
	public static Double[] semidetached(BigDecimal kloc) {
		Double [] cocomo = new Double[3];
		cocomo[0] = 3 * Math.pow(new Double(kloc.toPlainString()), 1.12);
		cocomo[1] = 2.5 * Math.pow(cocomo[0], 0.35);
		cocomo[2] = cocomo[0] / cocomo[1];
		return cocomo;
	}

	/**
	 * This method makes COCOMO calculations for an embedded software project.
	 * @param kloc is the software product size in source lines of code (expressed in thousands)
	 * @return cocomo the result represented in an array of Doubles
	 */
	public static Double[] embedded(BigDecimal kloc) {
		Double [] cocomo = new Double[3];
		cocomo[0] = 3.6 * Math.pow(new Double(kloc.toPlainString()), 1.2);
		cocomo[1] = 2.5 * Math.pow(cocomo[0], 0.32);
		cocomo[2] = cocomo[0] / cocomo[1];
		return cocomo;
	}
}
