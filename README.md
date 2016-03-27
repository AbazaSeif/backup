# Backup
Simple sync and backup tool I use for encrypted cloud backups.

## Prerequisites
This tool needs:
- Java 1.7 and higher
- 7zip - version 9.20
- (optional) Cloud sync software (such as Google Drive for desktop)

## What it does?
This is a very simple script written in Java that can be used for automated, compressed and encrypted (cloud) backup. It works in a following way:

1. (Zipper) Recursively "7zips" your source directory - It traverses your files to a given depth and creates a separate archive for each branch with all sub-directories included.

2. (Sync) It propagates changed archives to a "result" folder (your Cloud provider synchronized drive). - **It will push only files that differ by file-size**. (Time-stamps currently cannot be used as they change during archive creation / update). In general it works like this: "delete missing stuff", "copy new stuff" and "overwrite differently sized files".

It has two modes of operation:

1. Simple mode - It creates always creates the archives from scratch. No compression is used.

2. Advanced mode - It will try to update your existing archives with changes (using 7zip "u" option). Compression and custom 7zip workdir can be used.

Notice:
- **If your source data change often without changing their actual file-size this tool is not for you.**
- See "Issues" tab for currently known issues. (For example the password change may not be updated correctly by Sync tool as it will not change the file-size, etc.)

## No cloud?
Zipper and Sync can be used separately. If you do not need the "cloud" backup you can just use the "Zipper" tool to keep encrypted backup of your files in a local or network filesystem.

## How to use it?
- Download the .zip package from release folder in this repository.
- Check included .bat files and fill in the source, intermediate and resulting directories.
- If no arguments are used the help is printed:

```
Mandatory parameters: {7zipLocation} {source} {destination} {depth} {archivePass}

{7zipLocation}              7zip executable (7z.exe)
{source} & {destination}:   source and destination folders
{depth}:                    folder tree recursing depth - folders lower than {depth} will be merged into one archive
{archivePass}:              password for the resulting archives

Additional parameters: {reuse} {workingDir} {compression} 
{reuse}:                    update the destination folder - archives will be synchronized (true/false)
{workingDir}:               temporary directory location
{compression}:              compression: 0-9 - 0 no compression
```

The Backup.jar contains two main classes:
- java -cp Backup.jar cz.jkosnar.backup.zip.Zipper
- java -cp Backup.jar cz.jkosnar.backup.sync.Sync

## Future plans?
There are situation where the Sync tool can miss important changes because the file-size of archive did not change. This should be improved.

## Use it at your own risk.
This is a very simple tool I use for my personal non-critical backups. I cannot guarantee it will work for you. Please check the backup files very carefully during the backups. Currently there are only "alpha" releases.


