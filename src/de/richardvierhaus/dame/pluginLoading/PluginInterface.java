package de.richardvierhaus.dame.pluginLoading;

import java.util.ArrayList;

import de.richardvierhaus.dame.mechanics.ButtonColour;
import de.richardvierhaus.dame.mechanics.Move;
import de.richardvierhaus.dame.mechanics.Simulation;

public interface PluginInterface {
	
	public void setColour(ButtonColour colour);

	public Move doMove(ArrayList<Move> moves, Simulation s);

}
