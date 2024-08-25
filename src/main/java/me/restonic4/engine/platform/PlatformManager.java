package me.restonic4.engine.platform;

import me.restonic4.engine.exceptions.UnknownPlatformException;
import me.restonic4.engine.util.debug.Logger;

import java.util.Locale;

public class PlatformManager {
    private static PlatformManager instance;

    private OperatingSystem operatingSystem = getOperatingSystem();

    public static PlatformManager getInstance() {
        if (PlatformManager.instance == null) {
            PlatformManager.instance = new PlatformManager();
        }
        return PlatformManager.instance;
    }

    public OperatingSystem getOperatingSystem() {
        if (this.operatingSystem != null) {
            return this.operatingSystem;
        }

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        for (OperatingSystem os : OperatingSystem.values()) {
            if (os.matches(osName)) {
                Logger.log("Playing on " + os.getId());
                return os;
            }
        }

        return OperatingSystem.UNKNOWN;
    }

    public String getEndOfLine() {
        if (operatingSystem.isWindows()) {
            return "\r\n";
        }
        else if (operatingSystem.isLinux() || operatingSystem.isMac() || operatingSystem.isSolaris()) {
            return "\n";
        }

        throw new UnknownPlatformException();
    }
}

