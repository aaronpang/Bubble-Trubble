public class Unit
{
    protected int movement;   //movement tiles
    protected int hp;         //total hp
    protected int strength;   //attack amount
    protected int range;      //attack range
    protected int image;
    protected int attackType;
    protected int criticalHit;      //critical hit ratio

    /** Creates each unit with movement
      */
    public Unit (int movement, int hp, int strength, int range, int image, int attackType, int criticalHit)
    {
	this.movement = movement;
	this.hp = hp;
	this.strength = strength;
	this.range = range;
	this.image = image;
	this.attackType = attackType;
	this.criticalHit = criticalHit;
    }


    /** Determines the picture and returns the values
      */
    public String toString ()
    {
	return "" + "Movement: " + movement + "   HP:  " + hp +
	    "\nStrength:  " + strength + "   Range:  " + range;
    }



    public int getMovement ()
    {
	return movement;
    }


    public int getHP ()
    {
	return hp;
    }


    public int getStrength ()
    {
	return strength;
    }


    public int getRange ()
    {
	return range;
    }


    public int getImageNumber ()
    {
	return image;
    }


    public int getAttackType ()
    {
	return attackType;
    }


    public int getCriticalHit ()
    {
	return criticalHit;
    }


    /** Add one range to a unit
      * @param rangeCheck   the num
      */
    public int addRange (int rangeCheck)
    {
	range += rangeCheck;
	return range;
    }
}
