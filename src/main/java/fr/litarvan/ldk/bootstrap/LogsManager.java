package fr.litarvan.ldk.bootstrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogsManager
{
    private Process process;
    private File logsFile;

    public LogsManager(Process process, File logsFile)
    {
        this.process = process;
        this.logsFile = logsFile;
    }

    public void start() throws IOException
    {
        BufferedReader in = null;
        BufferedWriter out = null;

        try
        {
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            out = new BufferedWriter(new FileWriter(logsFile));

            String line;
            while ((line = in.readLine()) != null)
            {
                System.out.println(line);
                out.write(line + "\n");
            }
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ignored)
                {
                }
            }

            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException ignored)
                {
                }
            }
        }
    }
}
