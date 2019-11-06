package net.jfabricationgames.notebook.client.controll;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;

public class NoteViewSelector extends NoteSelector {
	
	public enum SortOrder {
		ID_ASC((n1, n2) -> Integer.compare(n1.getId(), n2.getId())),//
		ID_DESC((n1, n2) -> Integer.compare(n2.getId(), n1.getId())),//
		DATE_ASC((n1, n2) -> Note.compareExecutionDates(n1, n2)),//
		DATE_DESC((n1, n2) -> Note.compareExecutionDates(n2, n1)),//
		NAME_ASC((n1, n2) -> n1.getHeadline().compareToIgnoreCase(n2.getHeadline())),//
		NAME_DESC((n1, n2) -> n2.getHeadline().compareToIgnoreCase(n1.getHeadline())),//
		PRIORITY_ASC((n1, n2) -> Integer.compare(n1.getPriority(), n2.getPriority())),//
		PRIORITY_DESC((n1, n2) -> Integer.compare(n2.getPriority(), n1.getPriority())),//
		NONE((n1, n2) -> 0);
		
		private final Comparator<Note> comparator;
		
		private SortOrder(Comparator<Note> comparator) {
			this.comparator = comparator;
		}
		
		public Comparator<Note> getComparator() {
			return comparator;
		}
	}
	
	private SortOrder sortOrder = SortOrder.NONE;
	private String headlineContainsText;
	private String noteTextContainsText;
	
	public List<Note> getMatching(List<Note> notes) {
		notes = super.getMatching(notes);
		Stream<Note> noteStream = notes.stream();
		
		if (headlineContainsText != null) {
			noteStream.filter(note -> note.getHeadline().contains(headlineContainsText));
		}
		if (noteTextContainsText != null) {
			noteStream.filter(note -> note.getNoteText().contains(noteTextContainsText));
		}
		
		List<Note> matching = noteStream.collect(Collectors.toList());
		Collections.sort(matching, sortOrder.getComparator());
		return matching;
	}
	
	public SortOrder getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public String getHeadlineContainsText() {
		return headlineContainsText;
	}
	public void setHeadlineContainsText(String headlineContainsText) {
		this.headlineContainsText = headlineContainsText;
	}
	
	public String getNoteTextContainsText() {
		return noteTextContainsText;
	}
	public void setNoteTextContainsText(String noteTextContainsText) {
		this.noteTextContainsText = noteTextContainsText;
	}
}