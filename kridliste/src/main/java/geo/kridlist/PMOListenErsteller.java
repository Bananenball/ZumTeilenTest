package geo.kridlist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import geo.kridlist.util.GesamtStruktur;
import geo.kridlist.util.KridlistHelper;

/**
 * Einmal am Anfang aufrufen reicht, dann ist die Datei da
 *
 */
public class PMOListenErsteller {
	public static List<String> pmoCachesList = new ArrayList<>();

	static final String PMOListenDatei = "PMO-Liste.txt";

	public static void erstellePMOListe(List<GesamtStruktur> gesamtListe) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(PMOListenDatei);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Nicht gefunden PMODatei");
		}
		int zaehler = 0;
		for (GesamtStruktur g : gesamtListe) {
			zaehler++;
			if ((zaehler / 10) * 10 == zaehler) {
				System.out.println("Zähler in PMOListenErsteller: " + zaehler);

			}
			boolean isPremium = KridlistHelper.pruefeLink("https://www.geocaching.com/geocache/" + g.gcCode);
			if (isPremium) {
				writer.println(g.gcCode);

			}
		}
		writer.close();
	}

	public static void pmoListAusDateiEinlesen() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(PMOListenDatei));
			String line;
			while ((line = br.readLine()) != null) {
				pmoCachesList.add(line);
			}
			br.close();
		} catch (IOException e) {

			throw new RuntimeException("Fehler Lesen PMODAtei");
		}
	}

	public static boolean isPMO(GesamtStruktur g) {
		for (String str : pmoCachesList) {
			if (str.equals(g.gcCode)) {
				return true;
			}
//			System.out.println("ACHTUNG: DAS IST FUER EINE FEHLERHAFTE VERSION UND SOLLTE DEMNÄCHST RAUS!!!");
//			if(str.contains(g.gcCode)) {
//				return true;
//			}
		}
		return false;
	}

}
