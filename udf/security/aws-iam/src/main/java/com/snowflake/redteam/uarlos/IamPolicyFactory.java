package com.snowflake.redteam.uarlos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class IamPolicyFactory {

  private static final Gson Loader;
  private static final Map<IamPolicyCacheKey, IamPolicyCacheEntry> policyCache =
      new HashMap<>();

  private static final int MAX_CACHE_SIZE = 512 * 1024;

  private IamPolicyFactory() {}

  static {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(String[].class, new MultiStringAdaptor());
    Loader = builder.create();
  }

  private static void popCacheEntry() {
    Map.Entry<IamPolicyCacheKey, IamPolicyCacheEntry> oldest = null;

    for (Map.Entry<IamPolicyCacheKey, IamPolicyCacheEntry> entry :
         policyCache.entrySet()) {
      if (oldest == null || entry.getValue().getLastUsed().isBefore(
                                oldest.getValue().getLastUsed())) {
        oldest = entry;
      }
    }

    if (oldest != null) {
      policyCache.remove(oldest.getKey());
    }
  }

  public static synchronized IamPolicy fromString(String value) {
    IamPolicyCacheKey key = IamPolicyCacheKey.fromString(value);
    return policyCache
        .computeIfAbsent(key,
                         cacheKey -> {
                           while (policyCache.size() >= MAX_CACHE_SIZE) {
                             popCacheEntry();
                           }
                           try {
                             return new IamPolicyCacheEntry(
                                 Loader.fromJson(value, IamPolicy.class));
                           } catch (JsonSyntaxException err) {
                             throw new RuntimeException("Failed parsing `" +
                                                        err + "` with value `" +
                                                        value + "`");
                           }
                         })
        .getPolicy();
  }

  public static IamPolicy fromFile(String path) throws IOException {
    return IamPolicyFactory.fromString(
        new String(Files.readAllBytes(Paths.get(path))));
  }
}