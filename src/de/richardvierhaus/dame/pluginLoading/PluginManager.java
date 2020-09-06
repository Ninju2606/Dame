package de.richardvierhaus.dame.pluginLoading;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginManager {
	
	private PluginInterface plugin1, plugin2;

	public PluginManager() {
		plugin1 = null;
		plugin2 = null;
	}

	@SuppressWarnings({ "resource", "rawtypes" })
	public PluginInterface getPlugin(String path)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		File f = new File(path);

		JarFile jFile = new JarFile(f);
		Manifest manifest = jFile.getManifest();
		Attributes attrib = manifest.getMainAttributes();
		String main = attrib.getValue("Main-Class");

		Class cl = new URLClassLoader(new URL[] { new URL("file:/" + f.getAbsolutePath().replace("\\", "/")) })
				.loadClass(main);
		Class[] interfaces = cl.getInterfaces();

		boolean isplugin = false;
		for (int i = 0; i < interfaces.length && !isplugin; i++)
			if (interfaces[i].getName().equals("de.richardvierhaus.dame.pluginLoading.PluginInterface"))
				isplugin = true;
		if (isplugin) {
			PluginInterface plugin = (PluginInterface) cl.newInstance();
			return plugin;
		}
		throw new NullPointerException();
	}

	public PluginInterface getPlugin1() {
		return plugin1;
	}

	public PluginInterface getPlugin2() {
		return plugin2;
	}

	public void setPlugin1(PluginInterface plugin1) {
		this.plugin1 = plugin1;
	}

	public void setPlugin2(PluginInterface plugin2) {
		this.plugin2 = plugin2;
	}
	
	

}
