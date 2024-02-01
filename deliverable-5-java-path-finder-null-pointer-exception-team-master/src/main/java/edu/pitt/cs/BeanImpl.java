package edu.pitt.cs;

import java.util.Random;

/**
 * Code by @author Wonsun Ahn. Copyright Fall 2022.
 * 
 * <p>
 * Bean: If created in skill mode, each bean is assigned a skill level from 0-9
 * on creation according to a normal distribution with average SKILL_AVERAGE and
 * standard deviation SKILL_STDEV. The formula to calculate the skill level is:
 * 
 * <pre>
 * SKILL_AVERAGE = (double) (SLOT_COUNT-1) * 0.5
 * SKILL_STDEV = (double) Math.sqrt(SLOT_COUNT * 0.5 * (1 - 0.5))
 * SKILL_LEVEL = (int) Math.round(rand.nextGaussian() * SKILL_STDEV + SKILL_AVERAGE)
 * SKILL_LEVEL = SKILL_LEVEL > (SLOT_COUNT - 1) ? (SLOT_COUNT - 1) : SKILL_LEVEL;
 * SKILL_LEVEL = SKILL_LEVEL < 0 ? 0 : SKILL_LEVEL;
 * </pre>
 * 
 * <p>
 * A skill level of 9 means it always makes the "right" choices (pun intended)
 * when the machine is operating in skill mode ("skill" passed on command line).
 * That means the bean will always go right when a peg is encountered, resulting
 * it falling into slot 9. A skill level of 0 means that the bean will always go
 * left, resulting it falling into slot 0. For the in-between skill levels, the
 * bean will first go right then left. For example, for a skill level of 7, the
 * bean will go right 7 times then go left twice.
 * 
 * <p>
 * Skill levels are irrelevant when the machine operates in luck mode. In that
 * case, the bean will have a 50/50 chance of going right or left, regardless of
 * skill level. The formula to calculate the direction is: rand.nextInt(2). If
 * the return value is 0, the bean goes left. If the return value is 1, the bean
 * goes right.
 */

public class BeanImpl implements Bean {

	private int xpos;
	private int ypos;
	private boolean isLuck;
	private Random rand;
	int slotCount;
	int skillLevel;

	/**
	 * Constructor - creates a bean in either luck mode or skill mode.
	 * 
	 * @param slotCount the number of slots in the machine
	 * @param isLuck    whether the bean is in luck mode
	 * @param rand      the random number generator
	 */
	BeanImpl(int slotCount, boolean isLuck, Random rand) {

		xpos = 0;
		ypos = 0;
		this.isLuck = isLuck;
		this.rand = rand;
		this.slotCount = slotCount;

		if (!isLuck) {
			double skillAverage = (double) (slotCount - 1) * 0.5;
			double skillDev = (double) Math.sqrt(slotCount * 0.5 * (1 - 0.5));
			int skill = (int) Math.round(rand.nextGaussian() * skillDev + skillAverage);
			skill = skill > (slotCount - 1) ? (slotCount - 1) : skill;
			skill = skill < 0 ? 0 : skill;

			skillLevel = skill;
		}
	}

	/**
	 * Returns the current X-coordinate position of the bean in the logical
	 * coordinate system.
	 * 
	 * @return the current X-coordinate of the bean
	 */
	public int getXPos() {
		return xpos;
	}

	/**
	 * Returns the current Y-coordinate position of the bean in the logical
	 * coordinate system.
	 * 
	 * @return the current Y-coordinate of the bean
	 */
	public int getYPos() {
		return ypos;
	}

	/**
	 * Resets the bean to its initial state. The X-coordinate should be initialized
	 * to 0.
	 * resets with one bean at the top waiting to move down (0,0)
	 */
	public void reset() {
		xpos = 0;
		ypos = 0;
	}

	/**
	 * Update the X and Y coordinates of the bean when the bean is advanced one step
	 * in the machine.
	 * The Y-coordinate is incremented by 1. The X-coordinate gets updated depending
	 * on whether the bean chooses to bounce left or right from the current peg.
	 * The choice ie made randomly if the bean is in luck mode depending on the
	 * return value of rand.nextInt(2): if it is 0, the bean goes left, if it is 1,
	 * the bean goes right. If the bean is a skilled bean, the choice is made
	 * deterministically according to the algorithm on the class description.
	 */
	public void advanceStep() {
		ypos += 1;
		if (this.isLuck && ypos < slotCount) {
			// Each step the x coordinate grows by 1, so we do not need to update it to go
			// left.
			if (rand.nextInt(2) == 1) {
				// right
				// Need to stay within bounds
				if (xpos < slotCount - 1) {
					xpos += 1;
				}
			}
		} else if (ypos < slotCount) {
			// Skilled beans
			// We go right whatever our skill level is. numOfSteps starts at 0.
			if (ypos <= skillLevel) {
				if (xpos < slotCount - 1) {
					xpos += 1;
				}
			}
		}
	}
}