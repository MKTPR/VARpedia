package sample;

import java.util.logging.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * FileSystem aids the tracking and management of directories created and used
 * by the system.
 */
public class FileSystem {
	private static final Logger LOGGER = Logger.getLogger(FileSystem.class.getName());
	private static final FileSystem fileSystemInstance = new FileSystem();
	private String workingPath;
	private String tempPath;
	private String creationsPath;

	private FileSystem() {
		workingPath = Paths.get("").toAbsolutePath().toString();
		tempPath = workingPath + "/temp";
		creationsPath = workingPath + "/creations";
	}

	public static FileSystem getFileSystem() {
		return fileSystemInstance;
	}

	public void createTempDirectory() {
		try {
			if (Files.notExists(Paths.get(tempPath))) {
				Files.createDirectories(Paths.get(tempPath));
			}
		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, exception.toString(), exception);
		}
	}

	public void createCreationsDirectory() {
		try {
			if (Files.notExists(Paths.get(creationsPath))) {
				Files.createDirectories(Paths.get(creationsPath));
			}
		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, exception.toString(), exception);
		}

	}
	
	/**
	 * Get a list of files and folders that are contained within the creation directory
	 * @return String[] A list of files and directories within the creations directory
	 */
	public String[] getCreationDirectoryContents() {
		File f = new File(creationsPath);
		return f.list();
	}
}
