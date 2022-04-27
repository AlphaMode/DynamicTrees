package com.ferreusveritas.dynamictrees.api.resource.loading.preparation;

import com.ferreusveritas.dynamictrees.api.resource.ResourceCollector;
import com.ferreusveritas.dynamictrees.api.resource.Resource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * @author Harley O'Connor
 */
public final class MultiJsonResourcePreparer extends
        AbstractResourcePreparer<Iterable<JsonElement>> {

    private static final String JSON_EXTENSION = ".json";

    private static final Gson GSON = (new GsonBuilder())
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public MultiJsonResourcePreparer(String folderName) {
        this(folderName, ResourceCollector.ordered());
    }

    public MultiJsonResourcePreparer(String folderName, ResourceCollector<Iterable<JsonElement>> resourceCollector) {
        super(folderName, JSON_EXTENSION, resourceCollector);
    }

    @Override
    protected void readAndPutResources(Collection<ResourceLocation> resourceLocations,
                                       ResourceManager resourceManager) {
        resourceLocations.forEach(location -> {
            final ResourceLocation resourceName = this.getResourceName(location);
            this.tryReadAndPutResource(resourceManager, location, resourceName);
        });
    }

    private void tryReadAndPutResource(ResourceManager resourceManager, ResourceLocation location,
                                       ResourceLocation resourceName) {
        try {
            this.readAndPutResource(resourceManager, location, resourceName);
        } catch (PreparationException | IOException e) {
            this.logError(location, e);
        }
    }

    @Override
    protected void readAndPutResource(net.minecraft.server.packs.resources.Resource resource, ResourceLocation resourceName)
            throws PreparationException, IOException {

    }

    private void readAndPutResource(ResourceManager resourceManager, ResourceLocation location,
                                    ResourceLocation resourceName) throws PreparationException, IOException {
        this.computeResourceListIfAbsent(resourceName)
                .addAll(this.collectResources(resourceManager, location));
    }

    private List<JsonElement> computeResourceListIfAbsent(ResourceLocation resourceName) {
        return (List<JsonElement>)
                this.resourceCollector.computeIfAbsent(resourceName,
                                () -> new Resource<>(resourceName, new LinkedList<>())
                        ).getResource();
    }

    private List<JsonElement> collectResources(ResourceManager resourceManager, ResourceLocation location)
            throws IOException, PreparationException {
        final List<JsonElement> resources = new LinkedList<>();
        for (net.minecraft.server.packs.resources.Resource resource : resourceManager.getResources(location)) {
            resources.add(JsonResourcePreparer.readResource(resource));
        }
        return resources;
    }

}
