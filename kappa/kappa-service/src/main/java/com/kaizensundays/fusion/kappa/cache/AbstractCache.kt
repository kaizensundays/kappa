package com.kaizensundays.fusion.kappa.cache

import com.kaizensundays.fusion.kappa.unsupportedOperation
import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.configuration.CacheEntryListenerConfiguration
import javax.cache.configuration.Configuration
import javax.cache.integration.CompletionListener
import javax.cache.processor.EntryProcessor
import javax.cache.processor.EntryProcessorResult

/**
 * Created: Sunday 10/23/2022, 12:20 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class AbstractCache<K, V> : Cache<K, V> {
    override fun iterator(): MutableIterator<Cache.Entry<K, V>> {
        unsupportedOperation()
    }

    override fun close() {
        // to be implemented
    }

    override fun get(key: K): V {
        unsupportedOperation()
    }

    override fun getAll(keys: MutableSet<out K>?): MutableMap<K, V> {
        unsupportedOperation()
    }

    override fun containsKey(key: K): Boolean {
        unsupportedOperation()
    }

    override fun loadAll(keys: MutableSet<out K>?, replaceExistingValues: Boolean, completionListener: CompletionListener?) {
        unsupportedOperation()
    }

    override fun put(key: K, value: V) {
        unsupportedOperation()
    }

    override fun getAndPut(key: K, value: V): V {
        unsupportedOperation()
    }

    override fun putAll(map: MutableMap<out K, out V>?) {
        unsupportedOperation()
    }

    override fun putIfAbsent(key: K, value: V): Boolean {
        unsupportedOperation()
    }

    override fun remove(key: K): Boolean {
        unsupportedOperation()
    }

    override fun remove(key: K, oldValue: V): Boolean {
        unsupportedOperation()
    }

    override fun getAndRemove(key: K): V {
        unsupportedOperation()
    }

    override fun replace(key: K, oldValue: V, newValue: V): Boolean {
        unsupportedOperation()
    }

    override fun replace(key: K, value: V): Boolean {
        unsupportedOperation()
    }

    override fun getAndReplace(key: K, value: V): V {
        unsupportedOperation()
    }

    override fun removeAll(keys: MutableSet<out K>?) {
        unsupportedOperation()
    }

    override fun removeAll() {
        unsupportedOperation()
    }

    override fun clear() {
        unsupportedOperation()
    }

    override fun <C : Configuration<K, V>?> getConfiguration(clazz: Class<C>?): C {
        unsupportedOperation()
    }

    override fun <T : Any?> invoke(key: K, entryProcessor: EntryProcessor<K, V, T>?, vararg arguments: Any?): T {
        unsupportedOperation()
    }

    override fun <T : Any?> invokeAll(keys: MutableSet<out K>?, entryProcessor: EntryProcessor<K, V, T>?, vararg arguments: Any?): MutableMap<K, EntryProcessorResult<T>> {
        unsupportedOperation()
    }

    override fun getName(): String {
        unsupportedOperation()
    }

    override fun getCacheManager(): CacheManager {
        unsupportedOperation()
    }

    override fun isClosed(): Boolean {
        unsupportedOperation()
    }

    override fun <T : Any?> unwrap(clazz: Class<T>?): T {
        unsupportedOperation()
    }

    override fun registerCacheEntryListener(cacheEntryListenerConfiguration: CacheEntryListenerConfiguration<K, V>?) {
        unsupportedOperation()
    }

    override fun deregisterCacheEntryListener(cacheEntryListenerConfiguration: CacheEntryListenerConfiguration<K, V>?) {
        unsupportedOperation()
    }
}