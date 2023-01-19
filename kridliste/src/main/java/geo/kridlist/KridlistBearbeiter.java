package geo.kridlist;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import geo.kridlist.enums.CachetypEnum;
import geo.kridlist.util.GesamtStruktur;
import geo.kridlist.util.SachsencacherLogsStruktur;
import geo.kridlist.util.Teil1Struktur;
import geo.kridlist.util.Teil2Struktur;

public class KridlistBearbeiter
{

	String eingabeDatei = "Kridliste.txt";
	String ausgabeDatei = "gpxTest.gpx";
	String ausgabeDateiOhnePMO = "gpxTestOhnePMO.gpx";
	/** wenn neu ermittelt werden soll, welche Caches PMOs sind: */
	private boolean isPMOListErstellen = false;
	/** SachsencacherListe neu in Datei schreiben? */
	private boolean isSachsencacherListErstellen = false;

	List<Teil1Struktur> teil1Liste = new ArrayList<>();
	List<Teil2Struktur> teil2Liste = new ArrayList<>();
	List<GesamtStruktur> gesamtListe = new ArrayList<>();
	List<GesamtStruktur> gesamtListeNurPMO = new ArrayList<>();
	List<GesamtStruktur> gesamtListeOhnePMOUndTradi = new ArrayList<>();

	int countTeil1OhneKurezen = 0;
	int countTeil2OhneKurezen = 0;
	int countTeil1MitArchiviert = 0;
	int countTeil2MitArchiviert = 0;
	int countIrrelevant = 0;

	public void einlesen() throws IOException
	{

		try (BufferedReader br = new BufferedReader(new FileReader(eingabeDatei)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				// Teil1
				if (line.contains("var latlng = new L.LatLng("))
				{
					countTeil1OhneKurezen++;
					Teil1Struktur t1 = new Teil1Struktur(line);
					if (t1.isUnavailable)
					{
						for (Teil1Struktur andere : teil1Liste)
						{
							if (t1.restVerschlName.equals(andere.restVerschlName))
							{
								andere.isArchiviert = t1.isArchiviert;
								andere.isTempDisabled = t1.isTempDisabled;
								andere.isUnavailable = t1.isUnavailable;
							}
						}
					}
					else if (t1.isOwned)
					{
						continue; // nix
					}
					else
					{
						teil1Liste.add(t1);
					}
				}
				// Teil2
				else if (line.contains("width:200px;overflow:hidden;font-family:Verdana;font-size:10px")//
						&& line.contains("http://www.geocaching.com/seek/cache_details.aspx"))
				{
					countTeil2OhneKurezen++;
					Teil2Struktur t2 = new Teil2Struktur(line);
					if (t2.isUnavailableUVorn)
					{
						for (Teil2Struktur andere : teil2Liste)
						{
							if (t2.restVerschlName.equals(andere.restVerschlName))
							{
								if (andere.isArchiviert != t2.isArchiviert || andere.isUnavailable != t2.isUnavailable
										|| andere.isTempDisabled != t2.isTempDisabled
										|| t2.isUnavailable != t2.isUnavailableUVorn)
								{
									throw new RuntimeException("m und u passen nicht zusammen");
								}

							}
						}
					}
					else
					{
						if (t2.isArchiviert == false)
						{
							teil2Liste.add(t2);
						}

					}
				}
				else
				{
					countIrrelevant++;
				}
			}
			br.close();
			System.out.println(" Teil1: " + countTeil1OhneKurezen + " Teil2: " + countTeil2OhneKurezen + " Irrelevant: "
					+ countIrrelevant);

			System.out.println("Mit Archivierten: Teil1: " + teil1Liste.size() + " Teil2: " + teil2Liste.size());
			teil1Liste.removeIf(t -> t.isArchiviert);
			System.out.println("Teil1: " + teil1Liste.size() + " Teil2: " + teil2Liste.size());

			// Teile zusammenfuehren
			for (Teil1Struktur t1 : teil1Liste)
			{
				GesamtStruktur g = new GesamtStruktur(t1);
				Teil2Struktur t2 = null;
				for (Teil2Struktur t : teil2Liste)
				{
					if (t.titel.equals(t1.titel) && t.verschluesselterName.equals(t1.verschluesselterName)
							&& t.isUnavailable == t1.isUnavailable && t.isArchiviert == t1.isArchiviert
							&& t.isTempDisabled == t1.isTempDisabled)
					{
						t2 = t;
					}
				}
				g.setzeTeil2Str(t2);
				gesamtListe.add(g);
				if (t2 == null || g.typ == CachetypEnum.UNBEKANNT)
				{
					throw new RuntimeException("ein Fehler!");
				}

			}
			System.out.println("Gesamtliste fertig");

			if (isPMOListErstellen)
			{
				PMOListenErsteller.erstellePMOListe(gesamtListe);
			}
			PMOListenErsteller.pmoListAusDateiEinlesen();

			int premiumzaehler = 0;
			int zaehler = 0;
			for (GesamtStruktur g : gesamtListe)
			{
				zaehler++;
				if ((zaehler / 10) * 10 == zaehler)
				{
					System.out.println("Zähler: " + zaehler);

				}
				// if (g.restVerschlName.equals("3183")) {
				// boolean isPremium = pruefeLink("https://www.geocaching.com/geocache/" +
				// g.gcCode);
				boolean isPremium = PMOListenErsteller.isPMO(g);
				if (isPremium)
				{
					premiumzaehler++;
					gesamtListeNurPMO.add(g);

					// boolean isPremium = pruefeLink(g.cacheLink);
					// System.out.println("Premium?: " + isPremium);
				}
				else if (g.typ == CachetypEnum.LETTERBOX || g.typ == CachetypEnum.MULTI || g.typ == CachetypEnum.MYSTERY
						|| g.typ == CachetypEnum.WHERIGO)
				{
					gesamtListeOhnePMOUndTradi.add(g);
				}
			}
			System.out
					.println("Premiumzahl:" + premiumzaehler + "  gesamtlisteNurPMO zahl: " + gesamtListeNurPMO.size());
			System.out
					.println("Zahl Letterbox, Multi, Mystery, WHERIGO ohne PMO: " + gesamtListeOhnePMOUndTradi.size());
			// int zaehler=0;
//		for(GesamtStruktur g: gesamtListe) {
//			g.gpxWegpunkt=GPXBearbeiter.bearbeiten(g);
//			zaehler++;
//			System.out.println("Zähler: " + zaehler);
//		}

			// hier Sachsencacherzeug ermitteln
			if (isSachsencacherListErstellen)
			{
				System.out.println("Sachsencacherliste erstellen:");
				SachsencacherListenErsteller.erstelleSachsencacherListe(gesamtListeNurPMO);
				System.out.println("Sachsencacherliste erstellt");

			}
			System.out.println("Sachsencacherliste aus Datei lesen:");
			// SachsencacherListe einlesen
			SachsencacherListenErsteller.sachsencacherListAusDateiEinlesen(gesamtListeNurPMO);
			System.out.println("Sachsencacherliste aus Datei gelesen");

			List<SachsencacherLogsStruktur> logGesamtStrukturenListe = SachsencacherLogEinleser
					.leseGesamtLogStrukturen();
			PrintWriter writer =new PrintWriter(new OutputStreamWriter( new FileOutputStream(ausgabeDatei), "UTF-8"));
//			PrintWriter writer = new PrintWriter(ausgabeDatei);
			//encoding="windows-1252"
//			String ausgabe = "<?xml version=\"1.0\" encoding=\"windows-1252\"?>\r\n"
//					+ "<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" version=\"1.0\" creator=\"Opencaching.de - https://www.opencaching.de/\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd https://github.com/opencaching/gpx-extension-v1 https://raw.githubusercontent.com/opencaching/gpx-extension-v1/master/schema.xsd http://www.gsak.net/xmlv1/4 http://www.gsak.net/xmlv1/4/gsak.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\r\n"
//					+ "<name>Cache listing generated from Kridliste</name>\n";
			String ausgabe = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
					+ "<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" version=\"1.0\" creator=\"Opencaching.de - https://www.opencaching.de/\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd https://github.com/opencaching/gpx-extension-v1 https://raw.githubusercontent.com/opencaching/gpx-extension-v1/master/schema.xsd http://www.gsak.net/xmlv1/4 http://www.gsak.net/xmlv1/4/gsak.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\r\n"
					+ "<name>Cache listing generated from Kridliste</name>\n";
			writer.print(ausgabe);
			int zaehlerA = 0;
//		for (GesamtStruktur g : gesamtListeNurPMO) {
//			GPXBearbeiter.bearbeiten(g,writer);
//			zaehlerA++;
//			if ((zaehlerA / 100) * 100 == zaehlerA) {
//				System.out.println("ZählerA: " + zaehlerA);
//			}
//		}
			gesamtListeNurPMO.forEach(g -> {
				try
				{
					SachsencacherLogsStruktur logGesamtStruktur = SachsencacherLogEinleser
							.getPassendeGesamtStruktur(logGesamtStrukturenListe, g.gcCode);

					// GPXBearbeiter.bearbeiten(g, writer);
					GPXBearbeiter.bearbeitenNurPMO(g,logGesamtStruktur, writer);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			writer.println("</gpx>");
			System.out.println("Zwischenstand: " + zaehlerA);

			writer.close();

			PrintWriter writer2 = new PrintWriter(ausgabeDateiOhnePMO);
			ausgabe = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
					+ "<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" version=\"1.0\" creator=\"Opencaching.de - https://www.opencaching.de/\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd https://github.com/opencaching/gpx-extension-v1 https://raw.githubusercontent.com/opencaching/gpx-extension-v1/master/schema.xsd http://www.gsak.net/xmlv1/4 http://www.gsak.net/xmlv1/4/gsak.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\r\n"
					+ "<name>Cache listing generated from Kridliste</name>\n";
			writer2.print(ausgabe);
			gesamtListeOhnePMOUndTradi.forEach(g -> {
				try
				{
					GPXBearbeiter.bearbeiten2(g, writer2);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			writer2.println("</gpx>");
			writer2.close();

			for (CachetypEnum c : CachetypEnum.values())
			{
				int i = 0;
				for (GesamtStruktur g : gesamtListeNurPMO)
				{
					if (g.typ == c)
					{
						i++;
					}
				}
				System.out.println(c.name + ": " + i);
			}
			System.out.println("");
			System.out.println("Liste ohne PMO:");
			for (CachetypEnum c : CachetypEnum.values())
			{
				int i = 0;
				for (GesamtStruktur g : gesamtListeOhnePMOUndTradi)
				{
					if (g.typ == c)
					{
						i++;
					}
				}
				if (i > 0)
				{
					System.out.println(c.name + ": " + i);
				}

			}
		}
	}

}
