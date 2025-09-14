package dev.jamesswafford.chess4j.nativelib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public final class NativeLibraryLoader {

    private static final Logger LOGGER = LogManager.getLogger(NativeLibraryLoader.class);

    public static boolean attemptToUseNative = false;
    private static boolean nativeCodeInitialized = false;

    private NativeLibraryLoader() { }

    private static File loadNativeLibraryFromJar(String nativeLibrary) throws IOException {

        InputStream is = null;
        OutputStream os = null;
        File libFile;

        try {
            is = NativeLibraryLoader.class.getResourceAsStream(nativeLibrary);
            if (is == null) {
                throw new IllegalStateException("Prophet library not found in jar: " + nativeLibrary);
            }

            libFile = File.createTempFile("lib", ".tmp");
            libFile.deleteOnExit();

            os = new FileOutputStream(libFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            return libFile;
        } finally {
            if (is != null) {
                 is.close();
            }
            if (os != null) {
                 os.close();
            }
        }
    }

    public static synchronized void init() {

        if (attemptToUseNative && !nativeCodeInitialized) {
            String os = System.getProperty("os.name");
            LOGGER.info("# Detected OS: {}", os);

            String libFilePathInJar = null;
            if ("Linux".equals(os)) libFilePathInJar = "/libprophetlib.so";

            try {
                if (libFilePathInJar != null) {
                    File libFile = loadNativeLibraryFromJar(libFilePathInJar);
                    NativeEngineLib.initializeFFM(libFile);
                    LOGGER.info("# Prophet library initialized.");
                    nativeCodeInitialized = true;
                } else {
                    LOGGER.warn("# Prophet library not available for {}", os);
                    attemptToUseNative = false;
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Error initializing Prophet library", e);
            }
        }
    }

    public static boolean nativeCodeInitialized() {
        return nativeCodeInitialized;
    }

}
