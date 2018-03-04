package fr.litarvan.ldk.bootstrap;

public enum OS
{
    WINDOWS,
    OSX,
    OTHER;

    public static OS get()
    {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win"))
        {
            return WINDOWS;
        }

        if (os.contains("osx") || os.contains("mac"))
        {
            return OSX;
        }

        return OTHER;
    }
}
