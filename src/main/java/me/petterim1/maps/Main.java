package me.petterim1.maps;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMapInfoRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Main extends PluginBase implements Listener {

    public void onEnable() {
        if (!getServer().getName().equals("Nukkit")) {
            getLogger().error("Incompatible server software!");
            return;
        }
        saveDefaultConfig();
        ItemMap.mapCount = getConfig().getInt("map_count_do_not_edit");
        Item.list[ItemID.MAP] = ItemMapC.class;
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
        Config c = getConfig();
        c.set("map_count_do_not_edit", ItemMap.mapCount);
        c.save();
    }

    @EventHandler(ignoreCancelled = true)
    public void onMapInfoRequest(PlayerMapInfoRequestEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();
        ItemMapC map = (ItemMapC) e.getMap();
        if (map.trySendImage(p)) {
            return;
        }
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        try {
            Graphics2D graphics = image.createGraphics();
            int wX = (Math.floorDiv(p.getFloorX(), 128)) << 7;
            int wZ = (Math.floorDiv(p.getFloorZ(), 128)) << 7;
            for (int x = 0; x < 128; x++) {
                int avgY = 0;
                for (int y = -1; y < 128; y++) {
                    if (y == -1) {
                        avgY = p.getLevel().getHighestBlockAt(wX + x, wZ);
                        continue;
                    }
                    int wY = p.getLevel().getHighestBlockAt(wX + x, wZ + y);
                    double avgYD = (wY - avgY) * 4 / 5 + ((x + y & 1) - 0.5) * 0.4;
                    int depth = 1;
                    if (avgYD > 0.6) {
                        depth = 2;
                    }
                    if (avgYD < -0.6) {
                        depth = 0;
                    }
                    avgY = wY;
                    graphics.setColor(Util.colorizeMapColor(p.getLevel().getMapColorAt(wX + x, wZ + y), depth));
                    graphics.fillRect(x, y, x + 1, y + 1);
                }
            }
        } catch (Exception ex) {
            getServer().getLogger().error("There was an error while generating map image", ex);
        }
        map.setImage(image);
        map.sendImage(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == PlayerInteractEvent.Action.PHYSICAL) return;
        Player p = e.getPlayer();
        if (e.getItem().getId() == ItemID.EMPTY_MAP && !p.isSpectator()) {
            if (!p.isCreative()) {
                p.getInventory().decreaseCount(p.getInventory().getHeldItemIndex());
            }
            p.getInventory().addItem(new ItemMapC().initItem());
        }
    }
}
