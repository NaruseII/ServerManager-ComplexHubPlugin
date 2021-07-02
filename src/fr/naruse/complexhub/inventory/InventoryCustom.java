package fr.naruse.complexhub.inventory;

import com.google.common.collect.Maps;
import fr.naruse.complexhub.utils.Utils;
import fr.naruse.servermanager.bukkit.inventory.AbstractInventory;
import fr.naruse.servermanager.core.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class InventoryCustom extends AbstractInventory {

    private final Configuration configuration;
    private final Map<Integer, ClickAction> actionMap = Maps.newHashMap();

    public InventoryCustom(JavaPlugin pl, Player p, Configuration configuration) {
        super(pl, p, configuration.get("title"), configuration.getInt("size"), false);
        this.configuration = configuration;

        this.initInventory(this.inventory);
        p.openInventory(this.inventory);
    }

    @Override
    protected void initInventory(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if(!configuration.contains(i+"")){
                continue;
            }

            Configuration.ConfigurationSection section = configuration.getSection(i+"");
            inventory.setItem(i, Utils.buildFromSection(section));

            Configuration.ConfigurationSection actionSection = section.getSection("action");
            actionMap.put(i, ClickAction.getByName(actionSection.get("type")).build(pl, actionSection));
        }
    }

    @Override
    protected void actionPerformed(Player player, ItemStack itemStack, InventoryAction inventoryAction, int i) {
        ClickAction clickAction = actionMap.get(i);
        if(clickAction == null){
            return;
        }

        clickAction.run(pl, p, null);
    }
}
