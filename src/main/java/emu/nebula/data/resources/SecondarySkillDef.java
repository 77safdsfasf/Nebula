package emu.nebula.data.resources;

import java.util.ArrayList;
import java.util.List;

import emu.nebula.data.BaseDef;
import emu.nebula.data.ResourceType;
import emu.nebula.game.inventory.ItemParamMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
@ResourceType(name = "SecondarySkill.json")
public class SecondarySkillDef extends BaseDef {
    private int Id;
    private int GroupId;
    private int Score;
    private String NeedSubNoteSkills;
    
    private transient ItemParamMap reqSubNotes;
    
    @Getter
    private static transient Int2ObjectMap<List<SecondarySkillDef>> groups = new Int2ObjectOpenHashMap<>();
    
    @Override
    public int getId() {
        return Id;
    }
    
    public boolean match(ItemParamMap subNotes) {
        for (var item : this.reqSubNotes) {
            int reqId = item.getIntKey();
            int reqCount = item.getIntValue();
            
            int curCount = subNotes.get(reqId);
            if (curCount < reqCount) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void onLoad() {
        // Setup required subnotes
        this.reqSubNotes = ItemParamMap.fromJsonString(this.NeedSubNoteSkills);
        
        // Add to group cache
        var group = groups.computeIfAbsent(this.GroupId, id -> new ArrayList<>());
        group.add(this);
        
        // Clear to save memory
        this.NeedSubNoteSkills = null;
    }

    public static List<SecondarySkillDef> getGroup(int id) {
        return groups.get(id);
    }
}
