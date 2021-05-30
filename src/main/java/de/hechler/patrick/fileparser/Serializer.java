package de.hechler.patrick.fileparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Serializer {
	
	private static final int FINISH_VALUE = 2;
	private static final int NON_NULL     = 1;
	private static final int NULL         = 0;
	
	public static void writeValue(OutputStream out, Object value, boolean saveStatic, boolean saveNonStatic, boolean saveSuperClasses) throws IOException {
		if (value == null) {
			out.write(NULL);
		} else {
			out.write(NON_NULL);
			final Class <? extends Object> origCls = value.getClass();
			Class <? extends Object> cls = origCls;
			writeString(out, cls.getName());
			while (cls != null) {
				Field[] fields = cls.getDeclaredFields();
				writeInt(out, fields.length);
				for (int i = 0; i < fields.length; i ++ ) {
					int mod = fields[i].getModifiers();
					if ( !saveStatic && (mod & Modifier.STATIC) != 0) continue;
					if ( !saveNonStatic && (mod & Modifier.STATIC) == 0) continue;
					writeString(out, fields[i].getName());
					try {
						boolean wasAccessible = fields[i].isAccessible();
						fields[i].setAccessible(true);
						writeValue(out, fields[i].get(value), saveStatic, saveNonStatic, saveSuperClasses);
						fields[i].setAccessible(wasAccessible);
					} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
						throw new InternalError(e);
					}
				}
				cls = cls.getSuperclass();
			}
			out.write(FINISH_VALUE);
		}
	}
	
	public static void readValue(InputStream in, Field save) throws IOException {
		int isNull = in.read();
		try {
			if (isNull == NULL) {
				save.set(null, null);
			} else if (isNull == NON_NULL) {
				// writeString(out, cls.getName());
				String clsName = readString(in);
				final Class <?> origCls = Class.forName(clsName);
				Class <?> cls = origCls;
				while (cls != null) {
					// while (cls != null) {
					// Field[] fields = cls.getDeclaredFields();
					// writeInt(out, fields.length);
					int cnt = readInt(in);
					// for (int i = 0; i < fields.length; i ++ ) {
					// int mod = fields[i].getModifiers();
					// if ( !saveStatic && (mod & Modifier.STATIC) != 0) continue;
					// if ( !saveNonStatic && (mod & Modifier.STATIC) == 0) continue;
					for (int i = 0; i < cnt; i ++ ) {
						// writeString(out, fields[i].getName());
						Field f = cls.getField(readString(in));
						// try {
						// boolean wasAccessible = fields[i].isAccessible();
						boolean wa = f.isAccessible();
						f.setAccessible(true);
						readValue(in, f);//TODO save
						// fields[i].setAccessible(true);
						// writeValue(out, fields[i].get(value), saveStatic, saveNonStatic, saveSuperClasses);
						f.setAccessible(wa);
						// fields[i].setAccessible(wasAccessible);
						// } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
						// throw new InternalError(e);
						// }
					}
					// }
					// cls = cls.getSuperclass();
					cls = cls.getSuperclass();
				}
			} else {
				throw new InternalError("illegal read=" + isNull);
			}
		} catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException | SecurityException e) {
			throw new InternalError(e);
		}
	}
	
	private static String readString(InputStream in) throws IOException {
		int len = readInt(in);
		byte[] bytes = new byte[len];
		in.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	private static void writeString(OutputStream out, String write) throws IOException {
		byte[] zw = write.getBytes(StandardCharsets.UTF_8);
		byte[] bytes = new byte[zw.length + 8];
		intToBytes(bytes, 0, zw.length);
		System.arraycopy(zw, 0, bytes, 4, zw.length);
		out.write(bytes);
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
	
	private static void writeInt(OutputStream out, int val) throws IOException {
		byte[] b = new byte[4];
		intToBytes(b, 0, val);
		out.write(b);
	}
	
	private static void intToBytes(byte[] bytes, int off, int val) {
		bytes[off] = (byte) val;
		bytes[off + 1] = (byte) (val >> 8);
		bytes[off + 2] = (byte) (val >> 16);
		bytes[off + 4] = (byte) (val >> 24);
	}
	
	
	
	@SuppressWarnings("all")
	private static class QUATSCH {
		
		
		
		private static final Field modifiersField = getModifiersField();
		
		private static Field getModifiersField() {
			try {
				Field modField = Field.class.getDeclaredField("modifiers");
				modField.setAccessible(true);
				return modField;
			} catch (NoSuchFieldException | SecurityException e) {
				throw new InternalError(e);
			}
		}
		
		public static void main(String[] args) throws Throwable {
			System sys;
			Constructor <System> sysCreator = System.class.getDeclaredConstructor();
			sysCreator.setAccessible(true);
			sys = sysCreator.newInstance();
			Field out = System.class.getField("out");
			out.setAccessible(true);
			modifiersField.setInt(Serializer.class.getDeclaredField("NULL"), Serializer.class.getDeclaredField("NULL").getModifiers() & ~Modifier.FINAL);
			modifiersField.setInt(out, out.getModifiers() & ~Modifier.FINAL);
			Object origOut = System.out;
			out.set(null, System.err);
			System.out.println("err");
			modifiersField.setInt(out, out.getModifiers() & ~Modifier.STATIC);
			out.set(sys, origOut);
			sys.out.println("err2\n");
			// modifiersField.setInt(out, out.getModifiers() & ~Modifier.PUBLIC);
			// modifiersField.setInt(out, out.getModifiers() | Modifier.PRIVATE);
			System.out.println("ignore non static");
			try {
				((PrintStream) out.get(null)).print("null\n");
			} catch (NullPointerException npe) {
				((PrintStream) out.get(sys)).print("non static err\n");
			}
			Constructor <Void> vc = Void.class.getDeclaredConstructor();
			vc.setAccessible(true);
			Void v = vc.newInstance();
			sys.out.println(v);
			sys.out.println(obj);
			obj = "hello";
			sys.out.println(obj);
			modifiersField.setInt(Serializer.class.getDeclaredField("obj"), Modifier.FINAL);
			obj = "menno";
			sys.out.println(obj);
			Serializer.class.getDeclaredField("obj").set(null, "set to final or non static does not work");
			sys.out.println(obj);
			sys.out.println(Modifier.toString(sys.getClass().getModifiers()));
			sys.out.println(Arrays.deepToString(Class.class.getDeclaredFields()));
			B.a(sys);
			sys.out.println("second try");
			B.a(sys);
		}
		
		private static class B {
			
			private static void a(System sys) {
				C.s(sys);
				C.s(sys);
			}
			
			private static class C {
				
				private static void s(System sys) {
					for (int i = 0; i < 20; i ++ ) {
						Class <?> caller = sun.reflect.Reflection.getCallerClass(i);
						sys.out.println("deep=" + i + " caller: " + caller);
					}
				}
				
			}
			
		}
		
		private static Object obj = new Object();
		
		private static void setTotalAcces(Field field) throws IllegalArgumentException, IllegalAccessException {
			field.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		}
		
	}
	
}
