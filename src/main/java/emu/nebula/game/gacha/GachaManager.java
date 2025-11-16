package emu.nebula.game.gacha;

import java.util.Collection;

import emu.nebula.Nebula;
import emu.nebula.data.GameData;
import emu.nebula.data.resources.GachaDef;
import emu.nebula.game.player.Player;
import emu.nebula.game.player.PlayerChangeInfo;
import emu.nebula.game.player.PlayerManager;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class GachaManager extends PlayerManager {
    private final Int2ObjectMap<GachaBannerInfo> bannerInfos;
    private boolean loaded;
    
    public GachaManager(Player player) {
        super(player);
        this.bannerInfos = new Int2ObjectOpenHashMap<>();
    }
    
    public synchronized GachaBannerInfo getBannerInfoById(int id) {
        if (!this.loaded) {
            this.loadFromDatabase();
        }
        
        return this.bannerInfos.get(id);
    }
    
    public synchronized Collection<GachaBannerInfo> getBannerInfos() {
        if (!this.loaded) {
            this.loadFromDatabase();
        }
        
        return this.bannerInfos.values();
    }
    
    public synchronized GachaBannerInfo getBannerInfo(GachaDef gachaData) {
        if (!this.loaded) {
            this.loadFromDatabase();
        }
        
        return this.bannerInfos.computeIfAbsent(
            gachaData.getId(), 
            i -> new GachaBannerInfo(this.getPlayer(), gachaData)
        );
    }
    
    public PlayerChangeInfo recvGuarantee(int id) {
        // Get banner info
        var banner = this.getBannerInfoById(id);
        if (banner == null) {
            return null;
        }
        
        // Get banner data
        var data = GameData.getGachaDataTable().get(id);
        if (data == null) {
            return null;
        }
        
        // Check if we have enough pulls for a guarantee
        if (!data.canGuarantee() || banner.getTotal() < data.getGuaranteeTimes()) {
            return null;
        }
        
        // Make sure we havent used our guarantee yet
        if (banner.isUsedGuarantee()) {
            return null;
        }
        
        // Set guarantee
        banner.setUsedGuarantee(true);
        banner.save();
        
        // Give player the guaranteed item
        return getPlayer().getInventory().addItem(data.getGuaranteeTid(), data.getGuaranteeQty());
    }
    
    // Database
    
    private void loadFromDatabase() {
        var db = Nebula.getGameDatabase();
        
        db.getObjects(GachaBannerInfo.class, "playerUid", getPlayerUid()).forEach(bannerInfo -> {
            this.bannerInfos.put(bannerInfo.getBannerId(), bannerInfo);
        });
        
        this.loaded = true;
    }
}
