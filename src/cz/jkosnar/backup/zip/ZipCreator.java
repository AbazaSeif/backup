package cz.jkosnar.backup.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import cz.jkosnar.backup.sync.SyncTools;

/**
 * Processes the zip archives creation. Recursively searches the source directory tree up to given depth. For every directory or file found creates password
 * protected non-solid archive in a given location.
 */
public class ZipCreator {

	private static final String TIMESTAMP_STORAGE_FILE = ".sync";

	private List<File> toZip;

	/**
	 * Creates the archives with additional system.out messages and possibility to handle the "reuse" parameter.
	 * 
	 * @param sevenZipLocation
	 * @param source
	 * @param destination
	 * @param reuse
	 * @param zippingDepth
	 * @param archivePass
	 * @throws Exception
	 */
	public void createZip(String sevenZipLocation, File source, File destination, int zippingDepth, String archivePass, boolean reuse, String workingDir,
			int compression) throws Exception {
		long millisStart = System.currentTimeMillis();

		System.out.println(
				"Zipping started...\nSource: " + source.getAbsolutePath() + "\nDestination: " + destination.getAbsolutePath() + "\nDepth: " + zippingDepth);

		if (!reuse) {
			FileUtils.deleteDirectory(destination);
			FileUtils.forceMkdir(destination);
		}

		List<File> zippingResults = zip(sevenZipLocation, source, destination, zippingDepth, archivePass, workingDir, compression);

		if (reuse) {
			List<String> deletedFiles = SyncTools.deleteUnlistedFiles(zippingResults, destination);
			for (String s : deletedFiles) {
				System.out.println("Deleting removed file ... " + s);
			}
		}

		// store the "timestamp" file of the last sync
		FileUtils.touch(new File(destination, TIMESTAMP_STORAGE_FILE));
		System.out.println("Created new timestamp file ... " + TIMESTAMP_STORAGE_FILE);

		// show some statistics about zipping
		long millis = System.currentTimeMillis() - millisStart;
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;

		String time = String.format("%02d:%02d:%02d", hour, minute, second);
		System.out.println("Zipping took: " + time);
	}

	/**
	 * Creates the archives.
	 * 
	 * @param sevenZipLocation
	 * @param source
	 * @param destination
	 * @param zippingDepth
	 * @param archivePass
	 * @return
	 * @throws Exception
	 */
	public List<File> zip(String sevenZipLocation, File source, File destination, int zippingDepth, String archivePass, String workDir, int compression)
			throws Exception {
		List<File> zippingResults = new ArrayList<>();

		toZip = new ArrayList<>();
		recursivelyFillZipLocations(source, 0, zippingDepth);

		int sourcePathLength = source.getAbsolutePath().length();
		long lastBackupTimestamp = SyncTools.getModificationTimestampOfSubtree(new File(destination, TIMESTAMP_STORAGE_FILE));

		for (File f : toZip) {

			String relativePath = f.getAbsolutePath().substring(sourcePathLength);
			String srcFileRelativePath = source.getAbsolutePath() + relativePath;
			String tmpFileRelativePath = destination.getAbsolutePath() + relativePath;

			long toZipModificaitonTimestamp = SyncTools.getModificationTimestampOfSubtree(f);
			if (toZipModificaitonTimestamp >= lastBackupTimestamp) {
				List<String> processDefinition = new ArrayList<String>();
				processDefinition.add(sevenZipLocation);
				processDefinition.add("u"); // update mode
				processDefinition.add("-mx" + compression); // compression
				processDefinition.add("-mhe"); // encrypt headers
				processDefinition.add("-uq0"); // update with removal
				processDefinition.add("\"" + tmpFileRelativePath + ".7z\"");
				processDefinition.add("\"" + srcFileRelativePath + "\"");
				processDefinition.add("-p" + archivePass);
				processDefinition.add("-mmt"); // multi-threading
				processDefinition.add("-ms=off"); // non-solid archive (better update performance)

				// if the work dir is not specified and no compression is used 7zip
				// will create the file directly at given location
				if (!"".equals(workDir)) {
					processDefinition.add("-w" + workDir);
				}

				ProcessBuilder pb = new ProcessBuilder(processDefinition);

				pb.inheritIO();
				Process p = pb.start();
				p.waitFor();
				p.destroy();

			} else {
				System.out.println("Skipping unchanged file ... " + tmpFileRelativePath + ".7z");
			}
			zippingResults.add(new File(tmpFileRelativePath + ".7z"));
		}

		return zippingResults;
	}

	/**
	 * Recurses the directory tree up to given depth.
	 * 
	 * @param root
	 * @param depth
	 * @param zippingDepth
	 */
	private void recursivelyFillZipLocations(File root, int depth, int zippingDepth) {
		depth++;

		File[] files = root.listFiles();

		if (files.length == 0) {
			// toZip.add(root); //do not include empty directories
		} else {
			for (File f : files) {
				if (f.isDirectory()) {
					if (depth < zippingDepth) {
						recursivelyFillZipLocations(f, depth, zippingDepth);
					} else {
						toZip.add(f);
					}
				} else {
					toZip.add(f);
				}

			}
		}
	}

}
