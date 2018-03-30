package red.man10;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
public class Man10bag extends JavaPlugin {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
			        getServer().getPluginManager().disablePlugin(this);
			        getServer().getPluginManager().enablePlugin(this);
			        getLogger().info(prefix+"§a設定を再読み込みしました。");
			        return true;
				}
				 getLogger().info(prefix+ChatColor.RED+"mbag reload");
			return true;
			}
		}
		Player p = (Player)sender;
		if(args.length == 3) {
			if(args[0].equalsIgnoreCase("reissue")) {
				String bagname = "§8"+args[1];
		          if(bags.containsKey(bagname)==false) {
		        	  p.sendMessage(prefix+"§4そのバッグ名は存在しません！");
		        	  return true;
		          }
		          if(config1.getString("invs."+bagname+".code").equalsIgnoreCase(args[2])==false) {
		        	  p.sendMessage(prefix+"§4再発行コードが間違っています！");
		        	  return true;
		          }
		            if(Vault.economy.getBalance(p)<baldata.get("reissue")) {
		            	p.sendMessage(prefix+"§4お金が足りません!");
		            	return true;
		            }
		            Vault.economy.withdrawPlayer(p, baldata.get("reissue"));
		          ItemStack hand = config1.getItemStack("invs."+bagname+".key");
		          p.getInventory().addItem(hand);
		          p.sendMessage(prefix+"§a再発行しました。もう無くさないでね！");
				return true;
			}
          String bagname = "§8"+args[0];
          if(bags.containsKey(bagname)||bagname.equalsIgnoreCase("reissue")) {
        	  p.sendMessage(prefix+"§4すでにそのバッグは作成されています！");
        	  return true;
          }
			int size = 0;
            try {
            size = Integer.parseInt(args[1]);
			  }catch (NumberFormatException f){
			   p.sendMessage(prefix+"§4数字で入力してください。");
			   return true;
			}
            if(size!=9&&size!=18&&size!=27&&size!=36&&size!=45&&size!=54) {
            	p.sendMessage(prefix+"§49の倍数でかつ、54以下にしてください。");
            	return true;
            }
            if(Vault.economy.getBalance(p)<baldata.get(size+"")) {
            	p.sendMessage(prefix+"§4お金が足りません!");
            	return true;
            }
            Vault.economy.withdrawPlayer(p, baldata.get(size+""));
            ItemStack hand = new ItemStack(Material.GOLD_BARDING);
            ItemMeta handmeta = hand.getItemMeta();
            handmeta.setDisplayName(bagname);
            List<String> k = new ArrayList<String>();
            k.add("§6サイズ: "+size);
            k.add("§e作成者: "+p.getName());
            k.add("§b右クリックでGUIが開きます。");
            handmeta.setLore(k);
            hand.setItemMeta(handmeta);
            p.getInventory().addItem(hand);
            config1.set("invs."+bagname+".key",hand);
            config1.set("invs."+bagname+".size",size);
            config1.set("invs."+bagname+".code",args[2]);
            bag.put(hand, bagname);
            Inventory inv = Bukkit.createInventory((InventoryHolder)null, size, bagname);
            bags.put(bagname, inv);
            p.sendMessage(prefix+"§e$"+baldata.get(size+"")+"§a支払い、"+bagname+"§aバッグを作成しました！");
            saveConfig();
            return true;
		}else if(args.length == 4) {
	          String bagname = "§8"+args[0];
	          if(bags.containsKey(bagname)) {
	        	  p.sendMessage(prefix+"§4すでにそのバッグは作成されています！");
	        	  return true;
	          }
				int size = 0;
	            try {
	            size = Integer.parseInt(args[1]);
				  }catch (NumberFormatException f){
				   p.sendMessage(prefix+"§4数字で入力してください。");
				   return true;
				}
	            if(size!=9&&size!=18&&size!=27&&size!=36&&size!=45&&size!=54) {
	            	p.sendMessage(prefix+"§49の倍数でかつ、54以下にしてください。");
	            	return true;
	            }
	            int bal = baldata.get(size+"")+baldata.get("memo");
	            if(Vault.economy.getBalance(p)<bal) {
	            	p.sendMessage(prefix+"§4お金が足りません!");
	            	return true;
	            }
	            Vault.economy.withdrawPlayer(p, bal);
	            ItemStack hand = new ItemStack(Material.GOLD_BARDING);
	            ItemMeta handmeta = hand.getItemMeta();
	            handmeta.setDisplayName(bagname);
	            List<String> k = new ArrayList<String>();
	            k.add("§6サイズ: "+size);
	            k.add("§e作成者: "+p.getName());
	            k.add("§aメモ: §e"+args[2]);
	            k.add("§b右クリックでGUIが開きます。");
	            handmeta.setLore(k);
	            hand.setItemMeta(handmeta);
	            p.getInventory().addItem(hand);
	            config1.set("invs."+bagname+".key",hand);
	            config1.set("invs."+bagname+".size",size);
	            config1.set("invs."+bagname+".code",args[3]);
	            bag.put(hand, bagname);
	            Inventory inv = Bukkit.createInventory((InventoryHolder)null, size, bagname);
	            bags.put(bagname, inv);
	            p.sendMessage(prefix+"§e$"+bal+"§a支払い、"+bagname+"§aバッグを作成しました！");
	            saveConfig();
	            return true;
		}else if(args.length == 0) {
			p.sendMessage("=======§a§kaaa§6§l===="+prefix+"====§a§kaaa§r=======");
			p.sendMessage(prefix+"§a/mbag [バッグ名] [大きさ] [再発行コード]=> バッグを作成します");
			p.sendMessage(prefix+"§a/mbag [バッグ名] [大きさ] [メモ] [再発行コード]=> メモ付きバッグを作成します");
			p.sendMessage(prefix+"§a/mbag reissue [バッグ名] [再発行コード]=> バッグを再発行します。$10万必要");
			p.sendMessage("=======§a§kaaa§6§l====v1.1.0====§a§kaaa§r=======");
			return true;
		}
		p.sendMessage("=======§a§kaaa§6§l===="+prefix+"====§a§kaaa§r=======");
		p.sendMessage(prefix+"§a/mbag [バッグ名] [大きさ] [再発行コード]=> バッグを作成します");
		p.sendMessage(prefix+"§a/mbag [バッグ名] [大きさ] [メモ] [再発行コード]=> メモ付きバッグを作成します");
		p.sendMessage(prefix+"§a/mbag reissue [バッグ名] [再発行コード]=> バッグを再発行します。$10万必要");
		p.sendMessage("=======§a§kaaa§6§l====v1.1.0====§a§kaaa§r=======");
		return true;
	}

	@Override
	public void onDisable() {
        for(Player p:Bukkit.getOnlinePlayers()){
            if(playerState.containsKey(p.getUniqueId())) {
            	Inventory inv = p.getOpenInventory().getTopInventory();
            	if(bags.containsKey(inv.getName())) {
            	bags.put(inv.getName(), inv);
            	playerState.remove(p.getUniqueId());
                p.closeInventory();
            	}
            }
        }
        config1.set("invs", null);
		for (String name : bags.keySet()){
			Inventory inv = bags.get(name);
			config1.set("invs."+name+".code", bagcode.get(inv));
			int size = inv.getSize();
			config1.set("invs."+name+".size",size);
			for(int i = 0;i<size;i++) {
				if(inv.getItem(i)!=null) {
					ItemStack item = inv.getItem(i);
					config1.set("invs."+name+"."+i,item);
				}
			}
	    }
		for (ItemStack key : bag.keySet()){
			String name = bag.get(key);
			config1.set("invs."+name+".key",key);
		}
		saveConfig();
		super.onDisable();
	}
	public static FileConfiguration config1;
	public static FileConfiguration config2;
	String prefix = "§6§l[§a§lM§f§la§d§ln§f§l10§e§lbag§6§l]§r";
	private HashMap<UUID,Inventory> playerState;
	private HashMap<ItemStack,String> bag;
	private HashMap<String,Inventory> bags;
	private HashMap<Inventory,String> bagcode;
	private HashMap<String,Integer> baldata;
	Bags data;
	@Override
	public void onEnable() {
		new bag(this);
		new Vault(this);
		getCommand("mbag").setExecutor(this);
		saveDefaultConfig();
	    FileConfiguration config = getConfig();
        config2 = config;
        data = new Bags(this, "data.yml");
        data.saveDefaultConfig();
        FileConfiguration config4 = data.getConfig();
        config1 = config4;
        baldata = new HashMap<>();
        bagcode = new HashMap<>();
        bags = new HashMap<>();
        playerState = new HashMap<>();
        bag = new HashMap<>();
        baldata.put("9", config2.getInt("9",900000));
        baldata.put("18", config2.getInt("18",1800000));
        baldata.put("27", config2.getInt("27",2700000));
        baldata.put("36", config2.getInt("36",3600000));
        baldata.put("45", config2.getInt("45",4500000));
        baldata.put("54", config2.getInt("54",5400000));
        baldata.put("memo", config2.getInt("memo",10000));
        baldata.put("reissue", config2.getInt("reissue",100000));
        if(config1.contains("invs")) {
        for (String key : config1.getConfigurationSection("invs").getKeys(false)) {
        	int size = config1.getInt("invs."+key+".size");
        	ItemStack keyitem = config1.getItemStack("invs."+key+".key");
        	bag.put(keyitem, key);
        	Inventory inv = Bukkit.createInventory((InventoryHolder)null, size, key);
        	bagcode.put(inv, config1.getString("invs."+key+".code"));
        	for(int i = 0;i<size;i++) {
        		if(config1.contains("invs."+key+"."+i)==true) {
        			ItemStack item = config1.getItemStack("invs."+key+"."+i);
        			inv.setItem(i, item);
        		}
        	}
        	bags.put(key, inv);
        }
        }
		super.onEnable();
	}
    public class bag implements Listener{
    public bag(Man10bag plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e){
    	Player p = (Player) e.getPlayer();
        if(playerState.containsKey(p.getUniqueId())) {
    	 Inventory inv = p.getInventory();
     	 String name = inv.getName();
    	 if(bags.containsKey(name)) {
    		bags.put(name, inv);
    	 }
         p.getWorld().playSound(p.getLocation(),"block.chest.close",1.0F, 1.0F);
         p.getWorld().playSound(p.getLocation(),"block.enderchest.close",1.0F, 1.0F);
         p.getWorld().playSound(p.getLocation(),"block.enchantment_table.use",1.0F, 1.0F);
    	}
        playerState.remove(e.getPlayer().getUniqueId());
    	return;
    }
    @EventHandler
    public void onclick(InventoryClickEvent e){
        Player p= (Player) e.getWhoClicked();
        if(playerState.containsKey(p.getUniqueId())) {
        	if(e.getCurrentItem()==null) {
        		return;
        	}
        	if(!e.getCurrentItem().getType().equals(Material.GOLD_BARDING)) {
        		return;
        	}
        	if(e.getCurrentItem().getItemMeta().equals(null)) {
        	   return;
        	}
        	if(e.getCurrentItem().getItemMeta().hasDisplayName()==false) {
        	   return;
        	}
        	if(e.getCurrentItem().getItemMeta().getDisplayName().equals(e.getView().getTopInventory().getName())) {
               e.setCancelled(true);
               return;
        	}
        	if(e.getCursor()==null) {
        		return;
        	}
        	if(!e.getCursor().getType().equals(Material.GOLD_BARDING)) {
        		return;
        	}
        	if(e.getCursor().getItemMeta().equals(null)) {
        	   return;
        	}
        	if(e.getCursor().getItemMeta().hasDisplayName()==false) {
        	   return;
        	}
        	if(e.getCursor().getItemMeta().getDisplayName().equals(e.getView().getTopInventory().getName())) {
               e.setCancelled(true);
               return;
        	}
        }
        return;
    }
	@EventHandler
    public void onInteract(PlayerInteractEvent e) {
		Player p = (Player)e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_AIR||e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack a =p.getInventory().getItemInMainHand();
			for (ItemStack item : bag.keySet()) {
					if(item.getType() == a.getType()) {
						if(item.getItemMeta() == null || item.getItemMeta().toString().equalsIgnoreCase(a.getItemMeta().toString())) {
							e.setCancelled(true);
							String key = bag.get(item);
							Inventory inv = bags.get(key);
					        p.getWorld().playSound(p.getLocation(),"block.chest.open",1.0F, 1.0F);
					        p.getWorld().playSound(p.getLocation(),"block.end_portal_frame.fill",1.0F, 1.0F);
					        p.getWorld().playSound(p.getLocation(),"block.enderchest.open",1.0F, 1.0F);
							p.openInventory(inv);
                            playerState.put(p.getUniqueId(),inv);
							return;
						}
					}
			}
		}
	}
    }
}
