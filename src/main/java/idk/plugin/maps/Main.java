package idk.plugin.maps;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockGrass;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMapInfoRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMap;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.BlockColor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Main extends PluginBase implements Listener {

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void mapInfo(PlayerMapInfoRequestEvent e) {
        Player p = e.getPlayer();
        ItemMap map = (ItemMap) e.getMap();
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		try {
            Graphics2D graphics = image.createGraphics();
            for (int x = 0; x < 128; x++) {
                for (int y = 0; y < 128; y++) {
                    graphics.setColor(new Color(getColorAt(p.getLevel(), p.getFloorX() - 64 + x, p.getFloorZ() - 64 + y)));
                    graphics.fillRect(x, y, x, y);
                }
            }
		} catch (Exception ignored) {}
        map.setImage(image);
    }

    @EventHandler
    public boolean onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Item i = e.getItem();
        if (i.getId() == Item.EMPTY_MAP) {
            if (!p.isCreative()) {
                new NukkitRunnable() {
                    public void run() {
                        p.getInventory().removeItem(i);
                        p.getInventory().addItem(new ItemMap());
                    }
                }.runTaskLater(null, 1);
            } else {
                p.getInventory().addItem(new ItemMap());
            }
        }
        return true;
    }

    private int getColorAt(Level l, int x, int z) {
        int y = l.getHighestBlockAt(x, z);

        while (y > 1) {
                Block block = l.getBlock(new Vector3(x, y, z));
                if (block instanceof BlockGrass) {
                    return getGrassColorAt(l, x, z);
                } else {
                    BlockColor blockColor = block.getColor();
                    if (blockColor.getAlpha() == 0x00) {
                        y--;
                    } else {
                        return blockColor.getRGB();
                    }
                }
            }

        return BlockColor.VOID_BLOCK_COLOR.getRGB();
    }

    private int getGrassColorAt(Level l, int x, int z) {
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

    private int getColor(String c) {
        int red = Integer.valueOf(c.substring(1, 3), 16);
        int green = Integer.valueOf(c.substring(3, 5), 16);
        int blue = Integer.valueOf(c.substring(5, 7), 16);
        return (red << 16 | green << 8 | blue) & 0xffffff;
    }
}
