package geo.kridlist.util;

import geo.kridlist.enums.CachetypEnum;

public class Teil2Struktur {

	// z.B. m11 (unavailable u vorn, owned mit o, je zusätzlich)
	public String verschluesselterName;
	String erstesZeichenVerschlName;
	public String restVerschlName;

	public String cacheLink;
	public String gcCode; // GC123F1 oder so

	public String titel;

	public String owner;

	public String dWertung;
	public String tWertung;

	public String groesse;
	public String favPoints;

	// elevation weglassen

	public String funddatum; // von MrKrid

	// archiviert oder temp. disabled
	public boolean isUnavailable = false;

	public boolean isUnavailableUVorn = false;

	public boolean isArchiviert = false; // (kommen doppelt vor)
	public boolean isTempDisabled = false; // doppelt

	String teilzeile;
	int beginIndex;
	int endIndex;

	public Teil2Struktur(String zeile) {
		teilzeile = zeile.trim();

		verschluesselterName = setzeTeilString("", ".bindPopup");
		erstesZeichenVerschlName = verschluesselterName.substring(0, 1);
		restVerschlName = verschluesselterName.substring(1);

		if (erstesZeichenVerschlName.equals("u")) {
			isUnavailableUVorn = true;
		}

		cacheLink = setzeTeilString("href=\'", "\' target=");
		gcCode = setzeTeilString(">", "<");
		setzeTeilString(">", "<");// Wird nicht benoetigt

		titel = setzeTeilString(">", " <");

		owner = setzeTeilString(">by: ", " <");
		dWertung = setzeTeilString("Difficulty: ", ", ");
		tWertung = setzeTeilString("Terrain: ", "<");
		groesse = setzeTeilString("Size: ", ", ");
		favPoints = setzeTeilString("Fav Points: ", "<");
		funddatum = setzeTeilString("You found this cache on: ", "<");
		String gcCodeverglStr = setzeTeilString("gsakButtons(\'", "\'");
		if (!gcCodeverglStr.equals(gcCode)) {
			throw new RuntimeException("Fehler beim GCCode");
		}
		if (zeile.contains("Archived")) {
			isArchiviert = true;
			isUnavailable = true;
		} //
		else if (zeile.contains("Temporarily Disabled")) {
			isTempDisabled = true;
			isUnavailable = true;
		}

	}

	public String setzeTeilString(String startsuchstr, String endsuchstr) {
		teilzeile = teilzeile.substring(endIndex);
		beginIndex = KridlistHelper.getStartindexzuString(teilzeile, startsuchstr);
		teilzeile = teilzeile.substring(beginIndex);
		beginIndex = 0;
		endIndex = KridlistHelper.getEndindexzuString(teilzeile, endsuchstr);
		return teilzeile.substring(beginIndex, endIndex);
	}

}
