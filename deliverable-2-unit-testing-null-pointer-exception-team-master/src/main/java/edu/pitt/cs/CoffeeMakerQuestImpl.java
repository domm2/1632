package edu.pitt.cs;

import java.util.*;

public class CoffeeMakerQuestImpl implements CoffeeMakerQuest {

	// TODO: Add more member variables and methods as needed.
	Player player;
	ArrayList<Room> rooms;
	Room currentRoom = null;
	boolean playerDrankCoffee = false;		// If the player drank coffee

	/**
	 * Constructor. Rooms are laid out from south to north, such that the
	 * first room in rooms becomes the southernmost room and the last room becomes
	 * the northernmost room. Initially, the player is at the southernmost room.
	 * 
	 * @param player Player for this game
	 * @param rooms  List of rooms in this game
	 */
	CoffeeMakerQuestImpl(Player player, ArrayList<Room> rooms) {
		// TODO
		
		// Sets the player and rooms from the parameters to the global variables so they can be accessed
		this.player = player;
		//this.rooms = rooms;
		this.rooms = new ArrayList<>(rooms);
		currentRoom = this.rooms.get(0);
	}

	/**
	 * Whether the game is over. The game ends when the player drinks the coffee.
	 * 
	 * @return true if successful, false otherwise
	 */
	public boolean isGameOver() {
		// TODO

		// Return if the player drank their coffee. This will be manipulated in processCommand
		return playerDrankCoffee;
	}

	/**
	 * The method returns success if and only if: 1) Th southernmost room has a
	 * north door only, 2) The northernmost room has a south door only, and 3) The
	 * rooms in the middle have both north and south doors. If there is only one
	 * room, there should be no doors.
	 * 
	 * @return true if check successful, false otherwise
	 */
	public boolean areDoorsPlacedCorrectly() {
		// TODO

		int size = rooms.size();
		Room southMostRoom = rooms.get(0);
		Room northMostRoom = rooms.get(size - 1);
		
		if(southMostRoom.getSouthDoor() == null && southMostRoom.getNorthDoor() != null){
			if(northMostRoom.getNorthDoor() == null && northMostRoom.getSouthDoor() != null){
				for(int i = 1; i < size - 1; i++){
					Room room = rooms.get(i);

					if(room.getSouthDoor() == null || room.getNorthDoor() == null){
						return false;
					}
				}
			}else{
				return false; // Here
			}
		}else{
			return false; // Here
		}

		return true;
	}

	/**
	 * Checks whether each room has a unique adjective and furnishing.
	 * 
	 * @return true if check successful, false otherwise
	 */

	public boolean areRoomsUnique() {
		// TODO

		// Checks if both the adjectives and furnishings are unique by comparing all of the elements and
		// seeing if any are equal.
		for(int i = 0; i < rooms.size(); i++){
			for(int j = 0; j < rooms.size(); j++){
				Room room1 = rooms.get(i);
				Room room2 = rooms.get(j);
				String adjectiveRoom1 = room1.getAdjective();
				String adjectiveRoom2 = room2.getAdjective();
				String furnishingRoom1 = room1.getFurnishing();
				String furnishingRoom2 = room2.getFurnishing();

				if(adjectiveRoom1.equals(adjectiveRoom2) || furnishingRoom1.equals(furnishingRoom2)){
					if(i == j){
						continue;
					}else{
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Returns the room the player is currently in. If location of player has not
	 * yet been initialized with setCurrentRoom, returns null.
	 * 
	 * @return room player is in, or null if not yet initialized
	 */
	public Room getCurrentRoom() {
		// TODO

		// currentRoom is a global variable that is constantly updated via setCurrentRoom. 
		// Will be null at the start.
		return currentRoom;
	}

	/**
	 * Set the current location of the player. If room does not exist in the game,
	 * then the location of the player does not change and false is returned.
	 * 
	 * @param room the room to set as the player location
	 * @return true if successful, false otherwise
	 */
	public boolean setCurrentRoom(Room room) {
		// TODO
		if(room == null){
			return false;
		}

		currentRoom = room;
		return true;
	}

	/**
	 * Get the instructions string command prompt. It returns the following prompt:
	 * " INSTRUCTIONS (N,S,L,I,D,H) > ".
	 * 
	 * @return comamnd prompt string
	 */
	public String getInstructionsString() {
		// TODO
		return " INSTRUCTIONS (N,S,L,I,D,H) > ";
		//return "N - Go north\nS - Go south\nL - Look and collect any items in the room\nI - Show inventory of items collected\nD - Drink coffee made from items in inventory";
	}

	/**
	 * Processes the user command given in String cmd and returns the response
	 * string. For the list of commands, please see the Coffee Maker Quest
	 * requirements documentation (note that commands can be both upper-case and
	 * lower-case). For the response strings, observe the response strings printed
	 * by coffeemaker.jar. The "N" and "S" commands potentially change the location
	 * of the player. The "L" command potentially adds an item to the player
	 * inventory. The "D" command drinks the coffee and ends the game. Make
	 * sure you use Player.getInventoryString() whenever you need to display
	 * the inventory.
	 * 
	 * @param cmd the user command
	 * @return response string for the command
	 */
	public String processCommand(String cmd) {
		// TODO

		int currentRoomIndex = -1;
		for(int i = 0; i < rooms.size(); i++){
			if(currentRoom==rooms.get(i)){ //null pointer changed from .equals to ==
				currentRoomIndex = i;
				break;
			}
		}

		if(cmd.equals("N") || cmd.equals("n")){
			if(currentRoom.getNorthDoor() != null){
				setCurrentRoom(rooms.get(currentRoomIndex + 1));
				return "";
			}else{
				return "A door in that direction does not exist.\n"; // + currentRoom.getDescription();
			}

		}else if(cmd.equals("S") || cmd.equals("s")){
			if(currentRoom.getSouthDoor() != null){
				setCurrentRoom(rooms.get(currentRoomIndex - 1));
				return "";
			}else{
				return "A door in that direction does not exist.\n"; // + currentRoom.getDescription();
			}

		}else if(cmd.equals("L") || cmd.equals("l")){
			Item item = currentRoom.getItem();

			player.addItem(item);

			if(item.equals(Item.COFFEE)){
				return "There might be something here...\nYou found some caffeinated coffee!\n"; // + currentRoom.getDescription();
			}
			if(item.equals(Item.CREAM)){
				return "There might be something here...\nYou found some creamy cream!\n"; // + currentRoom.getDescription();
			}
			if(item.equals(Item.SUGAR)){
				return "There might be something here...\nYou found some sweet sugar!\n"; // + currentRoom.getDescription();
			}

			return "You don't see anything out of the ordinary.\n"; // + currentRoom.getDescription();

		}else if(cmd.equals("I") || cmd.equals("i")){

			return player.getInventoryString(); // + "\n"; // + currentRoom.getDescription();
			
		}else if(cmd.equals("D") || cmd.equals("d")){

			/* NOTE: Using Player.getInventoryString() errors because it needs to be static. However,
			I don't want to change it to be static as it is a pre-defined class. Will need to ask him
			what he wants us to do in that case.*/

			playerDrankCoffee = true;

			if(player.checkCoffee() && player.checkCream() && player.checkSugar()){
				// Has coffee, cream, and sugar
				return player.getInventoryString() + "\nYou drink the beverage and are ready to study!\nYou win!\n";
			}
			if(!player.checkCoffee() && !player.checkCream() && !player.checkSugar()){
				// Does not have coffee, cream, or sugar
				return player.getInventoryString() + "\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n"; 
			}
			if(player.checkCoffee() && !player.checkCream() && player.checkSugar()){
				// Has coffee and sugar. Does not have cream.
				return player.getInventoryString() + "\nWithout cream, you get an ulcer and cannot study.\nYou lose!\n";
			}
			if(player.checkCoffee() && player.checkCream() && !player.checkSugar()){
				// Has coffee and cream. Does not have sugar.
				return player.getInventoryString() + "\nWithout sugar, the coffee is too bitter. You cannot study.\nYou lose!\n";
			}
			if(player.checkCoffee() && !player.checkCream() && !player.checkSugar()){
				// Has coffee. Does not have cream or sugar.
				return player.getInventoryString() + "\nWithout cream, you get an ulcer and cannot study.\nYou lose!\n";
			}
			if(!player.checkCoffee() && player.checkCream() && player.checkSugar()){
				// Has cream and sugar. Does not have coffee.
				return player.getInventoryString() + "\nYou drink the sweetened cream, but without caffeine you cannot study.\nYou lose!\n";
			}
			if(!player.checkCoffee() && player.checkCream() && !player.checkSugar()){
				// Has cream. Does not have coffee and sugar.
				return player.getInventoryString() + "\nYou drink the cream, but without caffeine, you cannot study.\nYou lose!\n";
			}
			if(!player.checkCoffee() && !player.checkCream() && player.checkSugar()){
				// Has sugar. Does not have coffee or cream.
				return player.getInventoryString() + "\nYou eat the sugar, but without caffeine, you cannot study.\nYou lose!\n";
			}

		}else if(cmd.equals("H") || cmd.equals("h")){
			return "N - Go north\nS - Go south\nL - Look and collect any items in the room\nI - Show inventory of items collected\nD - Drink coffee made from items in inventory\n";
		}

		return "What?\n";
	}

}
