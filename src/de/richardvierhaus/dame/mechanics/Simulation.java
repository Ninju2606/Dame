package de.richardvierhaus.dame.mechanics;

import java.util.ArrayList;

public class Simulation extends Board {

	protected Simulation(Field[][] fields, ArrayList<Field> whiteFields, ArrayList<Field> blackFields, boolean turn) {
		super(fields, blackFields, whiteFields, turn);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((j % 2 == 0) == (i % 2 == 0)) {
					getFields()[i][j].setBoard(this);
				}
			}
		}
	}

	public void executeMove(Move m) {
		Move execute = null;
		Field start;
		Move p = m;
		while (p.getParent() != null) {
			p = p.getParent();
		}
		start = p.getStart();
		ArrayList<Move> moves = getFields()[start.getPositionX()][start.getPositionY()].getMoves();
		for (Move move : moves) {
			if (!move.isExecutable()) {
				moves.addAll(move.getFollowingMoves());
			} else {
				if (move.getEnd().getPositionX() == m.getEnd().getPositionX()
						&& move.getEnd().getPositionY() == m.getEnd().getPositionY()) {
					execute = move;
				}
			}
		}
		if (execute != null)
			execute.execute(this);
	}

}
