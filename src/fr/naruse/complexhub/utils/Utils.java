package fr.naruse.complexhub.utils;

import fr.naruse.servermanager.core.config.Configuration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

    public static ItemStack buildFromSection(Configuration.ConfigurationSection section){
        ItemStack itemStack = new ItemStack(Material.getMaterial(section.getInt("id")), 1, (short) section.getInt("data"));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(section.get("name"));
        if((boolean) section.get("shine")){
            meta.addEnchant(Enchantment.LUCK, 1, true);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
