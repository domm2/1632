package edu.pitt.cs;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.*;
import org.mockito.*;

import java.util.ArrayList;

public class CoffeeMakerQuestTest {

	CoffeeMakerQuest cmq; 
	Player player; 
	ArrayList<Room> rooms; 
	Item NONE, CREAM, COFFEE, SUGAR; 
	Room r1, r2, r3, r4, r5, r6; 

	@Before
	public void setup() {
		// 0. Turn on bug injection for Player and Room.
		Config.setBuggyPlayer(true);
		Config.setBuggyRoom(true);

		NONE = Item.NONE;
		CREAM = Item.CREAM;
		COFFEE = Item.COFFEE;
		SUGAR = Item.SUGAR;
		rooms = new ArrayList<Room>();

		// TODO: 1. Create a Player with no items (no coffee, no cream, no sugar)
		// and assign to player.
		player = Mockito.mock(Player.class);
		Mockito.when(player.checkCoffee()).thenReturn(false);
		Mockito.when(player.checkSugar()).thenReturn(false);
		Mockito.when(player.checkCream()).thenReturn(false);
		Mockito.when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n");
		
		// TODO: 2. Create 6 rooms as specified in rooms.config and add to rooms list.
		r6 = Mockito.mock(Room.class);
		Mockito.when(r6.getFurnishing()).thenReturn("Perfect air hockey table");
		Mockito.when(r6.getAdjective()).thenReturn("Rough");
		Mockito.when(r6.getItem()).thenReturn(SUGAR);
		Mockito.when(r6.getNorthDoor()).thenReturn(null);
		Mockito.when(r6.getSouthDoor()).thenReturn("Minimalist");
		Mockito.when(r6.getDescription()).thenReturn("You see a Rough room.\nIt has a Perfect air hockey table.\nA Minimalist door leads South.\n");
		
		r5 = Mockito.mock(Room.class);
		Mockito.when(r5.getFurnishing()).thenReturn("Beautiful bag of money");
		Mockito.when(r5.getAdjective()).thenReturn("Bloodthirsty");
		Mockito.when(r5.getItem()).thenReturn(NONE);
		Mockito.when(r5.getNorthDoor()).thenReturn("Purple");
		Mockito.when(r5.getSouthDoor()).thenReturn("Sandy");
		Mockito.when(r5.getDescription()).thenReturn("You see a Bloodthirsty room.\nIt has a Beautiful bag of money.\nA Purple door leads North.\nA Sandy door leads South.\n");

		r4 = Mockito.mock(Room.class);
		Mockito.when(r4.getFurnishing()).thenReturn("Flat energy drink");
		Mockito.when(r4.getAdjective()).thenReturn("Dumb");
		Mockito.when(r4.getItem()).thenReturn(NONE);
		Mockito.when(r4.getNorthDoor()).thenReturn("Vivacious");
		Mockito.when(r4.getSouthDoor()).thenReturn("Slim");
		Mockito.when(r4.getDescription()).thenReturn("You see a Dumb room.\nIt has a Flat energy drink.\nA Vivacious door leads North.\nA Slim door leads South.\n");

		r3 = Mockito.mock(Room.class);
		Mockito.when(r3.getFurnishing()).thenReturn("Tight pizza");
		Mockito.when(r3.getAdjective()).thenReturn("Refinanced");
		Mockito.when(r3.getItem()).thenReturn(COFFEE);
		Mockito.when(r3.getNorthDoor()).thenReturn("Dead");
		Mockito.when(r3.getSouthDoor()).thenReturn("Smart");
		Mockito.when(r3.getDescription()).thenReturn("You see a Refinanced room.\nIt has a Tight pizza.\nA Dead leads North.\nA Smart door leads South.\n");

		r2 = Mockito.mock(Room.class);
		Mockito.when(r2.getFurnishing()).thenReturn("Sad record player");
		Mockito.when(r2.getAdjective()).thenReturn("Funny");
		Mockito.when(r2.getItem()).thenReturn(NONE);
		Mockito.when(r2.getNorthDoor()).thenReturn("Beige");
		Mockito.when(r2.getSouthDoor()).thenReturn("Massive");
		Mockito.when(r2.getDescription()).thenReturn("You see a Funny room.\nIt has a Sad record player.\nA Beige door leads North.\nA Massive door leads South.\n");

		r1 = Mockito.mock(Room.class);
		Mockito.when(r1.getFurnishing()).thenReturn("Quaint sofa");
		Mockito.when(r1.getAdjective()).thenReturn("Small");
		Mockito.when(r1.getItem()).thenReturn(CREAM);
		Mockito.when(r1.getNorthDoor()).thenReturn("Magenta");
		Mockito.when(r1.getSouthDoor()).thenReturn(null);
		Mockito.when(r1.getDescription()).thenReturn("You see a Small room.\nIt has a Quaint sofa.\nA Magenta door leads North.\n");

		//Collections.addAll(rooms, r1, r2, r3, r4, r5, r6);
		rooms.add(r1);
		rooms.add(r2);
		rooms.add(r3);
		rooms.add(r4);
		rooms.add(r5);
		rooms.add(r6);

		// 3. Create Coffee Maker Quest game using player and rooms, and assign to cmq.
		cmq = CoffeeMakerQuest.createInstance(player, rooms);
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test case for String getInstructionsString().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.getInstructionsString().
	 * Postconditions: Return value is " INSTRUCTIONS (N,S,L,I,D,H) > ".
	 * </pre>
	 */
	@Test
	public void testGetInstructionsString() {
		// TODO
		assertEquals(" INSTRUCTIONS (N,S,L,I,D,H) > ", cmq.getInstructionsString());
	}

	/**
	 * Test case for Room getCurrentRoom().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.getCurrentRoom().
	 * Postconditions: Return value is rooms.get(0).
	 * </pre>
	 */
	@Test
	public void testGetCurrentRoom() { 
		// TODO
		//returns adjective of room?? or the pointer to same object? 
		assertSame(rooms.get(0), cmq.getCurrentRoom());
		//assertEquals(rooms.get(0).getAdjective(), cmq.getCurrentRoom());
	}

	/**
	 * Test case for void setCurrentRoom(Room room) and Room getCurrentRoom().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.setCurrentRoom(rooms.get(2)).
	 *                  Call cmq.getCurrentRoom().
	 * Postconditions: Return value of cmq.setCurrentRoom(rooms.get(2)) is true. 
	 *                 Return value of cmq.getCurrentRoom() is rooms.get(2).
	 * </pre>
	 */
	@Test
	public void testSetCurrentRoom() {
		// TODO
		assertTrue("did not set current room to r2", cmq.setCurrentRoom(rooms.get(2)));
		assertSame(rooms.get(2), cmq.getCurrentRoom()); //or should it be assert same? i think so
	}

	/**
	 * Test case for boolean areDoorsPlacedCorrectly() when check succeeds.
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.areDoorsPlacedCorrectly().
	 * Postconditions: Return value of cmq.areDoorsPlacedCorrectly() is true.
	 * </pre>
	 */
	@Test
	public void testAreDoorsPlacedCorrectly() {
		// TODO
		assertTrue("Doors are NOT placed correctly. boo", cmq.areDoorsPlacedCorrectly());
	}

	/**
	 * Test case for boolean areDoorsPlacedCorrectly() when check fails.
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                rooms.get(3) is modified so that it has no south door.
	 * Execution steps: Call cmq.areDoorsPlacedCorrectly().
	 * Postconditions: Return value of cmq.areDoorsPlacedCorrectly() is false.
	 * </pre>
	 */
	@Test
	public void testAreDoorsPlacedCorrectlyMissingSouthDoor() {
		// TODO
		Mockito.when(r3.getSouthDoor()).thenReturn(null);
		Mockito.when(r3.getDescription()).thenReturn("You see a musical room.\nIt has a piano.\nA red door leads North.\n");
		assertFalse("It says doors are placed correctly, but they arent", cmq.areDoorsPlacedCorrectly());
	}

	/**
	 * Test case for boolean areRoomsUnique() when check fails.
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                rooms.get(2) is modified so that its adjective is modified to "Small".
	 * Execution steps: Call cmq.areRoomsUnique().
	 * Postconditions: Return value of cmq.areRoomsUnique() is false.
	 * </pre>
	 */
	@Test
	public void testAreRoomsUniqueDuplicateAdjective() {
		// TODO
		Mockito.when(r2.getAdjective()).thenReturn("Small");
		Mockito.when(r2.getDescription()).thenReturn("You see a Small room.\nIt has a Sad record player.\nA Beige door leads North.\nA Massive door leads South.\n");
		assertFalse("It says room adj are unique, but they arent", cmq.areRoomsUnique());
	}

	/**
	 * Test case for String processCommand("I").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                Player has no items.
	 * Execution steps: Call cmq.processCommand("I").
	 * Postconditions: Return value is "YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n".
	 * </pre>
	 */
	@Test
	public void testProcessCommandI() {
		// TODO
		assertEquals("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n", cmq.processCommand("I"));
	}

	/**
	 * Test case for String processCommand("l").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.processCommand("l").
	 * Postconditions: Return value is "There might be something here...\nYou found some creamy cream!\n".
	 *                 Item.CREAM has been added to the Player.
	 * </pre>
	 */
	@Test
	public void testProcessCommandLCream() {
		// TODO
		assertEquals("There might be something here...\nYou found some creamy cream!\n", cmq.processCommand("l"));
		Mockito.verify(player).addItem(CREAM);
	}

	/**
	 * Test case for String processCommand("l").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.processCommand("N").
	 * 					Call cmq.processCommand("N").
	 * 					Call cmq.processCommand("N").
	 * 					Call cmq.processCommand("N").
	 * 					Call cmq.processCommand("N").
	 * 					Call cmq.processCommand("l").
	 * Postconditions: Return value is "There might be something here...\nYou found some sweet sugar!\n".
	 *                 Item.SUGAR has been added to the Player.
	 * </pre>
	 */
	@Test
	public void testProcessCommandLSugar() {
		// TODO
		cmq.processCommand("N");
		cmq.processCommand("N");
		cmq.processCommand("N");
		cmq.processCommand("N");
		cmq.processCommand("N");
		assertEquals("There might be something here...\nYou found some sweet sugar!\n", cmq.processCommand("l"));
		Mockito.verify(player).addItem(SUGAR);
	}

	/**
	 * Test case for String processCommand("l").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * 				  Player is in Refinanced room (room 3).
	 * Execution steps: Call cmq.processCommand("l").
	 * Postconditions: Return value is "There might be something here...\nYou found some caffeinated coffee!\n".
	 *                 Item.COFFEE has been added to the Player.
	 * </pre>
	 */
	@Test
	public void testProcessCommandLCoffee() {
		// TODO
		cmq.setCurrentRoom(r3);
		assertEquals("There might be something here...\nYou found some caffeinated coffee!\n", cmq.processCommand("l"));
		Mockito.verify(player).addItem(COFFEE);
	}

	/**
	 * Test case for String processCommand("l").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * 				  Player is in Funny room (room 2).
	 * Execution steps: Call cmq.processCommand("l").
	 * Postconditions: Return value is "You don't see anything out of the ordinary.\n".
	 *                 Item.NONE has been added to the Player.
	 * </pre>
	 */
	@Test
	public void testProcessCommandLNONE() {
		// TODO
		cmq.setCurrentRoom(r2);
		assertEquals("You don't see anything out of the ordinary.\n", cmq.processCommand("l"));
		Mockito.verify(player).addItem(NONE);
	}

	/**
	 * Test case for String processCommand("n").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                cmq.setCurrentRoom(rooms.get(3)) has been called.
	 * Execution steps: Call cmq.processCommand("n").
	 *                  Call cmq.getCurrentRoom().
	 * Postconditions: Return value of cmq.processCommand("n") is "".
	 *                 Return value of cmq.getCurrentRoom() is rooms.get(4).
	 * </pre>
	 */
	@Test
	public void testProcessCommandN() {
		// TODO
		cmq.setCurrentRoom(rooms.get(3));
		assertEquals("", cmq.processCommand("n"));
		assertSame(rooms.get(4), cmq.getCurrentRoom());
	}

	/**
	 * Test case for String processCommand("s").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.processCommand("s").
	 *                  Call cmq.getCurrentRoom().
	 * Postconditions: Return value of cmq.processCommand("s") is "A door in that direction does not exist.\n".
	 *                 Return value of cmq.getCurrentRoom() is rooms.get(0).
	 * </pre>
	 */
	@Test
	public void testProcessCommandS() {
		// TODO
		assertEquals("A door in that direction does not exist.\n", cmq.processCommand("s"));
		assertSame(rooms.get(0), cmq.getCurrentRoom());
	}

	/**
	 * Test case for String processCommand("D").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n".
	 *                 Return value of cmq.isGameOver() is true.
	 * </pre>
	 */
	@Test
	public void testProcessCommandDLose() {
		// TODO
		assertEquals("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n", 
		cmq.processCommand("D"));
		assertTrue("Game is not over when it should be we lost", cmq.isGameOver());
	}

	/**
	 * Test case for String processCommand("D").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                Player has all 3 items (coffee, cream, sugar).
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "You have a cup of delicious coffee.\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the beverage and are ready to study!\nYou win!\n".
	 *                 Return value of cmq.isGameOver() is true.
	 * </pre>
	 */
	@Test
	public void testProcessCommandDWin() {
		// TODO
		Mockito.when(player.checkCoffee()).thenReturn(true);
		Mockito.when(player.checkSugar()).thenReturn(true);
		Mockito.when(player.checkCream()).thenReturn(true);
		Mockito.when(player.getInventoryString()).thenReturn("You have a cup of delicious coffee.\nYou have some fresh cream.\nYou have some tasty sugar.\n");

		
		assertEquals("You have a cup of delicious coffee.\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the beverage and are ready to study!\nYou win!\n",
		cmq.processCommand("D"));
		assertTrue("Game is not over when it should be. we won", cmq.isGameOver());
	}

	// TODO: Put in more unit tests of your own making to improve coverage!

	/**
	 * Test case for void setCurrentRoomFalse().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                
	 * Execution steps: Call cmq.setCurrentRoomFalse(null).
	 *            
	 * Postconditions: Return value of cmq.setCurrentRoomFalse(null) is "false".
	 *             
	 * </pre>
	 */
	@Test
	public void testSetCurrentRoomFalse(){
		assertFalse("Returns a room when it should return false.", cmq.setCurrentRoom(null));
	}

	/**
	 * Test case for void areRoomsUniqueTrue().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created with unique names.
	 *                
	 * Execution steps: Call cmq.areRoomsUnique().
	 *            
	 * Postconditions: Return value of cmq.areRoomsUnique is "true".
	 *             
	 * </pre>
	 */
	@Test
	public void testAreRoomsUniqueTrue(){
		assertTrue("Returns false when all rooms should be unique. Should return true.", cmq.areRoomsUnique());
	}

	/**
	 * Test case for void areDoorsPlacedCorrectly().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                
	 * Execution steps: rooms.get(5) is modified so that its south door is modified to null.
	 * 					Call cmq.areDoorsPlacedCorrectly()
	 *            
	 * Postconditions: Return value of cmq.areDoorsPlacedCorrectly() is "false".
	 *             
	 * </pre>
	 */
	@Test
	public void testAreDoorsPlacedCorrectlyNorthmostDoor(){
		Mockito.when(r6.getSouthDoor()).thenReturn(null);
		Mockito.when(r6.getDescription()).thenReturn("You see a Rough room.\nIt has a Perfect air hockey table.\n");
		assertFalse("Northmost door's south door is set to a door when it should be null.", cmq.areDoorsPlacedCorrectly());
	}

	/**
	 * Test case for void areDoorsPlacedCorrectly().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                
	 * Execution steps: rooms.get(0) is modified so that its north door is modified to null.
	 * 					Call cmq.areDoorsPlacedCorrectly()
	 *            
	 * Postconditions: Return value of cmq.areDoorsPlacedCorrectly() is "false".
	 *             
	 * </pre>
	 */
	@Test 
	public void testAreDoorsPlacedCorrectlySouthmostDoor(){
		Mockito.when(r1.getNorthDoor()).thenReturn(null);
		Mockito.when(r1.getDescription()).thenReturn("You see a Small room.\nIt has a Quaint sofa.\n");
		assertFalse("Southmost door's north door is set to a door when it should be null.", cmq.areDoorsPlacedCorrectly());
	}

	/**
	 * Test case for void processCommand().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                
	 * Execution steps: Call cmq.setCurrentRoom(r6).
	 * 					Call cmq.processCommand("N").
	 *            
	 * Postconditions: Return value of cmq.processCommand("N") is "A door in that direction does not exist."
	 *             
	 * </pre>
	 */
	@Test
	public void testProcessCommandNKeyNorthmostDoor(){
		cmq.setCurrentRoom(r6);
		assertEquals("Return value does not match string.", cmq.processCommand("N"), "A door in that direction does not exist.\n");
	}

	/**
	 * Test case for void areRoomsUnique().
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                
	 * Execution steps: Call cmq.setCurrentRoom(r3).
	 * 					Call cmq.processCommand("S").
	 *            
	 * Postconditions: Return value of cmq.processCommand("S") is "".
	 *             
	 * </pre>
	 */
	@Test
	public void testProcessCommandSKeyMiddleDoor(){
		cmq.setCurrentRoom(r3);
		assertEquals("Return value does not match string.", cmq.processCommand("S"), "");
	}

	/**
	 * Test case for String processCommand("H").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.processCommand("H").
	 * Postconditions: Return value of cmq.processCommand("H") is "N - Go north\nS - Go south\nL - Look and collect any items in the room\nI - Show inventory of items collected\nD - Drink coffee made from items in inventory\n"

	 * </pre>
	 */
	@Test
	public void testHelpString(){
		assertEquals("N - Go north\nS - Go south\nL - Look and collect any items in the room\nI - Show inventory of items collected\nD - Drink coffee made from items in inventory\n", 
		cmq.processCommand("H"));
	}

	/**
	 * Test case for String processCommand("45").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 * Execution steps: Call cmq.processCommand("45").
	 * Postconditions: Return value of cmq.processCommand("45") is "What?\n"

	 * </pre>
	 */
	@Test
	public void testInvalidCommand(){
		assertEquals("What?\n",cmq.processCommand("45"));
	}
	
	/**
	 * Test case for String processCommand("D").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                Player has coffee & sugar.
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n\nWithout cream, you get an ulcer and cannot study.\nYou lose!\n"
	 *                 Return value of cmq.isGameOver() is true.
	 * </pre>
	 */
	@Test
	public void testProcessCommandCS() {
		Mockito.when(player.checkCoffee()).thenReturn(true);
		Mockito.when(player.checkSugar()).thenReturn(true);
		Mockito.when(player.getInventoryString()).thenReturn("You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n");
		
		assertEquals("You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n\nWithout cream, you get an ulcer and cannot study.\nYou lose!\n",
		cmq.processCommand("D"));
		assertTrue("Game is not over when it should be. we lost", cmq.isGameOver());
	}

	/**
	 * Test case for String processCommand("D").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                Player has coffee.
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nWithout cream, you get an ulcer and cannot study.\nYou lose!\n"
	 *                 Return value of cmq.isGameOver() is true.
	 * </pre>
	 */
	@Test
	public void testProcessCommandCoffee() {
		Mockito.when(player.checkCoffee()).thenReturn(true);
		Mockito.when(player.getInventoryString()).thenReturn("You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n");
		
		assertEquals("You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nWithout cream, you get an ulcer and cannot study.\nYou lose!\n",
		cmq.processCommand("D"));
		assertTrue("Game is not over when it should be. we lost", cmq.isGameOver());
	}

	/**
	 * Test case for String processCommand("D").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                Player has cream & sugar.
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the sweetened cream, but without caffeine you cannot study.\nYou lose!\n"
	 *                 Return value of cmq.isGameOver() is true.
	 * </pre>
	 */
	@Test
	public void testProcessCommandCRS() {
		Mockito.when(player.checkCream()).thenReturn(true);
		Mockito.when(player.checkSugar()).thenReturn(true);
		Mockito.when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYou have some tasty sugar.\n");
		
		assertEquals("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the sweetened cream, but without caffeine you cannot study.\nYou lose!\n",
		cmq.processCommand("D"));
		assertTrue("Game is not over when it should be. we lost", cmq.isGameOver());
	}

	/**
	 * Test case for String processCommand("D").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                Player has cream.
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n\nYou drink the cream, but without caffeine, you cannot study.\nYou lose!\n"
	 *                 Return value of cmq.isGameOver() is true.
	 * </pre>
	 */
	@Test
	public void testProcessCommandCream() {
		Mockito.when(player.checkCream()).thenReturn(true);
		Mockito.when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n");
		
		assertEquals("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n\nYou drink the cream, but without caffeine, you cannot study.\nYou lose!\n",
		cmq.processCommand("D"));
		assertTrue("Game is not over when it should be. we lost", cmq.isGameOver());
	}
	/**
	 * Test case for String processCommand("D").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                Player has sugar.
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n\nYou eat the sugar, but without caffeine, you cannot study.\nYou lose!\n"
	 *                 Return value of cmq.isGameOver() is true.
	 * </pre>
	 */
	@Test
	public void testProcessCommandSugar() {
		Mockito.when(player.checkSugar()).thenReturn(true);
		Mockito.when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n");
		
		assertEquals("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n\nYou eat the sugar, but without caffeine, you cannot study.\nYou lose!\n",
		cmq.processCommand("D"));
		assertTrue("Game is not over when it should be. we lost", cmq.isGameOver());
	}

	/**
	 * Test case for String processCommand("D").
	 * 
	 * <pre>
	 * Preconditions: Player, rooms, and cmq test fixture has been created.
	 *                Player has coffee and cream.
	 * Execution steps: Call cmq.processCommand("D").
	 * 
	 * Postconditions: Return value of cmq.processCommand("D") is "\nWithout sugar, the coffee is too bitter. You cannot study.\nYou lose!\n"
	 *                 
	 * </pre>
	 */
	@Test
	public void testProcessCommandDSugarAndCoffee() {
		Mockito.when(player.checkCream()).thenReturn(true);
		Mockito.when(player.checkCoffee()).thenReturn(true);
		Mockito.when(player.getInventoryString()).thenReturn("You have a cup of delicious coffee.\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n");
		
		assertEquals("You have a cup of delicious coffee.\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n\nWithout sugar, the coffee is too bitter. You cannot study.\nYou lose!\n",
		cmq.processCommand("D"));
	}
	
}
