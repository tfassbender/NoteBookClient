package net.jfabricationgames.notebook.client.error;

public class NoteBookException extends Exception {
	
	private static final long serialVersionUID = -4173280426425491607L;
	
	public NoteBookException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
	public NoteBookException(String arg0) {
		super(arg0);
	}
	
	public NoteBookException(Throwable arg0) {
		super(arg0);
	}
}