package geo.kridlist.enums;

public enum LogTypEnum {
	FOUND_IT("Found it", "/images/logtypes/2.png"),
	DID_NOT_FIND("Didn't find it", "/images/logtypes/3.png"),
	UPDATE_COORDINATES("Update Coordinates", "/images/logtypes/47.png"),
	WRITE_NOTE("Write note", "/images/logtypes/4.png"),
	PUBLISH_LISTING("Publish Listing", "/images/logtypes/24.png"),
	NEEDS_MAINTENANCE("Needs Maintenance", "/images/logtypes/45.png"),
	TEMP_DISABLE_LISTING("Temporarily Disable Listing", "/images/logtypes/22.png"),
	OWNER_MAINTENANCE("Owner Maintenance", "/images/logtypes/46.png"),
	ENABLE_LISTING("Enable Listing", "/images/logtypes/23.png"),
	ARCHIVE("Archive","/images/logtypes/5.png"),
	NEEDS_ARCHIVED("Needs Archived","/images/logtypes/7.png"),
	UNARCHIVE("Unarchive","/images/logtypes/12.png")
	//
	;

	String textInGcCom;
	String bildLinkEnde;

	LogTypEnum(String textInGcCom, String bildLink) {
		this.textInGcCom = textInGcCom;
		this.bildLinkEnde = bildLink;
	}

	public String getTextInGcCom() {
		return textInGcCom;
	}

	public String getBildLink() {
		return bildLinkEnde;
	}

	public static LogTypEnum getLogTypEnumForTextInGcCom(String text) {
		for (LogTypEnum logTypEnum : LogTypEnum.values()) {
			if (logTypEnum.textInGcCom.equals(text)) {
				return logTypEnum;
			}
		}
		throw new RuntimeException("Kein passender LogTypEnum gefunden fuer " + text);
	}
}
