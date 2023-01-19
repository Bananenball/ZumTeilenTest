package geo.kridlist;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import geo.kridlist.util.Attribut;
import geo.kridlist.util.GesamtStruktur;
import geo.kridlist.util.KridlistHelper;


/**
 * Einmal am Anfang aufrufen reicht, dann ist die Datei da
 *
 */
public class SachsencacherListenErsteller {

	static final String SachsencacherListenDatei = "Sachsencacher-Liste.txt";

	public static void erstelleSachsencacherListe(List<GesamtStruktur> pmoListe) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(SachsencacherListenDatei);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Nicht gefunden SachsencacherDatei");
		}
		int zaehler = 0;
		for (GesamtStruktur g : pmoListe) {
			zaehler++;
			if ((zaehler / 10) * 10 == zaehler) {
				System.out.println("Zähler in SachsencacherListenErsteller: " + zaehler);

			}
			List<Attribut> attributeList = KridlistHelper
					.holeAttributeAusLinkSachsencacher(KridlistHelper.getSachsencacherLink(g.gcCode));
			if (!attributeList.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(g.gcCode).append(":");
				for (Attribut a : attributeList) {
					builder.append(a.attributeId).append(",").append(a.attributPositivGesetzt).append(",")
							.append(a.beschreibung).append(";");
				}
				writer.println(builder.toString());

			}
		}
		writer.close();
	}

	public static void sachsencacherListAusDateiEinlesen(List<GesamtStruktur> pmoListe) {

		try(BufferedReader br = new BufferedReader(new FileReader(SachsencacherListenDatei))) {
			
			String line;
			while ((line = br.readLine()) != null) {
				String[] cacheTitelUndRestStr = line.split(":");
				String gcCode = cacheTitelUndRestStr[0];
				String rest = cacheTitelUndRestStr[1];
				rest=rest.replace("1 kmâ€“10 km", "1 km - 10 km");
				rest=rest.replace("&lt;1 km", "kl 1 km");
				rest = rest.replace("&gt;10 km","gr 10 km");
				String[] attributeStr = rest.split(";");
				List<Attribut> attributeList = new ArrayList<>();
				for (String str : attributeStr) {
					Attribut a = Attribut.getAttribut4String(str);
					attributeList.add(a);
				}
				for(GesamtStruktur g : pmoListe) {
					if(g.gcCode.equals(gcCode)) {
						g.attributeListAusSachsencacher = attributeList;
						break;
					}
				}
			}
		} catch (IOException e) {

			throw new RuntimeException("Fehler Lesen Sachsencacher-DAtei");
		}
	}

	//Fuer Testzwecke
	public static void allesRausschreibenLinkSachsencacher(String link) {
		try(PrintWriter 			writer = new PrintWriter("alles"+SachsencacherListenDatei)) {
			URL url = new URL(link);
			InputStreamReader isr = new InputStreamReader(url.openStream());
			BufferedReader br = new BufferedReader(isr);
			String zeile;

			while ((zeile = br.readLine()) != null) {
				writer.println(zeile);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
