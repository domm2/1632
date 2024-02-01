package edu.pitt.cs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.nasa.jpf.vm.Verify;
import java.util.Random;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//import static org.junit.Assert.*;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>
 * Uses the Java Path Finder model checking tool to check BeanCounterLogic in
 * various modes of operation. It checks BeanCounterLogic in both "luck" and
 * "skill" modes for various numbers of slots and beans. It also goes down all
 * the possible random path taken by the beans during operation.
 */

public class JPFJUnitTest {
	private static BeanCounterLogic logic; // The core logic of the program
	private static Bean[] beans; // The beans in the machine
	private static String failString; // A descriptive fail string for assertions

	private static int slotCount; // The number of slots in the machine we want to test
	private static int beanCount; // The number of beans in the machine we want to test
	private static boolean isLuck; // Whether the machine we want to test is in "luck" or "skill" mode

	/**
	 * Sets up the test fixture.
	 */
	@BeforeClass
	public static void setUp() {
		if (Config.getTestType() == TestType.JUNIT) {
			slotCount = 5;
			beanCount = 3;
			isLuck = true;
		} else if (Config.getTestType() == TestType.JPF_ON_JUNIT) {
			/*
			 * beanCount, and isLuck: slotCount should take values 1-5, beanCount should
			 * take values 0-3, and isLucky should be either true or false. For reference on
			 * how to use the Verify API, look at:
			 * https://github.com/javapathfinder/jpf-core/wiki/Verify-API-of-JPF
			 */
			slotCount = Verify.getInt(1, 5);
			beanCount = Verify.getInt(0, 3);
			isLuck = Verify.getBoolean();
		} else {
			assert (false);
		}

		// Create the internal logic
		logic = BeanCounterLogic.createInstance(slotCount);
		// Create the beans
		beans = new Bean[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = Bean.createInstance(slotCount, isLuck, new Random(42));
		}

		// A failstring useful to pass to assertions to get a more descriptive error.
		failString = "Failure in (slotCount=" + slotCount
				+ ", beanCount=" + beanCount + ", isLucky=" + isLuck + "):";
	}

	@AfterClass
	public static void tearDown() {
	}

	// private String getFailString(int slotCount, int beanCount) {
	// return "[Slot Count = " + slotCount + "] Test case with " + beanCount
	// + " initial beans failed";
	// }

	/**
	 * Test case for void reset(Bean[] beans).
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 * Invariants: If beanCount is greater than 0,
	 *             remaining bean count is beanCount - 1
	 *             in-flight bean count is 1 (the bean initially at the top)
	 *             in-slot bean count is 0.
	 *             If beanCount is 0,
	 *             remaining bean count is 0
	 *             in-flight bean count is 0
	 *             in-slot bean count is 0.
	 * </pre>
	 */
	@Test
	public void testReset() {
		/*
		 * Currently, it just prints out the failString to demonstrate to you all the
		 * cases considered by Java Path Finder. If you called the Verify API correctly
		 * in setUp(), you should see all combinations of machines
		 * (slotCount/beanCount/isLucky) printed here:
		 * 
		 * Failure in (slotCount=1, beanCount=0, isLucky=false):
		 * Failure in (slotCount=1, beanCount=0, isLucky=true):
		 * Failure in (slotCount=1, beanCount=1, isLucky=false):
		 * Failure in (slotCount=1, beanCount=1, isLucky=true):
		 * ...
		 * 
		 * PLEASE REMOVE when you are done implementing.
		 */

		// System.out.println(failString);

		logic.reset(beans);

		if (beanCount > 0) {
			// remaining bean count is beanCount - 1
			assertEquals(failString, beanCount - 1, logic.getRemainingBeanCount());
			// in-flight bean count is 1 (the bean initially at the top)
			assertEquals(failString, 1, getInFlightBeanCount());
			// in-slot bean count is 0.
			for (int i = 0; i < slotCount; i++) {
				assertEquals(failString, 0, logic.getSlotBeanCount(i));
			}

		}

		if (beanCount == 0) {
			// remaining bean count is 0
			assertEquals(failString, 0, logic.getRemainingBeanCount());
			// in-flight bean count is 0
			assertEquals(failString, 0, getInFlightBeanCount());
			// in-slot bean count is 0.
			for (int i = 0; i < slotCount; i++) {
				assertEquals(failString, 0, logic.getSlotBeanCount(i));
			}
		}
	}

	private int getInFlightBeanCount() {
		int inFlight = 0;
		for (int yPos = 0; yPos < slotCount; yPos++) {
			int xPos = logic.getInFlightBeanXPos(yPos);
			if (xPos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
				inFlight++;
			}
		}
		return inFlight;
	}

	/**
	 * Test case for boolean advanceStep().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * Invariants: After each advanceStep(),
	 *             getInFlightBeanXPos(ypos) for all rows in machine returns a legal xpos.
	 *             (For example, getInFlightBeanXPos(0) can either return 0 or BeanCounterLogic.NO_BEAN_IN_YPOS,
	 *              and getInFlightBeanXPos(1) can return 0, 1, or BeanCounterLogic.NO_BEAN_IN_YPOS.  And so on.)
	 * </pre>
	 */
	@Test
	public void testAdvanceStepCoordinates() {
		logic.reset(beans);
		while (logic.advanceStep()) {
			for (int i = 0; i < slotCount; i++) {
				int x = logic.getInFlightBeanXPos(i);
				assertTrue(failString + "yPos " + i + " returns a value not accepted.",
						((x <= i) || i == BeanCounterLogic.NO_BEAN_IN_YPOS) ? (true) : (false));
			}
		}
	}

	/**
	 * Test case for boolean advanceStep().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * Invariants: After each advanceStep(),
	 *             the sum of remaining, in-flight, and in-slot beans is equal to beanCount.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepBeanCount() {
		logic.reset(beans);

		while (logic.advanceStep()) {
			int total = 0;
			total += getInFlightBeanCount();
			total += logic.getRemainingBeanCount();
			for (int i = 0; i < slotCount; i++) {
				total += logic.getSlotBeanCount(i);
			}

			assertEquals(failString, beanCount, total);
		}

	}

	/**
	 * Test case for boolean advanceStep().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * Invariants: After the machine terminates,
	 *             remaining bean count is 0
	 *             in-flight bean count is 0
	 *             in-slot bean count is beanCount.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepPostCondition() {
		logic.reset(beans);
		while (logic.advanceStep()) {
		}

		assertEquals(failString, 0, logic.getRemainingBeanCount());
		assertEquals(failString, 0, getInFlightBeanCount());

		int total = 0;
		for (int i = 0; i < slotCount; i++) {
			total += logic.getSlotBeanCount(i);
		}
		assertEquals(failString, beanCount, total);
	}

	/**
	 * Test case for void lowerHalf()().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Calculate expected bean counts for each slot after having called logic.lowerHalf(),
	 *                  from current slot bean counts, and store into an expectedSlotCounts array.
	 *                  Call logic.lowerHalf().
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: remaining bean count is 0
	 *             in-flight bean count is 0
	 *             expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testLowerHalf() {
		int[] expectedSlotCounts = new int[slotCount];
		int[] observedSlotCounts = new int[slotCount];

		logic.reset(beans);
		int del = 0;
		while (logic.advanceStep()) {
		}

		int inSlotsObserved = getInSlotsBeanCount();
		assertEquals(failString, beanCount, inSlotsObserved);

		if (inSlotsObserved % 2 == 0) {
			del = inSlotsObserved / 2;
		} else {
			del = (inSlotsObserved - 1) / 2;
		}

		// expected amount
		for (int j = 0; j < slotCount; j++) {
			expectedSlotCounts[j] = logic.getSlotBeanCount(j);
		}

		// change expectedSlotCounts to take beans away from the upper slots first
		for (int m = slotCount - 1; m >= 0; m--) {
			while (del > 0) {
				if (expectedSlotCounts[m] > 0) {
					expectedSlotCounts[m] = expectedSlotCounts[m] - 1;
					del = del - 1;
				} else {
					break;
				}
			}
		}

		logic.lowerHalf();

		// actual amount
		for (int k = 0; k < slotCount; k++) {
			observedSlotCounts[k] = logic.getSlotBeanCount(k);
		}
		assertArrayEquals(failString + ". Check on in-slot bean count array", expectedSlotCounts, observedSlotCounts);

	}

	private int getInSlotsBeanCount() {
		int inSlots = 0;
		for (int i = 0; i < slotCount; i++) {
			inSlots += logic.getSlotBeanCount(i);
		}
		return inSlots;
	}

	/**
	 * Test case for void upperHalf().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Calculate expected bean counts for each slot after having called logic.upperHalf(),
	 *                  from current slot bean counts, and store into an expectedSlotCounts array.
	 *                  Call logic.upperHalf().
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: remaining bean count is 0
	 *             in-flight bean count is 0
	 *             expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testUpperHalf() {
		int[] expectedSlotCounts = new int[slotCount];
		int[] observedSlotCounts = new int[slotCount];

		logic.reset(beans);
		int del = 0;
		while (logic.advanceStep()) {
		}

		int inSlotsObserved = getInSlotsBeanCount();
		assertEquals(failString, beanCount, inSlotsObserved);

		if (inSlotsObserved % 2 == 0) {
			del = inSlotsObserved / 2;
		} else {
			del = (inSlotsObserved - 1) / 2;
		}

		// expected amount
		for (int j = 0; j < slotCount; j++) {
			expectedSlotCounts[j] = logic.getSlotBeanCount(j);
		}

		// change expectedSlotCounts to take beans away from the upper slots first
		for (int m = 0; m < slotCount; m++) {
			while (del > 0) {
				if (expectedSlotCounts[m] > 0) {
					expectedSlotCounts[m] = expectedSlotCounts[m] - 1;
					del = del - 1;
				} else {
					break;
				}
			}
		}

		logic.upperHalf();

		// actual amount
		for (int k = 0; k < slotCount; k++) {
			observedSlotCounts[k] = logic.getSlotBeanCount(k);
		}
		assertArrayEquals(failString + ". Check on in-slot bean count array", expectedSlotCounts, observedSlotCounts);

	}

	/**
	 * Test case for void repeat().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: If beans are created in skill mode (if isLuck is false),
	 *                  Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Construct an expectedSlotCounts array that stores current bean counts for each slot.
	 *                  Call logic.repeat();
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testRepeat() {
		if (!isLuck) {
			logic.reset(beans);
			while (logic.advanceStep()) {
			}

			Integer[] expectedSlotCounts = new Integer[slotCount];
			for (int i = 0; i < slotCount; i++) {
				expectedSlotCounts[i] = logic.getSlotBeanCount(i);
			}

			logic.repeat();

			while (logic.advanceStep()) {
			}

			Integer[] observedSlotCounts = new Integer[slotCount];
			for (int i = 0; i < slotCount; i++) {
				observedSlotCounts[i] = logic.getSlotBeanCount(i);
			}

			for (int i = 0; i < slotCount; i++) {
				assertEquals(failString, expectedSlotCounts[i], observedSlotCounts[i]);
			}

		}
	}

	/**
	 * Test case for int testFlightBeanXPos().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: If beans are created in skill mode (if isLuck is false),
	 *                  Call logic.reset(beans).
	 * 					Call logic.getInFlightBeanXPos(0).
	 *                  Call logic.advanceStep().
	 * 					Call logic.getInFlightBeanXPos(1).
	 *                  If slotCount is greater than 1:
	 * 						- Call logic.advanceStep().
	 * 						- Call logic.getInFlightBeanXPos(1).
	 * 						- Call logic.getInFlightBeanXPos(2).
	 * Invariants: Each bean XPos is within the given ranges of that corresponding Y row.
	 * </pre>
	 */
	@Test
	public void testInFlightBeanXPos() {
		if (!isLuck) {
			logic.reset(beans);
			assertEquals(0, logic.getInFlightBeanXPos(0));
			logic.advanceStep();
			assertTrue(0 <= logic.getInFlightBeanXPos(1) && logic.getInFlightBeanXPos(1) <= 1);

			if (slotCount > 1) {
				logic.advanceStep();
				assertTrue(0 <= logic.getInFlightBeanXPos(1) && logic.getInFlightBeanXPos(1) <= 1);
				assertTrue(0 <= logic.getInFlightBeanXPos(2) && logic.getInFlightBeanXPos(2) <= 2);
			}
		}
	}
}
