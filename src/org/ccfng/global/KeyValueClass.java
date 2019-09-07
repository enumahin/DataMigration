package org.ccfng.global;

public class KeyValueClass {

	private Integer key;

	private String value;

	public KeyValueClass() {
	}

	public KeyValueClass(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
