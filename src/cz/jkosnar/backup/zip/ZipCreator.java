package cz.jkosnar.backup.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.jkosnar.backup.sync.SyncTools;

/**
 * Processes the zip archives creation. Recursively searches the source
 * directory tree up to given depth. For every directory or file found creates
 * password protected non-solid archive in a given location.
 */
public class ZipCreator {

	private List<File> toZip;

	/**
	 * Creates the archives with additional system.out messages and possibility
	 * to handle the "reuse" parameter.
	 * 
	 * @param sevenZipLocation
	 * @param source
	 * @param destination
	 * @param reuse
	 * @param zippingDepth
	 * @param archivePass
	 * @throws Exception
	 */
	public void createZip(String sevenZipLocation, File source, File destination, boolean reuse, int zippingDepth, String archivePass) throws Exception {
		long millisStart = System.currentTimeMillis();

		System.out.println(
				"Zipping started...\nSource: " + source.getAbsolutePath() + "\nDestination: " + destination.getAbsolutePath() + "\nDepth: " + zippingDepth);

		// clean the destination directory, this is the only necessary thing -
		// the zipping command stays the same
		if (!reuse) {
			SyncTools.ensureEmptyDestination(destination);
		}

		List<File> zippingResults = zip(sevenZipLocation, source, destination, zippingDepth, archivePass);

		if (reuse) {
			SyncTools.deleteUnlistedFiles(zippingResults, destination);
		}

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
	public List<File> zip(String sevenZipLocation, File source, File destination, int zippingDepth, String archivePass) throws Exception {
		List<File> zippingResults = new ArrayList<>();
		toZip = new ArrayList<>();

		recursivelyFillZipLocations(source, 0, zippingDepth);

		int sourcePathLength = source.getAbsolutePath().length();
		for (File f : toZip) {

			System.out.println(f.getAbsolutePath());

			String relativePath = f.getAbsolutePath().substring(sourcePathLength);
			String srcFileRelativePath = source.getAbsolutePath() + relativePath;
			String tmpFileRelativePath = destination.getAbsolutePath() + relativePath;

			ProcessBuilder pb = new ProcessBuilder(sevenZipLocation, "u", "-mx0", "-mhe", "-uq0", "\"" + tmpFileRelativePath + ".7z\"",
					"\"" + srcFileRelativePath + "\"", "-p" + archivePass, "-mmt");
			zippingResults.add(new File(tmpFileRelativePath + ".7z"));

			pb.inheritIO();
			Process p = pb.start();
			p.waitFor();
			p.destroy();
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
