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
