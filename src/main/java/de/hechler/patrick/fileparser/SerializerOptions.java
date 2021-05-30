package de.hechler.patrick.fileparser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializerOptions {
	
	/**
	 * returns a String[] containing below specified selections:<br>
	 * the first element of each selection is the full qualifying class name, in which class this selection is active.<br>
	 * after each part of the selection is an {@link String#isEmpty() empty String}.<br>
	 * the first part of a selection contains a method to get a {@code int}-size.<br>
	 * the second part of a selection contains a method to get a element of a zero based index.<br>
	 * the third part of a selection contains a method to set a element of a zero based {@code int} and the {@link java.lang.Object}-element to overwrite as params (first the index and then the
	 * element).<br>
	 * the fourth part of a selection is optional and contains a method to set the size by a {@code int}-number.<br>
	 * the last element of each selection is an {@link String#isEmpty() empty String}, so at the end of a selection are two {@link String#isEmpty() empty Strings}.<br>
	 * <br>
	 * a method is defined as the name of the method or at first the class of the method and then the name of the method, but then it has to be static and to accept also the Object before the defined
	 * params, this Object will be the current loading/saving object.
	 * 
	 * @return a <code>{@link String}[]</code> containing above specified selections
	 */
	String[] saveLoadWithGetSetOfIndexAndGetSize() default {};
	
	/**
	 * returns a String[] containing below specified selections:<br>
	 * the first element of each selection is the full qualifying class name, in which class this selection is active.<br>
	 * the last element of each selection is an empty {@link String}.<br>
	 * between them are the names of all fields which should be saved.
	 * 
	 * @return a String[] containing above specified selections
	 */
	String[] saveFieldNames() default {};
	
	/**
	 * the first {@link String} of the selection is the name of the class where this selection is active.<br>
	 * the last {@link String} of the selection is the name of the method to call instead of an empty constructor (with no params). This method will return the new created instance, is static and accepts no params.<br>
	 * if this selection contains three elements the second element will be the fully qualifying class name of the class where the method is.
	 * to end the selection comes an empty string, which is not part of the selection itself.
	 * 
	 * @return a String[] containing multiple above specified selection
	 */
	String[] creator() default {};
	
	
}
