package game.entity.building.vanguard;

import game.entity.Init;
import game.entity.Tags;
import game.entity.building.Building;
import game.entity.players;
import inputHandler.InputType;
import utils.Models;
import utils.NumUtil;

import java.util.ArrayList;
import java.util.EnumSet;

@Init
public class VanguardBarracks extends Building {
    private static Models model;
    private static long maxHp;
    @Override
    protected long getMaxHp(){
        return maxHp;
    }
    @Override
    protected Models getModel(){
        return model;
    }
    public VanguardBarracks(long x, long y, players player) {
        super();

        this.x = NumUtil.DTL(x);
        this.y = NumUtil.DTL(y);
        this.z = 0;
        tags = EnumSet.of(Tags.HEAVY_ARMORED, Tags.MECHANICAL, Tags.UNMOVABLE);
        lastZ = 0;
        lastX = NumUtil.DTL(x);
        lastY = NumUtil.DTL(y);
        this.player = player;
        hp = NumUtil.DTL(Math.random()*2000);
        armor = NumUtil.DTL(3);
        direction = NumUtil.DTL(0);
        effects = new ArrayList<>();
    }

    public static void init() {
        hasCollision = true;
        validCommandTypes = new ArrayList<>();
        validCommandTypes.add(InputType.RIGHT_CLICK);

        model = Models.vanguardBarracks;
        maxHp = NumUtil.DTL(2000);
        Models.vanguardBarracks.set(55, 60, 30, Models.ModelType.BUILDING);
    }
}
