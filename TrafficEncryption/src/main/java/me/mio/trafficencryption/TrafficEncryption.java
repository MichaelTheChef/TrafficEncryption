package me.mio.trafficencryption;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.entity.Player;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class TrafficEncryption extends JavaPlugin implements Listener {
    private Map<Player, Cipher> playerCiphers;

    @Override
    public void onEnable() {
        playerCiphers = new HashMap<>();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        playerCiphers.clear();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        createCipher(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeCipher(player);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!hasCipher(player)) {
            event.disallow(Result.KICK_OTHER, "Unable to establish encrypted connection.");
        }
    }

    public void createCipher(Player player) {
        try {
            Key secretKey = generateSecretKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            playerCiphers.put(player, cipher);
        } catch (Exception e) {
            getLogger().warning("Failed to create cipher for player: " + player.getName());
        }
    }

    public void removeCipher(Player player) {
        playerCiphers.remove(player);
    }

    public boolean hasCipher(Player player) {
        return playerCiphers.containsKey(player);
    }

    public void encrypt(Player player, byte[] data) {
        try {
            Cipher cipher = playerCiphers.get(player);
            byte[] encryptedData = cipher.doFinal(data);
        } catch (Exception e) {
            getLogger().warning("Failed to encrypt data for player: " + player.getName());
        }
    }

    public void decrypt(Player player, byte[] encryptedData) {
        try {
            Cipher cipher = playerCiphers.get(player);
            byte[] decryptedData = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            getLogger().warning("Failed to decrypt data for player: " + player.getName());
        }
    }

    private Key generateSecretKey() {
        String keyString = "SzcP1fabE77FUpVM";
        byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "AES");
    }
}
