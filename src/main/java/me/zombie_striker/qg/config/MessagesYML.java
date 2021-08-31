package me.zombie_striker.qg.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

public class MessagesYML {

	private final FileConfiguration c;
	private final File s;
	public MessagesYML(File f) {
		s = f;
		if(!s.exists()) {
			try {
				s.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		c = CommentYamlConfiguration.loadConfiguration(s);
	}
	public Object a(String path, Object val){
		if(!c.contains(path)){
			c.set(path, val);
			save();
			return val;
		}
		return c.get(path);
	}
	public void set(String path, Object val) {
		c.set(path, val);
		save();
	}
	public void save(){
		try {
			c.save(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getConfig() {
		return c;
	}
}
