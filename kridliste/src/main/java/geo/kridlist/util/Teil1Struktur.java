package geo.kridlist.util;

import geo.kridlist.enums.CachetypEnum;

public class Teil1Struktur {

	// Koordinaten im Format 53.4345,7.34646
	public String koordinatenLat;
	public String koordinatenLng;

	// z.B. m11 (unavailable u vorn, owned mit o, je zusätzlich)
	public String verschluesselterName;
	String erstesZeichenVerschlName;
	public String restVerschlName;

	private String typeIconStr;
	public CachetypEnum typ;

	public String titel;
	// archiviert oder temp. disabled
	public boolean isUnavailable = false;

	public boolean isArchiviert = false; // (kommen doppelt vor)
	public boolean isTempDisabled = false; // doppelt
	public boolean isOwned = false;

	String teilzeile;
	int beginIndex;
	int endIndex;

	public Teil1Struktur(Teil1Struktur t) {
		this.koordinatenLat = t.koordinatenLat;
		this.koordinatenLng = t.koordinatenLng;
		this.verschluesselterName = t.verschluesselterName;
		this.erstesZeichenVerschlName = t.erstesZeichenVerschlName;
		this.restVerschlName = t.restVerschlName;
		this.titel = t.titel;
		this.isUnavailable = t.isUnavailable;
		this.isArchiviert = t.isArchiviert;
		this.isTempDisabled = t.isTempDisabled;
		this.isOwned = t.isOwned;
		this.teilzeile = t.teilzeile;
		this.beginIndex = t.beginIndex;
		this.endIndex = t.endIndex;
		this.typ = t.typ;
	}

	public Teil1Struktur(String zeile) {
		teilzeile = zeile;

		if (teilzeile.contains("opacity:0.3")) {
			isTempDisabled = true;
		}
		
		koordinatenLat=setzeTeilString("var latlng = new L.LatLng(", ",");
		koordinatenLng=setzeTeilString(",",")");
		verschluesselterName=setzeTeilString(";"," =");
		erstesZeichenVerschlName=verschluesselterName.substring(0,1);
		restVerschlName=verschluesselterName.substring(1);
		
		if(erstesZeichenVerschlName.equals("u")) {
			isUnavailable=true;
			if(!teilzeile.contains("opacity:0.3")) {
				isArchiviert=true;
			}
		}
		else if(erstesZeichenVerschlName.equals("o")) {
			isOwned=true;
		}
		typeIconStr=setzeTeilString("icon:",",");
		typ=CachetypEnum.getCachetyp4Icon(typeIconStr);
		
		titel=setzeTeilString("title:\"","\", draggable:");
	
	}
	
	public String setzeTeilString(String startsuchstr, String endsuchstr) {
		teilzeile=teilzeile.substring(endIndex);
		beginIndex=KridlistHelper.getStartindexzuString(teilzeile, startsuchstr);
		teilzeile=teilzeile.substring(beginIndex);
		beginIndex=0;
		endIndex=KridlistHelper.getEndindexzuString(teilzeile, endsuchstr);
		return teilzeile.substring(beginIndex,endIndex);
	}

}
