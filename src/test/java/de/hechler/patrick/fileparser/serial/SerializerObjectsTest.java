package de.hechler.patrick.fileparser.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.Start;

@CheckClass
public class SerializerObjectsTest extends Checker {
	
	Serializer   serializer;
	Deserializer deserializer;
	
	@Start
	public void setup() {
		serializer = new Serializer(false, true, false, true, true);
		deserializer = new Deserializer(Collections.emptyMap());
	}
	
	
	public <T> T readObject(Class <T> tClazz, InputStream in) throws IOException {
		return readObject(deserializer, tClazz, in);
	}
	
	public static <T> T readObject(Deserializer deser, Class <T> tClazz, InputStream in) throws IOException {
		Object result = deser.readObject(in);
		if (result == null) {
			return null;
		}
		if ( !tClazz.isInstance(result)) {
			fail("Wrong instance read: " + result.getClass().getName() + " instead of " + tClazz.getName());
		}
		return tClazz.cast(result);
	}
	
	
	public static class BooleanObjBean {
		
		public Boolean value1;
		public Boolean value2;
		
		public BooleanObjBean() {
			this(null, null);
		}
		
		public BooleanObjBean(Boolean value1, Boolean value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( ! (obj instanceof BooleanObjBean)) return false;
			BooleanObjBean other = (BooleanObjBean) obj;
			return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
		}
		
	}
	
	public static class ByteObjBean {
		
		public Byte value1;
		public Byte value2;
		
		public ByteObjBean() {
			this(null, null);
		}
		
		public ByteObjBean(Byte value1, Byte value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( ! (obj instanceof ByteObjBean)) return false;
			ByteObjBean other = (ByteObjBean) obj;
			return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
		}
		
	}
	
	public static class CharObjBean {
		
		public Character value1;
		public Character value2;
		
		public CharObjBean() {
			this(null, null);
		}
		
		public CharObjBean(Character value1, Character value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( ! (obj instanceof CharObjBean)) return false;
			CharObjBean other = (CharObjBean) obj;
			return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
		}
		
	}
	
	public static class ShortObjBean {
		
		public Short value1;
		public Short value2;
		
		public ShortObjBean() {
			this(null, null);
		}
		
		public ShortObjBean(Short value1, Short value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( ! (obj instanceof ShortObjBean)) return false;
			ShortObjBean other = (ShortObjBean) obj;
			return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
		}
		
	}
	
	public static class IntObjBean {
		
		public Integer value1;
		public Integer value2;
		
		public IntObjBean() {
			this(null, null);
		}
		
		public IntObjBean(Integer value1, Integer value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( ! (obj instanceof IntObjBean)) return false;
			IntObjBean other = (IntObjBean) obj;
			return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
		}
		
	}
	
	public static class LongObjBean {
		
		public Long value1;
		public Long value2;
		
		public LongObjBean() {
			this(null, null);
		}
		
		public LongObjBean(Long value1, Long value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( ! (obj instanceof LongObjBean)) return false;
			LongObjBean other = (LongObjBean) obj;
			return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
		}
		
	}
	
	public static class FloatObjBean {
		
		public Float value1;
		public Float value2;
		
		public FloatObjBean() {
			this(null, null);
		}
		
		public FloatObjBean(Float value1, Float value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( ! (obj instanceof FloatObjBean)) return false;
			FloatObjBean other = (FloatObjBean) obj;
			return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
		}
		
	}
	
	public static class DoubleObjBean {
		
		public Double value1;
		public Double value2;
		
		public DoubleObjBean() {
			this(null, null);
		}
		
		public DoubleObjBean(Double value1, Double value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		@Override
		public String toString() {
			return "[" + value1 + "," + value2 + "]";
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value1, value2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if ( ! (obj instanceof DoubleObjBean)) return false;
			DoubleObjBean other = (DoubleObjBean) obj;
			return Objects.equals(value1, other.value1) && Objects.equals(value2, other.value2);
		}
		
	}
	
	
	@Check
	public void testSerializeBooleanObj() throws IOException {
		BooleanObjBean nul = new BooleanObjBean(null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul);
		BooleanObjBean readNul = readObject(BooleanObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul, readNul);
		
		BooleanObjBean nul1 = new BooleanObjBean(null, true);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul1);
		BooleanObjBean readNul1 = readObject(BooleanObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul1, readNul1);
		
		BooleanObjBean nul2 = new BooleanObjBean(false, null);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul2);
		BooleanObjBean readNul2 = readObject(BooleanObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul2, readNul2);
		
		BooleanObjBean zero = new BooleanObjBean(false, false);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		BooleanObjBean readZero = readObject(BooleanObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		BooleanObjBean max = new BooleanObjBean(true, true);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		BooleanObjBean readMax = readObject(BooleanObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		BooleanObjBean mix = new BooleanObjBean(true, false);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, mix);
		BooleanObjBean readMix = readObject(BooleanObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(mix, readMix);
		
		BooleanObjBean mix2 = new BooleanObjBean(false, true);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, mix2);
		BooleanObjBean readMix2 = readObject(BooleanObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(mix2, readMix2);
	}
	
	@Check
	public void testSerializeByte() throws IOException {
		ByteObjBean nul = new ByteObjBean(null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul);
		ByteObjBean readNul = readObject(ByteObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul, readNul);
		
		ByteObjBean nul1 = new ByteObjBean(null, (byte) -1);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul1);
		ByteObjBean readNul1 = readObject(ByteObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul1, readNul1);
		
		ByteObjBean nul2 = new ByteObjBean((byte) 0, null);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul2);
		ByteObjBean readNul2 = readObject(ByteObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul2, readNul2);
		
		ByteObjBean zero = new ByteObjBean((byte) 0, (byte) 0);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		ByteObjBean readZero = readObject(ByteObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		ByteObjBean max = new ByteObjBean(Byte.MAX_VALUE, Byte.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		ByteObjBean readMax = readObject(ByteObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		ByteObjBean min = new ByteObjBean(Byte.MIN_VALUE, Byte.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		ByteObjBean readMin = readObject(ByteObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		ByteObjBean seq = new ByteObjBean((byte) 123, (byte) -98);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		ByteObjBean readSeq = readObject(ByteObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeShort() throws IOException {
		ShortObjBean nul = new ShortObjBean(null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul);
		ShortObjBean readNul = readObject(ShortObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul, readNul);
		
		ShortObjBean nul1 = new ShortObjBean(null, (short) -1);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul1);
		ShortObjBean readNul1 = readObject(ShortObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul1, readNul1);
		
		ShortObjBean nul2 = new ShortObjBean((short) 0, null);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul2);
		ShortObjBean readNul2 = readObject(ShortObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul2, readNul2);
		
		ShortObjBean zero = new ShortObjBean((short) 0, (short) 0);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		ShortObjBean readZero = readObject(ShortObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		ShortObjBean max = new ShortObjBean(Short.MAX_VALUE, Short.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		ShortObjBean readMax = readObject(ShortObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		ShortObjBean min = new ShortObjBean(Short.MIN_VALUE, Short.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		ShortObjBean readMin = readObject(ShortObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		ShortObjBean seq = new ShortObjBean((short) 12356, (short) 21098);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		ShortObjBean readSeq = readObject(ShortObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeChar() throws IOException {
		CharObjBean nul = new CharObjBean(null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul);
		CharObjBean readNul = readObject(CharObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul, readNul);
		
		CharObjBean nul1 = new CharObjBean(null, (char) -1);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul1);
		CharObjBean readNul1 = readObject(CharObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul1, readNul1);
		
		CharObjBean nul2 = new CharObjBean((char) 0, null);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul2);
		CharObjBean readNul2 = readObject(CharObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul2, readNul2);
		
		CharObjBean zero = new CharObjBean((char) 0, (char) 0);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		CharObjBean readZero = readObject(CharObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		CharObjBean max = new CharObjBean(Character.MAX_VALUE, Character.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		CharObjBean readMax = readObject(CharObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		CharObjBean min = new CharObjBean(Character.MIN_VALUE, Character.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		CharObjBean readMin = readObject(CharObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		CharObjBean seq = new CharObjBean((char) 12356, (char) 21098);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		CharObjBean readSeq = readObject(CharObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeInt() throws IOException {
		IntObjBean nul = new IntObjBean(null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul);
		IntObjBean readNul = readObject(IntObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul, readNul);
		
		IntObjBean nul1 = new IntObjBean(null, (int) -1);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul1);
		IntObjBean readNul1 = readObject(IntObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul1, readNul1);
		
		IntObjBean nul2 = new IntObjBean((int) 0, null);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul2);
		IntObjBean readNul2 = readObject(IntObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul2, readNul2);
		
		IntObjBean zero = new IntObjBean(0, 0);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		IntObjBean readZero = readObject(IntObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		IntObjBean max = new IntObjBean(Integer.MAX_VALUE, Integer.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		IntObjBean readMax = readObject(IntObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		IntObjBean min = new IntObjBean(Integer.MIN_VALUE, Integer.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		IntObjBean readMin = readObject(IntObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		IntObjBean seq = new IntObjBean(1234567890, 987654321);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		IntObjBean readSeq = readObject(IntObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeLong() throws IOException {
		LongObjBean nul = new LongObjBean(null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul);
		LongObjBean readNul = readObject(LongObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul, readNul);
		
		LongObjBean nul1 = new LongObjBean(null, (long) -1);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul1);
		LongObjBean readNul1 = readObject(LongObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul1, readNul1);
		
		LongObjBean nul2 = new LongObjBean((long) 0, null);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul2);
		LongObjBean readNul2 = readObject(LongObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul2, readNul2);
		
		LongObjBean zero = new LongObjBean(0L, 0L);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		LongObjBean readZero = readObject(LongObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		LongObjBean max = new LongObjBean(Long.MAX_VALUE, Long.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		LongObjBean readMax = readObject(LongObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		LongObjBean min = new LongObjBean(Long.MIN_VALUE, Long.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		LongObjBean readMin = readObject(LongObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		LongObjBean seq = new LongObjBean(1234567890123456789L, 987654321098765432L);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		LongObjBean readSeq = readObject(LongObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	@Check
	public void testSerializeFloat() throws IOException {
		FloatObjBean nul = new FloatObjBean(null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul);
		FloatObjBean readNul = readObject(FloatObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul, readNul);
		
		FloatObjBean nul1 = new FloatObjBean(null, -1.0f);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul1);
		FloatObjBean readNul1 = readObject(FloatObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul1, readNul1);
		
		FloatObjBean nul2 = new FloatObjBean(0.0f, null);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul2);
		FloatObjBean readNul2 = readObject(FloatObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul2, readNul2);
		
		FloatObjBean zero = new FloatObjBean(0.0f, 0.0f);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		FloatObjBean readZero = readObject(FloatObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		FloatObjBean max = new FloatObjBean(Float.MAX_VALUE, Float.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		FloatObjBean readMax = readObject(FloatObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		FloatObjBean min = new FloatObjBean(Float.MIN_VALUE, Float.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		FloatObjBean readMin = readObject(FloatObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		FloatObjBean min_norm = new FloatObjBean(Float.MIN_NORMAL, Float.MIN_NORMAL);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min_norm);
		FloatObjBean readMin_norm = readObject(FloatObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min_norm, readMin_norm);
		
		FloatObjBean seq = new FloatObjBean(123567890.1f, 098765432.1f);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		FloatObjBean readSeq = readObject(FloatObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	
	@Check
	public void testSerializeDouble() throws IOException {
		DoubleObjBean nul = new DoubleObjBean(null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul);
		DoubleObjBean readNul = readObject(DoubleObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul, readNul);
		
		DoubleObjBean nul1 = new DoubleObjBean(null, -1.0);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul1);
		DoubleObjBean readNul1 = readObject(DoubleObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul1, readNul1);
		
		DoubleObjBean nul2 = new DoubleObjBean(0.0, null);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, nul2);
		DoubleObjBean readNul2 = readObject(DoubleObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(nul2, readNul2);
		
		DoubleObjBean zero = new DoubleObjBean(0.0, 0.0);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, zero);
		DoubleObjBean readZero = readObject(DoubleObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(zero, readZero);
		
		DoubleObjBean max = new DoubleObjBean(Double.MAX_VALUE, Double.MAX_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, max);
		DoubleObjBean readMax = readObject(DoubleObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(max, readMax);
		
		DoubleObjBean min = new DoubleObjBean(Double.MIN_VALUE, Double.MIN_VALUE);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min);
		DoubleObjBean readMin = readObject(DoubleObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min, readMin);
		
		DoubleObjBean min_norm = new DoubleObjBean(Double.MIN_NORMAL, Double.MIN_NORMAL);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, min_norm);
		DoubleObjBean readMin_norm = readObject(DoubleObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(min_norm, readMin_norm);
		
		DoubleObjBean seq = new DoubleObjBean(12356789012345678.9, 98765432109876543.2);
		baos = new ByteArrayOutputStream();
		serializer.writeObject(baos, seq);
		DoubleObjBean readSeq = readObject(DoubleObjBean.class, new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(seq, readSeq);
	}
	
	
	public static void main(String[] args) {
		SerializerObjectsTest test = new SerializerObjectsTest();
		CheckResult result = test.result();
		result.print();
		result.forAllUnexpected((m, t) -> {
			System.err.println("method: " + m);
			System.err.println("\terror: " + t);
			t.printStackTrace();
		});
		if ( !result.wentExpected()) {
			fail("There were test failures: " + result.allUnexpected());
		}
	}
	
}
