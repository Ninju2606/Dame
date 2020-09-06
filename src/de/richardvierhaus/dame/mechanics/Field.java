package de.richardvierhaus.dame.mechanics;

import java.util.ArrayList;

import de.richardvierhaus.dame.exceptions.InvalidButtonTypeException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Field {

	private final ImageView stone;
	private Board board;

	private ButtonStatus status;
	private ButtonColour colour;
	private ButtonType type;

	private final int positionX;
	private final int positionY;

	private StoneClickListener listener;

	private ArrayList<Move> possibleMoves;

	protected Field(ImageView stone, int positionX, int positionY, Board b) {
		this.board = b;
		this.colour = ButtonColour.EMPTY;
		this.status = ButtonStatus.NONE;
		this.type = ButtonType.NONE;
		this.stone = stone;
		this.positionX = positionX;
		this.positionY = positionY;
		this.possibleMoves = new ArrayList<>();
		stone.setOnMouseClicked(e -> {
			if (listener != null)
				listener.onStoneClicked(new StoneClickEvent(this));
		});
	}

	protected Field(int positionX, int positionY, ButtonColour colour, ButtonType type) {
		this.stone = null;
		this.positionX = positionX;
		this.positionY = positionY;
		this.colour = colour;
		this.type = type;
		this.possibleMoves = new ArrayList<>();
	}
	
	

	public ArrayList<Field> getNeighbours(boolean empty, boolean onlyForward, ButtonColour colour) {
		ArrayList<Field> neighbours = new ArrayList<>();
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				if (positionX + i >= 0 && positionX + i <= 7 && positionY + j >= 0 && positionY + j <= 7) {
					if (!(onlyForward && (colour == ButtonColour.WHITE && j < 0)
							|| (colour == ButtonColour.BLACK && j > 0)) || !onlyForward) {
						Field f = board.getFields()[positionX + i][positionY + j];
						if ((empty && f.getColour() == ButtonColour.EMPTY) || !empty)
							neighbours.add(f);
					}
				}
			}
		}
		return neighbours;
	}

	public ArrayList<Field> getNeighbours(boolean empty, boolean onlyForward) {
		return getNeighbours(empty, onlyForward, colour);
	}

	protected void calculateMoves() {
		possibleMoves.clear();
		if (type == ButtonType.STONE) {
			ArrayList<Field> neighbours = getNeighbours(false, true);

			for (Field neighbour : neighbours) {
				ArrayList<Field> canKick = canKick();
				if (neighbour.getColour() == ButtonColour.EMPTY) {
					possibleMoves.add(new Move(this).setEnd(neighbour));
				} else if (canKick.size() > 0) {
					for (Field field : canKick) {
						possibleMoves.add(new Move(this).addKick(field));
					}
				}
			}
		} else {
			possibleMoves = getCheckerMoves(false);
			canKick().forEach(f -> possibleMoves.add(new Move(this).addKick(f)));
		}
	}

	private ArrayList<Move> getCheckerMoves(boolean onlyEnd) {
		ArrayList<Move> moves = new ArrayList<>();
		int tempX = positionX;
		int tempY = positionY;
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				ArrayList<Move> current = new ArrayList<>();
				tempX += i;
				tempY += j;
				while ((tempX > 0 && tempX < 7 && tempY > 0 && tempY < 7)
						&& board.getFields()[tempX][tempY].isEmpty()) {
					current.add(new Move(this).setEnd(board.getFields()[tempX][tempY]));
					tempX += i;
					tempY += j;
				}
				try {
					if (board.getFields()[tempX][tempY].isEmpty())
						current.add(new Move(this).setEnd(board.getFields()[tempX][tempY]));

				} catch (ArrayIndexOutOfBoundsException e) {
				}
				if (onlyEnd && current.size() > 0)
					moves.add(current.get(current.size() - 1));
				else
					moves.addAll(current);
				tempX = positionX;
				tempY = positionY;
			}
		}
		return moves;
	}

	protected void paintMoves() {
		possibleMoves.forEach(move -> move.paint());
	}

	public ArrayList<Field> canKick() {
		return this.canKick(colour, type);
	}

	public ArrayList<Field> canKick(ButtonColour colour, ButtonType type) {
		ArrayList<Field> kickableButtons = new ArrayList<>();
		if (type == ButtonType.STONE) {
			for (Field neighbour : getNeighbours(false, true, colour)) {
				if (neighbour.getColour() != ButtonColour.EMPTY && neighbour.getColour() != colour) {
					try {
						Field target = board.getFields()[neighbour.getPositionX()
								+ (neighbour.getPositionX() - this.getPositionX())][neighbour.getPositionY()
										+ (neighbour.getPositionY() - this.getPositionY())];
						if (target.isEmpty()) {
							kickableButtons.add(neighbour);
						}
					} catch (IndexOutOfBoundsException e) {

					}
				}
			}
		} else if (type == ButtonType.CHECKER) {
			ArrayList<Move> moves = getCheckerMoves(true);
			for (Move m : moves) {
				int xVec, yVec;
				if (m.getEnd().getPositionX() > positionX)
					xVec = 1;
				else
					xVec = -1;
				if (m.getEnd().getPositionY() > positionY)
					yVec = 1;
				else
					yVec = -1;
				try {
					Field kick = board.getFields()[m.getEnd().getPositionX() + xVec][m.getEnd()
							.getPositionY() + yVec];
					if (kick.getColour() != ButtonColour.EMPTY && kick.getColour() != colour) {
						Field target = board.getFields()[kick.getPositionX() + xVec][kick.getPositionY()
								+ yVec];
						if (target.isEmpty()) {
							kickableButtons.add(kick);
						}
					}

				} catch (IndexOutOfBoundsException e) {

				}
			}
			for (Field neighbour : getNeighbours(false, false, colour)) {
				if (neighbour.getColour() != ButtonColour.EMPTY && neighbour.getColour() != colour) {
					try {
						Field target = board.getFields()[neighbour.getPositionX()
								+ (neighbour.getPositionX() - this.getPositionX())][neighbour.getPositionY()
										+ (neighbour.getPositionY() - this.getPositionY())];
						if (target.isEmpty()) {
							kickableButtons.add(neighbour);
						}
					} catch (IndexOutOfBoundsException e) {

					}
				}
			}
		}
		return kickableButtons;
	}

	public boolean isEnd(ButtonColour colour, ButtonType type) {
		if (type == ButtonType.STONE && ((colour == ButtonColour.BLACK && positionY == 0)
				|| (colour == ButtonColour.WHITE && positionY == 7))) {
			return true;
		}
		return false;
	}

	protected void setStatus(ButtonStatus status) {
		this.status = status;
		updateImage();
	}

	protected void setStone(ButtonColour colour, ButtonType type) {
		if ((colour != ButtonColour.EMPTY && type == ButtonType.NONE)
				|| (colour == ButtonColour.EMPTY && type != ButtonType.NONE))
			throw new InvalidButtonTypeException();
		this.colour = colour;
		this.type = type;
		if (isEnd(colour, type))
			this.type = ButtonType.CHECKER;
		updateImage();
	}

	private void updateImage() {
		if (stone != null) {
			if (colour == ButtonColour.EMPTY) {
				if (status == ButtonStatus.ACTION)
					stone.setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_move.png"));
				else
					stone.setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark.png"));
			} else {
				String path = "/de/richardvierhaus/dame/grafics/field_dark";
				if (type == ButtonType.STONE)
					path += "_stone";
				if (type == ButtonType.CHECKER)
					path += "_checker";
				if (colour == ButtonColour.BLACK)
					path += "_dark";
				if (colour == ButtonColour.WHITE)
					path += "_bright";
				if (status == ButtonStatus.KICK)
					path += "_kick";
				if (status == ButtonStatus.ACTION)
					path += "_active";
				stone.setImage(new Image(path + ".png"));
			}
		}
	}

	public boolean isEmpty() {
		return colour == ButtonColour.EMPTY;
	}

	public ButtonColour getColour() {
		return colour;
	}

	public ButtonType getType() {
		return type;
	}

	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public ArrayList<Move> getMoves() {
		if (possibleMoves.isEmpty())
			calculateMoves();
		return possibleMoves;
	}
	
	protected Field setBoard(Board b) {
		this.board = b;
		return this;
	}

	protected Field setListener(StoneClickListener listener) {
		this.listener = listener;
		return this;
	}

}
