package fr.litarvan.ldk.bootstrap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap
{
    public static final String VERSION = "1.0.0";
    public static final String MIN_ELECTRON_VERSION = "v2";
    public static final String CONFIG_FILE = "/bootstrap.json";

    private static final Logger log = LoggerFactory.getLogger("Bootstrap");

    public void start()
    {
        long time = System.currentTimeMillis();
        OS os = OS.get();

        log.info("Starting Bootstrap v{}...", VERSION);
        JSONObject config;
        try
        {
            config = new JSONObject(FileUtils.read(Bootstrap.class.getResourceAsStream(CONFIG_FILE)));
        }
        catch (IOException e)
        {
            log.error("Unexpected error while reading bootstrap config, exiting", e);
            JOptionPane.showMessageDialog(null, "Erreur majeure : " + e.getMessage() + " (" + e.getClass().getName() + ")\nMerci de prévenir les développeurs du launcher dès que possible", "Erreur", JOptionPane.ERROR_MESSAGE);

            return;
        }

        JSONObject messages = config.getJSONObject("messages");

        BufferedImage image;
        BufferedImage icon;

        try
        {
            image = ImageIO.read(Bootstrap.class.getResourceAsStream("/" + config.getString("splash")));
            icon = ImageIO.read(Bootstrap.class.getResourceAsStream("/" + config.getString("icon")));
        }
        catch (Exception e)
        {
            log.error("Unexpected error while reading image, exiting", e);
            JOptionPane.showMessageDialog(null, messages.getString("base-error") + " : " + e.getMessage() + " (" + e.getClass().getName() + ")\nMerci de prévenir les développeurs du launcher dès que possible", "Erreur", JOptionPane.ERROR_MESSAGE);

            return;
        }

        Splash splash = new Splash(config.getString("title"), image, icon);
        splash.setVisible(true);

        File folder = getFolder(os);
        log.info("Minecraft folder is at {}", folder.getAbsolutePath());

        log.info("Setting up electron...");
        ElectronManager electronManager = new ElectronManager(new File(folder, "electron"), os, config.getString("electron-archive"));
        String version = null;
        try
        {
            version = electronManager.getVersion();
        }
        catch (IOException e)
        {
            log.error("Couldn't retrieve electron version", e);
        }

        boolean shouldInstall = false;

        if (version == null)
        {
            log.info("Can't find electron, installing...");
            shouldInstall = true;
        }
        else if (!version.startsWith(MIN_ELECTRON_VERSION))
        {
            log.warn("Unexpected electron version '{}', reinstalling...", version);
            shouldInstall = true;
        }
        else
        {
            log.info("Found electron version '{}'", version);
        }

        if (shouldInstall)
        {
            try
            {
                electronManager.install();
            }
            catch (IOException e)
            {
                log.error("Unexpected error while installing electron, aborting", e);
                JOptionPane.showMessageDialog(null, messages.getString("electron-error") + " : " + e.getMessage() + " (" + e.getClass().getName() + ")", "Erreur", JOptionPane.ERROR_MESSAGE);

                return;
            }

            try
            {
                log.info("Successfully installed electron version '{}'", electronManager.getVersion());
            }
            catch (IOException e)
            {
                log.warn("Warning : Newly installed electron version couldn't be read", e);
            }
        }

        File launcherFolder = new File(folder, "launcher/" + config.getString("id"));

        log.info("Updating launcher...");
        Updater updater = new Updater(config.getString("updater-url"), launcherFolder);
        try
        {
            updater.start();
        }
        catch (IOException e)
        {
            log.error("Unexpected error during update, exiting", e);
            JOptionPane.showMessageDialog(splash, messages.getString("update-error") + " : " + e.getMessage() + " (" + e.getClass().getName() + ")", "Erreur", JOptionPane.ERROR_MESSAGE);

            return;
        }

        log.info("Installing...");
        Installer installer = new Installer(os, new File(folder, "launcher/"), config);
        try
        {
            installer.install();
        }
        catch (IOException e)
        {
            log.warn("Unexpected error during installation, skipping", e);
            JOptionPane.showMessageDialog(splash, messages.getString("install-error") + " : " + e.getMessage() + " (" + e.getClass().getName() + ")", "Attention", JOptionPane.WARNING_MESSAGE);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        splash.setVisible(false);
        log.info("Total time : {}ms", System.currentTimeMillis() - time);

        log.info("Launching...");
        Process p;
        try
        {
            p = electronManager.launch(launcherFolder, ".");
        }
        catch (IOException e)
        {
            log.error("Unexpected error during launch, exiting", e);
            JOptionPane.showMessageDialog(splash, messages.getString("launch-error") + " : " + e.getMessage() + " (" + e.getClass().getName() + ")", "Erreur", JOptionPane.ERROR_MESSAGE);

            return;
        }

        System.out.println("-------------------------------------\n");

        LogsManager logsManager = new LogsManager(p, new File(folder, "launchers/" + config.getString("id") + ".logs"));
        try
        {
            logsManager.start();
        }
        catch (IOException e)
        {
            log.warn("Exception while writing logs, cancelling", e);
        }
    }

    protected File getFolder(OS os)
    {
        switch (os)
        {
            case WINDOWS:
                return new File(System.getenv("APPDATA") + "/.minecraft");
            case OSX:
                return new File(System.getProperty("user.home") + "/Library/Application Support/minecraft");
            default:
                return new File(System.getProperty("user.home") + "/.minecraft");
        }
    }
}
