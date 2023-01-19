package geo.kridlist.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import geo.kridlist.enums.LogTypEnum;

public class LogStruktur {
	public String logLink;
	public String glCode;
	public String logId;
	public String logDatum;
	public LogTypEnum type; // z.B. found it
	public String finder;
	public String logtext;
	
	public static final String ENDEMARKER= "----- Ende----";

	public LogStruktur() {
		//nix
	}
	
	public LogStruktur(String logLink) {
		this.logLink = logLink;

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		// read script file
		try {
			engine.eval(Files.newBufferedReader(Paths.get(
					"D:\\Programme\\EclipseWsp\\kridlist\\src\\main\\java\\geo\\kridlist\\util\\geocaching-base-converter.js"),
					StandardCharsets.UTF_8));

			Invocable inv = (Invocable) engine;
			// call function from script file
			int beginIndex = KridlistHelper.getStartindexzuString(logLink, "https://coord.info/");
			String glCode = logLink.substring(beginIndex);
			System.out.println(glCode);
			double decodedDouble = (double) inv.invokeFunction("decode", glCode);
			String decodedStr = Long.toString(Math.round(decodedDouble));
			System.out.println(decodedStr);

			this.glCode = glCode;
			this.logId = decodedStr;
		} catch (NoSuchMethodException | ScriptException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void ausgabe() {
		  StringBuilder result = new StringBuilder();
		  String newLine = System.getProperty("line.separator");
		  
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		System.out.println(result.toString());

	}
	
	//TODO hier weitermachen
	public List<String> getLogStrukturAlsStringListe(){
		List<String> stringList = new ArrayList<>();
		stringList.add(glCode);
		stringList.add(logLink);
		stringList.add( logId);
		stringList.add( logDatum);
		stringList.add( type.getTextInGcCom()); 
		stringList.add( finder);
		stringList.add( logtext);
		stringList.add(ENDEMARKER);
		return stringList;
	}
}
