package net.jfabricationgames.notebook.client.controll;

import java.util.List;

import net.jfabricationgames.notebook.client.error.NoteBookCommunicationException;
import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;

public class NoteClient {
	
	public int createNote(Note note) throws NoteBookCommunicationException {
		//TODO
		return -1;
	}
	
	public List<Note> getNotes(NoteSelector selector) throws NoteBookCommunicationException {
		//TODO
		return null;
	}
	
	public int updateNote(Note note) throws NoteBookCommunicationException {
		//TODO
		return -1;
	}
	
	public int deleteNotes(NoteSelector selector) throws NoteBookCommunicationException {
		//TODO
		return -1;
	}
}