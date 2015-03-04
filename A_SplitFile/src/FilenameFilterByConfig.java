import java.io.File;
import java.io.FilenameFilter;


public class FilenameFilterByConfig implements FilenameFilter {

	private String suffix;
	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(suffix);
	}

	public FilenameFilterByConfig(String suffix) {
		this.suffix = suffix;
	}
	

}
