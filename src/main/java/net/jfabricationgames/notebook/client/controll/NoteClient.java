package net.jfabricationgames.notebook.client.controll;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.jfabricationgames.json_rpc.JsonRpcErrorResponse;
import net.jfabricationgames.json_rpc.JsonRpcRequest;
import net.jfabricationgames.json_rpc.JsonRpcResponse;
import net.jfabricationgames.notebook.client.error.NoteBookCommunicationException;
import net.jfabricationgames.notebook.client.error.NoteBookException;
import net.jfabricationgames.notebook.note.Note;
import net.jfabricationgames.notebook.note.NoteSelector;
import net.jfabricationgames.notebook.service.NoteBookServiceMethods;

public class NoteClient {
	
	private static final Logger LOGGER = LogManager.getLogger(NoteClient.class);
	
	public static final String JSON_RPC = "2.0";
	
	private static int id = 1;
	
	public int createNote(Note note) throws NoteBookException {
		LOGGER.info("creating note: {}", note);
		
		//create a request for adding a new note
		JsonRpcRequest request = createGenericRequest();
		request.setMethod(NoteBookServiceMethods.CREATE_NOTE.getMethodName());
		request.setParams(note);
		
		JsonRpcResponse response = sendRequestAndReceiveResponse(request);
		
		//parse the response
		if (response.getResult() instanceof Integer) {
			Integer responseInt = (Integer) response.getResult();
			LOGGER.info("Received note's id: {}", responseInt);
			return responseInt.intValue();
		}
		else {
			LOGGER.error("Create: Response's result is not integer: {}", response.getResult());
			throw new NoteBookCommunicationException("The response's result could not be interpreted as Integer");
		}
	}
	
	public List<Note> getNotes(NoteSelector selector) throws NoteBookException {
		LOGGER.info("reqeusting notes with selector: {}", selector);
		
		//create a request for adding a new note
		JsonRpcRequest request = createGenericRequest();
		request.setMethod(NoteBookServiceMethods.GET_NOTES.getMethodName());
		request.setParams(selector);
		
		JsonRpcResponse response = sendRequestAndReceiveResponse(request);
		
		if (response.getResult() instanceof List) {
			try {
				@SuppressWarnings("unchecked")
				List<Object> responseList = (List<Object>) response.getResult();
				List<Note> notes = responseList.stream().map(Note::fromJsonRpcParametersSave).collect(Collectors.toList());
				return notes;
			}
			catch (Exception e) {
				throw new NoteBookCommunicationException("The response's result could not be parsed to a list of notes", e);
			}
		}
		else {
			throw new NoteBookCommunicationException("The response's result is not a list");
		}
	}
	
	public int updateNote(Note note) throws NoteBookException {
		LOGGER.info("updating note: {}", note);
		
		//create a request for adding a new note
		JsonRpcRequest request = createGenericRequest();
		request.setMethod(NoteBookServiceMethods.UPDATE_NOTE.getMethodName());
		request.setParams(note);
		
		JsonRpcResponse response = sendRequestAndReceiveResponse(request);
		
		//parse the response
		if (response.getResult() instanceof Integer) {
			Integer responseInt = (Integer) response.getResult();
			LOGGER.info("Update affected rows: {}", responseInt);
			
			if (responseInt.intValue() <= 0) {
				LOGGER.error("Update affected no rows");
				throw new NoteBookCommunicationException("The update affected no rows");
			}
			return responseInt.intValue();
		}
		else {
			LOGGER.error("Update: Response's result is not integer: {}", response.getResult());
			throw new NoteBookCommunicationException("The response's result could not be interpreted as Integer");
		}
	}
	
	public int deleteNotes(NoteSelector selector) throws NoteBookException {
		LOGGER.info("deleting notes: selector: {}", selector);
		
		//create a request for adding a new note
		JsonRpcRequest request = createGenericRequest();
		request.setMethod(NoteBookServiceMethods.DELETE_NOTES.getMethodName());
		request.setParams(selector);
		
		JsonRpcResponse response = sendRequestAndReceiveResponse(request);
		
		//parse the response
		if (response.getResult() instanceof Integer) {
			Integer responseInt = (Integer) response.getResult();
			LOGGER.info("Deleted selected; affected rows: {}", responseInt);
			return responseInt.intValue();
		}
		else {
			LOGGER.error("Delete: Response's result is not integer: {}", response.getResult());
			throw new NoteBookCommunicationException("The response's result could not be interpreted as Integer");
		}
	}
	
	private JsonRpcRequest createGenericRequest() {
		JsonRpcRequest request = new JsonRpcRequest();
		request.setJsonRpc(JSON_RPC);
		request.setId(getNextId());
		return request;
	}
	
	private String getNextId() {
		String currentId = Integer.toString(id);
		id++;
		return currentId;
	}
	
	private JsonRpcResponse sendRequestAndReceiveResponse(JsonRpcRequest request) throws NoteBookException {
		//convert to JSON (added JavaTimeModule to correctly serialize LocalDateTime objects)
		ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
		String json;
		try {
			json = ow.writeValueAsString(request);
		}
		catch (JsonProcessingException e) {
			LOGGER.error("Json representation failed", e);
			throw new NoteBookException(e);
		}
		
		//send the request to the server via POST
		HostConfiguration hostConfig = HostConfiguration.getInstance();
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(hostConfig.getHostUrlWithPort()).path(hostConfig.getHostResourcePath());
		
		LOGGER.info("Sending POST request to url: {}; request: {}", hostConfig.getHostUrlWithPort() + "/" + hostConfig.getHostResourcePath(),
				toOneLineJson(json));
		Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON));
		int responseCode = response.getStatus();
		LOGGER.info("Server sent response code: " + responseCode);
		
		//check whether the response was OK or an error code
		if (responseCode != Response.Status.OK.getStatusCode()) {
			throw new NoteBookException("HTTP error code: " + responseCode);
		}
		else if (response.hasEntity()) {
			//parse the response as integer
			return parseResponse(response);
		}
		else {
			throw new NoteBookException("The response was expected to contain data, but it's empty");
		}
	}
	
	/**
	 * Create a one lined json text from a pretty formatted text (for the logs)
	 */
	private String toOneLineJson(String json) {
		return Arrays.asList(json.split("\n")).stream().map(s -> s.trim()).collect(Collectors.joining(" "));
	}
	
	private JsonRpcResponse parseResponse(Response response) throws NoteBookCommunicationException {
		String responseText = response.readEntity(String.class);
		try {
			//try to parse the response as JsonRpcResponse
			JsonRpcResponse content = getJsonRpcResponse(responseText);
			
			LOGGER.info("Response content from server: {}", content);
			
			return content;
		}
		catch (IllegalStateException ise) {
			try {
				//if the response is no JsonRpcResponse try to parse it as JsonRpcErrorResponse
				JsonRpcErrorResponse error = getJsonRpcErrorResponse(responseText);
				
				LOGGER.error("Server responded with a JSON-RPC-Error: {}", error);
			}
			catch (IllegalStateException ise2) {
				LOGGER.error("Response couldn't be parsed", ise2);
				
				throw new NoteBookCommunicationException("Response couldn't be parsed as JsonRpcResponse nor as JsonRpcErrorResponse", ise);
			}
			
			throw new NoteBookCommunicationException("Response couldn't be parsed as JsonRpcResponse (was JsonRpcErrorResponse)", ise);
		}
	}
	
	/**
	 * Get a JsonRpcResponse from a Response object. (Deserializes JSON)
	 */
	private JsonRpcResponse getJsonRpcResponse(String responseText) throws IllegalStateException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			//"manually" parse JSON to Object
			JsonRpcResponse resp = mapper.readValue(responseText, JsonRpcResponse.class);
			return resp;
		}
		catch (IOException e) {
			throw new IllegalStateException("The response could not be read or parsed: " + responseText, e);
		}
	}
	/**
	 * Get a JsonRpcErrorResponse from a Response object. (Deserializes JSON)
	 */
	private JsonRpcErrorResponse getJsonRpcErrorResponse(String responseText) throws IllegalStateException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			//"manually" parse JSON to Object
			JsonRpcErrorResponse resp = mapper.readValue(responseText, JsonRpcErrorResponse.class);
			return resp;
		}
		catch (IOException e) {
			throw new IllegalStateException("The response could not be read or parsed: " + responseText, e);
		}
	}
}