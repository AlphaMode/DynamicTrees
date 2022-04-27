package com.ferreusveritas.dynamictrees.api.resource.loading.preparation;

import com.ferreusveritas.dynamictrees.api.resource.ResourceAccessor;
import com.ferreusveritas.dynamictrees.api.resource.ResourceCollector;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * @author Harley O'Connor
 */
public abstract class AbstractResourcePreparer<R> implements ResourcePreparer<R> {

    private final String folderName;
    private final String extension;
    private final int extensionLength;
    protected final ResourceCollector<R> resourceCollector;

    public AbstractResourcePreparer(String folderName, String extension, ResourceCollector<R> resourceCollector) {
        this.folderName = folderName;
        this.extension = extension;
        this.extensionLength = extension.length();
        this.resourceCollector = resourceCollector;
    }

    @Override
    public ResourceAccessor<R> prepare(ResourceManager resourceManager) {
        this.readAndPutResources(this.collectResources(resourceManager), resourceManager);
        ResourceAccessor<R> accessor = this.resourceCollector.createAccessor();
        this.resourceCollector.clear(); // Refresh collector for future use.
        return accessor;
    }

    protected Collection<ResourceLocation> collectResources(ResourceManager resourceManager) {
        return resourceManager.listResources(this.folderName, (fileName) -> fileName.endsWith(this.extension));
    }

    protected void readAndPutResources(Collection<ResourceLocation> resourceLocations, ResourceManager resourceManager) {
        resourceLocations.forEach(location -> {
            final ResourceLocation resourceName = this.getResourceName(location);
            this.tryReadAndPutResource(resourceManager, location, resourceName);
        });
    }

    private void tryReadAndPutResource(ResourceManager resourceManager, ResourceLocation location,
                                       ResourceLocation resourceName) {
        try {
            this.readAndPutResource(resourceManager.getResource(location), resourceName);
        } catch (PreparationException | IOException e) {
            this.logError(location, e);
        }
    }

    protected abstract void readAndPutResource(Resource resource, ResourceLocation resourceName)
            throws PreparationException, IOException;

    protected void logError(ResourceLocation location, Exception e) {
        LogManager.getLogger().error("Could not read file \"" + location + "\" due to exception.", e);
    }

    protected ResourceLocation getResourceName(ResourceLocation location) {
        final String resourcePath = location.getPath();
        final int pathIndex = this.folderName.length() + 1;
        final int pathEndIndex = resourcePath.length() - this.extensionLength;

        return new ResourceLocation(location.getNamespace(),
                resourcePath.substring(pathIndex, pathEndIndex));
    }

}
