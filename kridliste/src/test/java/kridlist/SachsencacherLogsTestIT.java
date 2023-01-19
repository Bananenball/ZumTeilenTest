package kridlist;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.jupiter.api.Test;

import geo.kridlist.SachsencacherLogEinleser;
import geo.kridlist.util.LogStruktur;
import geo.kridlist.util.SachsencacherLogsStruktur;

class SachsencacherLogsTestIT {

	@Test
	void test() throws ScriptException {
//        new ScriptEngineManager().getEngineByName("js")
//        .eval("print('Hello from Java\\n');");
//		System.out.println("Groesse: " + new ScriptEngineManager().getEngineFactories().size());
//     for (ScriptEngineFactory se : new ScriptEngineManager().getEngineFactories()) {
//         System.out.println("se = " + se.getEngineName());
//         System.out.println("se = " + se.getEngineVersion());
//         System.out.println("se = " + se.getLanguageName());
//         System.out.println("se = " + se.getLanguageVersion());
//         System.out.println("se = " + se.getNames());
//     }
		
		//TODO: ab hier wieder rein
		SachsencacherLogEinleser.initDriver();
		SachsencacherLogsStruktur struktur = SachsencacherLogEinleser
				.logLinksFuerSachsencacherLinkGenerieren("GC3EV6P");
		struktur.sysoLogLinks();
		
	for(LogStruktur logStruktur : struktur.logLinks) {
	SachsencacherLogEinleser.setzeLogTypUndLogText(logStruktur);
	logStruktur.ausgabe();

	}
//		SachsencacherLogEinleser.pruefeLinkTest("https://www.geocaching.com/seek/log.aspx?code=GLWDB1W3");
		
		SachsencacherLogEinleser.quitDriver();
	}

}
