package net.jfabricationgames.notebook.client.controll;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.jfabricationgames.notebook.client.error.NoteBookException;
import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;
import net.jfabricationgames.notebook.note.NoteSelectorBuilder;
import net.jfabricationgames.notebook.note.NoteRelation;

public class NoteManager {
	
	private static final Logger LOGGER = LogManager.getLogger(NoteManager.class);
	
	private List<Note> notes;
	private NoteClient client;
	
	public NoteManager() throws NoteBookException {
		client = new NoteClient();
		loadNotes();
	}
	
	public void loadNotes() throws NoteBookException {
		LOGGER.info("Loading notes from server");
		notes = client.getNotes(NoteSelector.empty());
	}
	
	public void addNote(Note note) throws NoteBookException {
		LOGGER.info("Adding new note: " + note);
		int id = client.createNote(note);
		note.setId(id);
		LOGGER.info("Adding new note; notes id is: " + id);
		notes.add(0, note);
	}
	
	public void updateNote(Note note) throws NoteBookException {
		LOGGER.info("Updating note: " + note);
		client.updateNote(note);
	}
	
	public void deleteNote(Note note) throws NoteBookException {
		LOGGER.info("Deleting note: " + note);
		NoteSelector selector = new NoteSelectorBuilder().addId(note.getId()).setIdRelation(NoteRelation.EQUALS).build();
		client.deleteNotes(selector);
		notes.remove(note);
	}
	
	public void deleteNotes(List<Note> notes) throws NoteBookException {
		List<Integer> noteIds = notes.stream().map(n -> n.getId()).collect(Collectors.toList());
		LOGGER.info("Deleting notes; ids: " + noteIds);
		NoteSelector selector = new NoteSelectorBuilder().addIds(noteIds).setIdRelation(NoteRelation.IN).build();
		client.deleteNotes(selector);
		notes.removeAll(notes);
	}
	
	public List<Note> getUpdatedNotes() throws NoteBookException {
		loadNotes();
		return getNotes();
	}
	public List<Note> getNotes() {
		return new ArrayList<Note>(notes);
	}
	public List<Note> getSelectedNotes(NoteViewSelector selector) {
		return selector.getMatching(getNotes());
	}
}