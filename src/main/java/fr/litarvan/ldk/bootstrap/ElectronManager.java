package fr.litarvan.ldk.bootstrap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

public class ElectronManager
{
    public static final int BUFFER_SIZE = 1024;

    private File folder;
    private OS os;
    private String archive;

    public ElectronManager(File folder, OS os, String archive)
    {
        this.folder = folder;
        this.os = os;
        this.archive = archive;
    }

    public Process launch(File folder, String... args) throws IOException
    {
        List<String> command = new ArrayList<String>();
        command.add(this.folder.getAbsolutePath() + "/" + getExecutable());
        command.addAll(Arrays.asList(args));

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        builder.directory(folder);

        return builder.start();
    }

    public String getVersion() throws IOException
    {
        return FileUtils.read(launch(folder, "--version").getInputStream()).trim();
    }

    public void install() throws IOException
    {
        if (!folder.exists())
        {
            folder.mkdirs();
        }

        TarArchiveInputStream in = new TarArchiveInputStream(new XZCompressorInputStream(Bootstrap.class.getResourceAsStream("/" + archive)));

        try
        {
            TarArchiveEntry entry;

            while ((entry = in.getNextTarEntry()) != null)
            {
                File file = new File(folder, entry.getName());

                if (entry.isDirectory())
                {
                    file.mkdirs();
                    continue;
                }

                int count;
                byte data[] = new byte[BUFFER_SIZE];

                FileOutputStream fos = new FileOutputStream(file, false);
                BufferedOutputStream out = new BufferedOutputStream(fos, BUFFER_SIZE);

                try
                {
                    while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
                    {
                        out.write(data, 0, count);
                    }
                }
                finally
                {
                    try
                    {
                        out.close();
                    }
                    catch (Exception ignored)
                    {
                    }
                }
            }
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (Exception ignored)
            {
            }
        }

        if (this.os != OS.WINDOWS)
        {
            FileUtils.setExecutable(new File(folder, getExecutable()));
        }
    }

    public String getExecutable()
    {
        switch (this.os)
        {
            case WINDOWS:
                return "electron.exe";
            case OSX:
                return "Electron.app/Contents/MacOS/Electron";
            default:
                return "electron";
        }
    }
}
