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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadTask implements Runnable
{
    private static final Logger log = LoggerFactory.getLogger("DownloadTask");

    private int tries = 0;

    private String url;
    private File dest;

    public DownloadTask(String url, File dest)
    {
        this.url = url;
        this.dest = dest;
    }

    @Override
    public void run()
    {
        while (tries < 3)
        {
            try
            {
                log.info("Downloading '{}' to '{}' (try {})", this.url, this.dest.getAbsolutePath(), this.tries);
                download();

                break;
            }
            catch (IOException e)
            {
                log.error("Unexpected error while downloading file '{}' to '{}'", this.url, this.dest.getAbsolutePath(), e);
                tries++;
            }
        }
    }

    protected void download() throws IOException
    {
        this.dest.getParentFile().mkdirs();
        FileUtils.copy(new URL(this.url).openStream(), this.dest);
    }
}
