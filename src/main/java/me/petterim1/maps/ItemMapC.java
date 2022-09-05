package me.petterim1.maps;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMap;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.utils.MainLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ItemMapC extends ItemMap {

    private BufferedImage image;

    public ItemMapC() {
        this(0, 1);
    }

    public ItemMapC(Integer meta) {
        this(meta, 1);
    }

    public ItemMapC(Integer meta, int count) {
        this.block = null;
        this.hasMeta = true;
        this.setCompoundTag(new byte[0]);
        this.durability = 0;
        if (meta != null && meta >= 0) {
            this.meta = meta & '\uffff';
        } else {
            this.hasMeta = false;
        }
        this.count = count;
        this.name = "Map";
    }

    public void setImage(File file) throws IOException {
        setImage(ImageIO.read(file));
    }

    public void setImage(BufferedImage image) {
        try {
            if (this.getMapId() == 0) {
                Server.getInstance().getLogger().debug("Uninitialized map", new Throwable());
                this.initItem();
            }
            if (image.getHeight() != 128 || image.getWidth() != 128) {
                this.image = new BufferedImage(128, 128, image.getType());
                Graphics2D g = this.image.createGraphics();
                g.drawImage(image, 0, 0, 128, 128, null);
                g.dispose();
            } else {
                this.image = image;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(this.image, "png", baos);
            this.setNamedTag(this.getNamedTag().putByteArray("Colors", baos.toByteArray()));
            baos.close();
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }
    }

    protected BufferedImage loadImageFromNBT() {
        try {
            byte[] data = getNamedTag().getByteArray("Colors");
            image = ImageIO.read(new ByteArrayInputStream(data));
            return image;
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }
        return null;
    }

    public long getMapId() {
        CompoundTag tag = this.getNamedTag();
        if (tag == null) return 0;
        return tag.getLong("map_uuid");
    }

    public void sendImage(Player p) {
        BufferedImage image = this.image != null ? this.image : loadImageFromNBT();
        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = getMapId();
        pk.update = 2;
        pk.scale = 0;
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;
        p.dataPacket(pk);
        Server.getInstance().getScheduler().scheduleDelayedTask(null, () -> p.dataPacket(pk), 20);
    }

    public boolean trySendImage(Player p) {
        BufferedImage image = this.image != null ? this.image : loadImageFromNBT();
        if (image == null) return false;
        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = getMapId();
        pk.update = 2;
        pk.scale = 0;
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;
        p.dataPacket(pk);
        Server.getInstance().getScheduler().scheduleDelayedTask(null, () -> p.dataPacket(pk), 20);
        return true;
    }

    public Item initItem() {
        long id;
        CompoundTag compoundTag = this.getNamedTag();
        if (compoundTag == null || !compoundTag.contains("map_uuid")) {
            CompoundTag tag = new CompoundTag();
            mapCount++;
            tag.putLong("map_uuid", mapCount);
            this.setNamedTag(tag);
        } else if ((id = getMapId()) > mapCount) {
            mapCount = (int) id;
        }
        return this;
    }

    public int getId() {
        return MAP;
    }
}
