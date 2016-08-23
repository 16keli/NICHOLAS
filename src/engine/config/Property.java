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
	
	public Property(String internalName, Object value) {
		this(internalName, value.toString());
	}
	
	public Property(String internalName, String value) {
		this.internalName = internalName;
		this.defaultValue = value;
	}
	
	public String getValue() {
		return (this.value == null ? this.defaultValue : this.value);
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
	
}
