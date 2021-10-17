package me.petterim1.maps;

import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.utils.Utils;

public class ClientboundMapItemDataPacketC extends ClientboundMapItemDataPacket {

    public void encode() {
        this.reset();
        this.putEntityUniqueId(mapId);

        int update = 0;
        if (eids.length > 0) {
            update |= 0x08;
        }
        if (decorators.length > 0) {
            update |= DECORATIONS_UPDATE;
        }

        if (image != null || colors.length > 0) {
            update |= TEXTURE_UPDATE;
        }

        this.putUnsignedVarInt(update);
        this.putByte(this.dimensionId);
        this.putBoolean(this.isLocked);

        if ((update & 0x08) != 0) {
            this.putUnsignedVarInt(eids.length);
            for (int eid : eids) {
                this.putEntityUniqueId(eid);
            }
        }
        if ((update & (6)) != 0) {
            this.putByte(this.scale);
        }

        if ((update & DECORATIONS_UPDATE) != 0) {
            this.putUnsignedVarInt(0);

            this.putUnsignedVarInt(decorators.length);

            for (ClientboundMapItemDataPacket.MapDecorator decorator : decorators) {
                this.putByte(decorator.icon);
                this.putByte(decorator.rotation);
                this.putByte(decorator.offsetX);
                this.putByte(decorator.offsetZ);
                this.putString(decorator.label);
                this.putUnsignedVarInt(decorator.color.getRGB());
            }
        }

        if ((update & TEXTURE_UPDATE) != 0) {
            this.putVarInt(width);
            this.putVarInt(height);
            this.putVarInt(offsetX);
            this.putVarInt(offsetZ);

            this.putUnsignedVarInt((long) width * height);

            if (image != null) {
                for (int y = 0; y < width; y++) {
                    for (int x = 0; x < height; x++) {
                        this.putUnsignedVarInt(Utils.toABGR(this.image.getRGB(x, y)));
                    }
                }

                image.flush();
            } else if (colors.length > 0) {
                for (int color : colors) {
                    this.putUnsignedVarInt(color);
                }
            }
        }
    }
}
