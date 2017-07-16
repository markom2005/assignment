package com.mmarjanovic.assignment.service;

import com.mmarjanovic.assignment.constants.Constants;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class IngestService {

    public String ingestMessage(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        File storedFile = null;
        File tempImageFile = createTempFile();
        if (tempImageFile == null) {
            return null;
        }

        // resolve number of lines and text height
        AffineTransform transform = getFont().getTransform();
        FontRenderContext frc2 = new FontRenderContext(transform, true, true);
        int textWidth = (int)(getFont().getStringBounds(text, frc2).getWidth());
        int textHeight = (int)(getFont().getStringBounds(text, frc2).getHeight());

        int numberOfLines = textWidth / Constants.IMAGE_MAX_WIDTH_IN_PX;
        numberOfLines += (textWidth % Constants.IMAGE_MAX_WIDTH_IN_PX > Constants.IMAGE_MAX_WIDTH_IN_PX / 2) ? 1 : 0;
        boolean textWiderThanMax = textWidth > Constants.IMAGE_MAX_WIDTH_IN_PX;

        if (textWiderThanMax) {
            textHeight = numberOfLines * (textHeight + 3);
        }
        textWidth = Math.min(textWidth, Constants.IMAGE_MAX_WIDTH_IN_PX) + Constants.IMAGE_PADDING;

        BufferedImage image = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        // set white background
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, textWidth, textHeight);

        // set font and color
        graphics.setColor(Color.BLUE);
        graphics.setFont(getFont());

        // break text to fit max allowed width
        Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
        AttributedString attributedText = new AttributedString(text, map);
        attributedText.addAttribute(TextAttribute.FONT, graphics.getFont());

        AttributedCharacterIterator paragraph = attributedText.getIterator();
        int startIndex = paragraph.getBeginIndex();
        int endIndex = paragraph.getEndIndex();
        FontRenderContext frc = graphics.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);

        float maxWidthInPx = Constants.IMAGE_MAX_WIDTH_IN_PX;
        float drawPosY = 0;

        lineMeasurer.setPosition(startIndex);

        // get lines until the entire paragraph has been displayed
        while (lineMeasurer.getPosition() < endIndex) {
            TextLayout layout = lineMeasurer.nextLayout(maxWidthInPx);

            float drawPosX = layout.isLeftToRight() ? 0 : maxWidthInPx - layout.getAdvance();
            drawPosY += layout.getAscent();
            layout.draw(graphics, drawPosX, drawPosY);
            drawPosY += layout.getDescent() + layout.getLeading();
        }

        graphics.dispose();

        // write to file and copy to storage location
        try {
            ImageIO.write(image, "png", tempImageFile);

            storedFile = new File(new File(Constants.IMAGE_STORAGE_LOCATION).getAbsolutePath() + File.separator + tempImageFile.getName());
            Files.copy(tempImageFile.toPath(), storedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            System.out.println("Error occurred while writing text to image file. " + e.getMessage());
        }
        finally {
            tempImageFile.delete();
        }

        // return image download URL
        return storedFile != null ? (Constants.APP_URL + Constants.IMAGE_DOWNLOAD_PATH + storedFile.getName()) : null;
    }

    private File createTempFile() {
        File tempImageFile = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.IMAGE_CALENDAR_FORMAT);
        try {
            tempImageFile = File.createTempFile(
                dateFormat.format(new Date()) + "-" + String.valueOf(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)), ".png");
        }
        catch (IOException e) {
            System.out.println("Error generating temp file. " + e.getMessage());
        }

        return tempImageFile;
    }

    private Font getFont() {
        return new Font("Verdana", Font.PLAIN, 20);
    }
}