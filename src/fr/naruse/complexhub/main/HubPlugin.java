package fr.naruse.complexhub.main;

import com.google.common.collect.Maps;
import fr.naruse.complexhub.inventory.InventoryCustom;
import fr.naruse.complexhub.utils.Table;
import fr.naruse.complexhub.utils.Utils;
import fr.naruse.servermanager.core.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

public class HubPlugin extends JavaPlugin implements Listener {

    private Map<String, Configuration> configurationMap = Maps.newHashMap();
    private Table<ItemStack, Configuration, Integer> itemInHandMap = new Table();

    @Override
    public void onEnable() {
        super.onEnable();

        Configuration defaultInv = new Configuration(new File(getDataFolder(), "inventory.json"), getClassLoader().getResourceAsStream("resources/inventory.json"));

        Configuration.ConfigurationSection defaultInHandSection = defaultInv.getSection("inHand");
        if((boolean) defaultInHandSection.get("giveInPlayerInventory")){
            itemInHandMap.put(Utils.buildFromSection(defaultInHandSection), defaultInv, defaultInHandSection.getInt("slot"));
        }

        configurationMap.put("inventory.json", defaultInv);
        if(getDataFolder().listFiles() != null){
            for (File file : getDataFolder().listFiles()) {
                if(!file.getName().equals("inventory.json")){

                    Configuration config = new Configuration(file, false);
                    Configuration.ConfigurationSection inHandSection = config.getSection("inHand");
                    if((boolean) inHandSection.get("giveInPlayerInventory")){
                        itemInHandMap.put(Utils.buildFromSection(inHandSection), config, inHandSection.getInt("slot"));
                    }

                    configurationMap.put(file.getName(), config);
                }
            }
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void join(PlayerJoinEvent e){
        Player p = e.getPlayer();
        p.getInventory().clear();
        p.getEquipment().clear();
        for (ItemStack itemStack : itemInHandMap.keySet()) {
            p.getInventory().setItem(itemInHandMap.getThird(itemStack), itemStack);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(e.getItem() != null && itemInHandMap.keySet().contains(e.getItem())){
            e.setCancelled(true);

            new InventoryCustom(this, p, itemInHandMap.get(e.getItem()));
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e){
        if(e.getCurrentItem() != null && itemInHandMap.keySet().contains(e.getCurrentItem())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e){
        if(itemInHandMap.keySet().contains(e.getItemDrop().getItemStack())){
            e.setCancelled(true);
        }
    }

    public Map<String, Configuration> getConfigurationMap() {
        return configurationMap;
    }

    public Table<ItemStack, Configuration, Integer> getItemInHandMap() {
        return itemInHandMap;
    }
}
