package com.jamesswafford.chess4j.init;

import java.io.*;

public final class Initializer {

    private static boolean initialized = false;
    private static boolean useNative = false;

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
                } catch (IOException e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
            if (libFile != null) {
                libFile.deleteOnExit();
            }
        }
    }

    public static synchronized void init() {

        if (!initialized) {
            // which OS are we running on?

            String os = System.getProperty("os.name");
            System.out.println("# Detected OS: " + os);

            if ("Linux".equals(os)) {
                System.out.println("# Loading Prophet4 native library.");
                File libFile = copyLibraryToFile();

                System.load(libFile.getPath());

                if (!p4Init()) {
                    throw new IllegalStateException("Could not initialize p4!");
                }

                useNative = true;
            }

            initialized = true;
        }
    }

    private static native boolean p4Init();

    public static boolean useNative() {
        return useNative;
    }
}
