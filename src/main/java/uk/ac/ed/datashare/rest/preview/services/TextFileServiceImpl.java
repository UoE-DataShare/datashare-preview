package uk.ac.ed.datashare.rest.preview.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import uk.ac.ed.datashare.rest.preview.domains.FileInfo;
import uk.ac.ed.datashare.rest.preview.domains.Preview;

@Service
public class TextFileServiceImpl implements TextFileService {

	private static final Logger logger = LoggerFactory.getLogger(TextFileServiceImpl.class);
	private int maxNumOfRecordsToPreview = 20;
	private final AtomicLong counter = new AtomicLong();

	@Override
	public ResponseEntity<Preview> previewTextFile(FileInfo fileInfo) throws IOException {
			String msg = "";
		String fullFileUrl = "";

		try {
			fullFileUrl = fileInfo.getFileUrl();
			URL url = new URL(fullFileUrl);

			logger.debug("fullFileUrl: " + fullFileUrl);

			String encoding = "UTF-8"; // Default
			try (InputStream urlOpenStreamForGettingEncoding = url.openStream()) {
				encoding = detectEncoding(urlOpenStreamForGettingEncoding);
				encoding = encoding != null ? encoding : "UTF-8";
			}

			logger.debug("encoding: " + encoding);

			try (InputStream urlOpenStreamForData = url.openStream()) {
				@SuppressWarnings("resource")
				Scanner scnr = new Scanner(urlOpenStreamForData, encoding);
				// read from your scanner
				int lineNumber = 1;
				StringBuffer sb = new StringBuffer();

				while (scnr.hasNextLine() && lineNumber <= maxNumOfRecordsToPreview + 5) {
					String line = scnr.nextLine();
					line += "\n";
					sb.append(line);
					lineNumber++;
				}
				// Set Scanner to null
				scnr = null;

				try (Reader reader = new InputStreamReader(
						new BOMInputStream(new ByteArrayInputStream(sb.toString().getBytes())), encoding);
						CSVParser parser = new CSVParser(reader,
								CSVFormat.EXCEL.withHeader().withSkipHeaderRecord(false));) {

					List<String> headers = parser.getHeaderNames();
					String headerString = "";
					for (String header : headers) {
						int colNumber = 1;
						headerString += header.replace(",", "&#44;");
						if (colNumber < headers.size()) {
							headerString += ",";
						}
						colNumber++;
					}
					msg += headerString + "\n";

					int recordNumber = 1;
					while (recordNumber <= maxNumOfRecordsToPreview) {
						for (final CSVRecord record : parser) {
							String rowString = "";
							int colNumber = 1;
							for (String col : record) {
								rowString += col.replace(",", "&#44;");
								if (colNumber < record.size()) {
									rowString += ",";
								}
								colNumber++;
							}
							msg += rowString + "\n";
							recordNumber++;
						}
					}
				}
			}
		} catch (Exception ex) {
			// there was some connection problem, or the file did not exist on the server,
			// or your URL was not in the right format.
			// think about what to do now, and put it here.
			ex.printStackTrace(); // for now, simply output it.
		}

		logger.debug("Response data msg is NOT EMPTY: " + !msg.isEmpty());
		return new ResponseEntity<>(new Preview(counter.incrementAndGet(), msg, fullFileUrl), HttpStatus.OK);
	}

	// Java: How To Autodetect The Charset Encoding of A
	// Text File and Remove Byte Order Mark (BOM).
	// https://fahri.id/posts/java-how-to-autodetect-the-charset/
	private String detectEncoding(InputStream inputStream) {
		try (BOMInputStream bomInputStream = new BOMInputStream(new BufferedInputStream(inputStream),
				ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE,
				ByteOrderMark.UTF_32LE)) {

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
