package com.chopin.sunny.registry.api;

import com.chopin.sunny.model.Concumer;
import com.chopin.sunny.model.ProviderService;

import java.util.List;

public interface RegisterCenter {

    void init();
    void registerProvider(List<ProviderService> providerServices);

    void registerConcumer(List<Concumer> concumers);
    void subscribe(List<ProviderService> providerServices);
    void unSubscribe(List<ProviderService> providerServices);
}
