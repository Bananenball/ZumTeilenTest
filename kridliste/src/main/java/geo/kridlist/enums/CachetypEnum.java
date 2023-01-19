package geo.kridlist.enums;

public enum CachetypEnum {

	TRADI("Traditional Cache"),//
	MULTI("Multi Cache"),
	MYSTERY("Unknown Cache"),
	EVENT("EVENT"),
	EARTH_CACHE("Earth Cache"),
	WEBCAM("Webcam Cache"),
	WHERIGO("Wherigo"),
	LETTERBOX("Letterbox"),
	CITO("CITO Event"),
	GIGA("GIGA-Event"),
	VIRTUAL("Virtual Cache"),
	UNBEKANNT("Keins/Unavailable/owned?")
	;
	
	public final String name;
	
	private CachetypEnum(String name) {
		this.name=name;
	}
	
	public static CachetypEnum getCachetyp4Icon(String iconName) {
	switch(iconName) {
	case "T_f_icon":
				return TRADI;
	case "M_f_icon":
		return MULTI;
	case "U_f_icon":
		return MYSTERY;
	case "E_f_icon":
		return EVENT;
	case "R_f_icon":
		return EARTH_CACHE;
	case "W_f_icon":
		return WEBCAM;
	case "I_f_icon":
		return WHERIGO;
	case "B_f_icon":
		return LETTERBOX;
	case "C_f_icon":
		return CITO;
	case "J_f_icon":
		return GIGA;
	case "V_f_icon":
		return VIRTUAL;
	case "unavailable_icon":
	case "ownedicon":
		return UNBEKANNT;
		default:
			throw new RuntimeException("Unbekannter Icon-Name");
	
	}
	}}
