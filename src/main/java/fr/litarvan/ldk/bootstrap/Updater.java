/*
 *  The LDK - Launcher development kit
 *  Copyright (C) 2017 Adrien 'Litarvan' Navratil
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.litarvan.ldk.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

public class Updater
{
    public static final String FILES_FOLDER = "files";
    public static final String UPDATER_SCRIPT = "updater.php";
    public static final int MAX_THREAD = 5;

    private String remote;
    private File dest;

    public Updater(String remote, File dest)
    {
        this.remote = (!remote.startsWith("http") ? "http://" : "") + remote + (!remote.endsWith("/") ? "/" : "");
        this.dest = dest;
    }

    public void start() throws IOException
    {
        RemoteFile[] files = getFiles();
        List<String> toDownload = new ArrayList<String>();

        for (RemoteFile file : files)
        {
            File local = new File(dest, file.name);

            if (!local.exists() || local.length() != file.size)
            {
                toDownload.add(file.name);
            }
        }

        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREAD);

        for (String file : toDownload)
        {
            pool.submit(new DownloadTask(this.remote + FILES_FOLDER + "/" + file, new File(dest, file)));
        }

        pool.shutdown();

        try
        {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        }
        catch (InterruptedException ignored)
        {
        }
    }

    protected RemoteFile[] getFiles() throws IOException
    {
        URL url = new URL(remote + UPDATER_SCRIPT);

        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();

        try
        {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = reader.readLine()) != null)
            {
                result.append(line);
            }
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ignored)
                {
                }
            }
        }

        JSONArray array = new JSONArray(result.toString());
        RemoteFile[] files = new RemoteFile[array.length()];

        for (int i = 0; i < files.length; i++)
        {
            JSONArray file = array.getJSONArray(i);
            files[i] = new RemoteFile(file.getString(0), file.getInt(1));
        }

        return files;
    }

    public static class RemoteFile
    {
        public final String name;
        public final int size;

        public RemoteFile(String name, int size)
        {
            this.name = name;
            this.size = size;
        }
    }
}
