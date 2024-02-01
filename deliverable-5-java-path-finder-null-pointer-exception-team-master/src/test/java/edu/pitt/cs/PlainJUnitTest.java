package edu.pitt.cs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "SpotBugs apparently does not know about @Before methods so "
		+ "it keeps complaining about the logics field not getting initialized in constructor")

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlainJUnitTest {

	private final int[] beanCounts = { 0, 2, 20, 200 }; // different test bean amounts
	private final int[] logicSlotCounts = { 1, 10, 20 }; // different test slot amounts
	private BeanCounterLogic[] logics; // IMPL, or BUGGY, or SOLUTION
	private Random rand;

	ByteArrayOutputStream out;
	PrintStream stdout;

	private String getFailString(int logicIndex, int beanCount) {
		return "[Slot Count = " + logicSlotCounts[logicIndex] + "] Test case with " + beanCount
				+ " initial beans failed";
	}

	private Bean[] createBeans(int slotCount, int beanCount, boolean luck) {
		Bean[] beans = new Bean[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = Bean.createInstance(slotCount, luck, rand);
		}
		return beans;
	}

	// counts all beans in movement in all xpos including the top
	private int getInFlightBeanCount(BeanCounterLogic logic, int slotCount) {
		int inFlight = 0;
		for (int yPos = 0; yPos < slotCount; yPos++) {
			int xPos = logic.getInFlightBeanXPos(yPos);
			if (xPos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
				inFlight++;
			}
		}
		return inFlight;
	}

	// number of beans in all the slots. slots are the end bucket
	private int getInSlotsBeanCount(BeanCounterLogic logic, int slotCount) {
		int inSlots = 0;
		for (int i = 0; i < slotCount; i++) {
			inSlots += logic.getSlotBeanCount(i);
		}
		return inSlots;
	}

	/**
	 * The test fixture. Creates multiple machines (logics) with different slot
	 * counts. It also creates a real random object. But the random object is seeded
	 * with 42 so the tests will be reproducible.
	 */
	@Before
	public void setUp() {
		logics = new BeanCounterLogic[logicSlotCounts.length];
		for (int i = 0; i < logics.length; i++) {
			logics[i] = BeanCounterLogic.createInstance(logicSlotCounts[i]);
		}
		rand = new Random(42);

		out = new ByteArrayOutputStream();
		stdout = System.out;
		try {
			System.setOut(new PrintStream(out, false, Charset.defaultCharset().toString()));
		} catch (UnsupportedEncodingException uex) {
			fail();
		}
	}

	/**
	 * After teardown().
	 */
	@After
	public void tearDown() {
		logics = null;
		rand = null;
		out = null;

		System.setOut(stdout);
	}

	/**
	 * Test reset(Bean[]).
	 * 
	 * <pre>
	 * Preconditions: logics for each slot count in logicSlotCounts are created.
	 * Execution steps: For each logic, and for each bean count in beanCounts,
	 *                  Call createBeans to create lucky beans for the slot count and bean count
	 *                  Call logic.reset(beans). 
	 * Invariants: If initial bean count is greater than 0,
	 *             remaining bean count is beanCount - 1
	 *             in-flight bean count is 1 (the bean initially at the top)
	 *             in-slot bean count is 0.
	 *             If initial bean count is 0,
	 *             remaining bean count is 0
	 *             in-flight bean count is 0
	 *             in-slot bean count is 0.
	 * </pre>
	 */
	@Test
	public void testReset() {
		for (int i = 0; i < logics.length; i++) { // tests each logic IMPL, BUGGY, and SOLUTION
			for (int beanCount : beanCounts) { // different cases of diff bean amounts
				String failString = getFailString(i, beanCount);
				Bean[] beans = createBeans(logicSlotCounts[i], beanCount, true);
				logics[i].reset(beans);

				int remainingObserved = logics[i].getRemainingBeanCount();
				int inFlightObserved = getInFlightBeanCount(logics[i], logicSlotCounts[i]);
				int inSlotsObserved = getInSlotsBeanCount(logics[i], logicSlotCounts[i]);
				int remainingExpected = (beanCount > 0) ? beanCount - 1 : 0; // becaus the bean in (0,0) doesnt count
				int inFlightExpected = (beanCount > 0) ? 1 : 0;
				int inSlotsExpected = 0;

				assertEquals(failString + ". Check on remaining bean count",
						remainingExpected, remainingObserved);
				assertEquals(failString + ". Check on in-flight bean count",
						inFlightExpected, inFlightObserved);
				assertEquals(failString + ". Check on in-slot bean count",
						inSlotsExpected, inSlotsObserved);
			}
		}
	}

	/**
	 * Test advanceStep() in luck mode.
	 * 
	 * <pre>
	 * Preconditions: logics for each slot count in logicSlotCounts are created.
	 * Execution steps: For each logic, and for each bean count in beanCounts,
	 *                  Call createBeans to create lucky beans for the slot count and bean count
	 *                  Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * Invariants: After each advanceStep(),
	 *             1) The remaining bean count, 2) the in-flight bean count, and 3) the in-slot bean count
	 *             all reflect the correct values at that step of the simulation.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepLuckMode() {
		for (int i = 0; i < logics.length; i++) {
			for (int beanCount : beanCounts) {
				final String failString = getFailString(i, beanCount);
				Bean[] beans = createBeans(logicSlotCounts[i], beanCount, true);
				logics[i].reset(beans); // one bean in (0,0)
				int steps = 0;
				int remainingExpected = beanCount - 1;
				int inSlotsExpected = 0;
				int inFlightExpected = 0;
				while (logics[i].advanceStep()) { // loop until returns false

					steps++;
					final int remainingObserved = logics[i].getRemainingBeanCount();
					final int inFlightObserved = getInFlightBeanCount(logics[i], logicSlotCounts[i]);
					final int inSlotsObserved = getInSlotsBeanCount(logics[i], logicSlotCounts[i]);

					// has it entered the bucket yet?
					// after [total slots] steps there is a bean entering the slot each step
					if (steps < logicSlotCounts[i]) {
						inSlotsExpected = 0;
					}
					if (steps == logicSlotCounts[i]) {
						inSlotsExpected = 1;
					}
					if (steps > logicSlotCounts[i]) {
						inSlotsExpected += 1;
					}

					// A new bean is inserted into the top of the machine if there are beans
					// remaining.
					// if there was a bean left last round we count the steps plus the top bean and
					// - slot beans
					// if no beans left we count the total beans - slot beans
					if (remainingExpected <= 0) {
						inFlightExpected = (beanCount > 0) ? (beanCount - inSlotsExpected) : 0;
					}
					if (remainingExpected > 0) {
						inFlightExpected = (beanCount > 0) ? ((steps + 1) - inSlotsExpected) : 0;
					}

					// beancount goes down 1 everytime
					remainingExpected = (beanCount > 0) ? (remainingExpected - 1) : 0;
					if (remainingExpected < 0) {
						remainingExpected = 0;
					}

					assertEquals(failString + ". Check on remaining bean count",
							remainingExpected, remainingObserved);
					assertEquals(failString + ". Check on in-flight bean count",
							inFlightExpected, inFlightObserved);
					assertEquals(failString + ". Check on in-slot bean count",
							inSlotsExpected, inSlotsObserved);
				}
			}
		}
	}

	/**
	 * Test advanceStep() in skill mode.
	 * 
	 * <pre>
	 * Preconditions: logics for each slot count in logicSlotCounts are created.
	 *                - rand.nextGaussian() always returns 0 (to fix skill level to 5).
	 * Execution steps: For the logic with 10 slot counts,
	 *                  Call createBeans to create 200 skilled beans
	 *                  Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * Invariants: After the machine terminates,
	 *             logics.getSlotBeanCount(5) returns 200,
	 *             logics.getSlotBeanCount(i) returns 0, where i != 5.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkillMode() {
		Random r1;
		r1 = Mockito.mock(Random.class);
		Mockito.when(r1.nextGaussian()).thenReturn(0.0);

		int slots = logicSlotCounts[1]; // 10 slots
		int beanCnt = beanCounts[3]; // 200 beans
		String failString = getFailString(1, beanCnt);
		Bean[] beans = new Bean[beanCnt];
		for (int i = 0; i < beanCnt; i++) {
			beans[i] = Bean.createInstance(slots, false, r1);
		}

		logics[1].reset(beans); // one bean in (0,0)
		while (logics[1].advanceStep()) {
		} // loop until returns false

		for (int i = 0; i < slots; i++) {
			if (i == 5) {
				assertEquals(failString, 200, logics[1].getSlotBeanCount(5));
			} else {
				assertEquals(failString, 0, logics[1].getSlotBeanCount(i));
			}
		}
	}

	/**
	 * Test lowerHalf() in luck mode.
	 * 
	 * <pre>
	 * Preconditions: logics for each slot count in logicSlotCounts are created.
	 * Execution steps: For the logic with 10 slot counts, and for each bean count in beanCounts,
	 *                  Call createBeans to create lucky beans for the slot count and bean count
	 *                  Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                 - Calculate expected bean counts for each slot after having called logic.lowerHalf(),
	 *                  from current slot bean counts, and store into an expectedSlotCounts array.
	 *                  Call logic.lowerHalf().
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testLowerHalf() {
		int slots = logicSlotCounts[1];
		int[] expectedSlotCounts = new int[slots];
		int[] observedSlotCounts = new int[slots];

		for (int beanCount : beanCounts) {
			String failString = getFailString(1, beanCount);
			Bean[] beans = createBeans(logicSlotCounts[1], beanCount, true);
			logics[1].reset(beans); // one bean in (0,0)
			int del = 0;
			while (logics[1].advanceStep()) {
			} // loop until returns false

			int inSlotsObserved = getInSlotsBeanCount(logics[1], logicSlotCounts[1]);

			assertEquals(failString, beanCount, inSlotsObserved);

			if (inSlotsObserved % 2 == 0) {
				del = inSlotsObserved / 2;
			} else {
				del = (inSlotsObserved - 1) / 2;
			}

			// expectedSlotCounts = actual
			for (int j = 0; j < slots; j++) {
				expectedSlotCounts[j] = logics[1].getSlotBeanCount(j);
			}

			// change expectedSlotCounts to take beans away from the upper slots first
			for (int m = slots - 1; m >= 0; m--) {
				while (del > 0) {
					if (expectedSlotCounts[m] > 0) {
						expectedSlotCounts[m] = expectedSlotCounts[m] - 1;
						del = del - 1;
					} else {
						break;
					}
				}
			}

			logics[1].lowerHalf();
			for (int k = 0; k < slots; k++) {
				observedSlotCounts[k] = logics[1].getSlotBeanCount(k);
			}
			assertArrayEquals(failString + ". Check on in-slot bean count array", expectedSlotCounts,
					observedSlotCounts);
		}
		// }
	}

	/**
	 * Test upperHalf() in luck mode.
	 * 
	 * <pre>
	 * Preconditions: logics for each slot count in logicSlotCounts are created.
	 * Execution steps: For the logic with 10 slot counts, and for each bean count in beanCounts,
	 *                  Call createBeans to create lucky beans for the slot count and bean count
	 *                  Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Calculate expected bean counts for each slot after having called logic.upperHalf(),
	 *                  from current slot bean counts, and store into an expectedSlotCounts array.
	 *                  Call logic.upperHalf().
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testUpperHalf() {
		int slots = logicSlotCounts[1]; // 10 slots
		int[] expectedSlotCounts = new int[slots];
		int[] observedSlotCounts = new int[slots];

		for (int beanCount : beanCounts) {
			String failString = getFailString(1, beanCount);
			Bean[] beans = createBeans(logicSlotCounts[1], beanCount, true);
			logics[1].reset(beans); // one bean in (0,0)
			int del = 0;
			while (logics[1].advanceStep()) {
			} // loop until returns false

			int inSlotsObserved = getInSlotsBeanCount(logics[1], logicSlotCounts[1]);

			assertEquals(failString, beanCount, inSlotsObserved);

			if (inSlotsObserved % 2 == 0) {
				del = inSlotsObserved / 2;
			} else {
				del = (inSlotsObserved - 1) / 2;
			}

			// expectedSlotCounts = actual
			for (int j = 0; j < slots; j++) {
				expectedSlotCounts[j] = logics[1].getSlotBeanCount(j);
			}

			// change expectedSlotCounts to take beans away from the upper slots first
			for (int m = 0; m < slots; m++) {
				while (del > 0) {
					if (expectedSlotCounts[m] > 0) {
						expectedSlotCounts[m] = expectedSlotCounts[m] - 1;
						del = del - 1;
					} else {
						break;
					}
				}
			}

			logics[1].upperHalf();
			for (int k = 0; k < slots; k++) {
				observedSlotCounts[k] = logics[1].getSlotBeanCount(k);
			}
			assertArrayEquals(failString + ". Check on in-slot bean count array", expectedSlotCounts,
					observedSlotCounts);
		}
		// }
	}

	/**
	 * Test repeat() in skill mode.
	 * 
	 * <pre>
	 * Preconditions: logics for each slot count in logicSlotCounts are created.
	 * Execution steps: For the logic with 10 slot counts, and for each bean count in beanCounts,
	 *                  Call createBeans to create skilled beans for the slot count and bean count
	 *                  Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Construct an expectedSlotCounts array that stores current bean counts for each slot.
	 *                  Call logic.repeat().
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testRepeat() {
		int slots = logicSlotCounts[1]; // 10 slots
		int[] expectedSlotCounts = new int[slots];
		int[] observedSlotCounts = new int[slots];

		for (int beanCount : beanCounts) {
			final String failString = getFailString(1, beanCount);
			Bean[] beans = createBeans(logicSlotCounts[1], beanCount, false);
			logics[1].reset(beans); // one bean in (0,0)

			while (logics[1].advanceStep()) {
			} // loop until returns false

			// expectedSlotCounts = actual
			for (int j = 0; j < slots; j++) {
				expectedSlotCounts[j] = logics[1].getSlotBeanCount(j);
			}

			logics[1].repeat();
			// if(beanCount >0) assertEquals("Beancount = " + beanCount,
			// 1,logics[1].getInFlightBeanCount());

			while (logics[1].advanceStep()) {
			} // loop until returns false

			for (int k = 0; k < slots; k++) {
				observedSlotCounts[k] = logics[1].getSlotBeanCount(k);
			}

			assertArrayEquals(failString + ". Check on in-slot bean count array", expectedSlotCounts,
					observedSlotCounts);
		}
	}

	/**
	 * Test getAverageSlotBeanCount() in luck mode.
	 * 
	 * <pre>
	 * Preconditions: logics for each slot count in logicSlotCounts are created.
	 * Execution steps: For the logic with 10 slot counts,
	 *                  Call createBeans to create 200 lucky beans
	 *                  Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Store an expectedAverage, an average of the slot number for each bean.
	 *                  Store an observedAverage, the result of logic.getAverageSlotBeanCount().
	 *                  Store an idealAverage, which is 4.5.
	 * Invariants: {@literal Math.abs(expectedAverage - observedAverage) < 0.01}.
	 *             {@literal Math.abs(idealAverage - observedAverage) < 0.5}.
	 * </pre>
	 */
	@Test
	public void testGetAverageSlotBeanCount() {
		// for (int i = 0; i < logics.length; i++)
		int slots = logicSlotCounts[1]; // 10 slots
		int beanCnt = beanCounts[3]; // 200 beans
		int slotxbean = 0;

		double observedAverage = 0;

		final String failString = getFailString(1, beanCnt);
		Bean[] beans = createBeans(slots, beanCnt, true);

		logics[1].reset(beans); // one bean in (0,0)
		final double idealAverage = 4.5;
		while (logics[1].advanceStep()) {
		} // loop until returns false
		observedAverage = logics[1].getAverageSlotBeanCount(); // returns 4.64....
		// find amount of beans in each slot. beans in tht slot * slot #. add those to a
		// variable
		for (int i = 0; i < slots; i++) {
			int beansIn = logics[1].getSlotBeanCount(i);
			slotxbean += i * beansIn;
		}
		double expectedAverage = 0;
		// divide var by 2
		expectedAverage = (double) slotxbean / 200;

		double check = Math.abs(expectedAverage - observedAverage);
		double check2 = Math.abs(idealAverage - observedAverage);

		assertTrue(
				failString + " expect: " + expectedAverage + " obs: " + observedAverage + " diff: " + check
						+ " Check on expectedAverage - observedAverage < 0.01",
				Math.abs(expectedAverage - observedAverage) < 0.01);
		assertTrue(
				failString + " ideal: " + idealAverage + " obs: " + observedAverage + " diff: " + check2
						+ "Check on idealAverage - observedAverage < 0.5",
				Math.abs(idealAverage - observedAverage) < 0.5);
	}

	/**
	 * Test main(String[] args).
	 * 
	 * <pre>
	 * Preconditions: None.
	 * Execution steps: Check invariants after either calling
	 *                  BeanCounterLogicImpl.main("10", "500", "luck"), or
	 *                  BeanCounterLogicImpl.main("10", "500", "skill").
	 * Invariants: There are two lines of output.
	 *             There are 10 slot counts on the second line of output.
	 *             The sum of the 10 slot counts is equal to 500.
	 * </pre>
	 * 
	 * @throws UnsupportedEncodingException throws if error for encoding is present.
	 */
	@Test
	public void testMain() throws UnsupportedEncodingException {
		// Create a stream to hold the output
		// ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		// PrintStream stream = new PrintStream(byteStream);
		// IMPORTANT: Save the old System.out!
		// final PrintStream oldStream = System.out;
		// Tell Java to use stream with a byteStream
		// System.setOut(stream);

		String[] args = new String[3];
		args[0] = "10";
		args[1] = "500";
		args[2] = "luck";

		BeanCounterLogicImpl.main(args);
		String output = out.toString("UTF-8");
		String[] outputSplit = output.split("\n");

		assertEquals("Unskilled output is not 2 lines.", outputSplit.length, 2);

		int total = 0;
		ArrayList<String> slots = new ArrayList<String>();
		for (int i = 0; i < outputSplit[1].length(); i++) {
			if (outputSplit[1].charAt(i) != ' ') {
				StringBuilder number = new StringBuilder();
				int j = i;
				while (true) {
					if (j < outputSplit[1].length() && outputSplit[1].charAt(j) != ' ') {
						number.append(outputSplit[1].charAt(j));
					} else {
						break;
					}
					j++;
				}
				slots.add(number.toString());
				total += Integer.parseInt(number.toString());
				i = j - 1;
			}
		}

		assertEquals("Unskilled slots are not of length 10", 10, slots.size());
		assertEquals("Unskilled total is: " + total + " when it should be 500.", total, 500);

		// Second part
		args = new String[3];
		args[0] = "10";
		args[1] = "500";
		args[2] = "skill";

		BeanCounterLogicImpl.main(args);
		output = out.toString("UTF-8");

		// Create new string array of random length
		String[] outputSplitDouble;
		outputSplitDouble = output.split("\n");
		outputSplit[0] = outputSplitDouble[2];
		outputSplit[1] = outputSplitDouble[3];

		assertEquals("Skilled output is not 2 lines.", outputSplit.length, 2);

		assertEquals("Skilled output is not of length 40", outputSplit[1].length(), 40);

		total = 0;
		slots.clear();
		for (int i = 0; i < outputSplit[1].length(); i++) {
			if (outputSplit[1].charAt(i) != ' ') {
				StringBuilder number = new StringBuilder();
				int j = i;
				while (true) {
					if (j < outputSplit[1].length() && outputSplit[1].charAt(j) != ' ') {
						number.append(outputSplit[1].charAt(j));
					} else {
						break;
					}
					j++;
				}
				slots.add(number.toString());
				total += Integer.parseInt(number.toString());
				i = j - 1;
			}
		}

		assertEquals("Skilled total is: " + total + " when it should be 500.", total, 500);
	}

}