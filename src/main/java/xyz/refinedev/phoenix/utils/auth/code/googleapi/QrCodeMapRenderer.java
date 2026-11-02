package xyz.refinedev.phoenix.utils.auth.code.googleapi;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.Image;

public class QrCodeMapRenderer extends MapRenderer {

    private Image image;

    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        mapCanvas.drawImage(0, 0, this.image);
    }

    public QrCodeMapRenderer(Image image2) {
        this.image = image2;
    }
}
