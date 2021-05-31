package de.hechler.patrick.fileparser.serial;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class Dirty {
	
	private static final Field modifiers;
	
	static {
		try {
			modifiers = Field.class.getField("modifiers");
		} catch(NoSuchFieldException e) {
			throw new InternalError(e);
		}
	}
	
	static final void addFinal(Field f) {
		try {
			boolean mf = modifiers.isAccessible();
			modifiers.setAccessible(true);
			int mod = modifiers.getInt(f);
			modifiers.setInt(f, mod | Modifier.FINAL);
			modifiers.setAccessible(mf);
		} catch (IllegalAccessException e) {
			throw new InternalError(e);
		}
	}
	
	static final boolean removeFinal(Field f) {
		try {
			boolean mf = modifiers.isAccessible();
			modifiers.setAccessible(true);
			int mod = modifiers.getInt(f);
			if ((mod & Modifier.FINAL) == 0) {
				return false;
			}
			modifiers.setInt(f, mod & ~Modifier.FINAL);
			modifiers.setAccessible(mf);
			return true;
		} catch (IllegalAccessException e) {
			throw new InternalError(e);
		}
	}
	
}
