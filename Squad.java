public class Squad
{
    final Unit[] squad = new Unit [8];
    /** Constructs a Squad object with 8 different units
      */
    public Squad ()
    {           // Range is minus 1, so 2 range is actually only 1
	//movement, hp, strength, range, image, attack type, criticalHit ratio
	// Unit 1 - Bubble Soldier
	squad [0] = new Unit (  6, 45, 11, 3, 1, 2,  15);
	// Unit 2 - Bubble Elite
	squad [1] = new Unit (  6, 40, 10, 3, 2, 1,  17);
	// Unit 3 - Bubble Ninja
	squad [2] = new Unit (  8, 35,  7, 2, 3, 4,  40);
	// Unit 4 - Bubble Mage
	squad [3] = new Unit (  7, 35, 13, 4, 4, 3,  30);
	// Unit 5 - Bubble Sniper
	squad [4] = new Unit (  4, 30,  9, 6, 5, 3,  25);
	// Unit 6 - Bubble Boy
	squad [5] = new Unit (  5, 38,  9, 4, 6, 0,  20);
	// Unit 7 - Mr. Bubble
	squad [6] = new Unit (  5, 38,  9, 4, 7, 1,  20);
	// Unit 8 - Bubble Tank
	squad [7] = new Unit (  3, 50, 15, 3, 8, 4,   5);
    }


    /** Returns the Squad
      * @return the Squad
      */
    public String toString ()
    {
	String totalSquad = "";
	for (int squadPos = 0 ; squadPos < 5 ; squadPos++)
	    totalSquad += squadPos + ")" + "\n" + squad [squadPos] + "\n";
	return totalSquad;
    }


    public Unit getUnit (int unitNumber)
    {
	return squad [unitNumber];
    }
}
