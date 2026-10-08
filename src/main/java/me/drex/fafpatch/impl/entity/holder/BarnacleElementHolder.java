package me.drex.fafpatch.impl.entity.holder;

import com.faboslav.friendsandfoes.common.entity.BarnacleEntity;
import me.drex.fafpatch.impl.FriendsAndFoesPatch;
import me.drex.fafpatch.impl.entity.SimpleElementHolder;
import me.drex.fafpatch.impl.entity.model.EntityModels;
import me.drex.fafpatch.impl.entity.model.entity.BarnacleEntityModel;

public class BarnacleElementHolder extends SimpleElementHolder<BarnacleEntity, BarnacleEntityModel> {
    public BarnacleElementHolder(BarnacleEntity entity) {
        super(entity);
        addConditionalLayer(barnacle -> !barnacle.isInvisible(), FriendsAndFoesPatch.id("kelp"),
            visible -> visible ? EntityModels.BARNACLE_KELP : null);
        addConditionalLayer(barnacle -> !barnacle.isInvisible(), FriendsAndFoesPatch.id("kelp_head"),
            visible -> visible ? EntityModels.BARNACLE_KELP_HEAD : null);
    }

    @Override
    public float getEntityScale() {
        return BarnacleEntityModel.SCALE;
    }
}
