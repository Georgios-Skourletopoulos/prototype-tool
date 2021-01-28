/**
 * The required package.
 */
package com.example.JUnits;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.example.usecases.CalculateCocomo;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * CalculateCocomoTest is a JUnit testing class for all the methods included in the CalculateCocomo
 * class.
 * @author Georgios Skourletopoulos
 * @version 18 August 2013
 */
public class CalculateCocomoTest {

	@SuppressWarnings("unused")
	private CalculateCocomo test;

	@Before
	public void setUp(){
		test = new CalculateCocomo();
	}

	@After
	public void tearDown(){
		test = null;
	}

	@SuppressWarnings("deprecation")
	@Test
	public void organicTest() {
		BigDecimal kloc = new BigDecimal(12);
		Double [] testCocomo = new Double[3];
		DecimalFormat df = new DecimalFormat("#.00");
		testCocomo[0] = 32.61;
		testCocomo[1] = 9.40;
		testCocomo[2] = 3.47;
		Double [] resultCocomo =  CalculateCocomo.organic(kloc);
		for(int i = 0; i < 3; i++) {
			resultCocomo[i] = new Double(df.format(resultCocomo[i]));
		}
		assertEquals(testCocomo, resultCocomo);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void semidetachedTest() {
		BigDecimal kloc = new BigDecimal(12);
		Double [] testCocomo = new Double[3];
		DecimalFormat df = new DecimalFormat("#.00");
		testCocomo[0] = 48.51;
		testCocomo[1] = 9.73;
		testCocomo[2] = 4.99;
		Double [] resultCocomo =  CalculateCocomo.semidetached(kloc);
		for(int i = 0; i < 3; i++) {
			resultCocomo[i] = new Double(df.format(resultCocomo[i]));
		}
		assertEquals(testCocomo, resultCocomo);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void embeddedTest() {
		BigDecimal kloc = new BigDecimal(12);
		Double [] testCocomo = new Double[3];
		DecimalFormat df = new DecimalFormat("#.00");
		testCocomo[0] = 71.01;
		testCocomo[1] = 9.78;
		testCocomo[2] = 7.26;
		Double [] resultCocomo =  CalculateCocomo.embedded(kloc);
		for(int i = 0; i < 3; i++) {
			resultCocomo[i] = new Double(df.format(resultCocomo[i]));
		}
		assertEquals(testCocomo, resultCocomo);
	}
}
