
package geo.kridlist.util;

public class Attribut {

	public int attributeId;
	/** positiv gesetzt==true, negativ gesetzt==false */
	public boolean attributPositivGesetzt;
	public String beschreibung;
	
	
	public Attribut(int attributeId, Boolean attributPositivGesetzt, String beschreibung) {
		this.attributeId = attributeId;
		this.attributPositivGesetzt = attributPositivGesetzt;
		this.beschreibung = beschreibung;
	}

	//id,attributPositiv,beschreibung
	public static Attribut getAttribut4String(String zeile) {
		String[] werte= zeile.split(",");
		try {
		return new Attribut(Integer.parseInt(werte[0]),Boolean.parseBoolean(werte[1]),werte[2]);
		}
		catch(NumberFormatException e) {
			System.out.println(zeile);
			throw e;
		}
	}
	
}
