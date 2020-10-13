package me.petterim1.maps;

import cn.nukkit.Player;
import cn.nukkit.item.ItemMap;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;

import java.awt.image.BufferedImage;

public class ItemMapC extends ItemMap {

    private BufferedImage image;

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
        return true;
    }
}
