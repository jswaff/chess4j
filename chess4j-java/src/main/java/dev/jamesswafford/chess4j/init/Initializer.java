package dev.jamesswafford.chess4j.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public final class Initializer {

    private static final Logger LOGGER = LogManager.getLogger(Initializer.class);

    public static boolean attemptToUseNative = false;
    private static boolean nativeCodeInitialized = false;

    private Initializer() { }

    private static File copyLibraryToFile() {

        InputStream is = null;
        OutputStream os = null;
        File libFile = null;

        try {
            is = Initializer.class.getResourceAsStream("/libchess4j-native.so");
            if (is == null) {
                throw new IllegalStateException("Could not get resource.");
            }

            libFile = File.createTempFile("lib", ".so");
            os = new FileOutputStream(libFile);
            byte[] buffer = new byte[16384];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            return libFile;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load class lib", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                }
            }
            if (libFile != null) {
                libFile.deleteOnExit();
            }
        }
    }

    public static synchronized void init() {

        if (attemptToUseNative && !nativeCodeInitialized) {
            // which OS are we running on?

            String os = System.getProperty("os.name");
            LOGGER.info("# Detected OS: " + os);

            if ("Linux".equals(os)) {
                System.out.println("# Loading Prophet native library.");
                File libFile = copyLibraryToFile();
                System.load(libFile.getPath());
                LOGGER.info("# Prophet loaded, initializing...");
                if (!prophetInit()) {
                    attemptToUseNative = false;
                    throw new IllegalStateException("Could not initialize p4!");
                }
                LOGGER.info("# Prophet initialized.");
            }

            nativeCodeInitialized = true;
        }
    }

    private static native boolean prophetInit();

    public static boolean nativeCodeInitialized() {
        return nativeCodeInitialized;
    }
}
