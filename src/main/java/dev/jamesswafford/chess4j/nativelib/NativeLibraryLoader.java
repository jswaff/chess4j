package dev.jamesswafford.chess4j.nativelib;

import dev.jamesswafford.chess4j.exceptions.NativeLibraryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class NativeLibraryLoader {

    private static final Logger LOGGER = LogManager.getLogger(NativeLibraryLoader.class);

    public static boolean attemptToUseNative = false;
    private static boolean nativeCodeInitialized = false;

    private NativeLibraryLoader() { }

    public static synchronized void init() {

        if (attemptToUseNative && !nativeCodeInitialized) {
            String os = System.getProperty("os.name");
            LOGGER.info("# Detected OS: {}", os);

            String libFileName = null;
            if ("Linux".equalsIgnoreCase(os)) libFileName = "libprophetlib.so";

            if (libFileName==null) {
                throw new NativeLibraryException("Native library not available for " + os);
            }

            try {
                Path jarPath = Paths.get(NativeLibraryLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                Path jarDirectory = jarPath.getParent();
                Path libFilePath = jarDirectory.resolve(libFileName);
                NativeEngineLib.initializeFFM(libFilePath);
                nativeCodeInitialized = true;
            } catch (URISyntaxException e) {
                throw new NativeLibraryException("Error initializing Prophet library", e);
            }
        }
    }

    public static boolean nativeCodeInitialized() {
        return nativeCodeInitialized;
    }

}
