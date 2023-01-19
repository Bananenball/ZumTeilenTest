package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import geo.kridlist.KridlistBearbeiter;
import geo.kridlist.PMOListenErsteller;
import geo.kridlist.SachsencacherLogEinleser;
import geo.kridlist.util.Attribut;
import geo.kridlist.util.KridlistHelper;
import geo.kridlist.util.SachsencacherLogsStruktur;

public class MainClass
{
	public static void main(String[] args) throws IOException
	{
//		kridlistBearbeitung();

		// TODO: die Attribute hiervon noch ordentlich in Cache packen
		// List<Attribut>
		// liste=KridlistHelper.holeAttributeAusLinkSachsencacher("https://sachsencacher.de/de-nw/geocache/GC3EV6P");
//		for(Attribut a : liste) {
//			System.out.println(a.attributeId + " / " + a.beschreibung + " / " + a.attributPositivGesetzt);
//		}
//SachsencacherListenErsteller.allesRausschreibenLinkSachsencacher("https://sachsencacher.de/de-nw/geocache/GC3EV6P");

		// SachsencacherLogEinleser.logLinksFuerSachsencacherLinkGenerieren("GCPDEK");

		kridlistBearbeitung();

//		String eingabeStr = "4 5 14 3 1 3 8 5 6 9 14 4 5 19 20 4 21 2 5 9 :\r\n" + "\r\n"
//				+ " 13 21 6 22 13 21 22 18 13 8 1 4 22 18 22 18 13 8 22 18 13 8 13 22 6 13 5 17 22 9 \r\n" + "\r\n"
//				+ " 22 8 18 22 25 22 13 1 4 22 18 22 18 13 8 23 9 22 18 21 6 22 13 21 8 18 22 25 22 13";
//		System.out.println(new Zahl2Buchstabe().getBuchstaben4Zahl(eingabeStr));
	}

	/**
	 * Fuer den richtigen Aufruf braucht es das hier:
	 * 
	 * @throws IOException
	 */
	private static void kridlistBearbeitung() throws IOException
	{
		KridlistBearbeiter k = new KridlistBearbeiter();
		k.einlesen();
	}

	private static void LogstrukturenLesenMitPMODatei()
	{
		PMOListenErsteller.pmoListAusDateiEinlesen();
		PrintWriter writer = SachsencacherLogsStruktur.oeffneWriter(SachsencacherLogEinleser.SachsencacherLogsDatei);
		int i = 0;
		SachsencacherLogEinleser.initDriver();

		for (String gcCode : PMOListenErsteller.pmoCachesList)
		{

			SachsencacherLogsStruktur struktur = SachsencacherLogEinleser
					.logLinksFuerSachsencacherLinkGenerieren(gcCode);
			struktur.schreibeLogStruktur(writer);
			i++;
			if (i % 10 == 0)
			{
				System.out.println("Logs einlesen: " + i);
			}

		}
		SachsencacherLogEinleser.quitDriver();

		writer.close();
	}
}
