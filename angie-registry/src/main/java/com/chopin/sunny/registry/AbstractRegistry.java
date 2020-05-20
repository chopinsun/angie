package com.chopin.sunny.registry;

import com.chopin.sunny.model.URL;
import com.chopin.sunny.registry.api.Registry;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


/**
 * @title: angie
 * @description: 注册中心部分实现
 * @author: sunxiaobo
 * @create: 2020-05-13 15:13
 **/
@Log4j2
public abstract class AbstractRegistry implements Registry {

    private final File localCache;
    private final String CACHE_FILE_PATH="";

    public AbstractRegistry() {
        localCache = new File(CACHE_FILE_PATH);
        loadFromCache();
        init();
    }
    @Override
    abstract  public void init();
    @Override
    abstract public void destroy();

    @Override
    public void register(URL url) {
        addRegister(url);
        doRegister(url);
        updateLocalCache();
    }

    @Override
    public void unregister(URL url) {
        removeRegister(url);
        doUnRegister(url);
        updateLocalCache();
    }

    @Override
    public void subscribe(URL url, Set<URL> producter) {
        addSubscribeMap(url,producter);
        doSubscribe(url,producter);
    }

    @Override
    public void unSubscribe(URL url, Set<URL> producers) {
        removeSubscribeMap(url,producers);
        doUnSubscribe(url,producers);
    }

    protected void notify(URL invoker , URL service){
        updateLocalCache();
    }

    abstract protected void doRegister(URL url);

    abstract protected void doUnRegister(URL url);

    abstract protected void doSubscribe(URL url, Set<URL> producter);

    abstract protected void doUnSubscribe(URL url, Set<URL> producter);

    private void addRegister(URL url){
        Set<URL> set = localRegisterCaches.get(url.getUniqueKey());
        if(set!=null){
            set.add(url);
        }else{
            set = new HashSet<>();
            set.add(url);
            localRegisterCaches.put(url.getUniqueKey(),set);
        }
    }

    private void removeRegister(URL url){
        Set<URL> set =  localRegisterCaches.get(url.getUniqueKey());
        set.remove(url);
    }

    private void addSubscribeMap(URL url, Set<URL> producers){

        localSubscribeCaches.entrySet().stream().forEach(x->
            producers.stream().forEach(y->{
                if(x.getKey().equals(y)){
                    y.getService().addListener(url);
                }
            })
        );
    }

    private void removeSubscribeMap(URL url,Set<URL> producers){
        localSubscribeCaches.entrySet().stream().forEach(x->
            producers.stream().forEach(y->{
                if(x.getKey().equals(y)){
                    y.getService().getListeners().remove(url);
                }
            })
        );
    }

    private void updateLocalCache(){
        //TODO 更新本地缓存
    }

    private void loadFromCache(){
        //TODO 从本地缓存读取register
    }


}
