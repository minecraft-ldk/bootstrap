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
