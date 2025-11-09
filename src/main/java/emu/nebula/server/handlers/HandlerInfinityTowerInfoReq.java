package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.InfinityTowerInfo.InfinityTowerInfoResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.infinity_tower_info_req)
public class HandlerInfinityTowerInfoReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Build response
        var rsp = InfinityTowerInfoResp.newInstance()
                .setBountyLevel(0);
        
        // Encode and send
        return session.encodeMsg(NetMsgId.infinity_tower_info_succeed_ack, rsp);
    }

}
