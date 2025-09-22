package org.chubby.github.symbioticstructures.common.blocks.heartstone;

public enum HeartStoneStages
{
    DORMANT(0),
    PRIMORDIAL(1),
    SYMBIOTIC(2),
    MATURE(3);

    final int level;

    HeartStoneStages(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}