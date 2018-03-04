package fr.litarvan.ldk.bootstrap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Splash extends JFrame
{
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    private Image splash;

    public Splash(String title, Image splash, Image icon)
    {
        this.splash = splash;

        this.setTitle(title);
        this.setUndecorated(true);
        this.setResizable(false);
        this.setIconImage(icon);
        this.setSize(this.splash.getWidth(this), this.splash.getHeight(this));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBackground(TRANSPARENT);

        this.setContentPane(new SplashPanel());
    }

    public class SplashPanel extends JPanel
    {
        public SplashPanel()
        {
            this.setBackground(TRANSPARENT);
            this.setOpaque(false);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            g.drawImage(splash, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
