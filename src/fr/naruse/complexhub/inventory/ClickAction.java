package fr.naruse.complexhub.inventory;

import com.google.common.collect.Lists;
import fr.naruse.complexhub.main.HubPlugin;
import fr.naruse.servermanager.core.CoreServerType;
import fr.naruse.servermanager.core.ServerManager;
import fr.naruse.servermanager.core.config.Configuration;
import fr.naruse.servermanager.core.connection.packet.PacketSwitchServer;
import fr.naruse.servermanager.core.server.Server;
import fr.naruse.servermanager.core.server.ServerList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class ClickAction<T> {

    public static final ClickAction NONE = new ClickAction() {
        @Override
        public void run(JavaPlugin pl, Player p, List list) {

        }
    };
    public static final ClickAction CLOSE_INVENTORY = new ClickAction() {
        @Override
        public void run(JavaPlugin pl, Player p, List list) {
            p.closeInventory();
        }
    };
    public static final ClickAction<Number> OPEN_INVENTORY = new ClickAction("inventoryName") {
        @Override
        public void run(JavaPlugin pl, Player p, List list) {
            Configuration configuration = ((HubPlugin) pl).getConfigurationMap().get(list.get(0));
            if(configuration == null){
                return;
            }
            new InventoryCustom(pl, p, configuration);
        }
    };
    public static final ClickAction<Integer> TELEPORT_TO = new ClickAction("x", "y", "z", "yaw", "pitch") {
        @Override
        public void run(JavaPlugin pl, Player p, List list) {
            p.teleport(new Location(p.getWorld(), Double.valueOf(list.get(0).toString()), Double.valueOf(list.get(1).toString()), Double.valueOf(list.get(2).toString()), Float.valueOf(list.get(3).toString()), Float.valueOf(list.get(4).toString())));
            p.closeInventory();
        }
    };
    public static final ClickAction<String> SWITCH_SERVER = new ClickAction("templateBaseName", "sortType") {

        @Override
        public void run(JavaPlugin pl, Player p, List list) {
            String templateBaseName = (String) list.get(0);
            ServerList.SortType sortType = ServerList.SortType.valueOf((String) list.get(1));

            Optional<Server> optional = ServerList.findServer(new CoreServerType[]{CoreServerType.SPONGE_MANAGER, CoreServerType.BUKKIT_MANAGER}, sortType, templateBaseName, server -> !server.equals(ServerManager.get().getCurrentServer()));
            if(optional.isPresent()){
                Optional<Server> proxyOptional = ServerList.findPlayerProxyServer(p.getName());
                if(proxyOptional.isPresent()){
                    proxyOptional.get().sendPacket(new PacketSwitchServer(optional.get(), p.getName()));
                }else{
                    p.closeInventory();
                    p.sendMessage("§cNo proxy found.");
                }
            }else{
                p.closeInventory();
                p.sendMessage("§cNo server found.");
            }
        }
    };

    public static ClickAction getByName(String name){
        if(name.equals("NONE")){
            return NONE;
        }else if(name.equals("CLOSE_INVENTORY")){
            return CLOSE_INVENTORY;
        }else if(name.equals("OPEN_INVENTORY")){
            return OPEN_INVENTORY;
        }else if(name.equals("TELEPORT_TO")){
            return TELEPORT_TO;
        }else if(name.equals("SWITCH_SERVER")){
            return SWITCH_SERVER;
        }
        return null;
    }

    private String[] paths;

    private ClickAction() { }

    private ClickAction(String... paths) {
        this.paths = paths;
    }

    public abstract void run(JavaPlugin pl, Player p, List<T> list);

    public ClickAction<T> build(JavaPlugin pl, Configuration.ConfigurationSection section){
        List<T> list = Lists.newArrayList();
        if(paths != null){
            for (String path : paths) {
                list.add(section.get(path));
            }
        }
        ClickAction clickAction = this;
        return new ClickAction<T>() {
            @Override
            public void run(JavaPlugin pl, Player p, List<T> l) {
                clickAction.run(pl, p, list);
            }
        };
    }
}
