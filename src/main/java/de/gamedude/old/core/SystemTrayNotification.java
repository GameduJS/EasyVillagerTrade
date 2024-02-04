package de.gamedude.old.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SystemTrayNotification {

    public static void sendNotification() throws IOException {
        File file = new File("../src/main/resources/assets/icon.png");
        TrayIcon icon = new TrayIcon(ImageIO.read(file), "Easy Villager Trade");
        icon.setImageAutoSize(true);
        try {
            SystemTray.getSystemTray().add(icon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        PopupMenu popupMenu = new PopupMenu("test");
        icon.setPopupMenu(popupMenu);
        icon.displayMessage("Image processing", "The image has been successfully rendered, click here to go to the location", TrayIcon.MessageType.NONE);
        icon.addActionListener(e -> {
            SystemTray.getSystemTray().remove(icon);
        });
    }
}
