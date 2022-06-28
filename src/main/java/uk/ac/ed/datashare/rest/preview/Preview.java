package uk.ac.ed.datashare.rest.preview;

public class Preview {

	private long id;
	private String content;
	private String fileUrl;
	
	
	public Preview(long id, String content, String fileUrl) {
		this.id = id;
		this.content = content;
		this.fileUrl = fileUrl;
	}

	public Preview(long id, String content) {
		this.id = id;
		this.content = content;
	}


	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public String getFileUrl() {
		return fileUrl;
	}

}
