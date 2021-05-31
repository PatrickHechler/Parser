package de.hechler.patrick.fileparser.serial;

import static de.hechler.patrick.fileparser.serial.SerialConsts.NULL;
import static de.hechler.patrick.fileparser.serial.SerialConsts.OBJECT;
import static de.hechler.patrick.fileparser.serial.SerialConsts.identyToStringOrElse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Deserializer {
	
	private final Map<Class<?>, Method> creates;
	
	public Deserializer(Map<Class<?>, Method> creates) {
		this.creates = Collections.unmodifiableMap(new HashMap <>(creates));
	}
	
	// public void writeObject(OutputStream out, Object val) throws IOException {
	public void overwriteObject(InputStream in, Object val) throws IOException {
		assertRead(in, OBJECT);
		Class <?> cls;
		{
			String clsName = readString(in);
			try {
				cls = Class.forName(clsName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new NoClassDefFoundError(clsName);
			}
		}
		val.getClass().asSubclass(cls);
		// while (true) {
		// Field[] fields = cls.getDeclaredFields();
		// List<Field> save = new ArrayList<>();
		// for(int i = 0; i < fields.length; i ++) {
		// int mod = fields[i].getModifiers();
		// if (!saveStatic && (mod & Modifier.STATIC) != 0) continue;
		// if (!saveNonStatic && (mod & Modifier.STATIC) == 0) continue;
		// if (!saveFinal && (mod & Modifier.FINAL) != 0) continue;
		// if (!saveNonFinal && (mod & Modifier.FINAL) == 0) continue;
		// save.add(fields[i]);
		// }
		// fields = save.toArray(new Field[save.size()]);
		while (true) {
			// writeInt(out, fields.length);
			int len = readInt(in);
			// for (int i = 0; i < fields.length; i ++) {
			for (int i = 0; i < len; i ++ ) {
				// writeString(out, fields[i].getName());
				Field field;
				{
					String fn = readString(in);
					try {
						field = cls.getDeclaredField(fn);
					} catch (NoSuchFieldException | SecurityException e) {
						throw new InternalError(e);
					}
				}
				
				// Class <?> ft = fields[i].getType();
				// try {
				// boolean flag = fields[i].isAccessible();
				// fields[i].setAccessible(true);
				// if (ft.isPrimitive()) {
				// writePrimitive(out, fields[i].get(val));
				// } else {
				// writeObject(out, fields[i].get(val));
				// }
				boolean wa = field.isAccessible();
				field.setAccessible(true);
				boolean wf = Dirty.removeFinal(field);
				Object obj = readObject(in);
				try {
					field.set(val, obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new InternalError(e);
				}
				if (wf) {
					Dirty.addFinal(field);
				}
				field.setAccessible(wa);
				// fields[i].setAccessible(flag);
				// } catch(IllegalAccessException e) {
				// throw new InternalError(e);
				// }
				// }
			}
			// if (saveSuperClassFiedls) {
			// cls = cls.getSuperclass();
			// if (cls != null) {
			// out.write(NEXT_SUPERCLASS);
			// }
			// } else {
			// cls = null;
			// }
			// if (cls == null) {
			// out.write(NULL);
			// break;
			// }
			// }
			int zw = assertRead(in, NULL, OBJECT);
			if (zw == 0) { // NULL
				break;
			} else {// OBJECT
				cls = cls.getSuperclass();
			}
			
		}
	}
	
	private Object readObject(InputStream in) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static int assertRead(InputStream in, int... values) throws IOException {
		int val = in.read();
		for (int i = 0; i < values.length; i ++ ) {
			if (val == values[i]) return i;
		}
		// "asserted to read '" + Arrays.toString(values) + "' (" + identyToStringOrElse(value, "no identy") + "), but got " + val
		StringBuilder errMsg = new StringBuilder("asserted to read [");
		if (values.length > 0) {
			errMsg.append(values[0]);
		}
		for (int i = 1; i < values.length; i ++ ) {
			errMsg.append(", ").append(values[i]);
		}
		errMsg.append("] in identys: [");
		if (values.length > 0) {
			errMsg.append(identyToStringOrElse(values[0], "no identy"));
		}
		for (int i = 1; i < values.length; i ++ ) {
			errMsg.append(", ").append(identyToStringOrElse(values[i], "no identy"));
		}
		errMsg.append("], but got: ").append(val).append(" identy: ").append(identyToStringOrElse(val, "no identy"));
		throw new AssertionError(errMsg.toString());
	}
	
	private static String readString(InputStream in) throws IOException {
		int len = readInt(in);
		byte[] bytes = new byte[len];
		in.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	private static int readInt(InputStream in) throws IOException {
		int ret = 0;
		byte[] bytes = new byte[4];
		in.read(bytes);
		ret = bytes[0] & 0xFF;
		ret = (bytes[1] & 0xFF) << 8;
		ret = (bytes[2] & 0xFF) << 16;
		ret = (bytes[3] & 0xFF) << 24;
		return ret;
	}
	
	
}
