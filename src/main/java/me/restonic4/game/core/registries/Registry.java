package me.restonic4.game.core.registries;

import me.restonic4.game.core.AssetLocation;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    private static final Map<RegistryKey<?>, Map<AssetLocation, ?>> registries = new HashMap<>();

    public static <T extends RegistryItem> T register(RegistryKey<T> registryKey, AssetLocation assetLocation, T object) {
        Map<AssetLocation, T> registry = getOrCreateRegistry(registryKey);

        assetLocation.setRegistryKey(registryKey);

        if (registry.containsKey(assetLocation)) {
            throw new IllegalArgumentException("Duplicate asset location: " + assetLocation);
        }

        object.setAssetLocation(assetLocation);
        object.onPopulate();

        registry.put(assetLocation, object);

        return object;
    }

    public static <T extends RegistryItem> T get(RegistryKey<T> registryKey, AssetLocation assetLocation) {
        Map<AssetLocation, T> registry = getRegistry(registryKey);

        if (registry == null) return null;

        return registry.get(assetLocation);
    }

    @SuppressWarnings("unchecked")
    private static <T extends RegistryItem> Map<AssetLocation, T> getOrCreateRegistry(RegistryKey<T> registryKey) {
        return (Map<AssetLocation, T>) registries.computeIfAbsent(registryKey, k -> new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    private static <T extends RegistryItem> Map<AssetLocation, T> getRegistry(RegistryKey<T> registryKey) {
        return (Map<AssetLocation, T>) registries.get(registryKey);
    }
}
