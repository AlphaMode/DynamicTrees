package com.ferreusveritas.dynamictrees.api.resource;

import com.google.common.collect.Maps;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Harley O'Connor
 */
public interface ResourceCollector<R> {

    Resource<R> put(Resource<R> resource);

    Resource<R> computeIfAbsent(ResourceLocation key, Supplier<Resource<R>> resourceSupplier);

    ResourceAccessor<R> createAccessor();

    void clear();

    static <R> ResourceCollector<R> unordered() {
        return new SimpleResourceCollector<>(Maps::newHashMap);
    }

    static <R> ResourceCollector<R> ordered() {
        return new SimpleResourceCollector<>(Maps::newLinkedHashMap);
    }

}
