package geo.kridlist.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.gargoylesoftware.htmlunit.javascript.host.intl.DateTimeFormat;

import geo.kridlist.GPXBearbeiter;

public class SachsencacherLogsStruktur
{
	String teilzeile;
	int beginIndex;
	int endIndex;

	public String gcCode;
	public int anzahlLogs = 0;

	public List<LogStruktur> logLinks = new ArrayList<>();

	public SachsencacherLogsStruktur(String gcCode, int anzahlLogs)
	{
		this.gcCode = gcCode;
		this.anzahlLogs = anzahlLogs;
	}

	public SachsencacherLogsStruktur(String zeile, String gcCode)
	{
		this.gcCode = gcCode;
		this.teilzeile = zeile;

		try
		{
			anzahlLogs = Integer.valueOf(
					KridlistHelper.getTeilString(zeile, "<div class=\"item-col nr\"><div><span>", "</span></div>"));
		}
		catch (Exception e)
		{
			System.out.println("Fehler bei GCCODE: " + gcCode);
			throw e;
		}
		String GLStartString = "https://coord.info/GL";
		while (teilzeile.indexOf(GLStartString) >= 0)
		{
			String logLink = setzeTeilString(GLStartString, "\"");
			teilzeile = teilzeile.substring(endIndex);
			LogStruktur logStruktur = new LogStruktur(logLink);

			logStruktur.logDatum = setzeTeilString2("found-date\"><div><span>", "</span>");

			String test = setzeTeilString2("href=\"/geocacher/", "<img src="); // nur ueberspringen
			test = setzeTeilString2("href=\"/geocacher/", "\">"); // nur ueberspringen

			String finder = setzeTeilString2("", "</a>");
			// warum die Schmierzeichen "> noch entfernt werden müssen,
			// verstehe ich nicht so ganz, aber ist wohl so...:
			logStruktur.finder = finder.substring(2);

			logLinks.add(logStruktur);

//			  NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
//	            ScriptEngine engine = factory.getScriptEngine("--language=es6");

		}
	}

	// nicht ganz wie bei Teil1Struktur, gesuchtes wird nicht übersprungen
	private String setzeTeilString(String startsuchstr, String endsuchstr)
	{
//		teilzeile = teilzeile.substring(endIndex);
		// beginIndex=KridlistHelper.getStartindexzuString(teilzeile, startsuchstr);
		beginIndex = teilzeile.indexOf(startsuchstr);
		if (beginIndex >= 0)
		{
			teilzeile = teilzeile.substring(beginIndex);
			beginIndex = 0;
			endIndex = KridlistHelper.getEndindexzuString(teilzeile, endsuchstr);
			return teilzeile.substring(beginIndex, endIndex);
		}
		throw new RuntimeException("Hier ist falsch...");

	}

	// ähnlicher wie bei Teil1Struktur
	private String setzeTeilString2(String startsuchstr, String endsuchstr)
	{
		teilzeile = teilzeile.substring(endIndex);
		beginIndex = KridlistHelper.getStartindexzuString(teilzeile, startsuchstr);
		// beginIndex = teilzeile.indexOf(startsuchstr);
		teilzeile = teilzeile.substring(beginIndex);
		beginIndex = 0;
		endIndex = KridlistHelper.getEndindexzuString(teilzeile, endsuchstr);
		return teilzeile.substring(beginIndex, endIndex);

	}

	public void sysoLogLinks()
	{
		System.out.println("Anzahl Logs: " + anzahlLogs);
		logLinks.forEach(x -> System.out.println(x.logLink));
	}

	public static PrintWriter oeffneWriter(String ausgabeDatei)
	{
		try
		{
			return new PrintWriter(ausgabeDatei);
		} //
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

//	public static void schliesseWriter(PrintWriter writer) {
//	writer.close();
//	}

	public void schreibeLogStruktur(PrintWriter writer)
	{
		String ersteZeile = this.gcCode + ":" + this.anzahlLogs;

		writer.println(ersteZeile);
		for (LogStruktur logStruktur : this.logLinks)
		{
			List<String> logZeilen = logStruktur.getLogStrukturAlsStringListe();
			for (String zeile : logZeilen)
			{
				writer.println(zeile);
			}
		}
		writer.println(LogStruktur.ENDEMARKER + gcCode);

	}

	public static List<String> erstelleGPXLogTeil(SachsencacherLogsStruktur struktur)
	{
		List<String> gpxListe = new ArrayList<>();

		gpxListe.add("<groundspeak:logs>");
		for (LogStruktur logStruktur : struktur.logLinks)
		{
			gpxListe.add(" <groundspeak:log id=\"" + logStruktur.logId + "\">");

			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			LocalDate logDatum = LocalDate.parse(logStruktur.logDatum, dateTimeFormatter);

			String formatiertesDatumString =new StringBuilder().append(logDatum.getYear()).append("-").append(logDatum.getMonthValue()).append("-")
					.append(logDatum.getDayOfMonth()).append("T00:00:00Z").toString();

			erstelleZeileGroundspeak(gpxListe,"date", formatiertesDatumString);
			erstelleZeileGroundspeak(gpxListe,"type", logStruktur.type.getTextInGcCom());
			erstelleZeileGroundspeak(gpxListe,"finder", logStruktur.finder); //mal versuchen ohne id leer anzugeben
			
//			erstelleZeileGroundspeak(gpxListe,"text encoded=\"False\"", logStruktur.logtext);
			//TODO: mal probieren, ob das funktioniert
			gpxListe.add("<groundspeak:text encoded=\"True\">");
			gpxListe.add(logStruktur.logtext);
			gpxListe.add("</groundspeak:text>");
			
			gpxListe.add(" </groundspeak:log>");
		}
		gpxListe.add("</groundspeak:logs>");
		
		return gpxListe;
	}
	
	private static void erstelleZeile(List<String> liste,String tag, String inhalt) {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(tag).append(">").append(inhalt).append("</").append(tag).append(">");
		String zeile = sb.toString();
		liste.add(zeile);
	}

private static void erstelleZeileGroundspeak(List<String> liste,String tag, String inhalt) {
		erstelleZeile(liste, "groundspeak:" + tag, inhalt);
	}

}
