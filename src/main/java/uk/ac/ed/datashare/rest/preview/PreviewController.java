package uk.ac.ed.datashare.rest.preview;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;


@RestController
public class PreviewController {

	private int maxNumOfLinesToPreview = 13;
	private final AtomicLong counter = new AtomicLong();



	@CrossOrigin
	@PostMapping(path = "/preview",
	consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Preview> previewPost(@RequestBody FileInfo fileInfo) {
		String msg = "";
		String fullFileUrl = "";

		try {
			fullFileUrl = fileInfo.getFileUrl();
			URL url = new URL(fullFileUrl);

			String encoding = detectEncoding(url.openStream());
			encoding = encoding != null ? encoding : "UTF-8";

			@SuppressWarnings("resource")
			Scanner scnr = new Scanner(url.openStream(), encoding);
			// read from your scanner
			int lineNumber = 1;
			while(scnr.hasNextLine() && lineNumber <= maxNumOfLinesToPreview){
				String line = scnr.nextLine();
				msg += line + "\n";
				lineNumber++;

			}


		}
		catch(IOException ex) {
			// there was some connection problem, or the file did not exist on the server,
			// or your URL was not in the right format.
			// think about what to do now, and put it here.
			ex.printStackTrace(); // for now, simply output it.
		}

		return new ResponseEntity<>(new Preview(counter.incrementAndGet(), msg, fullFileUrl), HttpStatus.OK);
	}

	// From https://howtodoinjava.com/java/regex/java-clean-ascii-text-non-printable-chars/
	private String cleanTextContent(String text) 
	{
		// strips off all non-ASCII characters
		text = text.replaceAll("[^\\x00-\\x7F]", "");

		// erases all the ASCII control characters
		//	    text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

		// removes non-printable characters from Unicode
		//	    text = text.replaceAll("\\p{C}", "");

		return text.trim();
	}

	// Java: How To Autodetect The Charset Encoding of A 
	// Text File and Remove Byte Order Mark (BOM).
	// https://fahri.id/posts/java-how-to-autodetect-the-charset/


	private String detectEncoding(InputStream inputStream) {
		try {
			BOMInputStream bomInputStream = new BOMInputStream(new BufferedInputStream(inputStream),
					ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);

			CharsetDetector detector = new CharsetDetector();
			detector.setText(bomInputStream);

			CharsetMatch charsetMatch = detector.detect();
			System.out.println("CHARSET MATCH : " + charsetMatch.getName());

			return charsetMatch.getName();
		} catch (Exception e) {
			return "UTF-8";
		}
	}
}
