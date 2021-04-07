package club.veluxpvp.practice.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import club.veluxpvp.practice.arena.Claim;
import club.veluxpvp.practice.match.TeamType;

public class Serializer {

	public static String serializeLocation(Location l) {
		return l.getWorld().getName() + "|" + l.getX() + "|" + l.getY() + "|" + l.getZ() + "|" + l.getYaw() + "|" + l.getPitch();
	}
	
	public static Location deserializeLocation(String location) {
		String[] l = location.split("\\|");
		
		World world = Bukkit.getWorld(l[0]);
		double x = Double.parseDouble(l[1]);
		double y = Double.parseDouble(l[2]);
		double z = Double.parseDouble(l[3]);
		float yaw = Float.parseFloat(l[4]);
		float pitch = Float.parseFloat(l[5]);
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public static String serializeCuboid(Cuboid c) {
		String string = "";
		
		string = c.worldName + "|";
		string += c.x1 + "|";
		string += c.y1 + "|";
		string += c.z1 + "|";
		string += c.x2 + "|";
		string += c.y2 + "|";
		string += c.z2;
		
		return string;
	}
	
	public static Cuboid deserializeCuboid(String string) {
		String[] s = string.split("\\|");
		
		String worldName = s[0];
		
		int x1 = Integer.parseInt(s[1]);
		int y1 = Integer.parseInt(s[2]);
		int z1 = Integer.parseInt(s[3]);
		
		int x2 = Integer.parseInt(s[4]);
		int y2 = Integer.parseInt(s[5]);
		int z2 = Integer.parseInt(s[6]);
		
		Location l1 = new Location(Bukkit.getWorld(worldName), x1, y1, z1);
		Location l2 = new Location(Bukkit.getWorld(worldName), x2, y2, z2);
		
		return new Cuboid(l1, l2);
	}
	
	public static String serializeClaim(Claim c) {
		String corner1 = c.getCorner1().getWorld().getName() + "|" + c.getCorner1().getBlockX() + "|" + c.getCorner1().getBlockY() + "|" + c.getCorner1().getBlockZ();
		String corner2 = c.getCorner2().getWorld().getName() + "|" + c.getCorner2().getBlockX() + "|" + c.getCorner2().getBlockY() + "|" + c.getCorner2().getBlockZ();
		return corner1 + "|" + corner2;
	}
	
	public static Claim deserializeClaim(TeamType ownerTeam, String string) {
		String[] c = string.split("\\|");
		
		Location corner1 = new Location(Bukkit.getWorld(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2]), Integer.parseInt(c[3]));
		Location corner2 = new Location(Bukkit.getWorld(c[4]), Integer.parseInt(c[5]), Integer.parseInt(c[6]), Integer.parseInt(c[7]));
	
		return new Claim(ownerTeam, corner1, corner2);
	}
	
    public static String serializeItemStackArray(ItemStack[] items) throws IllegalStateException {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            // Write the size of the inventory
            dataOutput.writeInt(items.length);
            
            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }
            
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    
    public static ItemStack[] deserializeItemStackArray(String data) throws IOException {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
    
            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
            	items[i] = (ItemStack) dataInput.readObject();
            }
            
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
