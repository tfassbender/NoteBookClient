package net.jfabricationgames.notebook.client.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import net.jfabricationgames.notebook.client.controll.NoteManager;
import net.jfabricationgames.notebook.client.controll.NoteViewSelector;
import net.jfabricationgames.notebook.client.controll.NoteViewSelector.SortOrder;
import net.jfabricationgames.notebook.client.error.NoteBookException;
import net.jfabricationgames.notebook.note.Note;
import tornadofx.control.DateTimePicker;

public class NoteBookClientController implements Initializable {
	
	private static final Logger LOGGER = LogManager.getLogger(NoteBookClientController.class);
	
	private static final String propertyAutoSave = "autoSave";
	private static final String propertyAlwaysAskBeforeClosing = "alwaysAskToSaveBeforeClosingNote";
	
	private static final String propertiesFile = "./notebookclient.controller.properties";
	private Properties properties;
	
	private NoteViewSelector viewSelector;
	
	private static final int minPriority = 5;
	private static final int maxPriority = 1;
	private final ObservableList<Integer> priorities;
	
	private final ObservableList<LocalDateTime> executionDates = FXCollections.observableArrayList();
	private final ObservableList<LocalDateTime> reminderDates = FXCollections.observableArrayList();
	
	@FXML
	private ListView<Note> listNotes;
	@FXML
	private Button buttonNewNote;
	@FXML
	private Button buttonSelectorSettings;
	@FXML
	private Button buttonUpdateNoteList;
	
	@FXML
	private TextField textFieldNoteTitle;
	@FXML
	private TextArea textAreaNoteText;
	@FXML
	private ChoiceBox<Integer> choiceBoxNotePriority;
	
	@FXML
	private Button buttonSaveNote;
	@FXML
	private Button buttonDeleteNote;
	
	@FXML
	private ChoiceBox<LocalDateTime> choiceBoxNoteExecutionDate;
	@FXML
	private DateTimePicker datePickerNoteExecutionDate;
	@FXML
	private Button buttonAddExecutionDate;
	@FXML
	private Button buttonRemoveExecutionDate;
	
	@FXML
	private ChoiceBox<LocalDateTime> choiceBoxNoteReminderDate;
	@FXML
	private Button buttonAddReminderDate;
	@FXML
	private Button buttonRemoveReminderDate;
	@FXML
	private DateTimePicker datePickerNoteReminderDate;
	
	@FXML
	private Button buttonReminderDateAdd5Minutes;
	@FXML
	private Button buttonReminderDateAdd15Minutes;
	@FXML
	private Button buttonReminderDateAddHour;
	@FXML
	private Button buttonReminderDateAddDay;
	@FXML
	private Button buttonReminderDateAddWeek;
	@FXML
	private Button buttonReminderDateAddMonth;
	
	@FXML
	private Button buttonExecutionDateAdd5Minutes;
	@FXML
	private Button buttonExecutionDateAdd15Minutes;
	@FXML
	private Button buttonExecutionDateAddHour;
	@FXML
	private Button buttonExecutionDateAddDay;
	@FXML
	private Button buttonExecutionDateAddWeek;
	@FXML
	private Button buttonExecutionDateAddMonth;
	
	private ObservableList<Note> notes;
	private NoteManager noteManager;
	
	public NoteBookClientController() throws NoteBookException {
		//set default view selection (id of notes in descending order)
		viewSelector = new NoteViewSelector();
		viewSelector.setSortOrder(SortOrder.ID_DESC);
		//init priority values
		List<Integer> priorities = new ArrayList<Integer>();
		for (int i = minPriority; i <= maxPriority; i++) {
			priorities.add(i);
		}
		//load properties
		this.priorities = FXCollections.observableArrayList(priorities);
		loadProperties();
		//create a note manager
		try {
			noteManager = new NoteManager();
			noteManager.loadNotes();
			notes = FXCollections.observableList(noteManager.getSelectedNotes(viewSelector));
		}
		catch (NoteBookException e) {
			LOGGER.error("Couldn't initialize NoteManager", e);
			showExceptionDialog(e);
			throw e;
		}
	}
	
	private void loadProperties() {
		LOGGER.info("loading configuration file: {}", propertiesFile);
		properties = new Properties();
		try (InputStream input = new FileInputStream(propertiesFile)) {
			properties.load(input);
		}
		catch (FileNotFoundException fnfe) {
			//set default values and store the file
			LOGGER.info("no configuration file found ({}); creating default", propertiesFile);
			properties.setProperty(propertyAutoSave, "true");
			properties.setProperty(propertyAlwaysAskBeforeClosing, "true");
			try (OutputStream output = new FileOutputStream(propertiesFile)) {
				properties.store(output, null);
			}
			catch (IOException ioe) {
				LOGGER.error("Unexpected IOException while trying to store default properties");
				showExceptionDialog(ioe);
			}
		}
		catch (IOException ioe) {
			LOGGER.error("Unexpected IOException while loading properties", ioe);
			showExceptionDialog(ioe);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		listNotes.setItems(notes);
		listNotes.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> updateSelectedNote());
		choiceBoxNotePriority.setItems(priorities);
		
		buttonNewNote.setOnAction(e -> newNote());
		buttonUpdateNoteList.setOnAction(e -> updateNoteList());
		buttonSelectorSettings.setOnAction(e -> showSelectorSettings());
		
		buttonSaveNote.setOnAction(e -> saveNote());
		buttonDeleteNote.setOnAction(e -> deleteNote());
		
		buttonAddExecutionDate.setOnAction(e -> addExecutionDate());
		buttonRemoveExecutionDate.setOnAction(e -> removeExecutionDate());
		buttonAddReminderDate.setOnAction(e -> addReminderDate());
		buttonRemoveReminderDate.setOnAction(e -> removeReminderDate());
		
		buttonExecutionDateAdd5Minutes.setOnAction(e -> addTimeToExecutionDate(5, 0, 0, 0));
		buttonExecutionDateAdd15Minutes.setOnAction(e -> addTimeToExecutionDate(15, 0, 0, 0));
		buttonExecutionDateAddHour.setOnAction(e -> addTimeToExecutionDate(0, 1, 0, 0));
		buttonExecutionDateAddDay.setOnAction(e -> addTimeToExecutionDate(0, 0, 1, 0));
		buttonExecutionDateAddWeek.setOnAction(e -> addTimeToExecutionDate(0, 0, 7, 0));
		buttonExecutionDateAddMonth.setOnAction(e -> addTimeToExecutionDate(0, 0, 0, 1));
		
		buttonReminderDateAdd5Minutes.setOnAction(e -> addTimeToReminderDate(5, 0, 0, 0));
		buttonReminderDateAdd15Minutes.setOnAction(e -> addTimeToReminderDate(15, 0, 0, 0));
		buttonReminderDateAddHour.setOnAction(e -> addTimeToReminderDate(0, 1, 0, 0));
		buttonReminderDateAddDay.setOnAction(e -> addTimeToReminderDate(0, 0, 1, 0));
		buttonReminderDateAddWeek.setOnAction(e -> addTimeToReminderDate(0, 0, 7, 0));
		buttonReminderDateAddMonth.setOnAction(e -> addTimeToReminderDate(0, 0, 0, 1));
	}
	
	private void newNote() {
		LOGGER.debug("Creating a new Note");
		try {
			Note newNote = new Note("New Note", "", minPriority);
			noteManager.addNote(newNote);
			updateNoteListLocally();
			//select the new note
			listNotes.getSelectionModel().select(newNote);
		}
		catch (NoteBookException nbe) {
			LOGGER.error("NoteBookExcepiton", nbe);
			showExceptionDialog(nbe);
		}
	}
	
	/**
	 * Update notes from database
	 */
	private void updateNoteList() {
		LOGGER.debug("Updating note list");
		try {
			noteManager.loadNotes();
			updateNoteListLocally();
		}
		catch (NoteBookException nbe) {
			LOGGER.error("NoteBookExcepiton", nbe);
			showExceptionDialog(nbe);
		}
	}
	/**
	 * Update the note list without loading from database
	 */
	private void updateNoteListLocally() {
		LOGGER.debug("updating note list locally");
		boolean autoSave = Boolean.parseBoolean(properties.getProperty(propertyAutoSave, "true"));
		boolean askBeforeClosing = Boolean.parseBoolean(properties.getProperty(propertyAlwaysAskBeforeClosing, "true"));
		if (autoSave) {
			LOGGER.debug("autosaving note");
			saveNote();
		}
		else if (askBeforeClosing) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Notiz speichern");
			alert.setHeaderText("Soll die aktuelle Notiz gespeichert werden?");
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				LOGGER.debug("saving note after confirm dialog");
				saveNote();
			}
		}
		notes.clear();
		notes.addAll(noteManager.getSelectedNotes(viewSelector));
	}
	
	private void showSelectorSettings() {
		// TODO Auto-generated method stub
	}
	
	private void saveNote() {
		LOGGER.debug("saving note");
		Note currentNote = listNotes.getSelectionModel().getSelectedItem();
		if (currentNote != null) {
			currentNote.setHeadline(textFieldNoteTitle.getText());
			currentNote.setNoteText(textAreaNoteText.getText());
			currentNote.setPriority(choiceBoxNotePriority.getValue());
			currentNote.setExecutionDates(new ArrayList<>(executionDates));
			currentNote.setReminderDates(new ArrayList<>(reminderDates));
			
			try {
				noteManager.updateNote(currentNote);
			}
			catch (NoteBookException nbe) {
				LOGGER.error("NoteBookExcepiton", nbe);
				showExceptionDialog(nbe);
			}
		}
	}
	
	private void deleteNote() {
		if (listNotes.getSelectionModel().getSelectedItem() == null) {
			LOGGER.debug("deleting note: no note selected");
			return;
		}
		LOGGER.debug("deleting note (id: {})", listNotes.getSelectionModel().getSelectedItem().getId());
		try {
			noteManager.deleteNote(listNotes.getSelectionModel().getSelectedItem());
		}
		catch (NoteBookException nbe) {
			LOGGER.error("NoteBookExcepiton", nbe);
			showExceptionDialog(nbe);
		}
	}
	
	private void updateSelectedNote() {
		LOGGER.debug("updating shown note");
		Note note = listNotes.getSelectionModel().getSelectedItem();
		
		textFieldNoteTitle.setText(note.getHeadline());
		textAreaNoteText.setText(note.getNoteText());
		choiceBoxNotePriority.getSelectionModel().select(Integer.valueOf(note.getPriority()));
		
		executionDates.clear();
		if (note.getExecutionDates() != null) {
			executionDates.addAll(note.getExecutionDates());
		}
		
		reminderDates.clear();
		if (note.getReminderDates() != null) {
			reminderDates.addAll(note.getReminderDates());
		}
	}
	
	private void addExecutionDate() {
		LocalDateTime date = datePickerNoteExecutionDate.getDateTimeValue();
		if (date != null) {
			LOGGER.debug("adding execution date: {}", date);
			executionDates.add(date);
		}
		else {
			LOGGER.debug("no execution date chosen that could be added");
		}
	}
	
	private void removeExecutionDate() {
		int selectedIndex = choiceBoxNoteExecutionDate.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			LOGGER.debug("deleting execution date (index: {})", selectedIndex);
			executionDates.remove(selectedIndex);
		}
		else {
			LOGGER.debug("no execution date selected that could be deleted");
		}
	}
	
	private void addReminderDate() {
		LocalDateTime date = datePickerNoteReminderDate.getDateTimeValue();
		if (date != null) {
			LOGGER.debug("adding reminder date: {}", date);
			reminderDates.add(date);
		}
		else {
			LOGGER.debug("no reminder date chosen that could be added");
		}
	}
	
	private void removeReminderDate() {
		int selectedIndex = choiceBoxNoteReminderDate.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			LOGGER.debug("deleting reminder date (index: {})", selectedIndex);
			reminderDates.remove(selectedIndex);
		}
		else {
			LOGGER.debug("no reminder date selected that could be deleted");
		}
	}
	
	private void addTimeToExecutionDate(int minutes, int hours, int days, int months) {
		LOGGER.debug("updating reminder date");
		LocalDateTime date = choiceBoxNoteExecutionDate.getSelectionModel().getSelectedItem();
		if (date == null) {
			LOGGER.debug("no execution date selected to change; trying to use date from date time picker");
			date = datePickerNoteExecutionDate.getDateTimeValue();
		}
		if (date == null) {
			LOGGER.debug("still no date chosen; aborting");
			return;
		}
		addTimeToDate(date, minutes, hours, days, months);
	}
	
	private void addTimeToReminderDate(int minutes, int hours, int days, int months) {
		LOGGER.debug("updating reminder date");
		LocalDateTime date = choiceBoxNoteReminderDate.getSelectionModel().getSelectedItem();
		if (date == null) {
			LOGGER.debug("no reminder date selected to change; trying to use date from date time picker");
			date = datePickerNoteReminderDate.getDateTimeValue();
		}
		if (date == null) {
			LOGGER.debug("still no date chosen; aborting");
			return;
		}
		addTimeToDate(date, minutes, hours, days, months);
	}
	
	private void addTimeToDate(LocalDateTime date, int minutes, int hours, int days, int months) {
		LOGGER.debug("updating date: {}", date);
		date.plusMinutes(minutes);
		date.plusHours(hours);
		date.plusDays(days);
		date.plusMonths(months);
		LOGGER.debug("updated date to: {}", date);
	}
	
	private void showExceptionDialog(Throwable t) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Fehler");
		alert.setHeaderText("Ein Fehler ist aufgetreten");
		
		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String exceptionText = sw.toString();
		
		Label label = new Label("The exception stacktrace was:");
		
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		
		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		
		alert.showAndWait();
	}
}