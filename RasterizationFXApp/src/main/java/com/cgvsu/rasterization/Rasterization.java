package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Rasterization {

    public static void drawEllipse(GraphicsContext graphicsContext, int centerX, int centerY, int a, int b, Color startColor, Color endColor, int thickness) {
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        int x = 0;
        int y = b;
        int a2 = a * a;
        int b2 = b * b;
        int fa2 = 4 * a2, fb2 = 4 * b2;
        int sigma = 2 * b2 + a2 * (1 - 2 * b);

        // Рисуем верхнюю часть эллипса
        while (b2 * x <= a2 * y) {
            plotEllipsePoints(graphicsContext, pixelWriter, centerX, centerY, x, y, a, b, startColor, endColor, thickness);
            if (sigma >= 0) {
                sigma += fa2 * (1 - y);
                y--;
            }
            sigma += b2 * (4 * x + 6);
            x++;
        }

        // Рисуем нижнюю часть эллипса
        x = a;
        y = 0;
        sigma = 2 * a2 + b2 * (1 - 2 * a);
        while (a2 * y <= b2 * x) {
            plotEllipsePoints(graphicsContext, pixelWriter, centerX, centerY, x, y, a, b, startColor, endColor, thickness);
            if (sigma >= 0) {
                sigma += fb2 * (1 - x);
                x--;
            }
            sigma += a2 * (4 * y + 6);
            y++;
        }
    }

    private static void plotEllipsePoints(GraphicsContext graphicsContext, PixelWriter pixelWriter, int centerX, int centerY, int x, int y, int a, int b, Color startColor, Color endColor, int thickness) {
        double distance = Math.sqrt(x * x + y * y);
        double maxDistance = Math.sqrt(a * a + b * b);
        double ratio = distance / maxDistance;

        Color interpolatedColor = interpolateColor(startColor, endColor, ratio);

        // Рисуем симметричные точки
        drawThickPoint(graphicsContext, pixelWriter, centerX + x, centerY + y, interpolatedColor, thickness);
        drawThickPoint(graphicsContext, pixelWriter, centerX - x, centerY + y, interpolatedColor, thickness);
        drawThickPoint(graphicsContext, pixelWriter, centerX + x, centerY - y, interpolatedColor, thickness);
        drawThickPoint(graphicsContext, pixelWriter, centerX - x, centerY - y, interpolatedColor, thickness);
    }

    private static void drawThickPoint(GraphicsContext graphicsContext, PixelWriter pixelWriter, int x, int y, Color color, int thickness) {
        for (int dx = -thickness / 2; dx <= thickness / 2; dx++) {
            for (int dy = -thickness / 2; dy <= thickness / 2; dy++) {
                if (isWithinBounds(x + dx, y + dy, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight())) {
                    pixelWriter.setColor(x + dx, y + dy, color);
                }
            }
        }
    }

    private static boolean isWithinBounds(int x, int y, double width, double height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private static Color interpolateColor(Color startColor, Color endColor, double t) {
        double red = startColor.getRed() + t * (endColor.getRed() - startColor.getRed());
        double green = startColor.getGreen() + t * (endColor.getGreen() - startColor.getGreen());
        double blue = startColor.getBlue() + t * (endColor.getBlue() - startColor.getBlue());
        return new Color(red, green, blue, 1.0);
    }
}