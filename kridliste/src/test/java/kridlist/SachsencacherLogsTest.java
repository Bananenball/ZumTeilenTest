package kridlist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.jupiter.api.Test;

import geo.kridlist.SachsencacherLogEinleser;
import geo.kridlist.util.LogStruktur;
import geo.kridlist.util.SachsencacherLogsStruktur;

class SachsencacherLogsTest
{

	@Test
	void testLogDateiEinlesen()
	{
		List<SachsencacherLogsStruktur> strukturList = SachsencacherLogEinleser.leseGesamtLogStrukturen();
//		for (SachsencacherLogsStruktur struktur : strukturList)
//		{
//struktur.sysoLogLinks();
//		}
		assertEquals(strukturList.size(), 759);
	}

}
