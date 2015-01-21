package jline;

import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiOutputStream;
import org.fusesource.jansi.WindowsAnsiOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class AnsiWindowsTerminal extends WindowsTerminal
{
    private final boolean ansiSupported = detectAnsiSupport();

    public OutputStream wrapOutIfNeeded(OutputStream out)
    {
        return wrapOutputStream(out);
    }

    private static OutputStream wrapOutputStream(OutputStream stream)
    {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows"))
            try
            {
                return new WindowsAnsiOutputStream(stream);
            }
            catch (Throwable ignore)
            {
                return new AnsiOutputStream(stream);
            }
        return stream;
    }

    private static boolean detectAnsiSupport() {
        AnsiConsole.systemInstall();
        OutputStream out = AnsiConsole.wrapOutputStream(new ByteArrayOutputStream());
        try {
            out.close();
        }
        catch (Exception e)
        {
        }
        return out instanceof WindowsAnsiOutputStream;
    }

    public AnsiWindowsTerminal() throws Exception
    {
    }

    public boolean isAnsiSupported()
    {
        return this.ansiSupported;
    }

    public boolean hasWeirdWrap()
    {
        return false;
    }
}