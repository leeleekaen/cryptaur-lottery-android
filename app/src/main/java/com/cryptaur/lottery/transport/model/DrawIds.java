package com.cryptaur.lottery.transport.model;

import java.util.ArrayList;
import java.util.Collections;

public class DrawIds extends ArrayList<DrawId> {
    public DrawIds() {
        super(Lottery.values().length);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DrawIds))
            return false;

        DrawIds other = (DrawIds) o;
        if (this.size() != other.size())
            return false;

        for (DrawId drawId : this) {
            boolean found = false;
            for (DrawId otherId : other) {
                if (otherId.lottery == drawId.lottery) {
                    if (otherId.number != drawId.number) {
                        return false;
                    }
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }

    public void sortByLotteryId() {
        Collections.sort(this, (o1, o2) -> o2.lottery.ordinal() - o1.lottery.ordinal());
    }

    public DrawId get(Lottery lottery) {
        for (DrawId drawId : this) {
            if (drawId.lottery == lottery)
                return drawId;
        }
        return null;
    }

    public DrawIds prev() {
        DrawIds result = new DrawIds();
        for (DrawId drawId : this) {
            if (drawId.number > 1)
                result.add(new DrawId(drawId.lottery, drawId.number - 1));
            else {
                result.add(new DrawId(drawId.lottery, 1));
            }
        }
        return result;
    }

    public DrawIds next() {
        DrawIds result = new DrawIds();
        for (DrawId drawId : this) {
            result.add(new DrawId(drawId.lottery, drawId.number + 1));
        }
        return result;
    }
}
