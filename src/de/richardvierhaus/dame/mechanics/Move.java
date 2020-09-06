package de.richardvierhaus.dame.mechanics;

import java.util.ArrayList;

import de.richardvierhaus.dame.exceptions.MoveNotExecutableException;

public class Move {

	private Field start, kick, end;
	private ButtonColour colour;
	private ButtonType type;
	private ArrayList<Move> followingMoves;
	private Move parent;

	private boolean painted;

	protected Move(Field startField) {
		this(startField, null, startField.getColour(), startField.getType());
	}

	protected Move(Field startField, Move parent, ButtonColour colour, ButtonType type) {
		painted = false;
		this.parent = parent;
		this.start = startField;
		this.colour = colour;
		this.type = type;
		kick = null;
		followingMoves = new ArrayList<>();
	}

	protected Move setEnd(Field end) {
		this.end = end;
		return this;
	}

	protected Move addKick(Field kick) {
		this.kick = kick;
		int xVec, yVec;
		if (kick.getPositionX() > start.getPositionX())
			xVec = 1;
		else
			xVec = -1;
		if (kick.getPositionY() > start.getPositionY())
			yVec = 1;
		else
			yVec = -1;
		setEnd(Board.getInstance().getFields()[kick.getPositionX() + xVec][kick.getPositionY() + yVec]);
		ArrayList<Field> canKick = end.canKick(colour, type);
		boolean contains = false;
		Move move = this;
		while (move.getParent() != null) {
			move = move.getParent();
			if (canKick.contains(move.getKick())) {
				contains = true;
				break;
			}
		}
		if (canKick.size() > 0 && !contains) {
			xVec *= -1;
			yVec *= -1;
			for (Field field : canKick) {
				int x = end.getPositionX();
				int y = end.getPositionY();
				boolean found = false;
				while (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
					if (field.getPositionX() == x && field.getPositionY() == y) {
						found = true;
					}
					x += xVec;
					y += yVec;
				}
				if (!found)
					followingMoves.add(new Move(end, this, colour, type).addKick(field));
			}
		}
		return this;
	}

	protected void paint() {
		if (!painted) {
			painted = true;
			start.setStatus(ButtonStatus.ACTION);

			end.setStatus(ButtonStatus.ACTION);
			if (kick != null) {
				kick.setStatus(ButtonStatus.KICK);
				if (Math.abs(kick.getPositionX() - start.getPositionX()) > 1) {
					int xVec, yVec, x, y;
					x = start.getPositionX();
					y = start.getPositionY();
					if (kick.getPositionX() > start.getPositionX())
						xVec = 1;
					else
						xVec = -1;
					if (kick.getPositionY() > start.getPositionY())
						yVec = 1;
					else
						yVec = -1;
					while (x != kick.getPositionX()) {
						Board.getInstance().getFields()[x][y].setStatus(ButtonStatus.ACTION);
						x += xVec;
						y += yVec;
					}
				}
			}
			followingMoves.forEach(move -> {
				if (!move.isPainted())
					move.paint();
			});
			if (followingMoves.size() == 0)
				Board.getInstance().getActiveMoves().put(end, this);
		}
	}

	protected void unpaint() {
		if (painted) {
			painted = false;
			start.setStatus(ButtonStatus.NONE);
			end.setStatus(ButtonStatus.NONE);
			if (kick != null) {
				kick.setStatus(ButtonStatus.NONE);
				if (Math.abs(kick.getPositionX() - start.getPositionX()) > 1) {
					int xVec, yVec, x, y;
					x = start.getPositionX();
					y = start.getPositionY();
					if (kick.getPositionX() > start.getPositionX())
						xVec = 1;
					else
						xVec = -1;
					if (kick.getPositionY() > start.getPositionY())
						yVec = 1;
					else
						yVec = -1;
					while (x != kick.getPositionX()) {
						Board.getInstance().getFields()[x][y].setStatus(ButtonStatus.NONE);
						x += xVec;
						y += yVec;
					}
				}
			}
			if (parent != null) {
				parent.unpaint();
			}
			followingMoves.forEach(move -> move.unpaint());
		}
	}

	protected void execute(Board b) {
		if (b instanceof Simulation || b == Board.getInstance()) {
			Move current = this;
			ArrayList<Move> moves = new ArrayList<>();
			moves.add(this);
			while (current.getParent() != null) {
				current = current.getParent();
				moves.add(current);
			}
			current.getStart().getMoves().forEach(startMove -> startMove.unpaint());

			for (int i = moves.size() - 1; i >= 0; i--) {
				current = moves.get(i);
				current.getEnd().setStone(colour, type);
				current.getEnd().setStatus(ButtonStatus.NONE);
				current.getStart().setStone(ButtonColour.EMPTY, ButtonType.NONE);
				current.getStart().setStatus(ButtonStatus.NONE);
				if (b.getWhiteFields().contains(current.getStart())) {
					b.getWhiteFields().remove(current.getStart());
					b.getWhiteFields().add(current.getEnd());
				} else {
					b.getBlackFields().remove(current.getStart());
					b.getBlackFields().add(current.getEnd());
				}

				if (current.getKick() != null) {
					current.getKick().setStatus(ButtonStatus.NONE);
					current.getKick().setStone(ButtonColour.EMPTY, ButtonType.NONE);
					if (b.getWhiteFields().contains(current.getKick())) {
						b.getWhiteFields().remove(current.getKick());
					} else {
						b.getBlackFields().remove(current.getKick());
					}
				}
			}
			b.changeTurn();
		}else 
			throw new MoveNotExecutableException("Move can not be executed manualy");
		
	}

	protected void paintAndExecute() {
		if (!isExecutable())
			throw new MoveNotExecutableException("Move has children");
		Thread execute = new Thread(() -> {

			try {
				Thread.sleep(1000);
				Move m = this;
				while (m.getParent() != null) {
					m = m.getParent();
				}
				Board.getInstance().setActiveField(m.getStart());
				m.getStart().paintMoves();
				Thread.sleep(2500);
			} catch (InterruptedException e) {
			}
			execute(Board.getInstance());

		});
		execute.start();

	}

	public boolean isExecutable() {
		return followingMoves.size() == 0;
	}

	public ArrayList<Move> getFollowingMoves() {
		return followingMoves;
	}

	public Move getParent() {
		return parent;
	}

	public Field getEnd() {
		return end;
	}

	public Field getStart() {
		return start;
	}

	public Field getKick() {
		return kick;
	}

	private boolean isPainted() {
		return painted;
	}

}
