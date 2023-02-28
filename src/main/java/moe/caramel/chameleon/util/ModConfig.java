package moe.caramel.chameleon.util;

import com.mojang.blaze3d.platform.MacosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.server.packs.resources.IoSupplier;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

public final class ModConfig extends Settings<ModConfig> {

    private static final Path MOD_CONFIG = new File("./config/caramel.chameleon.properties").toPath();
    public static final ResourceLocation ORIGINAL_MAC_ICON = new ResourceLocation("icons/minecraft.icns");
    public static final ResourceLocation ORIGINAL_WIN_ICON = new ResourceLocation("icons/icon_32x32.png");
    public static final Function<Minecraft, Set<ResourceLocation>> GET_ICON_SET = client -> {
        return client.getResourceManager().listResources("icons", Objects::nonNull).keySet();
    };

    /* ======================================== */
    private static ModConfig instance;

    /**
     * Get Mod config instance.
     * @return Mod config instance
     */
    public static ModConfig getInstance() {
        if (instance == null) instance = new ModConfig();
        return instance;
    }
    /* ======================================== */


    /* ======================================== */
    public final Settings<ModConfig>.MutableValue<ResourceLocation> iconLocation;

    /**
     * Mod config constructor
     */
    private ModConfig() {
        this(Settings.loadFromFile(MOD_CONFIG));
    }

    /**
     * Mod config constructor
     */
    private ModConfig(Properties properties) {
        super(properties);
        this.iconLocation = this.getMutable(
            "icon-location", ResourceLocation::tryParse,
            (Minecraft.ON_OSX ? ORIGINAL_MAC_ICON : ORIGINAL_WIN_ICON)
        );
    }
    /* ======================================== */

    @Override
    protected ModConfig reload(RegistryAccess registryAccess, Properties properties) {
        instance = new ModConfig(properties);
        instance.store(MOD_CONFIG);
        return getInstance();
    }

    /* ======================================== */
    /**
     * Apply and save icon setting.
     *
     * @param client Minecraft object
     * @param icon Icon Resource location
     * @throws IOException InputStream open failed
     */
    public static void changeIcon(Minecraft client, ResourceLocation icon) throws IOException {
        final IoSupplier<InputStream> iconSupplier = ResourceIo.create(client.getResourceManager().getResource(icon).get());
        if (Minecraft.ON_OSX) {
            MacosUtil.loadIcon(iconSupplier);
        } else {
            client.getWindow().setIcon(iconSupplier, iconSupplier);
        }
        ModConfig.getInstance().iconLocation.update(null, icon);
    }
    /* ======================================== */
}
