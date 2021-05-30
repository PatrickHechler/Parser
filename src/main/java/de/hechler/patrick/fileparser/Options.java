package de.hechler.patrick.fileparser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Options {
	
	private Options() {
	}
	
	private Set <Field> save = new HashSet <>();
	
	private Method size    = null;
	private Method setSize = null;
	private Method get     = null;
	private Method set     = null;
	private Method create  = null;
	
	
	public Set <Field> save() {
		return save;
	}
	
	public Method size() {
		return size;
	}
	
	public Method serSize() {
		return setSize;
	}
	
	public Method set() {
		return set;
	}
	
	public Method get() {
		return get;
	}
	
	public Method create() {
		return create;
	}
	
	public static Map <Class <?>, Options> create(SerializerOptions serialOpts) {
		if (serialOpts == null) return Collections.emptyMap();
		Map <Class <?>, Options> opts = new HashMap <>();
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
					throw new InternalError("the security manager denied the permission to load the method '" + strs[i] + "' from the class '" + cls + "' m: " + e.getMessage() + " lm: " + e.getLocalizedMessage(),
							e);
				}
			}
		}
		return opts;
	}
	
}
