package com.interdimensionalmallet.twtpthvtt.world;

import com.interdimensionalmallet.twtpthvtt.model.Thing;

public interface World {

    Thing getThingById(Long id);

    void saveThing(Thing thing);

}
