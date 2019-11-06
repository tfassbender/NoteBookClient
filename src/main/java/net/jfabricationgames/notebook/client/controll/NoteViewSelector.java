package net.jfabricationgames.notebook.client.controll;

import java.util.Comparator;

import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;

public class NoteViewSelector extends NoteSelector {
	
	public enum SortOrder {
		//TODO add comparators
		ID_ASC(null),//
		ID_DESC(null),//
		DATE_ASC(null),//
		DATE_DESC(null),//
		NAME_ASC(null),//
		NAME_DESC(null),//
		PRIORITY_ASC(null),//
		PRIORITY_DESC(null),//
		NONE(null);
		
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