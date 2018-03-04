package fr.litarvan.ldk.bootstrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public final class FileUtils
{
    public static final int BUFFER_SIZE = 1024;

    public static void setExecutable(File file) throws IOException
    {
        ProcessBuilder builder = new ProcessBuilder("chmod", "u+x", file.getAbsolutePath());

        try
        {
            builder.start().waitFor();
        }
        catch (InterruptedException ignored)
        {
        }
    }

    public static String read(InputStream in) throws IOException
    {
        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
            {
                result.append(line).append("\n");
            }

            return result.toString();
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
    }

    public static void write(File file, String toWrite) throws IOException
    {
        BufferedWriter out = null;

        try
        {
            out = new BufferedWriter(new FileWriter(file));
            out.write(toWrite);
        }
        finally
        {
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

    public static void copy(File from, File to) throws IOException
    {
        FileInputStream in = null;

        try
        {
            copy(in = new FileInputStream(from), to);
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
        }
    }

    public static void copy(InputStream in, File to) throws IOException
    {
        FileOutputStream out = null;

        try
        {
            copy(in, out = new FileOutputStream(to));
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException ignored)
            {
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

    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int count;

        while ((count = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, count);
        }
    }
}
