package fr.litarvan.ldk.bootstrap;

import javax.swing.UIManager;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ignored)
        {
        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.start();

        System.exit(0);
    }
}
