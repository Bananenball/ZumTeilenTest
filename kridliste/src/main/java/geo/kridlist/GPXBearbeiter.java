package geo.kridlist;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import geo.kridlist.util.Attribut;
import geo.kridlist.util.GesamtStruktur;
import geo.kridlist.util.KridlistHelper;
import geo.kridlist.util.SachsencacherLogsStruktur;

public class GPXBearbeiter
{
	private static List<String> einwptListe = new ArrayList<>();

	final static private String UMBRUCH = "\n &lt;br /&gt;";

	/**
	 * nur fuer PMO,alte Variante! Stattdessen bearbeitenNurPMO nutzen
	 * 
	 * @param g
	 * @param w
	 * @throws IOException
	 */
	@Deprecated
	public static void bearbeiten(GesamtStruktur g, PrintWriter w) throws IOException
	{
		einwptListe = new ArrayList<>();
		// List<String> einwptListe=new ArrayList<>();
		String zeile = "<wpt lat=\"" + g.koordinatenLat + "\" lon=\"" + g.koordinatenLng + "\">";
		einwptListe.add(zeile);
		erstelleZeile("name", g.gcCode);
		erstelleZeile("url", g.cacheLink);
		erstelleZeile("urlname", g.titel);
		erstelleZeile("sym", "Geocache");

		erstelleZeile("type", "Geocache|" + g.typ.name);

		String isAvailable = g.isTempDisabled ? "False" : "True";
		if (g.isArchiviert)
		{
			throw new RuntimeException("hier sollte nichts archiviertes mehr sein!");
		}
		zeile = "<groundspeak:cache id=\"1\" available=\"" + isAvailable
				+ "\" archived=\"False\" xmlns:groundspeak=\"http://www.groundspeak.com/cache/1/0/1\">";
		einwptListe.add(zeile);

		erstelleZeileGroundspeak("name", g.titel);
		erstelleZeileGroundspeak("placed_by", g.owner);
		erstelleZeileGroundspeak("owner", g.owner);
		erstelleZeileGroundspeak("type", g.typ.name);
		erstelleZeileGroundspeak("container", g.groesse);

		erstelleZeileGroundspeak("difficulty", g.dWertung);
		erstelleZeileGroundspeak("terrain", g.tWertung);

		erstelleZeileGroundspeak("long_description",
				"Aus Kridliste. \n Favoritenpunkte: " + g.favPoints + "\n Fund von MrKrid am: " + g.funddatum
						+ "\n Link: http://coord.info/" + g.gcCode + "\n oder " + g.cacheLink);
		einwptListe.add("</groundspeak:cache>");
		einwptListe.add("</wpt>");
		for (String l : einwptListe)
		{
			w.println(l);
		}
//		String wegpunkt = "";
//		StringBuilder sb=new StringBuilder("");
//		for (String l : einwptListe) {
//			sb.append(l).append("\n");
//			wegpunkt = wegpunkt + l + "\n";

//		wegpunkt=sb.toString();
//		return wegpunkt;
	}

	// TODO: einbauen
	/**
	 * Eingeschraenkte Daten ausgeben, der Rest wird sowieso aus der Datenbank
	 * generiert
	 * 
	 * TODO: ggf. auch noch Attribute dazu generieren
	 * 
	 * TODO: html-Tauglich
	 * 
	 * TODO: Sachsencacher
	 * 
	 * @param g
	 * @param w
	 * @throws IOException
	 */
	public static void bearbeitenNurPMO(GesamtStruktur g, SachsencacherLogsStruktur logGesamtStruktur, PrintWriter w)
			throws IOException
	{
		einwptListe = new ArrayList<>();
		// List<String> einwptListe=new ArrayList<>();
		String zeile = "<wpt lat=\"" + g.koordinatenLat + "\" lon=\"" + g.koordinatenLng + "\">";
		einwptListe.add(zeile);
		erstelleZeile("name", g.gcCode);
		// erstelleZeile("url", g.cacheLink);
		// erstelleZeile("urlname", g.titel);
		// erstelleZeile("sym", "Geocache");

		// erstelleZeile("type", "Geocache|" + g.typ.name);

		String isAvailable = g.isTempDisabled ? "False" : "True";
		if (g.isArchiviert)
		{
			throw new RuntimeException("hier sollte nichts archiviertes mehr sein!");
		}
		zeile = "<groundspeak:cache available=\"" + isAvailable
				+ "\" archived=\"False\" xmlns:groundspeak=\"http://www.groundspeak.com/cache/1/0/1\">";
		einwptListe.add(zeile);

		String titel =g.titel.replace("&", "und");
		//if(!titel.equals(g.titel)||!titel.equals(titel.replace("<", "kleiner")) || !titel.equals(titel.replace(">", "groesser"))) {
//			System.out.println("Titel geaendert:" + titel);
//		}
		//die werden wohl eh nicht aufgerufen:
//		titel=titel.replace("<", "kleiner");
//		titel=titel.replace(">", "groesser");
		
		erstelleZeileGroundspeak("name", titel);
//		erstelleZeileGroundspeak("placed_by", g.owner);
//		erstelleZeileGroundspeak("owner", g.owner);
//		erstelleZeileGroundspeak("type", g.typ.name);
//		erstelleZeileGroundspeak("container", g.groesse);
//
//		erstelleZeileGroundspeak("difficulty", g.dWertung);
//		erstelleZeileGroundspeak("terrain", g.tWertung);

		String heute = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
//		erstelleZeileGroundspeak("long_description",
//				"Aus Kridliste. \n Favoritenpunkte Stand letzter Erzeugung(" + heute + "):" + g.favPoints
//						+ "\n Fund von MrKrid am: " + g.funddatum + "\n Link: http://coord.info/" + g.gcCode
//						+ "\n oder " + g.cacheLink);

		StringBuilder longDescBuilder = new StringBuilder();
		longDescBuilder.append("PMO-Cache aus Kridliste generiert (Stand letzter Erzeugung: am ").append(heute)
				.append(")") //
				.append(UMBRUCH).append("Favoritenpunkte: ").append(g.favPoints).append(UMBRUCH) //
				.append("Fund von MrKrid am: ").append(g.funddatum).append(UMBRUCH)//
				.append("Link zum Cache: ").append(erstelleGroundspeakLinkAsHtml(g.gcCode)).append(UMBRUCH)//
				.append("Link zu Sachsencacher: ").append(erstelleSachsencacherLinkAsHtml(g.gcCode)).append(UMBRUCH) //
				.append("Gesamtfunde: ").append(logGesamtStruktur.anzahlLogs)//
		;
		if (!g.attributeListAusSachsencacher.isEmpty())
		{
			longDescBuilder.append(UMBRUCH).append("Attribute:");
		}
		for (Attribut a : g.attributeListAusSachsencacher)
		{
			String ggfNicht = a.attributPositivGesetzt ? "" : "NICHT ";
			longDescBuilder.append(UMBRUCH).append(ggfNicht).append(a.beschreibung).append("(Id: ")
					.append(a.attributeId).append(")");
		}
		String longDescription = longDescBuilder.toString();

		Map<String, String> mapZusatzAttribute = new HashMap<>();
		mapZusatzAttribute.put("html", "True");

		erstelleZeileMitZusatzAttributenGroundspeak("long_description", longDescription, mapZusatzAttribute);

		einwptListe.add("<groundspeak:attributes>");
		for (Attribut a : g.attributeListAusSachsencacher)
		{
			String incWertAusAttributPositivGesetzt = a.attributPositivGesetzt ? "1" : "0";
			Map<String, String> mapZusatzAttribute2 = new HashMap<>();
			mapZusatzAttribute2.put("id", Integer.toString(a.attributeId));
			mapZusatzAttribute2.put("inc", incWertAusAttributPositivGesetzt);
			erstelleZeileMitZusatzAttributenGroundspeak("attribute", a.beschreibung, mapZusatzAttribute2);

		}
		einwptListe.add("</groundspeak:attributes>");
		einwptListe.addAll(SachsencacherLogsStruktur.erstelleGPXLogTeil(logGesamtStruktur));
		einwptListe.add("</groundspeak:cache>");

		einwptListe.add("</wpt>");
		for (String l : einwptListe)
		{
			w.println(l);
		}
//		String wegpunkt = "";
//		StringBuilder sb=new StringBuilder("");
//		for (String l : einwptListe) {
//			sb.append(l).append("\n");
//			wegpunkt = wegpunkt + l + "\n";

//		wegpunkt=sb.toString();
//		return wegpunkt;
	}

	private static void erstelleZeile(String tag, String inhalt)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(tag).append(">").append(inhalt).append("</").append(tag).append(">");
		String zeile = sb.toString();
		einwptListe.add(zeile);
	}

	private static void erstelleZeileGroundspeak(String tag, String inhalt)
	{
		erstelleZeile("groundspeak:" + tag, inhalt);
	}

	private static void erstelleZeileMitZusatzAttributenGroundspeak(String tag, String inhalt,
			Map<String, String> attribute)
	{
		erstelleZeileMitZusatzAttributen("groundspeak:" + tag, inhalt, attribute);
	}

	private static void erstelleZeileMitZusatzAttributen(String tag, String inhalt, Map<String, String> attribute)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(tag);
		for (Entry<String, String> attribut : attribute.entrySet())
		{
			sb.append(" ").append(attribut.getKey()).append("=\"").append(attribut.getValue()).append("\"");

		}

		sb.append(">").append(inhalt).append("</").append(tag).append(">");
		String zeile = sb.toString();
		einwptListe.add(zeile);
	}

	private static String erstelleGroundspeakLinkAsHtml(String gcCode)
	{
		StringBuilder builder = new StringBuilder();
		String link = builder.append("http://coord.info/").append(gcCode).toString();
		return erstelleLinkEintragAsHtml(link, link);
	}

	private static String erstelleSachsencacherLinkAsHtml(String gcCode)
	{
		StringBuilder builder = new StringBuilder();
		String link = KridlistHelper.getSachsencacherLink(gcCode);
		return erstelleLinkEintragAsHtml(link, link);
	}

	// html-Linkeintrag erstellen
	private static String erstelleLinkEintragAsHtml(String link, String linkBeschreibung)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&lt;a href=\"").append(link).append("\" &gt; ") //
				.append(linkBeschreibung).append("&lt;/a&gt;");
		return builder.toString();
	}

	/**
	 * Version ohne PMO fuer OsmAnd oder sowas, wenn nicht alle Felder angezeigt
	 * werden (andere Felder ausgegeben)
	 * 
	 * @param g
	 * @param w
	 * @throws IOException
	 */
	public static void bearbeiten2(GesamtStruktur g, PrintWriter w) throws IOException
	{
		einwptListe = new ArrayList<>();
		// List<String> einwptListe=new ArrayList<>();
		String zeile = "<wpt lat=\"" + g.koordinatenLat + "\" lon=\"" + g.koordinatenLng + "\">";
		einwptListe.add(zeile);
		erstelleZeile("name", g.gcCode + " " + g.typ.name + " " + g.titel.substring(0, Math.min(g.titel.length(), 40)));
		erstelleZeile("url", g.cacheLink);
		erstelleZeile("urlname", "Favoritenpunkte: " + g.favPoints + " Fund von MrKrid am: " + g.funddatum
				+ " Link: http://coord.info/" + g.gcCode);
		erstelleZeile("sym", "Geocache");

		erstelleZeile("type", "Geocache|" + g.typ.name);

		String isAvailable = g.isTempDisabled ? "False" : "True";
		if (g.isArchiviert)
		{
			throw new RuntimeException("hier sollte nichts archiviertes mehr sein!");
		}
		zeile = "<groundspeak:cache id=\"1\" available=\"" + isAvailable
				+ "\" archived=\"False\" xmlns:groundspeak=\"http://www.groundspeak.com/cache/1/0/1\">";
		einwptListe.add(zeile);

		erstelleZeileGroundspeak("name", g.titel);
		erstelleZeileGroundspeak("placed_by", g.owner);
		erstelleZeileGroundspeak("owner", g.owner);
		erstelleZeileGroundspeak("type", g.typ.name);
		erstelleZeileGroundspeak("container", g.groesse);

		erstelleZeileGroundspeak("difficulty", g.dWertung);
		erstelleZeileGroundspeak("terrain", g.tWertung);

		erstelleZeileGroundspeak("long_description",
				"Aus Kridliste. \n Favoritenpunkte: " + g.favPoints + "\n Fund von MrKrid am: " + g.funddatum
						+ "\n Link: http://coord.info/" + g.gcCode + "\n oder " + g.cacheLink);
		einwptListe.add("</groundspeak:cache>");
		einwptListe.add("</wpt>");
		for (String l : einwptListe)
		{
			w.println(l);
		}
	}
}
