package uk.ac.ed.datashare.rest.preview.services;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import uk.ac.ed.datashare.rest.preview.domains.FileInfo;
import uk.ac.ed.datashare.rest.preview.domains.Preview;

public interface TextFileService {
	
	public ResponseEntity<Preview> previewTextFile(FileInfo fileInfo) throws IOException;
}