package geo.kridlist.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class KridlistHelper {

	public static int getStartindexzuString(String gesamtString, String suchString) {
		return gesamtString.indexOf(suchString) + suchString.length();
	}

	public static int getEndindexzuString(String gesamtString, String suchString) {
		return gesamtString.indexOf(suchString);
	}

	public static boolean pruefeLink(String link) {
		try {
			URL url = new URL(link);
			InputStreamReader isr = new InputStreamReader(url.openStream());
			BufferedReader br = new BufferedReader(isr);
			String zeile;
			boolean markerGCPremium = false;
			boolean markerUpgrade = false;
			boolean markerPMO = false;
			while ((zeile = br.readLine()) != null) {
				// System.out.println(zeile);

				if (zeile.contains("Geocaching > Hide and Seek a Geocache > Premium Member Only Cache")) {
					markerPMO = true;
				}
				if (zeile.contains("Geocaching Premium")) {
					markerGCPremium = true;
				}
				if (zeile.contains("Upgrade")) {
					markerUpgrade = true;
				}
				if (zeile.contains("window.isCachePMO = 'No';")) {
					return false;

				}
				// unsichere Variante
				if (markerGCPremium || markerPMO || markerUpgrade) {
					return true;
				}
				// sicherere Variante
//				if (markerGCPremium && markerUpgrade) {
//					return true;
//				}
			}

			if (markerGCPremium != markerUpgrade) {
				throw new RuntimeException("Marker unterschiedlich");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	// Attribute erstellen
	public static List<Attribut> holeAttributeAusLinkSachsencacher(String link) {
		try {
			URL url = new URL(link);
			InputStreamReader isr = new InputStreamReader(url.openStream());
			BufferedReader br = new BufferedReader(isr);
			String zeile;
			List<Attribut> listeAttribute = new ArrayList<>();

			while ((zeile = br.readLine()) != null && !zeile.contains("<div class=\"card-content attributes\">")) {
            //nix, weiterlesen bis zur Attributeliste
			}
			
			int attributId = 0;
			while ((zeile = br.readLine()) != null && !zeile.contains("</div>")) {
				
				if (zeile.contains("/assets/images/attributes/")) {
					attributId++;
					//Sonderfall: Attribut 68 gibt es wohl momentan nicht! Daher überspringen
					if(attributId==68) {
						attributId++;
					}
					if (zeile.contains("-yes.png")) {
						Attribut attribut = new Attribut(attributId, true, getTeilString(zeile, "title=\"", "\" />"));
						listeAttribute.add(attribut);
					}
					if (zeile.contains("-no.png")) {
						Attribut attribut = new Attribut(attributId, false, getTeilString(zeile, "title=\"", "\" />"));
						listeAttribute.add(attribut);
					}
				}
			}
return listeAttribute;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	// Attribute erstellen und weiteres, methode holeAttributeAusLinkSachsencacher, TODO: alles einbauen und die obige ersetzen
	public static List<Attribut> holeAttributeAusLinkSachsencacher(String link) {
		try {
			URL url = new URL(link);
			InputStreamReader isr = new InputStreamReader(url.openStream());
			BufferedReader br = new BufferedReader(isr);
			String zeile;
			List<Attribut> listeAttribute = new ArrayList<>();

			while ((zeile = br.readLine()) != null && !zeile.contains("<div class=\"card-content attributes\">")) {
            //nix, weiterlesen bis zur Attributeliste
			}
			
			int attributId = 0;
			while ((zeile = br.readLine()) != null && !zeile.contains("</div>")) {
				
				if (zeile.contains("/assets/images/attributes/")) {
					attributId++;
					//Sonderfall: Attribut 68 gibt es wohl momentan nicht! Daher überspringen
					if(attributId==68) {
						attributId++;
					}
					if (zeile.contains("-yes.png")) {
						Attribut attribut = new Attribut(attributId, true, getTeilString(zeile, "title=\"", "\" />"));
						listeAttribute.add(attribut);
					}
					if (zeile.contains("-no.png")) {
						Attribut attribut = new Attribut(attributId, false, getTeilString(zeile, "title=\"", "\" />"));
						listeAttribute.add(attribut);
					}
				}
			}
return listeAttribute;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	

	// ohne Änderungen an der Zeile

	public static String getTeilString(String zeile, String startsuchstr, String endsuchstr) {
		int beginIndex = KridlistHelper.getStartindexzuString(zeile, startsuchstr);
		zeile = zeile.substring(beginIndex);
		beginIndex = 0;
		int endIndex = KridlistHelper.getEndindexzuString(zeile, endsuchstr);
		return zeile.substring(beginIndex, endIndex);
	}
	
	
	
	
	public static String getSachsencacherLink(String gcCode) {
		return new StringBuilder().append("https://sachsencacher.de/de-nw/geocache/").append(gcCode).toString();
	}

}
