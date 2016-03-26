package cz.jkosnar.backup.sync;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Utility class for operations with local files and directories.
 */
public class SyncTools {
	// todo, return back logging by returning a link of "deleted" or otherwise
	// processed files

	/**
	 * Deletes and creates the destination directory.
	 * 
	 * @param destination
	 * @throws IOException
	 */
	public static void ensureEmptyDestination(File destination) throws IOException {
		FileUtils.deleteDirectory(destination);
		FileUtils.forceMkdir(destination);
	}

	/**
	 * Deletes files from "dst" if they are not in a "validFiles" list. <br/>
	 * NOTICE: Leaves empty directories behind.
	 * 
	 * @param validFiles
	 * @param dst
	 * @return names of deleted files
	 */
	public static List<String> deleteUnlistedFiles(List<File> validFiles, File dst) {
		List<String> result = new ArrayList<>();
		List<File> dstFiles = new ArrayList<>(FileUtils.listFilesAndDirs(dst, TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY));

		for (File file : dstFiles) {
			if (!file.isDirectory()) {
				// TODO: This can be optimized
				if (!validFiles.contains(file)) {
					result.add(file.getAbsolutePath());
					file.delete();
				}
			}
		}
		return result;
	}

	/**
	 * Propagates the deletions from "src" to "dst". If the file or directory no
	 * longer exists in "src" it is deleted from "dst".
	 * 
	 * @param src
	 * @param dst
	 * @return names of deleted files
	 * @throws IOException
	 */
	public static List<String> propagateDeletions(File src, File dst) throws IOException {
		List<String> result = new ArrayList<>();
		List<File> dstFiles = new ArrayList<>(FileUtils.listFilesAndDirs(dst, TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY));
		Collections.sort(dstFiles);
		Collections.reverse(dstFiles);

		int dstPathLength = dst.getAbsolutePath().length();
		for (File f : dstFiles) {
			String dstFileRelativePath = f.getAbsolutePath().substring(dstPathLength);
			if (f.exists()) {
				File expectedSrcFile = new File(src.getAbsolutePath() + "\\" + dstFileRelativePath);
				if (!expectedSrcFile.exists()) {
					result.add(f.getAbsolutePath());
					f.delete();
				}
			}
		}
		return result;
	}

	/**
	 * Copies new files from "src" to "dst". Files that do not exist in "dst"
	 * are copied there from "src".
	 * 
	 * @param src
	 * @param dst
	 * @return dst names of copied files
	 * @throws IOException
	 */
	public static List<String> copyNew(File src, File dst) throws IOException {
		List<String> result = new ArrayList<>();
		List<File> srcFiles = new ArrayList<>(FileUtils.listFilesAndDirs(src, TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY));
		Collections.sort(srcFiles);
		Collections.reverse(srcFiles);

		int srcPathLength = src.getAbsolutePath().length();
		for (File f : srcFiles) {
			String srcFileRelativePath = f.getAbsolutePath().substring(srcPathLength);
			if (f.exists()) {
				File expectedDstFile = new File(dst.getAbsolutePath() + "\\" + srcFileRelativePath);
				if (!expectedDstFile.exists()) {
					if (!f.isDirectory()) {
						result.add(expectedDstFile.getAbsolutePath());
						FileUtils.copyFile(f, expectedDstFile);
					} else {
						result.add(expectedDstFile.getAbsolutePath());
						FileUtils.copyDirectory(f, expectedDstFile);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Overwrites files with different file sizes from "src" to "dst".
	 * 
	 * @param src
	 * @param dst
	 * @return dst names of overridden files
	 * @throws IOException
	 */
	public static List<String> overwriteFilesWithDifferentSize(File src, File dst) throws IOException {
		List<String> result = new ArrayList<>();
		List<File> srcFiles = new ArrayList<>(FileUtils.listFilesAndDirs(src, TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY));

		int srcPathLength = src.getAbsolutePath().length();
		for (File f : srcFiles) {
			if (!f.isDirectory()) {
				String srcFileRelativePath = f.getAbsolutePath().substring(srcPathLength);
				if (f.exists()) {
					File expectedDstFile = new File(dst.getAbsolutePath() + "\\" + srcFileRelativePath);
					if (expectedDstFile.exists()) {
						long sizeSrc = FileUtils.sizeOf(f);
						long sizeDst = FileUtils.sizeOf(expectedDstFile);

						if (sizeSrc != sizeDst) {
							result.add(expectedDstFile.getAbsolutePath());
							FileUtils.copyFile(f, expectedDstFile);
						}
					}
				}
			}
		}
		return result;
	}

}
