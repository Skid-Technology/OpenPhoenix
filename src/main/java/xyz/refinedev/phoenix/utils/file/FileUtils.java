package xyz.refinedev.phoenix.utils.file;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {

    private static final long timeTime = 2147483647L;

    public static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {
        try {
            FileOutputStream fileOutputStream = openOutputStream(file);
            try {
                copy(inputStream, fileOutputStream);
                fileOutputStream.close();
            } finally {
                closeQuietly(fileOutputStream);
            }
        } finally {
            closeQuietly(inputStream);
        }
    }

    public static void closeQuietly(OutputStream outputStream) {
        closeQuietly((Closeable) outputStream);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException oException) {
        }
    }

    public static int copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        long value;
        return (value = copyLarge(inputStream, outputStream)) > timeTime ? -1 : (int) value;
    }

    public static long copyLarge(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        return copyLarge(inputStream, outputStream, new byte[4096]);
    }

    public static long copyLarge(InputStream inputStream, OutputStream outputStream, byte[] values)
            throws IOException {
        long value = 0L;
        int value2;
        while (-1 != (value2 = inputStream.read(values))) {
            outputStream.write(values, 0, value2);
            value += value2;
        }
        return value;
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    public static FileOutputStream openOutputStream(File file, boolean matches) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File file2;
            if ((file2 = file.getParentFile()) != null && !file2.mkdirs() && !file2.isDirectory()) {
                throw new IOException("Directory '" + file2 + "' could not be created");
            }
        }
        return new FileOutputStream(file, matches);
    }

    public static void copyURLToFile(URL uRL, File file) throws IOException {
        copyInputStreamToFile(uRL.openStream(), file);
    }

    public static List<String> readLines(File file, Charset charset) throws IOException {
        FileInputStream fileInputStream = null;
        List<String> values;
        try {
            values = readLines(fileInputStream = openInputStream(file), charset);
        } finally {
            closeQuietly(fileInputStream);
        }
        return values;
    }

    public static List<String> readLines(File file, String text) throws IOException {
        return readLines(file, Charset.forName(text));
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            } else if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            } else {
                return new FileInputStream(file);
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
    }

    public static List<String> readLines(InputStream inputStream, Charset charset)
            throws IOException {
        return readLines(new InputStreamReader(inputStream, charset));
    }

    public static List<String> readLines(InputStream inputStream, String text) throws IOException {
        return readLines(inputStream, Charset.forName(text));
    }

    public static List<String> readLines(Reader reader) throws IOException {
        BufferedReader bufferedReader = toBufferedReader(reader);
        ArrayList<String> values = new ArrayList<>();
        for (String text = bufferedReader.readLine();
                text != null;
                text = bufferedReader.readLine()) {
            values.add(text);
        }
        return values;
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader
                ? (BufferedReader) reader
                : new BufferedReader(reader);
    }

    private FileUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
