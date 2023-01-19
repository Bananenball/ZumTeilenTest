package geo.kridlist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import geo.kridlist.enums.LogTypEnum;
import geo.kridlist.util.KridlistHelper;
import geo.kridlist.util.LogStruktur;
import geo.kridlist.util.SachsencacherLogsStruktur;
import io.github.bonigarcia.wdm.WebDriverManager;

public class SachsencacherLogEinleser
{

	// TODO einfuegen
	public static final String SachsencacherLogsDatei = "Logs.txt";

	public static WebDriver driver;

	private static boolean isGCAngemeldet = false;

	public static void initDriver()
	{
		WebDriverManager.chromedriver().setup();

		driver = new ChromeDriver();
	}

	public static void quitDriver()
	{
		driver.quit();

	}

	private static void waitForLoad(WebDriver driver)
	{
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>()
		{
			public Boolean apply(WebDriver driver)
			{
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(9000));
		wait.until(pageLoadCondition);
	}

	public static SachsencacherLogsStruktur logLinksFuerSachsencacherLinkGenerieren(String gcCode)
	{
		String link = "https://sachsencacher.de/de-nw/geocache/" + gcCode;

		driver.get(link);
		String content = driver.getPageSource();
		SachsencacherLogsStruktur sachsencacherLogsStruktur;
		try
		{
			sachsencacherLogsStruktur = new SachsencacherLogsStruktur(content, gcCode);
		}
		catch (NumberFormatException e)
		{
			// nochmal versuchen
			try
			{
				content = driver.getPageSource();
				sachsencacherLogsStruktur = new SachsencacherLogsStruktur(content, gcCode);
			}
			catch (NumberFormatException e2)
			{
				// nochmal versuchen
				try
				{
					content = driver.getPageSource();
					sachsencacherLogsStruktur = new SachsencacherLogsStruktur(content, gcCode);
				}
				catch (NumberFormatException e3)
				{
					// nochmal versuchen
					try
					{
						Thread.sleep(9000);
						content = driver.getPageSource();
						sachsencacherLogsStruktur = new SachsencacherLogsStruktur(content, gcCode);
					}
					catch (NumberFormatException | InterruptedException e4)
					{
						throw new RuntimeException(e4);
					}

				}
			}

		}
		List<LogStruktur> entfernenListe = new ArrayList<>();
//	sachsencacherLogsStruktur.sysoLogLinks();
		// System.out.println(content);
		for (LogStruktur l : sachsencacherLogsStruktur.logLinks)
		{
			try
			{
				setzeLogTypUndLogText(l);
			}
			catch (NoSuchElementException e)
			{
				// falls archivierter Log-Eintrag: entfernen
				entfernenListe.add(l);
			}
		}
		sachsencacherLogsStruktur.logLinks.removeAll(entfernenListe);

		return sachsencacherLogsStruktur;

		// Mit HTMLUnit geht es wohl nicht...
		// WebClient webClient = new WebClient(BrowserVersion.CHROME);
		//
		// try {
		// HtmlPage page = webClient.getPage(link);
		// WebResponse response = page.getWebResponse();
		// String content = response.getContentAsString();
		// System.out.println(content);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// webClient.close();
	}

	public static void setzeLogTypUndLogText(LogStruktur logstruktur)
	{
		String link = logstruktur.logLink;

		driver.get(link);

		if (!isGCAngemeldet)
		{
//			WebElement cookieElement = driver
//					.findElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowallSelection"));
//			cookieElement.click();
			new WebDriverWait(driver, Duration.ofMillis(10000)).until(ExpectedConditions
					.elementToBeClickable(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowallSelection")))
					.click();

			WebElement elementUsername = driver.findElement(By.id("UsernameOrEmail"));
			elementUsername.sendKeys("Birnenhocker");

			WebElement elementPasswort = driver.findElement(By.id("Password"));
			elementPasswort.sendKeys("Birnenhocker");

			// WebElement submitButton = driver.findElement(By.id("SignIn"));

			new WebDriverWait(driver, Duration.ofMillis(10000))
					.until(ExpectedConditions.elementToBeClickable(By.id("SignIn"))).click();

//			submitButton.click();
			isGCAngemeldet = true;
		}

		String content = driver.getPageSource();

		if (content.contains("Du kannst keine archivierten Logeinträge lesen."))
		{
			throw new NoSuchElementException("archivierter Logeintrag");
		}

		// System.out.println(content);
//		boolean markerFoundIt = false;
		boolean markerTreffer = false;
//		LogTypEnum logTyp;
		for (LogTypEnum logTypEnum : LogTypEnum.values())
		{
			if (content.contains(logTypEnum.getTextInGcCom()) && content.contains(logTypEnum.getBildLink()))
			{
				markerTreffer = true;
//				logTyp = logTypEnum;
				logstruktur.type = logTypEnum;
			}
		}
		if (markerTreffer == false)
		{
			System.out.println(content);
			System.out.println("kein Logtyptreffer");
			throw new RuntimeException("kein Treffer beim Logtyp");
		}
		logstruktur.logtext = KridlistHelper.getTeilString(content,
				"<span id=\"ctl00_ContentBody_LogBookPanel1_LogText\">", "</span>");

	}

	public static List<SachsencacherLogsStruktur> leseGesamtLogStrukturen()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(SachsencacherLogsDatei));

			String line;
			List<SachsencacherLogsStruktur> strukturListe = new ArrayList<>();
			SachsencacherLogsStruktur struktur;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith("GC"))
				{
					String[] stringarray = line.split(":");
					String gcCode = stringarray[0];
					int loganzahl = Integer.valueOf(stringarray[1]);
					struktur = new SachsencacherLogsStruktur(gcCode, loganzahl);

					boolean weiterlesen = true;
					while (weiterlesen)
					{
						line = br.readLine();

						if (!line.equals(LogStruktur.ENDEMARKER + gcCode))
						{
							struktur.logLinks.add(leseEinzelneLogStruktur(line, br));
						}
						else
						{
							strukturListe.add(struktur);
							weiterlesen = false;
						}
					}
				}
				else
				{
					if (StringUtils.isNotBlank(line))
					{
						throw new RuntimeException("Fehler: kein GC vorn gefunden!");
					}
				}
			}
			br.close();
			return strukturListe;
		}
		catch (IOException e)
		{

			throw new RuntimeException("Fehler Lesen PMODAtei");
		}
	}
	
	public static SachsencacherLogsStruktur getPassendeGesamtStruktur(List<SachsencacherLogsStruktur> strukturListe,String gcCode) {
		for(SachsencacherLogsStruktur struktur : strukturListe) {
			if(struktur.gcCode.equals(gcCode)) {
				return struktur;
			}
		}
		throw new RuntimeException("Kein Treffer zum GCCODE: "+gcCode);
	}

	/**
	 * 1. Zeile wird mitgegeben, damit sie in der aufrufenden Methode schon
	 * analysiert werden kann
	 * 
	 * @param zeile die vorher ausgelesene Zeile
	 * @param br    der Reader
	 * @return die gelesene LogStruktur
	 * @throws IOException .
	 */
	private static LogStruktur leseEinzelneLogStruktur(String zeile, BufferedReader br) throws IOException
	{
		String line;
		LogStruktur log = new LogStruktur();

		line = zeile;
		log.glCode = line;

		line = br.readLine();
		log.logLink = line;

		line = br.readLine();
		log.logId = line;

		line = br.readLine();
		log.logDatum = line;

		line = br.readLine();
		log.type = LogTypEnum.getLogTypEnumForTextInGcCom(line);

		line = br.readLine();
		log.finder = line;

		String logStr = "";
		// bis zum Endemarker gehoeren alle Zeilen zum Log
		while (!LogStruktur.ENDEMARKER.equals(line = br.readLine()))
		{
//			if(!line.equals(line.replaceAll("(<img src=)[^&]*(align=\"middle\">)", ""))){
//				System.out.println(line.replaceAll("(<img src=)[^&]*(align=\"middle\">)", ""));
//				System.out.println(line);
//			}
			//Bilder entfernen, vermutlich nicht notwendig. mit regex vorn
			//line=line.replaceAll("(<img src=)[^&]*(align=\"middle\">)", "");
			line=line.replace("<", "&lt;");
			line = line.replace(">","&gt;");
			line = line.replace("&nbsp;"," ");
			//line = htmlUmlaute(line); wird wohl nicht benoetigt
			// leere Zeilen kann man auch einfach weglassen...
			if (StringUtils.isNotBlank(line))
			{
				logStr = logStr + line + "\n";
			}
		}
		log.logtext = logStr;
		return log;
	}

	//Wird wohl nicht benoetigt
    //Methode für Sonderzeichen/Umlaute, mit &auml; oder so geht es irgendwie nicht richtig... große fehlen noch
    private static String htmlUmlaute(String zeile){
        zeile = zeile.replace("ä", "ae");
        zeile = zeile.replace("ö", "oe");
        zeile = zeile.replace("ü", "ue");
        zeile = zeile.replace("ß", "ss");
        return zeile;
    }

}
