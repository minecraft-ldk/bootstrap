package fr.litarvan.ldk.bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.jimmc.jshortcut.JShellLink;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Installer
{
    private static final Logger log = LoggerFactory.getLogger("Installer");

    private OS os;
    private File folder;
    private JSONObject config;

    public Installer(OS os, File folder, JSONObject config)
    {
        this.os = os;
        this.folder = folder;
        this.config = config;
    }

    public void install() throws IOException, URISyntaxException
    {
        File dest = new File(folder, config.getString("id") + ".jar");

        if (dest.exists())
        {
            log.info("Jar already exist, skipping installation");
            return;
        }

        File source = new File(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

        // In case we're in IDE
        if (!source.getName().endsWith(".jar") && !source.getName().endsWith(".exe"))
        {
            log.info("IDE detected, skipping installation");
            return;
        }

        log.info("Copying jar to {}", dest.getAbsolutePath());
        FileUtils.copy(source, dest);

        switch (os)
        {
            case WINDOWS:
                installWindows(dest);
                break;
            case OSX:
                installOSX(dest);
                break;
            default:
                installLinux(dest);
                break;
        }
    }

    protected void installWindows(File jarFile) throws IOException
    {
        File windowsIcon = new File(folder, config.getString("id") + ".ico");

        log.info("Copying Windows icon to {}", windowsIcon.getAbsolutePath());
        FileUtils.copy(Bootstrap.class.getResourceAsStream("/" + config.getString("windows-icon")), windowsIcon);

        String desktop = JShellLink.getDirectory("desktop");
        log.info("Creating Windows desktop shortcut in {}", desktop);

        JShellLink link = new JShellLink();
        link.setName(config.getString("title"));
        link.setPath(System.getProperty("java.home") + "/bin/javaw.exe");
        link.setArguments("-jar " + jarFile.getAbsolutePath());
        link.setIconLocation(windowsIcon.getAbsolutePath());
        link.setFolder(desktop);
        link.save();
    }

    protected void installOSX(File jarFile) throws IOException
    {
        File app = new File(System.getProperty("user.home") + "/Applications", config.getString("title") + ".app");
        File icon = new File(app, "Contents/Resources/icon.icns");

        log.info("Copying mac icon to {}", icon.getAbsolutePath());
        FileUtils.copy(Bootstrap.class.getResourceAsStream("/" + config.getString("mac-icon")), icon);

        File launchScript = new File(app, "Contents/MacOS/Launcher");

        log.info("Writing Mac launch script to {}", launchScript.getAbsolutePath());
        String scriptTemplate = FileUtils.read(Bootstrap.class.getResourceAsStream("/" + config.getString("mac-launch-script")));
        scriptTemplate = scriptTemplate.replace("$NAME", config.getString("title"))
                           .replace("$PATH", jarFile.getAbsolutePath());

        FileUtils.write(launchScript, scriptTemplate);
        FileUtils.setExecutable(launchScript);

        File plist = new File(app, "Contents/Info.plist");

        log.info("Writing Mac plist to {}", plist.getAbsolutePath());
        FileUtils.copy(Bootstrap.class.getResourceAsStream("/" + config.getString("mac-plist")), plist);

        log.info("Created Mac application at {}", app.getAbsolutePath());
    }

    protected void installLinux(File jarFile) throws IOException
    {
        String icon = config.getString("icon");
        File iconFile = new File(folder, config.getString("id") + icon.substring(icon.lastIndexOf('.')));

        log.info("Copying icon to {}", iconFile.getAbsolutePath());
        FileUtils.copy(Bootstrap.class.getResourceAsStream("/" + icon), iconFile);

        File desktopEntry = new File(System.getProperty("user.home") + "/.local/share/applications", config.getString("id") + ".desktop");

        log.info("Writing Linux desktop entry to {}", desktopEntry.getAbsolutePath());

        String template = FileUtils.read(Bootstrap.class.getResourceAsStream("/" + config.getString("desktop-entry-template")));
        template = template.replace("$NAME", config.getString("title"))
                           .replace("$ICON", iconFile.getAbsolutePath())
                           .replace("$PATH", jarFile.getAbsolutePath());

        FileUtils.write(desktopEntry, template);
        FileUtils.setExecutable(desktopEntry);

        // Refresh application menu on KDE
        try
        {
            ProcessBuilder builder = new ProcessBuilder("kbuildsycoca5");
            builder.start();
        }
        catch (Exception ignored)
        {
        }
    }
}
