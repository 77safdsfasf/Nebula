package emu.nebula.game.infinitytower;

import dev.morphia.annotations.Id;
import emu.nebula.data.GameData;
import emu.nebula.game.player.Player;
import emu.nebula.game.player.PlayerChangeInfo;
import emu.nebula.game.player.PlayerManager;

import lombok.Getter;

@Getter
public class InfinityTowerManager extends PlayerManager {
    @Id
    private int uid;
    
    private transient int levelId;
    private transient long buildId;
    
    @Deprecated // Morphia only
    public InfinityTowerManager() {
        
    }
    
    public InfinityTowerManager(Player player) {
        super(player);
        this.uid = player.getUid();
        
        //this.save();
    }
    
    public int getBountyLevel() {
        return 1;
    }
    
    public boolean apply(int levelId, long buildId) {
        // Verify level data
        var data = GameData.getInfinityTowerLevelDataTable().get(levelId);
        if (data == null) {
            return false;
        }
        
        // Set level id
        this.levelId = levelId;
        
        // Set build id
        if (buildId >= 0) {
            this.buildId = buildId;
        }
        
        // Success
        return true;
    }

    public PlayerChangeInfo settle(int value) {
        // Verify level data
        var data = GameData.getInfinityTowerLevelDataTable().get(this.getLevelId());
        if (data == null) {
            return null;
        }
        
        // Init change info
        var change = new PlayerChangeInfo();
        
        // TODO
        if (value != 1) {
            return change;
        }
        
        // Calculate rewards
        var rewards = data.generateRewards();
        
        // Add items
        this.getPlayer().getInventory().addItems(rewards, change);
        
        // Set in change info
        change.setExtraData(rewards);
        
        // Success
        return change.setSuccess(true);
    }

}
