# NoteBookClient

A simple note manager client that connects to a java webservice: [NoteBookService](https://github.com/tfassbender/NoteBookService)

Notes can be added, received, updated and removed using the REST interface with a JSON-RPC 2.0 communication standard.

# Methods

The methods that can be called using JSON-RPC 2.0 are:

* **create_note(Note)** - Receives a note and creates it in the database (returns the notes new id)
* **get_notes(NoteSelector)** - Returns all notes in the database that match the NoteSelector
* **update_note(Note)** - Updates the note (returns the number of affected rows)
* **delete_notes(NoteSelector)** - Deletes all notes that match the NoteSelector (returns the number of affected rows)

# Communication classes

The classes (with fields) needed for the communication are the following:

* **Note**
    * id: int
    * headline: String
    * noteText: String
    * priority: int
    * executionDates: List&lt;LocalDateTime&gt;
    * reminderDates: List&lt;LocalDateTime&gt;
* **NoteSelector**
    * ids: List&lt;Integer&gt;
    * idRelation: Relation
    * date: LocalDateTime
    * dateRelation: Relation
    * priority: int
    * priorityRelation: Relation
* **Relation** (enum)
    * NONE
    * BEFORE
    * AFTER
    * GREATER
    * LESS
    * EQUALS
    * GREATER_EQUALS
    * LESS_EQUALS
    * IN
  
All communication classes need to satisfy the [java bean convention](https://en.wikipedia.org/wiki/JavaBeans).

# Links

The NoteBookService implementation can be found here: [NoteBookService](https://github.com/tfassbender/NoteBookService)

For the compiled webservice project that can be run using docker see: [NoteBookService_docker](https://github.com/tfassbender/NoteBookService_docker)