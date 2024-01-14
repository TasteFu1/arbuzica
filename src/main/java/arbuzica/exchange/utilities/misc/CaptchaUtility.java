package arbuzica.exchange.utilities.misc;

import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import arbuzica.exchange.utilities.java.StringUtility;

public class CaptchaUtility {
    private static void drawRandomLines(int width, int height, Graphics graphics) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 16; i++) {
            graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));

            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);

            graphics.drawLine(x1, y1, x2, y2);
        }
    }

    private static void drawForegroundText(String text, int height, Graphics graphics) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int x = 50;

        graphics.setFont(getFont(90));

        for (char c : text.toCharArray()) {
            graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 150));
            graphics.drawString(String.valueOf(c), x, height / 2 + random.nextInt(-30, 30) + 20);
            x += graphics.getFontMetrics().charWidth(c);
        }
    }

    private static void drawBackgroundText(int width, int height, Graphics graphics) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        graphics.setFont(getFont(240));

        for (int i = 0; i < 7; i++) {
            graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 50));
            graphics.drawString(StringUtility.random(1).toLowerCase(), ThreadLocalRandom.current().nextInt(width), height / 2 + ThreadLocalRandom.current().nextInt(-30, 30) + 20);
        }
    }

    private static void drawRandomSquares(int width, int height, Graphics graphics) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 180; i++) {
            graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            graphics.fillRect(ThreadLocalRandom.current().nextInt(width), ThreadLocalRandom.current().nextInt(height), 3, 3);
        }
    }

    private static BufferedImage resize(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    private static Font getFont(int size) {
        return new Font("Arial", Font.ITALIC, size);
    }

    private static BufferedImage generateCaptcha(String text) {
        int width = 100;
        int height = 180;

        BufferedImage captchaImage = resize(width, height);
        FontMetrics fontMetrics = captchaImage.getGraphics().getFontMetrics(getFont(90));

        for (char c : text.toCharArray()) {
            width += fontMetrics.charWidth(c);
        }

        captchaImage = resize(width, height);
        Graphics2D graphics = captchaImage.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        drawBackgroundText(width, height, graphics);
        drawForegroundText(text, height, graphics);
        drawRandomSquares(width, height, graphics);
        drawRandomLines(width, height, graphics);

        graphics.dispose();
        return captchaImage;
    }

    private static byte[] getImageBytes(BufferedImage image) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", byteArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArray.toByteArray();
    }

    public static FileUpload newCaptcha(String name, String text) {
        return FileUpload.fromData(getImageBytes(generateCaptcha(text)), name);
    }
}
