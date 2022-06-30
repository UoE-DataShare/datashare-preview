package uk.ac.ed.datashare.rest.preview.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ed.datashare.rest.preview.domains.FileInfo;
import uk.ac.ed.datashare.rest.preview.domains.Preview;
import uk.ac.ed.datashare.rest.preview.services.TextFileService;

@RestController
public class PreviewController {
	
	 @Autowired 
	 private TextFileService textFileService;

	private static final Logger logger = LoggerFactory.getLogger(PreviewController.class);
	
	@CrossOrigin
	@PostMapping(path = "/preview", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Preview> previewPost(@RequestBody FileInfo fileInfo) throws IOException {
		return textFileService.previewTextFile(fileInfo);
	}
}
