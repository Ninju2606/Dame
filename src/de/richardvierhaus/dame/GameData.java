package de.richardvierhaus.dame;

import de.richardvierhaus.dame.pluginLoading.PluginManager;

public class GameData {

	private String player1Name;
	private String player2Name;
	private PluginManager pluginManager;

	private boolean devmode;

	public GameData(String player1Name, String player2Name, PluginManager manager,
			boolean devmode) {
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.pluginManager = manager;
		this.devmode = devmode;
	}

	public String getPlayer1Name() {
		return player1Name;
	}

	public String getPlayer2Name() {
		return player2Name;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public boolean isDevmode() {
		return devmode;
	}

}
