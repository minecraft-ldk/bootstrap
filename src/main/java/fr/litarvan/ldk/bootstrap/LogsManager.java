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
