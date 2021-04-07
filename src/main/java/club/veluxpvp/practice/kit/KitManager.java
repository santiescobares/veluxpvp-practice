package club.veluxpvp.practice.kit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import club.veluxpvp.practice.Practice;
import club.veluxpvp.practice.Settings;
import club.veluxpvp.practice.item.ItemManager;
import club.veluxpvp.practice.kit.menu.listener.EditKitsMenuListener;
import club.veluxpvp.practice.utilities.ChatUtil;
import club.veluxpvp.practice.utilities.PlayerUtil;
import club.veluxpvp.practice.utilities.Serializer;
import lombok.Getter;

public class KitManager {

	@Getter private Set<Kit> kits;
	@Getter private Map<UUID, KitType> editingKit;
	
	public KitManager() {
		this.kits = Sets.newHashSet();
		this.editingKit = Maps.newConcurrentMap();
	
		loadKits();
	}
	
	public Set<Kit> getDefaultKits() {
		return this.kits.stream().filter(k -> k.isDefault()).collect(Collectors.toSet());
	}
	
	public Kit getDefaultKit(KitType type) {
		return this.getDefaultKits().stream().filter(k -> k.getType() == type).findFirst().orElse(null);
	}
	
	public Kit getKitByID(String id) {
		return this.kits.stream().filter(k -> k.getId().equals(id)).findFirst().orElse(null);
	}
	
	public Set<Kit> getAllPlayerKits(OfflinePlayer player) {
		return this.kits.stream().filter(k -> k.getOwnerUUID() != null && k.getOwnerUUID().equals(player.getUniqueId())).collect(Collectors.toSet());
	}
	
	public Set<Kit> getKitsOfType(OfflinePlayer player, KitType type) {
		return this.getAllPlayerKits(player).stream().filter(k -> k.getType() == type).collect(Collectors.toSet());
	}
	
	public int getKitNumberBySlot(int slot) {
		return slot == 2 ? 1 : slot == 3 ? 2 : slot == 4 ? 3 : slot == 5 ? 4 : 5;
	}
	
	public Kit getClickedKit(OfflinePlayer player, KitType type, int slot) {
		return this.getKitsOfType(player, type).stream().filter(k -> k.getNumber() == this.getKitNumberBySlot(slot)).findFirst().orElse(null);
	}
	
	public boolean isEditingKit(Player player) {
		return this.editingKit.containsKey(player.getUniqueId());
	}
	
	public KitType getEditingKitType(Player player) {
		return this.editingKit.get(player.getUniqueId());
	}
	
	public void saveAsync() {
		Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> this.saveKits());
	}
	
	public void sendToKitEditorRoom(Player player, KitType kitType) {
		this.editingKit.put(player.getUniqueId(), kitType);
		
		PlayerUtil.reset(player, GameMode.SURVIVAL, true);
		PlayerUtil.sendToKitEditorSpawn(player);
		Kit defaultKit = this.getDefaultKit(kitType);
		
		if(defaultKit != null) defaultKit.apply(player, true);
		
		player.sendMessage(" ");
		player.sendMessage(ChatUtil.TRANSLATE("&b&lKit Editor Room"));
		player.sendMessage(" ");
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fClick the anvil to view your kits"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fClick the chest to customize your current kit"));
		player.sendMessage(ChatUtil.TRANSLATE(" &7* &fClick the sign to back to the spawn"));
		player.sendMessage(" ");
	}
	
	public void sendAgainToSpawn(Player player, boolean cancelled) {
		this.editingKit.remove(player.getUniqueId());
		EditKitsMenuListener.renamingKit.remove(player.getUniqueId());
		
		if(!cancelled) {
			PlayerUtil.reset(player, GameMode.SURVIVAL, true);
			PlayerUtil.sendToSpawn(player);
			player.sendMessage(ChatUtil.TRANSLATE("&aTeleported to the spawn!"));
			
			if(Practice.getInstance().getPartyManager().getPlayerParty(player) != null) {
				ItemManager.loadPartyItems(player);
			} else {
				ItemManager.loadLobbyItems(player);
			}
		}
	}
	
	public void loadKits() {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM " + Settings.KITS_TABLE);
			ResultSet r = s.executeQuery();
			
			while(r.next()) {
				UUID ownerUUID = r.getString("OwnerUUID").equals("") ? null : UUID.fromString(r.getString("OwnerUUID"));
				KitType type = KitType.valueOf(r.getString("Type").toUpperCase());
				boolean Default = r.getBoolean("IsDefault");
				
				Kit kit = new Kit(ownerUUID, type, Default, r.getInt("Number"));
				
				kit.setId(r.getString("ID"));
				kit.setDisplayName(r.getString("DisplayName"));
				if(!r.getString("Contents").equals("")) kit.setContents(Serializer.deserializeItemStackArray(r.getString("Contents")));
				if(!r.getString("ArmorContents").equals("")) kit.setArmorContents(Serializer.deserializeItemStackArray(r.getString("ArmorContents")));
			
				this.kits.add(kit);
			}
			
			r.close();
			s.close();
		} catch(SQLException | IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&b[Practice] &cAn error has ocurred while loading kits from the database!"));
			e.printStackTrace();
		}
	}
	
	public void saveKits() {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		deleteUnexistingKits();
		
		try {
			for(Kit k : this.kits) {
				if(!kitExists(k.getId())) {
					PreparedStatement s = connection.prepareStatement("INSERT INTO " + Settings.KITS_TABLE + " VALUE (?,?,?,?,?,?,?,?,?)");
					
					s.setString(1, k.getOwnerUUID() == null ? "" : k.getOwnerUUID().toString());
					s.setString(2, k.getOwnerUUID() == null ? "" : Bukkit.getOfflinePlayer(k.getOwnerUUID()).getName());
					s.setString(3, k.getId());
					s.setString(4, k.getDisplayName());
					s.setString(5, k.getType().name());
					s.setInt(6, k.getNumber());
					s.setBoolean(7, k.isDefault());
					s.setString(8, k.getContents() == null ? "" : Serializer.serializeItemStackArray(k.getContents()));
					s.setString(9, k.getArmorContents() == null ? "" : Serializer.serializeItemStackArray(k.getArmorContents()));
					
					s.executeUpdate();
					
					s.close();
				} else {
					PreparedStatement s = connection.prepareStatement("UPDATE " + Settings.KITS_TABLE + " SET OwnerUUID=?, Name=?, DisplayName=?, Type=?, Number=?, IsDefault=?, Contents=?, ArmorContents=? WHERE ID=?");
					
					s.setString(1, k.getOwnerUUID() == null ? "" : k.getOwnerUUID().toString());
					s.setString(2, k.getOwnerUUID() == null ? "" : Bukkit.getOfflinePlayer(k.getOwnerUUID()).getName());
					s.setString(3, k.getDisplayName());
					s.setString(4, k.getType().name());
					s.setInt(5, k.getNumber());
					s.setBoolean(6, k.isDefault());
					s.setString(7, k.getContents() == null ? "" : Serializer.serializeItemStackArray(k.getContents()));
					s.setString(8, k.getArmorContents() == null ? "" : Serializer.serializeItemStackArray(k.getArmorContents()));
					s.setString(9, k.getId());
					
					s.executeUpdate();
					
					s.close();
				}
			}
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&b[Practice] &cAn error has ocurred while saving kits into the database!"));
			e.printStackTrace();
		}
	}
	
	public void deleteUnexistingKits() {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM " + Settings.KITS_TABLE);
			ResultSet r = s.executeQuery();
			
			while(r.next()) {
				String id = r.getString("ID");
				
				if(this.getKitByID(id) == null) {
					PreparedStatement s2 = connection.prepareStatement("DELETE FROM " + Settings.KITS_TABLE + " WHERE ID=?");
					
					s2.setString(1, id);
					s2.executeUpdate();
					s2.close();
				}
			}
		} catch(SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatUtil.TRANSLATE("&b[Practice] &cAn error has ocurred while deleting unexisting kits from the database!"));
			e.printStackTrace();
		}
	}
	
	public boolean kitExists(String id) {
		Connection connection = Practice.getInstance().getMySQLManager().getConnection();
		
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM " + Settings.KITS_TABLE + " WHERE ID=?");
			
			s.setString(1, id);
			
			if(s.executeQuery().next()) {
				s.close();
				return true;
			} else {
				s.close();
				return false;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
