package de.hechler.patrick.fileparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Serializer {
	
	private static final int OBJECT_COSTUM_NO_STEP_B     = 23;
	private static final int OBJECT_COSTUM_START_STEP_B  = 22;
	private static final int OBJECT_COSTUM_FINISH_STEP_A = 21;
	private static final int OBJECT_COSTUM               = 20;
	private static final int OBJECT_UNKNOWN              = 19;
	private static final int CHAR_ARRAY                  = 18;
	private static final int BOOLEAN_ARRAY               = 17;
	private static final int FLOAT_ARRAY                 = 16;
	private static final int DOUBLE_ARRAY                = 15;
	private static final int SHORT_ARRAY                 = 14;
	private static final int LONG_ARRAY                  = 13;
	private static final int BYTE_ARRAY                  = 12;
	private static final int INT_ARRAY                   = 11;
	private static final int OBJECT_ARRAY                = 10;
	private static final int OBJECT_STRING               = 9;
	private static final int PRIMITIVE_SHORT             = 8;
	private static final int PRIMITIVE_CHAR              = 7;
	private static final int PRIMITIVE_BYTE              = 6;
	private static final int PRIMITIVE_LONG              = 5;
	private static final int PRIMITIVE_INT               = 4;
	private static final int PRIMITIVE_DOUBLE            = 3;
	private static final int PRIMITIVE_FLOAT             = 2;
	private static final int PRIMITIVE_BOOLEAN           = 1;
	private static final int NULL                        = -1;
	private static final int FINISH_VALUE                = -2;
	private static final int FINISH_ALL                  = -3;
	
	public static void load(Object obj, File loadFile) throws IOException {
		// OutputStream out = new FileOutputStream(saveFile);
		// Field[] fields = getClass().getDeclaredFields();
		// for (int i = 0; i < fields.length; i++) {
		// int mod = fields[i].getModifiers();
		// if ((mod & Modifier.TRANSIENT) != 0)
		// continue;// transient say do not serialize this
		// if ((mod & Modifier.FINAL) != 0)
		// continue;// can't set finals by loading anyway
		// save(fields[i], out);
		// }
		Class <?> myClass = obj.getClass();
		InputStream in = new FileInputStream(loadFile);
		load(obj, myClass, in);
	}
	
	private static void load(Object obj, Class <?> myClass, InputStream in) throws IOException, AssertionError, InternalError, Error, NoClassDefFoundError {
		while (true) {
			try {
				readField(obj, myClass, in);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				throw new InternalError("does not know the loaded field with name: ", e);
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new InternalError("the permission to acces the field was denied by the security manager!", e);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new InternalError("illegal argument: field.set.*(obj{'" + obj + "'", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new InternalError("security manager denied the permission to acces saved field of class: " + obj.getClass(), e);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				if (e.getCause() != null && e.getCause() instanceof Error) {
					throw (Error) e.getCause();
				}
				throw new NoClassDefFoundError("could not find the class! m: '" + e.getMessage() + "' lm: '" + e.getLocalizedMessage() + "'");
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new InternalError(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new InternalError(e);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new AssertionError("could not load the method m: '" + e.getMessage() + "' lm: '" + e.getLocalizedMessage() + "'");
			}
		}
	}
	
	private static void readField(Object obj, Class <?> myClass, InputStream in)
			throws IOException, NoSuchFieldException, IllegalAccessException, AssertionError, ClassNotFoundException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		String name = readString(in);
		Field field = myClass.getDeclaredField(name);
		Map <Class <?>, Options> opts = Options.create(field.getAnnotation(SerializerOptions.class));
		Object val = readValue(in, opts);
		field.set(obj, val);
	}
	
	private static Object readValue(InputStream in, Map <Class <?>, Options> opts)
			throws IOException, IllegalAccessException, AssertionError, ClassNotFoundException, InstantiationException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
		Object ret;
		int identy = readInt(in);
		switch (identy) {
		case NULL:
			ret = null;
			break;
		case PRIMITIVE_BOOLEAN: {
			// if (type == Boolean.Type) {
			// out.write(1);
			// boolean bool = (boolean) (Boolean) val;
			// out.write(bool ? 1 : 0);
			int val = in.read();
			if (val != 1 && val != 0) {
				throw new AssertionError("expected to read 0 or 1, but got: " + val);
			}
			ret = val == 1;
			assertFinish(in);
			break;
		}
		case PRIMITIVE_BYTE: {
			byte val = (byte) in.read();
			ret = val;
			assertFinish(in);
			break;
		}
		case PRIMITIVE_CHAR: {
			char val = (char) in.read();
			val |= in.read() << 8;
			ret = val;
			assertFinish(in);
			break;
		}
		case PRIMITIVE_DOUBLE: {
			long val = (long) in.read();
			val |= in.read() << 8;
			val |= in.read() << 16;
			val |= in.read() << 24;
			val |= in.read() << 32;
			val |= in.read() << 40;
			val |= in.read() << 48;
			val |= in.read() << 56;
			ret = Double.longBitsToDouble(val);
			assertFinish(in);
			break;
		}
		case PRIMITIVE_FLOAT: {
			int val = in.read();
			val |= in.read() << 8;
			val |= in.read() << 16;
			val |= in.read() << 24;
			ret = Float.intBitsToFloat(val);
			assertFinish(in);
			break;
		}
		case PRIMITIVE_INT: {
			int val = in.read();
			val |= in.read() << 8;
			val |= in.read() << 16;
			val |= in.read() << 24;
			ret = val;
			assertFinish(in);
			break;
		}
		case PRIMITIVE_LONG: {
			long val = (long) in.read();
			val |= in.read() << 8;
			val |= in.read() << 16;
			val |= in.read() << 24;
			val |= in.read() << 32;
			val |= in.read() << 40;
			val |= in.read() << 48;
			val |= in.read() << 56;
			ret = val;
			assertFinish(in);
			break;
		}
		case PRIMITIVE_SHORT: {
			short val = (short) in.read();
			val |= in.read() << 8;
			ret = val;
			assertFinish(in);
			break;
		}
		case BOOLEAN_ARRAY: {
			int len = readInt(in);
			boolean[] val = new boolean[len];
			for (int i = 0, ii = 0, b = in.read(); i < val.length; i ++ , ii ++ ) {
				if (ii >= Byte.SIZE) {
					b = in.read();
					ii = 0;
				}
				val[i] = (b & (1 << ii)) != 0;
			}
			ret = val;
			assertFinish(in);
			break;
		}
		case BYTE_ARRAY: {
			int len = readInt(in);
			byte[] val = new byte[len];
			in.read(val);
			ret = val;
			assertFinish(in);
			break;
		}
		case CHAR_ARRAY: {
			int len = readInt(in);
			char[] val = new char[len];
			byte[] bytes = new byte[2];
			for (int i = 0; i < val.length; i ++ ) {
				in.read(bytes);
				val[i] = (char) bytes[0];
				val[i] |= bytes[0] << 8;
			}
			ret = val;
			assertFinish(in);
			break;
		}
		case DOUBLE_ARRAY: {
			int len = readInt(in);
			double[] val = new double[len];
			byte[] bytes = new byte[8];
			for (int i = 0; i < val.length; i ++ ) {
				in.read(bytes);
				long zw = bytes[0];
				zw |= bytes[1] << 8;
				zw |= bytes[2] << 16;
				zw |= bytes[3] << 24;
				zw |= bytes[4] << 32;
				zw |= bytes[5] << 40;
				zw |= bytes[6] << 48;
				zw |= bytes[7] << 56;
				val[i] = Double.longBitsToDouble(zw);
			}
			ret = val;
			assertFinish(in);
			break;
		}
		case FLOAT_ARRAY: {
			int len = readInt(in);
			float[] val = new float[len];
			byte[] bytes = new byte[4];
			for (int i = 0; i < val.length; i ++ ) {
				in.read(bytes);
				int zw = bytes[0];
				zw |= bytes[1] << 8;
				zw |= bytes[2] << 16;
				zw |= bytes[3] << 24;
				val[i] = Float.intBitsToFloat(zw);
			}
			ret = val;
			assertFinish(in);
			break;
		}
		case INT_ARRAY: {
			int len = readInt(in);
			int[] val = new int[len];
			byte[] bytes = new byte[4];
			for (int i = 0; i < val.length; i ++ ) {
				in.read(bytes);
				val[i] = bytes[0];
				val[i] |= bytes[1] << 8;
				val[i] |= bytes[2] << 16;
				val[i] |= bytes[3] << 24;
			}
			ret = val;
			assertFinish(in);
			break;
		}
		case LONG_ARRAY: {
			int len = readInt(in);
			long[] val = new long[len];
			byte[] bytes = new byte[8];
			for (int i = 0; i < val.length; i ++ ) {
				in.read(bytes);
				val[i] = bytes[8];
				val[i] |= bytes[1] << 8;
				val[i] |= bytes[2] << 16;
				val[i] |= bytes[3] << 24;
				val[i] |= bytes[4] << 32;
				val[i] |= bytes[5] << 40;
				val[i] |= bytes[6] << 48;
				val[i] |= bytes[7] << 56;
			}
			ret = val;
			assertFinish(in);
			break;
		}
		case SHORT_ARRAY: {
			int len = readInt(in);
			short[] val = new short[len];
			byte[] bytes = new byte[2];
			for (int i = 0; i < val.length; i ++ ) {
				in.read(bytes);
				val[i] = bytes[0];
				val[i] |= bytes[1] << 8;
			}
			ret = val;
			assertFinish(in);
			break;
		}
		case OBJECT_ARRAY: {
			int len = readInt(in);
			String clsName = readString(in);
			Class <?> cls = Class.forName(clsName);
			Object val = Array.newInstance(cls, len);
			for (int i = 0; i < len; i ++ ) {
				Object zw = readValue(in, opts);
				Array.set(val, i, zw);
			}
			ret = val;
			assertFinish(in);
			break;
		}
		case OBJECT_STRING: {
			String val = readString(in);
			ret = val;
			assertFinish(in);
			break;
		}
		case OBJECT_UNKNOWN: {
			String clsName = readString(in);
			Class <?> cls = Class.forName(clsName);
			Object val = cls.getConstructor().newInstance();
			load(val, val.getClass(), in);
			ret = val;
			assertFinish(in);
			break;
		}
		case OBJECT_COSTUM: {
			// writeString(out, val.getClass().getName());
			String clsName = readString(in);
			Class <?> cls = Class.forName(clsName);
			Options opt = opts.get(cls);
			// out.write(intToByteArr(options.save.size()));
			// for (Field save : options.save) {
			// save(val, save, out, saveStatic);
			// }
			// out.write(OBJECT_COSTUM_FINISH_STEP_A);
			if (opt == null || opt.create == null) {
				ret = cls.getConstructor().newInstance();
			} else {
				ret = opt.create.invoke(null);
			}
			int len = readInt(in);
			if (opt == null || opt.save.size() != len) {
				System.err.println("WARN: costum class with diffrent costumasation than on saving: "
						+ ( (opt == null) ? "no options" : ("I would save " + opt.save.size() + " fields, but there has been saved " + len + " fields")));
			}
			for (int i = 0; i < len; i ++ ) {
				String fn = readString(in);
				Field f = cls.getField(fn);
				if (opt != null && !opt.save.contains(f)) {
					System.err.println("WARN: costum class wih diffrent a saved field which is not in my save fields: '" + f + "' my save fields: '" + opt.save + "'");
				}
				Object zw = readValue(in, opts);
				f.set(ret, zw);
			}
			int zw = in.read();
			if (zw != OBJECT_COSTUM_FINISH_STEP_A) throw new AssertionError("expected to read OBJECT_COSTUM_FINISH_STEP_A (" + OBJECT_COSTUM_FINISH_STEP_A + "), but got " + zw);
			zw = in.read();
			if (zw == OBJECT_COSTUM_NO_STEP_B) {
				assertFinish(in);
			} else if (zw == OBJECT_COSTUM_START_STEP_B) {
				if (opt == null) {
					System.err.println("ERROR: unknown costum class with a saved Part_B! Part_B requires to have a costum class with saveLoadWithGetSetOfIndexAndGetSize settings!");
					throw new InternalError("I can not load Part_B, because I do not have a costumasation for this class and so I do not know wich methods I should call!");
				}
				if (opt.get == null) {
					System.err
							.println("ERROR: costum class with a saved Part_B, but my costum class does not have a Part_B! Part_B requires to have a costum class with saveLoadWithGetSetOfIndexAndGetSize settings!");
					throw new InternalError(
							"I can not load Part_B, because I do not have a costumasation with saveLoadWithGetSetOfIndexAndGetSize settings for this class and so I do not know wich methods I should call!");
				}
				// Method met = options.size;
				// Object[] args = new Object[0];
				// try {
				// int size = (int) met.invoke(val, args);
				// out.write(intToByteArr(size));
				// args = new Object[1];
				// met = options.get;
				// for (int i = 0; i < size; i ++ ) {
				// args[0] = i;
				// Object zw = met.invoke(out, args);
				// writeValue(zw, out, opts, saveStatic);
				// }
				len = readInt(in);
				if (opt.setSize != null) {// setSize is optional
					if (opt.setSize.getDeclaringClass().isAssignableFrom(cls)) {
						opt.setSize.invoke(ret, len);
					} else {
						opt.setSize.invoke(null, ret, len);
					}
				}
				for (int i = 0; i < len; i ++ ) {
					Object rv = readValue(in, opts);
					if (opt.set.getDeclaringClass().isAssignableFrom(cls)) {
						opt.set.invoke(ret, i, rv);
					} else {
						opt.set.invoke(null, ret, i, rv);
					}
				}
			} else {
				throw new AssertionError("expected to read OBJECT_COSTUM_START_STEP_B (" + OBJECT_COSTUM_START_STEP_B + ") or OBJECT_COSTUM_NO_STEP_B (" + OBJECT_COSTUM_NO_STEP_B + "), but got " + zw);
			}
			break;
		}
		default:
			throw new AssertionError("unknown identifier: " + identy);
		}
		return ret;
	}
	
	private static void assertFinish(InputStream in) throws IOException, AssertionError {
		int read = in.read();
		if (FINISH_VALUE != read) throw new AssertionError("expected to read magix FINISH_VALUE (" + FINISH_VALUE + "), but got " + read);
	}
	
	public static void save(Object obj, File saveFile, boolean saveStatic) throws IOException {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile))) {
			Field[] fields = obj.getClass().getDeclaredFields();
			Field.setAccessible(fields, true);
			for (int i = 0; i < fields.length; i ++ ) {
				int mod = fields[i].getModifiers();
				if ( (mod & Modifier.TRANSIENT) != 0) continue;// transient say do not serialize this
				if ( (mod & Modifier.FINAL) != 0) continue;// can't set finals by loading anyway
				if ( !saveStatic && (mod & Modifier.STATIC) != 0) continue;// can't set finals by loading anyway
				save(obj, fields[i], out, saveStatic);
			}
		}
	}
	
	public static void save(Object obj, OutputStream out, boolean saveStatic) throws IOException {
		Field[] fields = obj.getClass().getDeclaredFields();
		Field.setAccessible(fields, true);
		for (int i = 0; i < fields.length; i ++ ) {
			int mod = fields[i].getModifiers();
			if ( (mod & Modifier.TRANSIENT) != 0) continue;// transient say do not serialize this
			if ( (mod & Modifier.FINAL) != 0) continue;// can't set finals by loading anyway
			if ( !saveStatic && (mod & Modifier.STATIC) != 0) continue;// can't set finals by loading anyway
			save(obj, fields[i], out, saveStatic);
		}
		out.write(FINISH_ALL);
	}
	
	private static void save(Object obj, Field field, OutputStream out, boolean saveStatic) throws IOException {
		{// write name
			String name = field.getName();
			writeString(out, name);
		}
		{// write value
			try {
				Object val = field.get(obj);
				Map <Class <?>, Options> opts = Options.create(field.getAnnotation(SerializerOptions.class));
				writeValue(val, out, opts, saveStatic);
			} catch (IllegalArgumentException e) {
				throw new InternalError("i do not know anything about this: " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new InternalError("can not acces on my own fields: " + e.getMessage(), e);
			}
		}
	}
	
	public static void main(String[] args) throws Throwable {
		ParserGUI p = new ParserGUI();
		Class <? extends ParserGUI> cls = p.getClass();
		Field field = cls.getDeclaredField("replaces");
		SerializerOptions so = field.getAnnotation(SerializerOptions.class);
		Options.create(so).forEach((c, o) -> {
			System.out.println(c);
			System.out.println("   save:    " + o.save);
			System.out.println("   get:     " + o.get);
			System.out.println("   set:     " + o.set);
			System.out.println("   size:    " + o.size);
			System.out.println("   setSize: " + o.setSize);
		});
	}
	
	private static class Options {
		
		private Options() {
		}
		
		private Set <Field> save = new HashSet <>();
		
		private Method size    = null;
		private Method setSize = null;
		private Method get     = null;
		private Method set     = null;
		private Method create  = null;
		
		public static Map <Class <?>, Options> create(SerializerOptions serialOpts) {
			if (serialOpts == null) return Collections.emptyMap();
			Map <Class <?>, Options> opts = new HashMap <Class <?>, Serializer.Options>();
			String[] strs = serialOpts.saveFieldNames();
			Class <?> cls = null;
			Options opt = null;
			for (int i = 0; strs.length > 0; i ++ ) {
				if (cls == null) {
					try {
						cls = Class.forName(strs[i]);
						if (opts.containsKey(cls)) throw new InternalError("class already defined!");
						opt = new Options();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						throw new NoClassDefFoundError("could not find class '" + strs[i] + "' m: " + e.getMessage() + " lm: " + e.getLocalizedMessage());
					}
				} else if (strs[i].isEmpty()) {
					opts.put(cls, opt);
					cls = null;
					if (strs.length == i + 1) break;
				} else {
					try {
						Field f = cls.getDeclaredField(strs[i]);
						opt.save.add(f);
					} catch (NoSuchFieldException | SecurityException e) {
						e.printStackTrace();
						throw new InternalError("could not get field '" + strs[i] + "' msg: " + e.getMessage(), e);
					}
				}
			}
			strs = serialOpts.saveLoadWithGetSetOfIndexAndGetSize();
			for (int i = 0; strs.length > 0; i ++ ) {
				try {
					cls = Class.forName(strs[i]);
					if ( !opts.containsKey(cls)) {// opts could got this class key already from saveFieldNames
						opts.put(cls, new Options());
					}
					opt = opts.get(cls);
					if (opt.get != null) throw new AssertionError("opt.get != null opt.get=" + opt.get);
					if (opt.set != null) throw new AssertionError("opt.set != null opt.set=" + opt.set);
					if (opt.size != null) throw new AssertionError("opt.size != null opt.size=" + opt.size);
					if (opt.setSize != null) throw new AssertionError("opt.setSize != null opt.setSize=" + opt.setSize);
					i ++ ;
					if ( !strs[i].isEmpty()) throw new AssertionError("there was no empty string, to end the part: " + strs[i]);
					i ++ ;
					// the first part of a selection contains a method to get a int-size.
					Method met;
					if (strs[i + 1].isEmpty()) {
						met = cls.getDeclaredMethod(strs[i]);
					} else {
						Class <?> zw = cls;// to generate the right message if an error occurs by calling getMethod
						cls = Class.forName(strs[i]);
						i ++ ;
						met = cls.getDeclaredMethod(strs[i], zw);
						cls = zw;// rewrite cls
					}
					opt.size = met;
					i ++ ;
					if ( !strs[i].isEmpty()) throw new AssertionError("there was no empty string, to end the part: " + strs[i]);
					i ++ ;
					// the second part of a selection contains a method to get a element of a zero based index.
					if (strs[i + 1].isEmpty()) {
						met = cls.getDeclaredMethod(strs[i], Integer.TYPE);
					} else {
						Class <?> zw = cls;// to generate the right message if an error occurs by calling getMethod
						cls = Class.forName(strs[i]);
						i ++ ;
						met = cls.getDeclaredMethod(strs[i], zw, Integer.TYPE);
						cls = zw;// rewrite cls
					}
					opt.get = met;
					i ++ ;
					if ( !strs[i].isEmpty()) throw new AssertionError("there was no empty string, to end the part: " + strs[i]);
					i ++ ;
					
					// the third part of a selection contains a method to set a element of a zero based int and the java.lang.Object-element to overwrite as params (first the index and then the
					if (strs[i + 1].isEmpty()) {
						met = cls.getDeclaredMethod(strs[i], Integer.TYPE, Object.class);
					} else {
						Class <?> zw = cls;// to generate the right message if an error occurs by calling getMethod
						cls = Class.forName(strs[i]);
						i ++ ;
						met = cls.getDeclaredMethod(strs[i], zw, Integer.TYPE, Object.class);
						cls = zw;// rewrite cls
					}
					opt.set = met;
					i ++ ;
					if ( !strs[i].isEmpty()) throw new AssertionError("there was no empty string, to end the part: " + strs[i]);
					i ++ ;
					// element).
					// the fourth part of a selection is optional and contains a method to set the size by a int-number.
					if (strs[i].isEmpty()) {// optional
						met = null;
						i -= 2;// to read the empty string again
					} else if (strs[i + 1].isEmpty()) {
						met = cls.getDeclaredMethod(strs[i], Integer.TYPE);
					} else {
						Class <?> zw = cls;// to generate the right message if an error occurs by calling getMethod
						cls = Class.forName(strs[i]);
						i ++ ;
						met = cls.getDeclaredMethod(strs[i], zw, Integer.TYPE);
						cls = zw;// rewrite cls
					}
					opt.setSize = met;
					i ++ ;
					if ( !strs[i].isEmpty()) throw new AssertionError("there was no empty string, to end the part: " + strs[i]);
					i ++ ;
					if ( !strs[i].isEmpty()) throw new AssertionError("there was no empty string, to end the part: " + strs[i]);
					i ++ ;
					if (i == strs.length) break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new NoClassDefFoundError("could not find class '" + strs[i] + "' m: " + e.getMessage() + " lm: " + e.getLocalizedMessage());
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					throw new InternalError("could not find field '" + strs[i] + "' in class '" + cls + "' m: '" + e.getMessage() + "'", e);
				}
			}
			strs = serialOpts.creator();
			if (strs.length > 0) {
				for (int i = 0; true; i ++ ) {
					try {
						cls = Class.forName(strs[i]);
						if ( !opts.containsKey(cls)) {
							opts.put(cls, new Options());
						}
						opt = opts.get(cls);
						i ++ ;
						if (strs[i + 1].isEmpty()) {
							opt.create = cls.getDeclaredMethod(strs[i]);
							i += 2;// i+1 is empty
						} else if (strs[i + 2].isEmpty()) {
							Class <?> zw = cls; // for the right error message if something goes wrong
							cls = Class.forName(strs[i]);
							i ++ ;
							opt.create = cls.getDeclaredMethod(strs[i]);
							cls = zw;
							i += 2;// i + 1 is empty (already incremented i, so it isn't i+2 which is empty)
						} else {
							throw new AssertionError("corrupt SerializerOptions: expected to have a String empty string at item: " + i + " + (1 or 2) in create: '" + Arrays.deepToString(strs) + "'");
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						throw new NoClassDefFoundError("could not find class '" + strs[i] + "' m: " + e.getMessage() + " lm: " + e.getLocalizedMessage());
					} catch (ArrayIndexOutOfBoundsException error) {
						error.printStackTrace();
						throw new InternalError("corrupt SerializerOptions: expected to have a String at item: " + i + " in create: '" + Arrays.deepToString(strs) + "' m: '" + error.getMessage() + "'", error);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
						throw new InternalError("could not find method '" + strs[i] + "' in class '" + cls + "' m: " + e.getMessage() + " lm: " + e.getLocalizedMessage(), e);
					} catch (SecurityException e) {
						e.printStackTrace();
						throw new InternalError(
								"the security manager denied the permission to load the method '" + strs[i] + "' from the class '" + cls + "' m: " + e.getMessage() + " lm: " + e.getLocalizedMessage(), e);
					}
				}
			}
			return opts;
		}
		
	}
	
	private static void writeValue(Object val, OutputStream out, Map <Class <?>, Options> opts, boolean saveStatic) throws InternalError, IOException {
		if (val == null) {
			out.write(NULL);
		} else {// write value
			Class <?> type = val.getClass();
			if (type.isPrimitive()) {
				if (type == Boolean.TYPE) {
					out.write(PRIMITIVE_BOOLEAN);
					boolean bool = (boolean) (Boolean) val;
					out.write(bool ? 1 : 0);
				} else if (type == Integer.TYPE) {
					out.write(PRIMITIVE_INT);
					out.write(intToByteArr((int) (Integer) val));
				} else if (type == Long.TYPE) {
					out.write(PRIMITIVE_LONG);
					byte[] bytes = new byte[Long.BYTES];
					intToByteArr(bytes, 0, (int) ((long) (Long) val) << 0);
					intToByteArr(bytes, Integer.BYTES, (int) ((long) (Long) val) << 32);
					out.write(bytes);
				} else if (type == Byte.TYPE) {
					out.write(PRIMITIVE_BYTE);
					out.write( ((int) (byte) (Byte) val) & 0xFF);
				} else if (type == Short.TYPE) {
					out.write(PRIMITIVE_SHORT);
					short s = (short) (Short) val;
					byte[] bytes = new byte[2];
					bytes[0] = (byte) (s & 0xFF);
					bytes[1] = (byte) ( (s & 0xFF) << 8);
					out.write(bytes);
				} else if (type == Character.TYPE) {
					out.write(PRIMITIVE_CHAR);
					char c = (char) (Character) val;
					// char has two bytes
					byte[] bytes = new byte[Character.BYTES];
					bytes[0] = (byte) c;
					bytes[0] = (byte) (c << 8);
					out.write(bytes);
				} else if (type == Double.TYPE) {
					out.write(PRIMITIVE_DOUBLE);
					long lval = Double.doubleToRawLongBits((double) (Double) val);
					byte[] bytes = new byte[Long.BYTES];
					intToByteArr(bytes, 0, (int) lval);
					intToByteArr(bytes, Integer.BYTES, (int) (lval << 32));
					out.write(bytes);
				} else if (type == Float.TYPE) {
					out.write(PRIMITIVE_FLOAT);
					int ival = Float.floatToRawIntBits((float) (Float) val);
					byte[] bytes = new byte[Integer.BYTES];
					intToByteArr(bytes, 0, ival);
					out.write(bytes);
				} else {
					throw new InternalError("unknown primitive type: " + type.getName() + " with value: " + val);
				}
			} else {
				if (type == String.class) {
					out.write(OBJECT_STRING);
					String str = (String) val;
					writeString(out, str);
				} else if (type.isArray()) {
					if (type == int[].class) {
					} else if (type == byte[].class) {
						out.write(BYTE_ARRAY);
						byte[] zw = (byte[]) val;
						byte[] bytes = new byte[zw.length + Integer.BYTES];
						System.arraycopy(zw, 0, bytes, Integer.BYTES, zw.length);
						intToByteArr(bytes, 0, zw.length);
						out.write(bytes);
					} else if (type == int[].class) {
						out.write(INT_ARRAY);
						int[] iaval = (int[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, iaval.length);
						out.write(bytes);
						for (int i = 0; i < iaval.length; i ++ ) {
							bytes = new byte[Integer.BYTES];
							intToByteArr(bytes, 0, iaval[i]);
							out.write(bytes);
						}
					} else if (type == long[].class) {
						out.write(LONG_ARRAY);
						long[] laval = (long[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, laval.length);
						out.write(bytes);
						for (int i = 0; i < laval.length; i ++ ) {
							bytes = new byte[Integer.BYTES];
							intToByteArr(bytes, 0, (int) laval[i]);
							intToByteArr(bytes, Integer.BYTES, (int) (laval[i] << 32));
							out.write(bytes);
						}
					} else if (type == short[].class) {
						out.write(SHORT_ARRAY);
						short[] saval = (short[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, saval.length);
						out.write(bytes);
						for (int i = 0; i < saval.length; i ++ ) {
							bytes = new byte[Short.BYTES];
							bytes[0] = (byte) saval[0];
							bytes[0] = (byte) (saval[1] << 8);
						}
					} else if (type == double[].class) {
						out.write(DOUBLE_ARRAY);
						double[] daval = (double[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, daval.length);
						out.write(bytes);
						for (int i = 0; i < daval.length; i ++ ) {
							bytes = new byte[Integer.BYTES];
							long zwl = Double.doubleToRawLongBits(daval[i]);
							intToByteArr(bytes, 0, (int) zwl);
							intToByteArr(bytes, Integer.BYTES, (int) (zwl << 32));
							out.write(bytes);
						}
					} else if (type == float[].class) {
						out.write(FLOAT_ARRAY);
						float[] iaval = (float[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, iaval.length);
						out.write(bytes);
						for (int i = 0; i < iaval.length; i ++ ) {
							bytes = new byte[Integer.BYTES];
							intToByteArr(bytes, 0, Float.floatToRawIntBits(iaval[i]));
							out.write(bytes);
						}
					} else if (type == boolean[].class) {
						out.write(BOOLEAN_ARRAY);
						boolean[] baval = (boolean[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, baval.length);
						out.write(bytes);
						bytes = new byte[ (baval.length >> 3) + ( ( (baval.length & 0x7) == 0) ? 0 : 1)];
						int i, ii;
						for (i = 0, ii = 0; i < (baval.length >> 3); i ++ ) {
							bytes[i] = (byte) (baval[ii ++ ] ? 0x01 : 0);
							bytes[i] |= (byte) (baval[ii ++ ] ? 0x02 : 0);
							bytes[i] |= (byte) (baval[ii ++ ] ? 0x04 : 0);
							bytes[i] |= (byte) (baval[ii ++ ] ? 0x08 : 0);
							bytes[i] |= (byte) (baval[ii ++ ] ? 0x10 : 0);
							bytes[i] |= (byte) (baval[ii ++ ] ? 0x20 : 0);
							bytes[i] |= (byte) (baval[ii ++ ] ? 0x40 : 0);
							bytes[i] |= (byte) (baval[ii ++ ] ? 0x80 : 0);
						}
						if (ii < baval.length) {
							bytes[i] = (byte) (baval[ii ++ ] ? 0x01 : 0);
							if (ii < baval.length) {
								bytes[i] |= (byte) (baval[ii ++ ] ? 0x02 : 0);
								if (ii < baval.length) {
									bytes[i] |= (byte) (baval[ii ++ ] ? 0x04 : 0);
									if (ii < baval.length) {
										bytes[i] |= (byte) (baval[ii ++ ] ? 0x08 : 0);
										if (ii < baval.length) {
											bytes[i] |= (byte) (baval[ii ++ ] ? 0x10 : 0);
											if (ii < baval.length) {
												bytes[i] |= (byte) (baval[ii ++ ] ? 0x20 : 0);
												if (ii < baval.length) {
													bytes[i] |= (byte) (baval[ii ++ ] ? 0x40 : 0);
													if (ii < baval.length) {
														bytes[i] |= (byte) (baval[ii] ? 0x80 : 0);
													}
												}
											}
										}
									}
								}
							}
						}
					} else if (type == char[].class) {
						out.write(CHAR_ARRAY);
						char[] saval = (char[]) val;
						byte[] bytes = new byte[Integer.BYTES];
						intToByteArr(bytes, 0, saval.length);
						out.write(bytes);
						for (int i = 0; i < saval.length; i ++ ) {
							bytes = new byte[Character.BYTES];
							bytes[0] = (byte) saval[0];
							bytes[0] = (byte) (saval[1] << 8);
						}
					} else {
						out.write(OBJECT_ARRAY);
						Object[] objs = (Object[]) val;
						writeString(out, objs.getClass().getName());// first write name (because the value class can be a sub class of the type)
						out.write(intToByteArr(objs.length));
						for (int i = 0; i < objs.length; i ++ ) {
							writeValue(objs[i], out, opts, saveStatic);
						}
					}
				} else {
					if (opts != null && opts.containsKey(type)) {
						out.write(OBJECT_COSTUM);
						writeString(out, type.getName());
						Options options = opts.get(type);
						out.write(intToByteArr(options.save.size()));
						for (Field save : options.save) {
							save(val, save, out, saveStatic);
						}
						out.write(OBJECT_COSTUM_FINISH_STEP_A);
						if (options.size != null) {
							out.write(OBJECT_COSTUM_START_STEP_B);
							Method met = options.size;
							Object[] args = new Object[met.getParameterCount()];
							if (args.length > 0) {
								args[0] = val;
							}
							try {
								int size = (int) met.invoke(val, args);
								out.write(intToByteArr(size));
								met = options.get;
								args = new Object[met.getParameterCount()];
								if (args.length > 1) {
									args[0] = val;
								}
								for (int i = 0; i < size; i ++ ) {
									args[args.length - 1] = i;
									Object zw = met.invoke(out, args);
									writeValue(zw, out, opts, saveStatic);
								}
							} catch (IllegalAccessException e) {
								e.printStackTrace();
								throw new AssertionError("i do not have the permission to call the method: '" + met + "' obj.cls: '" + val.getClass() + "' obj: '" + val + "' msg: '" + e.getMessage() + "'", e);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
								throw new InternalError("that can't be happening to me! illegal args for methed: '" + met + "' args: '" + Arrays.deepToString(args) + "' msg: '" + e.getMessage() + "'", e);
							} catch (InvocationTargetException e) {
								e.printStackTrace();
								System.err.println();
								e.getCause().printStackTrace();
								throw new AssertionError("there was an error inside of the methodcall: '" + met + "'", e.getCause());
							}
						} else {
							out.write(OBJECT_COSTUM_NO_STEP_B);
						}
					} else {
						out.write(OBJECT_UNKNOWN);
						writeString(out, val.getClass().getName());
						save(val, out, saveStatic);
					}
				}
			}
		}
		out.write(FINISH_VALUE);
	}
	
	private static String readString(InputStream in) throws IOException {
		// byte[] zw = write.getBytes(StandardCharsets.UTF_8);
		// byte[] bytes = new byte[zw.length + Integer.BYTES];
		// intToByteArr(bytes, 0, bytes.length);
		// System.arraycopy(zw, 0, bytes, Integer.BYTES, zw.length);
		// out.write(bytes);
		byte[] bytes = new byte[Integer.BYTES];
		in.read(bytes);
		int len = byteArrToInt(bytes, 0);
		bytes = new byte[len];
		in.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	private static void writeString(OutputStream out, String write) throws IOException {
		byte[] zw = write.getBytes(StandardCharsets.UTF_8);
		byte[] bytes = new byte[zw.length + Integer.BYTES];
		intToByteArr(bytes, 0, bytes.length);
		System.arraycopy(zw, 0, bytes, Integer.BYTES, zw.length);
		out.write(bytes);
	}
	
	
	private static int readInt(InputStream in) throws IOException {
		byte[] bytes = new byte[Integer.BYTES];
		in.read(bytes);
		return byteArrToInt(bytes, 0);
	}
	
	private static int byteArrToInt(byte[] bytes, int off) {
		int ret = 0;
		for (int i = 0; i < Integer.BYTES; i ++ ) {
			ret |= ( ((int) bytes[off + i]) & 0xFF) << (i << 3);
		}
		return ret;
	}
	
	private static void intToByteArr(byte[] bytes, int off, int val) {
		for (int i = 0; i < Integer.BYTES; i ++ ) {
			bytes[i + off] = (byte) (val >> (i << 3));
		}
	}
	
	private static byte[] intToByteArr(int val) {
		byte[] bytes = new byte[Integer.BYTES];
		intToByteArr(bytes, 0, val);
		return bytes;
	}
	
}
