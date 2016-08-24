package engine.config;

/**
 * A simple property that a config file contains
 * <p>
 * Properties are stored in the file in the format
 * 
 * <pre>
 * internalName = value
 * </pre>
 * 
 * What is done with {@code Property} instances is entirely up to the user. The value fields are stored as
 * simply as they appear in the File, as a String. From there, it is definitely possible to use methods such
 * as {@code Integer.parseInt()} to convert this value.
 * 
 * @author Kevin
 */
public class Property {
	
	/**
	 * The internal name used for storage in the config file
	 */
	public String internalName;
	
	/**
	 * The value of the property
	 */
	private String value;
	
	/**
	 * The default value of the property
	 */
	private String defaultValue;
	
	/**
	 * The {@code Class} that this should be
	 */
	private Class<?> checkClass;
	
	public Property(String internalName, Object value) {
		this(internalName, value.toString());
	}
	
	public Property(String internalName, String value) {
		this(internalName, value, String.class);
	}
	
	public Property(String internalName, Object value, Class<?> checkClass) {
		this(internalName, value.toString(), checkClass);
	}
	
	public Property(String internalName, String value, Class<?> checkClass) {
		this.internalName = internalName;
		this.defaultValue = value;
		this.checkClass = checkClass;
	}
	
	public boolean hasValue() {
		return this.value != null;
	}
	
	public String getValue() {
		return (this.value == null ? this.defaultValue : this.value);
	}
	
	/**
	 * Checks whether the value contained in this {@code Property} is valid
	 * <p>
	 * Currently supported are Integers, Doubles, and Booleans
	 * 
	 * @return True if valid, False if not
	 */
	public boolean checkValue() {
		if (checkClass.equals(Integer.class) || checkClass.equals(int.class)) {
			try {
				Integer.parseInt(this.value);
			} catch (Exception e) {
				return false;
			}
			return true;
		} else if (checkClass.equals(Double.class) || checkClass.equals(double.class)) {
			try {
				Double.parseDouble(this.value);
			} catch (Exception e) {
				return false;
			}
			return true;
		} else if (checkClass.equals(Boolean.class) || checkClass.equals(boolean.class)) {
			try {
				Boolean.parseBoolean(this.value);
			} catch (Exception e) {
				return false;
			}
			return true;
		} else {
			return true;
		}
	}
	
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValue(Object value) {
		setValue(value.toString());
	}

	public Class<?> getCheckClass() {
		return this.checkClass;
	}
	
}
