package edu.pitt.cs;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameOfLifePinningTest {
	/*
	 * READ ME: You may need to write pinning tests for methods from multiple
	 * classes, if you decide to refactor methods from multiple classes.
	 * 
	 * In general, a pinning test doesn't necessarily have to be a unit test; it can
	 * be an end-to-end test that spans multiple classes that you slap on quickly
	 * for the purposes of refactoring. The end-to-end pinning test is gradually
	 * refined into more high quality unit tests. Sometimes this is necessary
	 * because writing unit tests itself requires refactoring to make the code more
	 * testable (e.g. dependency injection), and you need a temporary end-to-end
	 * pinning test to protect the code base meanwhile.
	 * 
	 * For this deliverable, there is no reason you cannot write unit tests for
	 * pinning tests as the dependency injection(s) has already been done for you.
	 * You are required to localize each pinning unit test within the tested class
	 * as we did for Deliverable 2 (meaning it should not exercise any code from
	 * external classes). You will have to use Mockito mock objects to achieve this.
	 * 
	 * Also, you may have to use behavior verification instead of state verification
	 * to test some methods because the state change happens within a mocked
	 * external object. Remember that you can use behavior verification only on
	 * mocked objects (technically, you can use Mockito.verify on real objects too
	 * using something called a Spy, but you wouldn't need to go to that length for
	 * this deliverable).
	 */

	/* TODO: Declare all variables required for the test fixture. */
	private Cell[][] cells;
	MainPanel mp;

	@Before
	public void setUp() {
		/*
		 * TODO: initialize the text fixture. For the initial pattern, use the "blinker"
		 * Start from the vertical bar on a 5X5 matrix as shown in the GIF.
		 */

		 //sets the vertical line to alive 
		cells = new Cell[5][5];
		
		for (int j = 0; j < 5; j++) {
			for (int k = 0; k < 5; k++) {
				if(j == 2 && k == 1) {
					cells[2][1] = Mockito.mock(Cell.class);
					Mockito.when(cells[2][1].getAlive()).thenReturn(true);
				}
				else if(j == 2 && k == 2) {
					cells[2][2] = Mockito.mock(Cell.class);
					Mockito.when(cells[2][2].getAlive()).thenReturn(true);
				}
				else if(j == 2 && k == 3) {
					cells[2][3] = Mockito.mock(Cell.class);
					Mockito.when(cells[2][3].getAlive()).thenReturn(true);
				}
				else {
					cells[j][k] = Mockito.mock(Cell.class);
					Mockito.when(cells[j][k].getAlive()).thenReturn(false);
				}
			}
		}

		mp = new MainPanel(cells);
	}

	/* TODO: Write the three pinning unit tests for the three optimized methods */
	
	/**
	 * Test case for boolean iterateCell(int x, int y).
	 * 
	 * Preconditions: cells[2][1] is alive.
	 * 				  cells[2][2] is alive.
	 * 				  cells[2][3] is alive.
	 * 				  all other cells are dead.
	 * Execution steps: Call mp.iterateCell(2,1).
	 * Postconditions: mp.iterateCell(2,2) returns false.
	 */ 
	@Test
	public void testIterateCell(){
		//call func
		//cells[2][1].setAlive(mp.iterateCell(2,1));
		//test if output is correct
		//assertEquals(false, cells[2][1].getAlive());
		assertFalse(mp.iterateCell(2,1));
	}

	/**
	 * Test case for boolean iterateCell(int x, int y).
	 * 
	 * Preconditions: cells[2][1] is alive.
	 * 				  cells[2][2] is alive.
	 * 				  cells[2][3] is alive.
	 * 				  all other cells are dead.
	 * Execution steps: Call mp.iterateCell(2,2).
	 * Postconditions: mp.iterateCell(2,2) returns true.
	 */ 
	@Test
	public void testIterateCell2(){
		assertTrue(mp.iterateCell(2,2));
	}

	/**
	 * Test case for void calculateNextIteration().
	 * 
	 * Preconditions: cells[2][1] is alive.
	 * 				  cells[2][2] is alive.
	 * 				  cells[2][3] is alive.
	 * 				  all other cells are dead.
	 * Execution steps: Call mp.calculateNextIteration().
	 * Postconditions: cells[1][2] is alive.
	 * 				   cells[2][2] is alive.
	 * 				   cells[3][2] is alive.
	 *                 all other cells are dead.
	 */ 
	@Test
	public void testCalculateNextIteration() {

		mp.calculateNextIteration();
		for (int j = 0; j < 5; j++) {
			for (int k = 0; k < 5; k++) {
				if(j == 1 && k == 2) {
					verify(cells[1][2]).setAlive(true);
				}
				else if(j == 2 && k == 2) {
					verify(cells[2][2]).setAlive(true);
				}
				else if(j == 3 && k == 2) {
					verify(cells[3][2]).setAlive(true);
				}
				else {
					verify(cells[j][k]).setAlive(false);
				}
			}
		}
		
	}

	/**
	 * Test case for String toString().
	 * 
	 * Preconditions: Cell c is alive.
	 * Execution steps: Call c.toString().
	 * Postconditions: Return value is "X".
	 */ 
	@Test
	public void testToStringTrue() {
		// when(cells[2][2].toString()).thenCallRealMethod();
		// when(cells[3][4].toString()).thenCallRealMethod();
		Cell c = new Cell(true);
		assertEquals("X", c.toString());
	}
	
	/**
	 * Test case for String toString().
	 * 
	 * Preconditions: Cell n is not alive.
	 * Execution steps: Call n.toString().
	 * Postconditions: Return value is ".".
	 */ 
	@Test
	public void testToStringFalse() {
		// when(cells[2][2].toString()).thenCallRealMethod();
		// when(cells[3][4].toString()).thenCallRealMethod();
		Cell n = new Cell(false);
		assertEquals(".", n.toString());
	}
}
