package net.jfabricationgames.notebook.client.error;

public class NoteBookCommunicationException extends NoteBookException {
	
	private static final long serialVersionUID = -8400843882101704613L;
	
	public NoteBookCommunicationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
	public NoteBookCommunicationException(String arg0) {
		super(arg0);
	}
	
	public NoteBookCommunicationException(Throwable arg0) {
		super(arg0);
	}
}