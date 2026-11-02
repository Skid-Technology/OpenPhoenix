package xyz.refinedev.phoenix.utils.auth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.ErrorReporter;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.auth.api.Authenticator;
import xyz.refinedev.phoenix.utils.auth.code.googleapi.GoogleAuthenticator;
import xyz.refinedev.phoenix.utils.auth.code.googleapi.GoogleAuthenticatorKey;
import xyz.refinedev.phoenix.utils.auth.code.googleapi.QrCodeImage;
import xyz.refinedev.phoenix.utils.auth.code.googleapi.QrCodeMapRenderer;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import javax.imageio.ImageIO;

public class GoogleCodeAuthenticator implements Authenticator {

    private final CommonPlatform platform;

    private final GoogleAuthenticator googleAuthenticator;

    private static Method method;

    @Override
    public String getName() {
        return "QRCode";
    }

    @Override
    public void setup(Player player) {
        IProfile cachedProfile =
                this.platform.getProfileHandler().getCachedProfile(player.getUniqueId());
        try {
            GoogleAuthenticatorKey googleAuthenticatorKey =
                    this.googleAuthenticator.createCredentials();
            String text =
                    QrCodeImage.formatString(
                            player, googleAuthenticatorKey, SettingsConfig.AUTH.NAME);
            BufferedImage bufferedImage = ImageIO.read(new URL(text));
            MapView mapView;
            List<org.bukkit.map.MapRenderer> renderers =
                    (mapView = Bukkit.createMap(player.getWorld())).getRenderers();
            MapView mapView2 = mapView;
            renderers.forEach(mapView2::removeRenderer);
            mapView.addRenderer(new QrCodeMapRenderer(bufferedImage));
            short value = this.findShort(mapView);
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.FILLED_MAP.get(), 1, value))
                    .setMapView(mapView);
            itemBuilder.setDurability(value);
            itemBuilder.setName("&aQR Code");
            itemBuilder.addLoreLine("&7Two Factor QR Code");
            player.getInventory().addItem(new ItemStack[] {itemBuilder.toItemStack()});
            player.sendMap(mapView);
            player.getInventory().setHeldItemSlot(QrCodeImage.calculateCount(player));
            Clickable clickable;
            (clickable = new Clickable())
                    .addUrl(MessageConfig.AUTH.QRCODE_URL, "&aClick to open.", text);
            clickable.sendToPlayer(player);
            cachedProfile.setSecretKey(googleAuthenticatorKey.getKey());
            cachedProfile.setAuthType(this.getName());
            cachedProfile.saveProfile();
        } catch (Exception exception) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.AUTH.SETUP_FAILED)));
            this.platform.getLogger().log(Level.SEVERE, ErrorReporter.formatString(exception));
        }
    }

    public short findShort(MapView mapView) {
        if (method == null) {
            return 0;
        }
        try {
            return (Short) method.invoke(mapView);
        } catch (Exception exception) {
            return 0;
        }
    }

    @Override
    public boolean authenticate(Player player, String text) {
        try {
            Integer.parseInt(text);
        } catch (Exception exception) {
            return false;
        }
        int value;
        if ((value = Integer.parseInt(text)) < 0) {
            return false;
        }
        IProfile cachedProfile =
                this.platform.getProfileHandler().getCachedProfile(player.getUniqueId());
        if (!QrCodeImage.authorize(this.googleAuthenticator, cachedProfile.getSecretKey(), value)) {
            return false;
        }
        QrCodeImage.applyPlayer(player);
        return true;
    }

    @Override
    public boolean canSetup(Player player) {
        if (!this.platform
                .getProfileHandler()
                .getCachedProfile(player.getUniqueId())
                .getSecretKey()
                .isEmpty()) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.AUTH.ALREADY_ENABLED)));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canAuthenticate(Player player) {
        if (this.platform
                .getProfileHandler()
                .getProfile(player.getUniqueId())
                .getSecretKey()
                .isEmpty()) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.AUTH.NOT_ENABLED)));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isEnabled() {
        return SettingsConfig.AUTH.QRCODE_ENABLED;
    }

    public GoogleCodeAuthenticator(
            CommonPlatform commonPlatform, GoogleAuthenticator googleAuthenticator2) {
        this.platform = commonPlatform;
        this.googleAuthenticator = googleAuthenticator2;
    }

    static {
        try {
            method = MapView.class.getMethod("getId", (Class<?>[]) null);
        } catch (Exception exception) {
            method = null;
        }
    }
}
