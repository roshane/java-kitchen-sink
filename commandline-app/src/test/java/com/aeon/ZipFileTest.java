package com.aeon;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileTest {

    @Test
    public void testZipWithPassword() {
        createPasswordProtectedZip("./test.zip", "password");
    }

    @Test
    public void readRemoteImageToBase64String() {
        final URI uri = URI.create("https://img.icons8.com/color/512/whatsapp--v1.png");
        try {
            final InputStream stream = uri.toURL().openConnection().getInputStream();
            final byte[] bytes = stream.readAllBytes();
            stream.close();
            writeToImage(bytes);
            final byte[] base64String = Base64.getEncoder().encode(bytes);
            System.out.println(base64String);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeToImage(byte[] imageBytes) throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes);
        final BufferedImage bufferedImage = ImageIO.read(stream);
        ImageIO.write(bufferedImage, "PNG", new File("./whatsapp.png"));
    }

    private void createPasswordProtectedZip(String zipFilePath, String password) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            // Add entries to the zip file
            ZipEntry entry = new ZipEntry("entry1.txt");
            // Set the password for the entry
            entry.setExtra(("P" + password).getBytes());
            zos.putNextEntry(entry);
            zos.write("This is the content of entry 1".getBytes());

            entry = new ZipEntry("entry2.txt");
            // Set the password for the entry
            entry.setExtra(("P" + password).getBytes());
            zos.putNextEntry(entry);
            zos.write("This is the content of entry 2".getBytes());

            // Close the zip output stream
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
