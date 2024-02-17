package me.petterim1.maps;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockGrass;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.awt.*;

public class Util {

    static int getColorAt(Level l, int x, int z) {
        FullChunk c = l.getChunk(x >> 4, z >> 4, true);
        Vector3 pos = new Vector3(x, getHighestBlockInChunk(c, x & 0x0f, z & 0x0f), z);
        while (pos.y > 1) {
            Block block = l.getBlock(pos);
            if (block instanceof BlockGrass) {
                return getGrassColorAt(l, x, z);
            } else {
                BlockColor blockColor = block.getColor();
                if (blockColor.getAlpha() == 0x00) {
                    pos.y--;
                } else {
                    return blockColor.getRGB();
                }
            }
        }
        return BlockColor.VOID_BLOCK_COLOR.getRGB();
    }

    private static int getGrassColorAt(Level l, int x, int z) {
        int biome = l.getBiomeId(x, z);
        switch (biome) {
            case 0:
            case 7:
            case 9:
            case 24:
                return getColor("#8eb971");
            case 1:
            case 16:
            case 129:
                return getColor("#91bd59");
            case 2:
            case 8:
            case 17:
            case 35:
            case 36:
            case 130:
            case 163:
            case 164:
                return getColor("#bfb755");
            case 3:
            case 20:
            case 25:
            case 34:
            case 131:
            case 162:
                return getColor("#8ab689");
            case 4:
            case 132:
                return getColor("#79c05a");
            case 5:
            case 19:
            case 32:
            case 33:
            case 133:
            case 160:
                return getColor("#86b783");
            case 6:
            case 134:
                return getColor("#6A7039");
            case 10:
            case 11:
            case 12:
            case 30:
            case 31:
            case 140:
            case 158:
                return getColor("#80b497");
            case 14:
            case 15:
                return getColor("#55c93f");
            case 18:
            case 27:
            case 28:
            case 155:
            case 156:
                return getColor("#88bb67");
            case 21:
            case 22:
            case 149:
                return getColor("#59c93c");
            case 23:
            case 151:
                return getColor("#64c73f");
            case 26:
                return getColor("#83b593");
            case 29:
            case 157:
                return getColor("#507a32");
            case 37:
            case 38:
            case 39:
            case 165:
            case 166:
            case 167:
                return getColor("#90814d");
            default:
                return BlockColor.GRASS_BLOCK_COLOR.getRGB();
        }
    }

    private static int getColor(String c) {
        int red = Integer.valueOf(c.substring(1, 3), 16);
        int green = Integer.valueOf(c.substring(3, 5), 16);
        int blue = Integer.valueOf(c.substring(5, 7), 16);
        return (red << 16 | green << 8 | blue) & 0xffffff;
    }

    private static int getHighestBlockInChunk(FullChunk c, int x, int z) {
        for (int y = 255; y >= 0; --y) {
            if (c.getBlockId(x, y, z) != 0x00) {
                return y;
            }
        }
        return 0;
    }

    static Color colorizeMapColor(BlockColor color, int level) {
        int depth;

        if (level == 2) {
            depth = 255;
        } else if (level == 1) {
            depth = 220;
        } else if (level == 0) {
            depth = 180;
        } else {
            throw new IllegalArgumentException("Invalid level: " + level);
        }

        int r = color.getRed() * depth / 255;
        int g = color.getGreen() * depth / 255;
        int b = color.getBlue() * depth / 255;

        return new Color(r, g, b);
    }
}
