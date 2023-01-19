package geo.kridlist.util;

import java.util.ArrayList;
import java.util.List;

public class GesamtStruktur extends Teil1Struktur {
	public String cacheLink;
	public String gcCode; // GC123F1 oder so

	public String owner;

	public String dWertung;
	public String tWertung;

	public String groesse;
	public String favPoints;

	// elevation weglassen

	public String funddatum; // von MrKrid
	
	public String gpxWegpunkt;
	
	public List<Attribut> attributeListAusSachsencacher = new ArrayList<>();

	public GesamtStruktur(Teil1Struktur t) {
		super(t);
	}
	
	public void setzeTeil2Str(Teil2Struktur t) {
		this.cacheLink=t.cacheLink;
		this.gcCode=t.gcCode;
		this.owner=t.owner;
		this.dWertung=t.dWertung;
		this.tWertung=t.tWertung;
		this.groesse=t.groesse;
		this.favPoints=t.favPoints;
		this.funddatum=t.funddatum;
	}
}
