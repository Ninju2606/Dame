package de.richardvierhaus.dame.mechanics;

public class StoneClickEvent {
	
	private final Field source;

	public StoneClickEvent(Field source) {
		this.source = source;
	}
	
	public Field getSource() {
		return source;
	}

}
