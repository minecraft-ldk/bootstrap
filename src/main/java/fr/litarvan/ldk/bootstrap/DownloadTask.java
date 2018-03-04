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
