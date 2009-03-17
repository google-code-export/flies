/**
 * 
 */
package org.fedorahosted.flies.projects;

public final class AdapterException extends RuntimeException{
	public AdapterException() {
		super();
	}
	public AdapterException(String message) {
		super(message);
	}
	public AdapterException(Throwable cause) {
		super(cause);
	}
	public AdapterException(String message, Throwable cause) {
		super(message, cause);
	}
	
}