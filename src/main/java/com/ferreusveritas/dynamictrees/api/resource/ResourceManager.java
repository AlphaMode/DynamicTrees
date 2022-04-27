package com.ferreusveritas.dynamictrees.api.resource;

import com.ferreusveritas.dynamictrees.api.resource.loading.ResourceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.server.packs.PackResources;

/**
 * @author Harley O'Connor
 */
public interface ResourceManager extends net.minecraft.server.packs.resources.ResourceManager {

    void addLoader(ResourceLoader<?> loader);

    void addLoaders(ResourceLoader<?>... loaders);

    void addLoaderBefore(ResourceLoader<?> loader, ResourceLoader<?> existing);

    void addLoaderAfter(ResourceLoader<?> loader, ResourceLoader<?> existing);

    void registerAppliers();

    void load();

    void gatherData();

    void setup();

    CompletableFuture<?>[] prepareReload(Executor gameExecutor, Executor backgroundExecutor);

    void reload(CompletableFuture<?>[] futures);

    void addPack(TreeResourcePack pack);

    @Override
    Stream<PackResources> listPacks();

}
